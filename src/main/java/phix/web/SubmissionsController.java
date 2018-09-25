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
package phix.web;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import phix.service.SubmissionsService;
import phix.submission.Submission;
import phix.submission.SubmissionFileNotFoundException;
import phix.submission.SubmissionNotFoundException;
import phix.web.annotation.PhixApiController;

import java.io.IOException;

@PhixApiController(path="/submissions")
public class SubmissionsController {

    private final SubmissionsService submissionsService;

    public SubmissionsController(SubmissionsService submissionsService) {
        this.submissionsService = submissionsService;
    }

    @GetMapping(path="{id}")
    public Submission getSubmission(@PathVariable String id) {
        return submissionsService.getSubmission(id);
    }

    @GetMapping(path="{submissionId}/file", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public InputStreamResource getSubmissionFile(@PathVariable String submissionId) throws IOException {
        return new InputStreamResource(submissionsService.getSubmissionFile(submissionId).getInputStream());
    }

    @ExceptionHandler(SubmissionNotFoundException.class)
    @ResponseStatus(code= HttpStatus.NOT_FOUND, reason="Submission not found")
    public void handleSubmissionNotFound() {

    }

    @ExceptionHandler(SubmissionFileNotFoundException.class)
    @ResponseStatus(code= HttpStatus.NOT_FOUND, reason="Submission file not found")
    public void handleSubmissionFileNotFoundException() {

    }

}
