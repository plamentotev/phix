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

public class DockerSubmissionExecutorParams {

    private String solutionPath;
    private String solutionFileName;
    private int solutionFileMode = 0664;
    private String compilationImage;
    private String compilationOutputPath;
    private long compilationMaxDiskUsage;
    private int compilationPidLimit;
    private long compilationFilesLimit;
    private long compilationMaxMemory;
    private long compilationMaxSwapMemory;
    private boolean compilationOutputHidden;
    private long compilationMaxLogSize;
    private int compilationExpectedExitCode;
    private boolean compilationNetworkEnabled;
    private String runImage;
    private String runCompilationArtifactPath;
    private long runMaxDiskUsage;
    private int runPidLimit;
    private long runFilesLimit;
    private long runMaxMemory;
    private long runMaxSwapMemory;
    private boolean runOutputHidden;
    private long runMaxLogSize;
    private int runExpectedExitCode;
    private boolean runNetworkEnabled;
    private String verificationImage;
    private String verificationOutputPath;
    private String verificationOutputFileName;
    private int verificationOutputFileMode = 0664;
    private long verificationMaxDiskUsage;
    private int verificationPidLimit;
    private long verificationFilesLimit;
    private long verificationMaxMemory;
    private long verificationMaxSwapMemory;
    private boolean verificationOutputHidden;
    private long verificationMaxLogSize;
    private int verificationExpectedExitCode;
    private boolean verificationNetworkEnabled;
    private long timeLimit;

    public String getSolutionPath() {
        return solutionPath;
    }

    public void setSolutionPath(String solutionPath) {
        this.solutionPath = solutionPath;
    }

    public String getSolutionFileName() {
        return solutionFileName;
    }

    public void setSolutionFileName(String solutionFileName) {
        this.solutionFileName = solutionFileName;
    }

    public int getSolutionFileMode() {
        return solutionFileMode;
    }

    public void setSolutionFileMode(int solutionFileMode) {
        this.solutionFileMode = solutionFileMode;
    }

    public String getCompilationImage() {
        return compilationImage;
    }

    public void setCompilationImage(String compilationImage) {
        this.compilationImage = compilationImage;
    }

    public String getCompilationOutputPath() {
        return compilationOutputPath;
    }

    public void setCompilationOutputPath(String compilationOutputPath) {
        this.compilationOutputPath = compilationOutputPath;
    }

    public long getCompilationMaxDiskUsage() {
        return compilationMaxDiskUsage;
    }

    public void setCompilationMaxDiskUsage(long compilationMaxDiskUsage) {
        this.compilationMaxDiskUsage = compilationMaxDiskUsage;
    }

    public int getCompilationPidLimit() {
        return compilationPidLimit;
    }

    public void setCompilationPidLimit(int compilationPidLimit) {
        this.compilationPidLimit = compilationPidLimit;
    }

    public long getCompilationFilesLimit() {
        return compilationFilesLimit;
    }

    public void setCompilationFilesLimit(long compilationFilesLimit) {
        this.compilationFilesLimit = compilationFilesLimit;
    }

    public long getCompilationMaxMemory() {
        return compilationMaxMemory;
    }

    public void setCompilationMaxMemory(long compilationMaxMemory) {
        this.compilationMaxMemory = compilationMaxMemory;
    }

    public long getCompilationMaxSwapMemory() {
        return compilationMaxSwapMemory;
    }

    public void setCompilationMaxSwapMemory(long compilationMaxSwapMemory) {
        this.compilationMaxSwapMemory = compilationMaxSwapMemory;
    }

    public boolean isCompilationOutputHidden() {
        return compilationOutputHidden;
    }

    public void setCompilationOutputHidden(boolean compilationOutputHidden) {
        this.compilationOutputHidden = compilationOutputHidden;
    }

    public long getCompilationMaxLogSize() {
        return compilationMaxLogSize;
    }

    public void setCompilationMaxLogSize(long compilationMaxLogSize) {
        this.compilationMaxLogSize = compilationMaxLogSize;
    }

    public int getCompilationExpectedExitCode() {
        return compilationExpectedExitCode;
    }

    public void setCompilationExpectedExitCode(int compilationExpectedExitCode) {
        this.compilationExpectedExitCode = compilationExpectedExitCode;
    }

    public boolean isCompilationNetworkEnabled() {
        return compilationNetworkEnabled;
    }

    public void setCompilationNetworkEnabled(boolean compilationNetworkEnabled) {
        this.compilationNetworkEnabled = compilationNetworkEnabled;
    }

    public String getRunImage() {
        return runImage;
    }

    public void setRunImage(String runImage) {
        this.runImage = runImage;
    }

    public String getRunCompilationArtifactPath() {
        return runCompilationArtifactPath;
    }

    public void setRunCompilationArtifactPath(String runCompilationArtifactPath) {
        this.runCompilationArtifactPath = runCompilationArtifactPath;
    }

