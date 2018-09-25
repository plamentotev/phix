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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import phix.AbstractPhixIT;
import phix.assignment.Assignment;
import phix.assignment.AssignmentNotFoundException;
import phix.assignment.InvalidAssignmentException;
import phix.repository.PhixRepositoryException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestPropertySource(properties="assignments.base-directory=assignments_with_errors")
public class FileSystemAssignmentsRepositoryTestIT extends AbstractPhixIT {

    private static final String ASSIGNMENTS_DIR = "assignments";
    private static final String ASSIGNMENTS_EMPTY_DIR = "assignments_empty_dir";

    private static final int ASSIGNMENTS_COUNT = 2;

    private static final String ASSIGNMENT_HELLO_WORLD_ID = "hello-world";
    private static final String ASSIGNMENT_HELLO_WORLD_TITLE = "Hello World Title";
    private static final String ASSIGNMENT_DOCKER_ID = "docker-assignment";

    private static final String NOT_EXISTING_ASSIGNMENT_ID = "no-such-assignment";
    private static final String NOT_VALID_ASSIGNMENT_ID = "not-valid-assignment";
    private static final String NOT_PARSABLE_ASSIGNMENT_ID = "id-not-same-as-dir-name";
    private static final String ASSIGNMENT_DIR_TRAVERSAL_ID = "../" + ASSIGNMENTS_DIR + "/" + ASSIGNMENT_HELLO_WORLD_ID;

    @Autowired
    private FileSystemAssignmentsRepository assignmentsRepository;

    @Autowired
    private AssignmentParser assignmentParser;

    @Test
    public void testGetAllAssignments() {
        List<Assignment> assignments = assignmentsRepository.getAllAssignments();
        validateAssignmentsList(assignments);
    }

    @Test
    public void testGetAssignmentsDefaultDir() {
        FileSystemAssignmentsRepository defaultDirRepository = new FileSystemAssignmentsRepository(
                ASSIGNMENTS_DIR, assignmentParser);

        List<Assignment> assignments = defaultDirRepository.getAllAssignments();
        validateAssignmentsList(assignments);
    }

    @Test
    public void testGetAllAssignmentsFromEmptyFolder() {
        FileSystemAssignmentsRepository emptyDirRepository = new FileSystemAssignmentsRepository(
                ASSIGNMENTS_EMPTY_DIR, assignmentParser);

        assertThat(emptyDirRepository.getAllAssignments()).isEmpty();
    }

    @Test
    public void testGetAssignment() {
        Assignment assignment = assignmentsRepository.getAssignment(ASSIGNMENT_HELLO_WORLD_ID);

        assertThat(assignment.getId()).isEqualTo(ASSIGNMENT_HELLO_WORLD_ID);
        assertThat(assignment.getTitle()).isEqualTo(ASSIGNMENT_HELLO_WORLD_TITLE);
    }

    @Test(expected=AssignmentNotFoundException.class)
    public void testGetNotExistingAssignment() {
        assignmentsRepository.getAssignment(NOT_EXISTING_ASSIGNMENT_ID);
    }

    @Test(expected=InvalidAssignmentException.class)
    public void testGetInvalidAssignment() {
        assignmentsRepository.getAssignment(NOT_VALID_ASSIGNMENT_ID);
    }

    @Test(expected=InvalidAssignmentException.class)
    public void testNotParsableAssignment() {
        assignmentsRepository.getAssignment(NOT_PARSABLE_ASSIGNMENT_ID);
    }

    @Test(expected=PhixRepositoryException.class)
    public void testDirTraversal() {
        assignmentsRepository.getAssignment(ASSIGNMENT_DIR_TRAVERSAL_ID);
    }

    private void validateAssignmentsList(List<Assignment> assignments) {
        assertAssignmentExists(assignments, ASSIGNMENT_HELLO_WORLD_ID);
        assertAssignmentExists(assignments, ASSIGNMENT_DOCKER_ID);

        assertThat(assignments).hasSize(ASSIGNMENTS_COUNT);
    }

    private void assertAssignmentExists(List<Assignment> assignments, String assignmentId) {
        Optional<Assignment> assignment = assignments.stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();

        if (!assignment.isPresent()) {
            fail(String.format("The assignments list should contain assignment with id=%s, but it does not.",
                    assignmentId));
        }
    }

}