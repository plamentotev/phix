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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import phix.assignment.Assignment;
import phix.assignment.Assignment.AssignmentSummary;
import phix.assignment.AssignmentNotFoundException;
import phix.service.AssignmentsService;
import phix.service.SubmissionsService;
import phix.submission.Submission;
import phix.submission.SubmissionFile;
import phix.web.annotation.PhixApiController;

import java.security.Principal;
import java.util.List;

@PhixApiController(path="/assignments")
public class AssignmentsController {

    static final String POST_SUBMISSION_FILE_KEY = "submission";

    private final AssignmentsService assignmentsService;

    private final SubmissionsService submissionsService;

    public AssignmentsController(AssignmentsService assignmentsService, SubmissionsService submissionsService) {
        this.assignmentsService = assignmentsService;
        this.submissionsService = submissionsService;
    }

    @GetMapping
    public List<AssignmentSummary> getAssignments() {
        return assignmentsService.getAssignments();
    }

    @GetMapping(path="{id}")
    public Assignment getAssignments(@PathVariable String id) {
        return assignmentsService.getAssignment(id);
    }

    @PostMapping(path="{assignmentId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public Submission addSubmission(@PathVariable String assignmentId,
                                    @RequestPart(POST_SUBMISSION_FILE_KEY) MultipartFile submissionRequest)
    {
        SubmissionFile submissionFile = new SpringMultipartSubmissionFile(submissionRequest);

        return submissionsService.addSubmission(assignmentId, submissionFile);
    }

    @ExceptionHandler(AssignmentNotFoundException.class)
    @ResponseStatus(code=HttpStatus.NOT_FOUND, reason="Assignment not found")
    public void handleAssignmentNotFound() {

    }

}