    public long getRunMaxDiskUsage() {
        return runMaxDiskUsage;
    }

    public void setRunMaxDiskUsage(long runMaxDiskUsage) {
        this.runMaxDiskUsage = runMaxDiskUsage;
    }

    public int getRunPidLimit() {
        return runPidLimit;
    }

    public void setRunPidLimit(int runPidLimit) {
        this.runPidLimit = runPidLimit;
    }

    public long getRunFilesLimit() {
        return runFilesLimit;
    }

    public void setRunFilesLimit(long runFilesLimit) {
        this.runFilesLimit = runFilesLimit;
    }

    public long getRunMaxMemory() {
        return runMaxMemory;
    }

    public void setRunMaxMemory(long runMaxMemory) {
        this.runMaxMemory = runMaxMemory;
    }

    public long getRunMaxSwapMemory() {
        return runMaxSwapMemory;
    }

    public void setRunMaxSwapMemory(long runMaxSwapMemory) {
        this.runMaxSwapMemory = runMaxSwapMemory;
    }

    public boolean isRunOutputHidden() {
        return runOutputHidden;
    }

    public void setRunOutputHidden(boolean runOutputHidden) {
        this.runOutputHidden = runOutputHidden;
    }

    public long getRunMaxLogSize() {
        return runMaxLogSize;
    }

    public void setRunMaxLogSize(long runMaxLogSize) {
        this.runMaxLogSize = runMaxLogSize;
    }

    public int getRunExpectedExitCode() {
        return runExpectedExitCode;
    }

    public void setRunExpectedExitCode(int runExpectedExitCode) {
        this.runExpectedExitCode = runExpectedExitCode;
    }

    public boolean isRunNetworkEnabled() {
        return runNetworkEnabled;
    }

    public void setRunNetworkEnabled(boolean runNetworkEnabled) {
        this.runNetworkEnabled = runNetworkEnabled;
    }

    public String getVerificationImage() {
        return verificationImage;
    }

    public void setVerificationImage(String verificationImage) {
        this.verificationImage = verificationImage;
    }

    public String getVerificationOutputPath() {
        return verificationOutputPath;
    }

    public void setVerificationOutputPath(String verificationOutputPath) {
        this.verificationOutputPath = verificationOutputPath;
    }

    public String getVerificationOutputFileName() {
        return verificationOutputFileName;
    }

    public void setVerificationOutputFileName(String verificationOutputFileName) {
        this.verificationOutputFileName = verificationOutputFileName;
    }

    public int getVerificationOutputFileMode() {
        return verificationOutputFileMode;
    }

    public void setVerificationOutputFileMode(int verificationOutputFileMode) {
        this.verificationOutputFileMode = verificationOutputFileMode;
    }

    public long getVerificationMaxDiskUsage() {
        return verificationMaxDiskUsage;
    }

    public void setVerificationMaxDiskUsage(long verificationMaxDiskUsage) {
        this.verificationMaxDiskUsage = verificationMaxDiskUsage;
    }

    public int getVerificationPidLimit() {
        return verificationPidLimit;
    }

    public void setVerificationPidLimit(int verificationPidLimit) {
        this.verificationPidLimit = verificationPidLimit;
    }

    public long getVerificationFilesLimit() {
        return verificationFilesLimit;
    }

    public void setVerificationFilesLimit(long verificationFilesLimit) {
        this.verificationFilesLimit = verificationFilesLimit;
    }

    public long getVerificationMaxMemory() {
        return verificationMaxMemory;
    }

    public void setVerificationMaxMemory(long verificationMaxMemory) {
        this.verificationMaxMemory = verificationMaxMemory;
    }

    public long getVerificationMaxSwapMemory() {
        return verificationMaxSwapMemory;
    }

    public void setVerificationMaxSwapMemory(long verificationMaxSwapMemory) {
        this.verificationMaxSwapMemory = verificationMaxSwapMemory;
    }

    public boolean isVerificationOutputHidden() {
        return verificationOutputHidden;
    }

    public void setVerificationOutputHidden(boolean verificationOutputHidden) {
        this.verificationOutputHidden = verificationOutputHidden;
    }

    public long getVerificationMaxLogSize() {
        return verificationMaxLogSize;
    }

    public void setVerificationMaxLogSize(long verificationMaxLogSize) {
        this.verificationMaxLogSize = verificationMaxLogSize;
    }

    public int getVerificationExpectedExitCode() {
        return verificationExpectedExitCode;
    }

    public void setVerificationExpectedExitCode(int verificationExpectedExitCode) {
        this.verificationExpectedExitCode = verificationExpectedExitCode;
    }

    public boolean isVerificationNetworkEnabled() {
        return verificationNetworkEnabled;
    }

    public void setVerificationNetworkEnabled(boolean verificationNetworkEnabled) {
        this.verificationNetworkEnabled = verificationNetworkEnabled;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

}
