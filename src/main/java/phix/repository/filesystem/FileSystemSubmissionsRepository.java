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
package phix.repository.filesystem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import phix.repository.PhixRepositoryException;
import phix.submission.Submission;
import phix.submission.SubmissionFile;
import phix.submission.SubmissionFileNotFoundException;
import phix.submission.SubmissionNotFoundException;
import phix.submission.SubmissionStatus;
import phix.submission.SubmissionsRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemSubmissionsRepository implements SubmissionsRepository {

    private static final String SOLUTION_FILE_NAME = "solution";
    private static final String SUBMISSION_FILE_NAME = "submission.yml";

    private static final Log log = LogFactory.getLog(FileSystemSubmissionsRepository.class);

    private final Path basePath;

    private final SubmissionParser submissionParser;

    public FileSystemSubmissionsRepository(String baseDirectory, SubmissionParser submissionParser) {
        this.basePath = Paths.get(baseDirectory).normalize();
        this.submissionParser = submissionParser;
    }

    @Override
    public void addSubmission(Submission submission, SubmissionFile submittedFile) {
        Path submissionPath = getSubmissionPath(submission.getId());
        try {
            if (!Files.exists(submissionPath)) {
                    Files.createDirectories(submissionPath);
            } else {
                log.warn("Trying to override submission with id: " + submission.getId());

                throw new PhixRepositoryException(String.format("Submission with id: %s already exists",
                        submission.getId()));
            }

            writeSubmission(submissionPath, submission);

            submittedFile.moveTo(submissionPath.resolve(SOLUTION_FILE_NAME));
        } catch (IOException e) {
            log.warn("IO error occurred while trying to write submission to " + submissionPath, e);

            throw new PhixRepositoryException("IO error occurred while trying to add the submission", e);
        }
    }

    @Override
    public Submission getSubmission(String id) {
        Path submissionFilePath = getSubmissionPath(id).resolve(SUBMISSION_FILE_NAME);

        if (!Files.exists(submissionFilePath)) {
            throw new SubmissionNotFoundException(String.format("No submission with id: %s found.", id));
        }

        try (InputStream input = Files.newInputStream(submissionFilePath)) {
            return submissionParser.parseSubmission(input);
        } catch (IOException e) {
            log.warn("IO error occurred while trying to read submission with id: " + id, e);

            throw new PhixRepositoryException("IO error occurred while trying to read submission", e);
        }
    }

    @Override
    public void updateSubmissionStatus(String id, SubmissionStatus status, String message) {
        Submission submission = getSubmission(id);
        Path submissionPath = getSubmissionPath(id);

        submission.setSubmissionStatus(status);
        submission.setMessage(message);

        try {
            writeSubmission(submissionPath, submission);
        } catch (IOException e) {
            log.warn("IO error occurred while trying to update submission with id: " + id, e);

            throw new PhixRepositoryException("IO error occurred while trying to update submission", e);
        }
    }

    @Override
    public SubmissionFile getSubmissionFile(String submissionId) {
        Path submissionFilePath = getSubmissionPath(submissionId).resolve(SOLUTION_FILE_NAME);

        if (!Files.exists(submissionFilePath)) {
            throw new SubmissionFileNotFoundException(String.format("No submission file for submission with id: %s found.",
                    submissionId));
        }

        return new FilesystemSubmissionFile(submissionFilePath);
    }

    private void writeSubmission(Path submissionPath, Submission submission) throws IOException {
        Files.createDirectories(submissionPath);

        try(OutputStream submissionOutputStream = Files.newOutputStream(submissionPath.resolve(SUBMISSION_FILE_NAME))) {
            submissionParser.writeSubmission(submission, submissionOutputStream);
        }
    }

    private Path getSubmissionPath(String submissionId) {
        Path submissionPath = basePath.resolve(submissionId);

        if (!submissionPath.startsWith(basePath)) {
            log.error("Trying to access submission outside submissions directory. Trying to access " + submissionPath);

            throw new PhixRepositoryException("Invalid submission directory.");
        }

        return submissionPath;
    }

}
