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
package phix.service;

import phix.assignment.Assignment;
import phix.assignment.AssignmentsRepository;
import phix.submission.Submission;
import phix.submission.executor.SubmissionExecutionResult;
import phix.submission.executor.SubmissionExecutorsRegistry;
import phix.submission.SubmissionFile;
import phix.submission.SubmissionsRepository;

import java.util.UUID;

public class SubmissionsService {

    private final SubmissionsRepository submissionsRepository;
    private final SubmissionExecutorsRegistry submissionExecutorsRegistry;
    private final AssignmentsRepository assignmentsRepository;

    public SubmissionsService(SubmissionsRepository submissionsRepository,
                              SubmissionExecutorsRegistry submissionExecutorsRegistry,
                              AssignmentsRepository assignmentsRepository) {
        this.submissionsRepository = submissionsRepository;
        this.submissionExecutorsRegistry = submissionExecutorsRegistry;
        this.assignmentsRepository = assignmentsRepository;
    }

    public Submission addSubmission(String assignmentId, SubmissionFile submissionFile) {
        String submissionId = UUID.randomUUID().toString();
        long submissionTimestamp = System.currentTimeMillis();
        Assignment assignment = assignmentsRepository.getAssignment(assignmentId);

        Submission submission = new Submission(submissionId, assignmentId, submissionTimestamp);
        submissionsRepository.addSubmission(submission, submissionFile);

        submissionExecutorsRegistry
                .getExecutorByAssignmentType(assignment.getType())
                .execute(assignment, submission, submissionsRepository.getSubmissionFile(submissionId),
                        (result) -> updateSubmission(submissionId, result));

        return submission;
    }

    public Submission getSubmission(String id) {
        return submissionsRepository.getSubmission(id);
    }

    public SubmissionFile getSubmissionFile(String submissionId) {
        return submissionsRepository.getSubmissionFile(submissionId);
    }

    private void updateSubmission(String submissionId, SubmissionExecutionResult result) {
        submissionsRepository.updateSubmissionStatus(submissionId, result.getSubmissionStatus(), result.getMessage());
    }

}
