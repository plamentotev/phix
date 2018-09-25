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

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import phix.submission.Submission;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class YamlSubmissionParser implements  SubmissionParser {

    private final Yaml yamlProcessor;

    public YamlSubmissionParser(Yaml yamlProcessor) {
        this.yamlProcessor = yamlProcessor;
    }

    @Override
    public Submission parseSubmission(InputStream submission) throws SubmissionParserException {
        try {
            return yamlProcessor.loadAs(submission, Submission.class);
        } catch (YAMLException e) {
            throw new AssignmentParserException("Exception thrown while trying to parse submission.", e);
        }
    }

    @Override
    public void writeSubmission(Submission submission, OutputStream output) throws SubmissionParserException {
        try {
            String submissionYaml = yamlProcessor.dumpAsMap(submission);
            output.write(submissionYaml.getBytes(StandardCharsets.UTF_8));
        } catch (YAMLException | IOException e) {
            throw new AssignmentParserException("Exception thrown while trying to parse submission.", e);
        }
    }

}
