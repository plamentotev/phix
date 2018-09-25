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
package phix.submission.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import phix.assignment.Assignment;
import phix.submission.Submission;
import phix.submission.SubmissionFile;
import phix.submission.SubmissionStatus;

import java.util.Map;
import java.util.function.Consumer;

public class ManualReviewSubmissionExecutor implements SubmissionExecutor {

    private static final Log log = LogFactory.getLog(ManualReviewSubmissionExecutor.class);

    @Override
    public void init(Map<String, String> config) {

    }

    @Override
    public void execute(Assignment assignment, Submission submission, SubmissionFile submissionFile,
                        Consumer<? super SubmissionExecutionResult> listener)
    {
        log.info(String.format("Submission with id %s submitted for manual review.", submission.getId()));

        listener.accept(new SubmissionExecutionResult(SubmissionStatus.PROCESSING, ""));
    }

    @Override
    public void shutdown() {

    }

}
