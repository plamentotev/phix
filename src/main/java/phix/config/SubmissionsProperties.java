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
package phix.config;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="submissions")
public class SubmissionsProperties {

    @NotBlank
    private String baseDirectory;
    @Min(1)
    private long maxSubmissionSize;
    @NotEmpty
    private Map<String, SubmissionExecutorConfiguration> submissionExecutors;

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public long getMaxSubmissionSize() {
        return maxSubmissionSize;
    }

    public void setMaxSubmissionSize(long maxSubmissionSize) {
        this.maxSubmissionSize = maxSubmissionSize;
    }

    public Map<String, SubmissionExecutorConfiguration> getSubmissionExecutors() {
        return submissionExecutors;
    }

    public void setSubmissionExecutors(Map<String, SubmissionExecutorConfiguration> submissionExecutors) {
        this.submissionExecutors = submissionExecutors;
    }

    public static class SubmissionExecutorConfiguration {

        private String className;

        private Map<String, String> config;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Map<String, String> getConfig() {
            return config;
        }

        public void setConfig(Map<String, String> config) {
            this.config = config;
        }
    }

}
