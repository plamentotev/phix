/*
Copyright (c) 2017 Faculty of Mathematics and Informatics - Sofia University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package phix.submission.executor.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.RemoveContainerParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Ulimit;
import com.spotify.docker.client.messages.LogConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import phix.assignment.Assignment;
import phix.submission.Submission;
import phix.submission.SubmissionFile;
import phix.submission.SubmissionStatus;
import phix.submission.executor.SubmissionExecutionResult;
import phix.submission.executor.SubmissionExecutor;
import phix.util.TarUtils;
import phix.util.TimeLimitedExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DockerSubmissionExecutor implements SubmissionExecutor {

    private static final String CONFIG_URI = "uri";
    private static final String CONFIG_CONCURRENT_EXECUTIONS = "concurrentExecutions";

    private static final String DOCKER_STORAGE_OPTION_SIZE = "size";
    private static final String DOCKER_LOG_CONFIG_JSON_FILE = "json-file";
    private static final String DOCKER_LOG_CONFIG_OPTION_MAX_SIZE = "max-size";
    private static final String DOCKER_LOG_CONFIG_OPTION_MAX_FILE = "max-file";
    private static final String DOCKER_ULIMIT_FILES = "nofile";

    private static final Log log = LogFactory.getLog(DockerSubmissionExecutor.class);

    private DockerClient dockerClient;

    private TimeLimitedExecutor executorService;

    public void init(Map<String, String> config) {
        int threadsNum = Integer.parseInt(config.get(CONFIG_CONCURRENT_EXECUTIONS));
        executorService = new TimeLimitedExecutor(Executors.newFixedThreadPool(threadsNum));

        dockerClient = DefaultDockerClient.builder()
                .connectionPoolSize(threadsNum)
                .uri(config.get(CONFIG_URI))
                .build();
    }

    @Override
    public void execute(Assignment assignment, Submission submission, SubmissionFile submissionFile,
                        Consumer<? super SubmissionExecutionResult> listener)
    {
        try {
            DockerSubmissionExecutorParams params = new DockerSubmissionExecutorParams();
            BeanUtils.populate(params, assignment.getExecutorParams());
            executorService.execute(() -> doExecute(params, submission, submissionFile, listener),
                    params.getTimeLimit());
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException("Illegal executor parameters.", ex);
        }
    }

    private void doExecute(DockerSubmissionExecutorParams params,
                           Submission submission, SubmissionFile submissionFile,
                           Consumer<? super SubmissionExecutionResult> listener)
    {
        log.info("Executing submission with id: " + submission.getId());

        String compilationContainerId = null;
        String runContainerId = null;
        String verificationContainerId = null;

        try {
            if (StringUtils.isNotBlank(params.getCompilationImage())) {
                ContainerConfig compilationContainerConfig = configureCompilationContainer(params);
                compilationContainerId = dockerClient.createContainer(compilationContainerConfig).id();
                copySubmissionFile(submission, params, submissionFile, compilationContainerId);

                int compilerExitCode = executeContainer(compilationContainerId);

                if (compilerExitCode != params.getCompilationExpectedExitCode()) {
                    log.info(String.format("Submission with id: %s compilation process exited with unexpected code: %s",
                            submission.getId(), compilerExitCode));

                    String executionOutput = getExecutionOutput(compilationContainerId, null, null, params);
                    listener.accept(new SubmissionExecutionResult(SubmissionStatus.COMPILATION_ERROR, executionOutput));

                    return;
                }
            }

            boolean verificationStageRequired = StringUtils.isNotBlank(params.getVerificationImage());

            ContainerConfig runContainerConfig = configureRunContainer(params);
            runContainerId = dockerClient.createContainer(runContainerConfig).id();
            if (StringUtils.isNotBlank(params.getCompilationOutputPath())) {
                copyCompilationArtifact(compilationContainerId, params.getCompilationOutputPath(),
                        runContainerId, params.getRunCompilationArtifactPath());
            } else {
                copySubmissionFile(submission, params, submissionFile, runContainerId);
            }
            int runExitCode = executeContainer(runContainerId);

            if (runExitCode != params.getRunExpectedExitCode()) {
                log.info(String.format("Submission with id: %s process exited with unexpected code: %s",
                        submission.getId(), runExitCode));

                String executionOutput = getExecutionOutput(compilationContainerId, runContainerId, null, params);
                listener.accept(new SubmissionExecutionResult(
                        (verificationStageRequired) ? SubmissionStatus.RUNTIME_ERROR : SubmissionStatus.WRONG_ANSWER,
                        executionOutput));

                return;
            }

            if (verificationStageRequired) {
                ContainerConfig verificationContainerConfig = configureVerificationContainer(params);
                verificationContainerId = dockerClient.createContainer(verificationContainerConfig).id();
                String runLog = getContainerLogs(runContainerId);
                copyLogForVerification(verificationContainerId, runLog, params);
                int verificationExitCode = executeContainer(verificationContainerId);

                if (verificationExitCode != params.getVerificationExpectedExitCode()) {
                    log.info(String.format("Submission with id: %s verification exited with unexpected code: %s",
                            submission.getId(), verificationExitCode));

                    String executionOutput = getExecutionOutput(compilationContainerId, runContainerId,
                            verificationContainerId, params);
                    listener.accept(new SubmissionExecutionResult(SubmissionStatus.WRONG_ANSWER, executionOutput));

                    return;
                }
            }

            log.info(String.format("Submission with id: %s completed successfully", submission.getId()));

            String executionOutput = getExecutionOutput(compilationContainerId, runContainerId,
                    verificationContainerId, params);
            listener.accept(new SubmissionExecutionResult(SubmissionStatus.SUCCESS, executionOutput));
        } catch (DockerException | IOException ex) {
            log.warn(String.format("Error occurred while executing submission with id: %s", submission.getId()), ex);

            listener.accept(new SubmissionExecutionResult(SubmissionStatus.INTERNAL_ERROR, ""));
        } catch (InterruptedException ex) {
            log.info(String.format("Time limit exceeded while executing submission with id: %s", submission.getId()), ex);

            try {
                String executionOutput = getExecutionOutput(compilationContainerId, runContainerId,
                        verificationContainerId, params);
                listener.accept(new SubmissionExecutionResult(SubmissionStatus.TIMEOUT, executionOutput));
            } catch (DockerException | InterruptedException e) {
                log.warn(String.format("Error occurred while executing submission with id: %s", submission.getId()), e);

                listener.accept(new SubmissionExecutionResult(SubmissionStatus.INTERNAL_ERROR, ""));
            }
        } catch (Exception ex) {
            log.warn(String.format("Error occurred while executing submission with id: %s", submission.getId()), ex);

            listener.accept(new SubmissionExecutionResult(SubmissionStatus.INTERNAL_ERROR, ""));

            throw ex;
        } finally {
            removeContainer(compilationContainerId);
            removeContainer(runContainerId);
            removeContainer(verificationContainerId);
        }
    }

    @Override
    public void shutdown() {
        log.info("Shutting down docker submission executor..." );

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private int executeContainer(String containerId)
            throws DockerException, InterruptedException, IOException
    {
        dockerClient.startContainer(containerId);

        return dockerClient.waitContainer(containerId).statusCode();
    }

    private void copySubmissionFile(Submission submission, DockerSubmissionExecutorParams params,
                                    SubmissionFile submissionFile, String containerId)
            throws IOException, DockerException, InterruptedException
    {
        InputStream submissionAsTar = TarUtils.wrapInputStream(submissionFile.getInputStream(),
                params.getSolutionFileName(), submissionFile.getSize(),
                submission.getTimestamp(), params.getSolutionFileMode());

        dockerClient.copyToContainer(submissionAsTar, containerId, params.getSolutionPath());
    }

    private void copyCompilationArtifact(String compilationContainerId, String compilationOutputPath,
                                         String runContainerId, String runCompilationArtifactPath)
            throws IOException, DockerException, InterruptedException
    {
        Path compilationArtifact = Files.createTempFile(null, null);
        log.info(String.format("Temporary file %s created. Copying the compilation output from container id: %s",
                compilationArtifact, compilationContainerId));

        try {
            try (InputStream compilationOutput =
                         dockerClient.archiveContainer(compilationContainerId, compilationOutputPath))
            {
                Files.copy(compilationOutput, compilationArtifact, StandardCopyOption.REPLACE_EXISTING);
            }
            try (InputStream compilationArtifactInputStream = Files.newInputStream(compilationArtifact)) {
                dockerClient.copyToContainer(compilationArtifactInputStream, runContainerId, runCompilationArtifactPath);
            }
        } finally {
            removeFile(compilationArtifact);
        }

    }

    private void copyLogForVerification(String containerId, String log, DockerSubmissionExecutorParams params)
            throws InterruptedException, DockerException, IOException
    {
        InputStream logAsTar = TarUtils.wrapString(log, params.getVerificationOutputFileName(),
                System.currentTimeMillis(), params.getVerificationOutputFileMode());

        dockerClient.copyToContainer(logAsTar, containerId, params.getVerificationOutputPath());
    }

    private String getExecutionOutput(String compilationContainerId, String runContainerId,
                                      String verificationContainerId, DockerSubmissionExecutorParams params)
            throws DockerException, InterruptedException
    {
        StringBuilder executionOutput = new StringBuilder();

        if (compilationContainerId != null && !params.isCompilationOutputHidden()) {
            executionOutput.append(getContainerLogs(compilationContainerId));
        }

        if (runContainerId != null && !params.isRunOutputHidden()) {
            executionOutput.append(getContainerLogs(runContainerId));
        }

        if (verificationContainerId != null && !params.isVerificationOutputHidden()) {
            executionOutput.append(getContainerLogs(verificationContainerId));
        }

        return executionOutput.toString();
    }

    private String getContainerLogs(String containerId) throws DockerException, InterruptedException {
        StringBuilder logs = new StringBuilder();

        LogStream logStream = dockerClient.logs(containerId,
                DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr());
        logStream.forEachRemaining(msg -> logs.append(StandardCharsets.UTF_8.decode(msg.content())));

        return logs.toString();
    }

    private ContainerConfig configureCompilationContainer(DockerSubmissionExecutorParams params) {
        return configureContainer(params.getCompilationImage(),
                params.getCompilationMaxDiskUsage(), params.getCompilationPidLimit(), params.getCompilationFilesLimit(),
                params.getCompilationMaxMemory(), params.getCompilationMaxSwapMemory(),
                params.getCompilationMaxLogSize(), params.isCompilationNetworkEnabled());
    }

    private ContainerConfig configureRunContainer(DockerSubmissionExecutorParams params) {
        return configureContainer(params.getRunImage(),
                params.getRunMaxDiskUsage(), params.getRunPidLimit(), params.getRunFilesLimit(),
                params.getRunMaxMemory(), params.getRunMaxSwapMemory(),
                params.getRunMaxLogSize(), params.isRunNetworkEnabled());
    }

    private ContainerConfig configureVerificationContainer(DockerSubmissionExecutorParams params) {
        return configureContainer(params.getVerificationImage(),
                params.getVerificationMaxDiskUsage(), params.getVerificationPidLimit(), params.getVerificationFilesLimit(),
                params.getVerificationMaxMemory(), params.getVerificationMaxSwapMemory(),
                params.getVerificationMaxLogSize(), params.isVerificationNetworkEnabled());
    }

    private ContainerConfig configureContainer(String image,
                                               long maxDiskUsage, int pidLimit, long filesLimit,
                                               long maxMemory, long maxSwapMemory,
                                               long maxLogSize, boolean networkEnabled)
    {
        HostConfig.Builder hostConfigBuilder = HostConfig.builder();

        if (maxMemory > 0) {
            hostConfigBuilder.memory(maxMemory);
        }

        if (maxSwapMemory > 0) {
            hostConfigBuilder.memorySwap(maxSwapMemory);
        }

        if (maxLogSize > 0) {
            Map<String, String> runLogConfigOption = new HashMap<>();
            runLogConfigOption.put(DOCKER_LOG_CONFIG_OPTION_MAX_SIZE, Long.toString(maxLogSize));
            runLogConfigOption.put(DOCKER_LOG_CONFIG_OPTION_MAX_FILE, "1");
            LogConfig runLogConfig = LogConfig.create(DOCKER_LOG_CONFIG_JSON_FILE, runLogConfigOption);
            hostConfigBuilder.logConfig(runLogConfig);
        }

        if (maxDiskUsage > 0) {
            hostConfigBuilder.storageOpt(Collections.singletonMap(DOCKER_STORAGE_OPTION_SIZE,
                    Long.toString(maxDiskUsage)));
        }

        if (pidLimit > 0) {
            hostConfigBuilder.pidsLimit(pidLimit);
        }

        if (filesLimit > 0) {
            hostConfigBuilder.ulimits(Collections.singletonList(
                    Ulimit.create(DOCKER_ULIMIT_FILES, filesLimit, filesLimit)));
        }

        HostConfig hostConfig = hostConfigBuilder.build();
        return ContainerConfig.builder()
                .image(image)
                .networkDisabled(!networkEnabled)
                .hostConfig(hostConfig)
                .build();
    }

    private void removeContainer(String containerId) {
        if (containerId != null) {
            try {
                log.info("Removing container with id: " + containerId);

                dockerClient.removeContainer(containerId, RemoveContainerParam.forceKill());
            } catch (DockerException | InterruptedException ex) {
                log.warn(String.format("Cannot remove container with id: %s", containerId), ex);
            }
        }
    }

    private void removeFile(Path file) {
        if (file != null) {
            try {
                log.info("Removing temporary file: " + file);

                Files.deleteIfExists(file);
            } catch (IOException ex) {
                log.warn("Cannot remove temporary file: " + file, ex);
            }
        }
    }

}
