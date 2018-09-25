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

import org.hamcrest.CustomMatcher;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.Before;
import org.junit.Test;
import phix.assignment.Assignment;

import javax.validation.Validator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class YamlAssignmentParserTest {

    private static final String ASSIGNMENT_ID = "assignment_id";
    private static final String ASSIGNMENT_TITLE = "assignment_title";
    private static final String ASSIGNMENT_DESCRIPTION = "assignment_description";
    private static final String ASSIGNMENT_INITIAL_CODE = "initial_code";
    private static final String ASSIGNMENT_CUSTOM_VALUE_STRING = "custom_value";
    private static final int ASSIGNMENT_CUSTOM_VALUE_INT = 42;

    private static final String BASE_ASSIGNMENT_YAML = "id: " + ASSIGNMENT_ID + "\n" +
            "title: " + ASSIGNMENT_TITLE + "\n";

    private static final String ASSIGNMENT_YAML = BASE_ASSIGNMENT_YAML +
            "description: " + ASSIGNMENT_DESCRIPTION + "\n" +
            "initialCode: " + ASSIGNMENT_INITIAL_CODE;

    private static final String YAML_WITH_ADDITIONAL_FIELD = ASSIGNMENT_YAML +
            "\ncustom: " + ASSIGNMENT_CUSTOM_VALUE_STRING + "\n" +
            "customInt: " + ASSIGNMENT_CUSTOM_VALUE_INT + "\n";

    private static final String ADVANCED_ASSIGNMENT_ID = "advanced-assignment";
    private static final String ADVANCED_ASSIGNMENT_TITLE = "title";
    private static final String ADVANCED_ASSIGNMENT_DESCRIPTION = "multi-line description";
    private static final String ADVANCED_ASSIGNMENT_INITIAL_CODE = "# This is comment\n" +
            "def func(param):\n" +
            "  #implement code";
    private static final String ADVANCED_ASSIGNMENT_YAML = "id: " + ADVANCED_ASSIGNMENT_ID + "\n" +
            "title: " + ADVANCED_ASSIGNMENT_TITLE + "\n\n" +
            "description: >-\n" +
            "  multi-line\n" +
            "  description\n" +
            "initialCode: |\n" +
            "    # This is comment\n" +
            "    def func(param):\n" +
            "      #implement code";

    private static final String INVALID_ASSIGNMENT_ID = "invalid-assignment";
    private static final String INVALID_ASSIGNMENT_YAML = String.format("id: %s\n", INVALID_ASSIGNMENT_ID);

    private YamlAssignmentParser yamlAssignmentParser;

    @Before
    public void setup() {
        Validator mockValidator = mock(Validator.class);

        when(mockValidator.validate(any())).thenReturn(Collections.emptySet());
        when(mockValidator.validate(argThat(new AssignmentMatcher(INVALID_ASSIGNMENT_ID))))
                .thenReturn(Collections.singleton(ConstraintViolationImpl
                        .forBeanValidation(null, null,null, null,null, null,null,null,null,null)));

        yamlAssignmentParser = new YamlAssignmentParser(mockValidator);
    }

    @Test
    public void testParseAssignment() {
        Assignment assignment = yamlAssignmentParser.parseAssignment(asInpuStream(ASSIGNMENT_YAML));

        assertThat(assignment.getId()).isEqualTo(ASSIGNMENT_ID);
        assertThat(assignment.getTitle()).isEqualTo(ASSIGNMENT_TITLE);
        assertThat(assignment.getDescription()).isEqualTo(ASSIGNMENT_DESCRIPTION);
        assertThat(assignment.getInitialCode()).isEqualTo(ASSIGNMENT_INITIAL_CODE);
    }

    @Test
    public void testParseComplexAssignment() {
        Assignment assignment = yamlAssignmentParser.parseAssignment(asInpuStream(ADVANCED_ASSIGNMENT_YAML));

        assertThat(assignment.getId()).isEqualTo(ADVANCED_ASSIGNMENT_ID);
        assertThat(assignment.getTitle()).isEqualTo(ADVANCED_ASSIGNMENT_TITLE);
        assertThat(assignment.getDescription()).isEqualTo(ADVANCED_ASSIGNMENT_DESCRIPTION);
        assertThat(assignment.getInitialCode()).isEqualTo(ADVANCED_ASSIGNMENT_INITIAL_CODE);
    }

    @Test
    public void testAssignmentWithLessFields() {
        Assignment assignment = yamlAssignmentParser.parseAssignment(asInpuStream(BASE_ASSIGNMENT_YAML));

        assertThat(assignment.getId()).isEqualTo(ASSIGNMENT_ID);
        assertThat(assignment.getTitle()).isEqualTo(ASSIGNMENT_TITLE);
        assertThat(assignment.getDescription()).isNull();
        assertThat(assignment.getInitialCode()).isNull();
    }

    @Test(expected=AssignmentParserException.class)
    public void testAssignmentWithAdditionalField() {
        yamlAssignmentParser.parseAssignment(asInpuStream(YAML_WITH_ADDITIONAL_FIELD));
    }

    @Test(expected=AssignmentParserException.class)
    public void testInvalidAssignment() {
        yamlAssignmentParser.parseAssignment(asInpuStream(INVALID_ASSIGNMENT_YAML));
    }

    private InputStream asInpuStream(String string) {
        return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
    }

    private static class AssignmentMatcher extends CustomMatcher<Assignment> {

        private final String id;

        AssignmentMatcher(String id) {
            super("assignment with id  " + id);
            this.id = id;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof Assignment)) {
                return false;
            }

            Assignment assignment = (Assignment) item;

            return assignment.getId().equals(id);
        }
    }

}
