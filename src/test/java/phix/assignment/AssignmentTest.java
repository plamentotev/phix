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
package phix.assignment;

import org.junit.Test;
import phix.assignment.Assignment.AssignmentSummary;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignmentTest {

    private static final String ASSIGNMENT_ID = "assignment_id";
    private static final String TITLE = "title";

    @Test
    public void testGetAssignmentSummary() {
        Assignment assignment = new Assignment();
        assignment.setId(ASSIGNMENT_ID);
        assignment.setTitle(TITLE);

        AssignmentSummary assignmentSummary = assignment.createAssignmentSummary();

        assertThat(assignmentSummary.getId()).isEqualTo(ASSIGNMENT_ID);
        assertThat(assignmentSummary.getTitle()).isEqualTo(TITLE);
    }

}