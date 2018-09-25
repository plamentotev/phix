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

import org.junit.Test;
import phix.assignment.Assignment;
import phix.assignment.Assignment.AssignmentSummary;
import phix.assignment.AssignmentsRepository;
import phix.repository.PhixRepositoryException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssignmentsServiceTest {

    private static final String ASSIGNMENT_1_ID = "assignment1";
    private static final String ASSIGNMENT_1_TITLE = "assignment title1";

    private static final String ASSIGNMENT_2_ID = "assignment2";
    private static final String ASSIGNMENT_2_TITLE = "assignment title2";

    @Test
    public void testGetAssignments() {
        AssignmentsRepository assignmentsRepository = mock(AssignmentsRepository.class);
        when(assignmentsRepository.getAllAssignments()).thenReturn(generateTestAssignments());

        AssignmentsService assignmentsService = new AssignmentsService(assignmentsRepository);

        List<AssignmentSummary> assignments = assignmentsService.getAssignments();
        assertThat(assignments.get(0).getId()).isEqualTo(ASSIGNMENT_1_ID);
        assertThat(assignments.get(1).getId()).isEqualTo(ASSIGNMENT_2_ID);
    }

    @Test
    public void testGetAssignmentsNoAssignments() {
        AssignmentsRepository assignmentsRepository = mock(AssignmentsRepository.class);
        when(assignmentsRepository.getAllAssignments()).thenReturn(Collections.emptyList());

        AssignmentsService assignmentsService = new AssignmentsService(assignmentsRepository);

        List<AssignmentSummary> assignments = assignmentsService.getAssignments();
        assertThat(assignments).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test(expected=PhixRepositoryException.class)
    public void testGetAssignmentsRethrowsExceptions() {
        AssignmentsRepository assignmentsRepository = mock(AssignmentsRepository.class);
        when(assignmentsRepository.getAllAssignments()).thenThrow(PhixRepositoryException.class);

        AssignmentsService assignmentsService = new AssignmentsService(assignmentsRepository);

        assignmentsService.getAssignments();
    }

    @Test
    public void testGetAssignment() {
        AssignmentsRepository assignmentsRepository = mock(AssignmentsRepository.class);
        when(assignmentsRepository.getAssignment(ASSIGNMENT_1_ID)).thenReturn(generateTestAssignment());

        AssignmentsService assignmentsService = new AssignmentsService(assignmentsRepository);

        Assignment assignment = assignmentsService.getAssignment(ASSIGNMENT_1_ID);
        assertThat(assignment.getId()).isEqualTo(ASSIGNMENT_1_ID);
    }

    private List<Assignment> generateTestAssignments() {
        Assignment assignment1 = generateTestAssignment(ASSIGNMENT_1_ID, ASSIGNMENT_1_TITLE);
        Assignment assignment2 = generateTestAssignment(ASSIGNMENT_2_ID, ASSIGNMENT_2_TITLE);

        return Arrays.asList(assignment1, assignment2);
    }

    private Assignment generateTestAssignment() {
        return generateTestAssignment(ASSIGNMENT_1_ID, ASSIGNMENT_1_TITLE);
    }

    private Assignment generateTestAssignment(String id, String title) {
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setTitle(title);

        return assignment;
    }

}