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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import phix.AbstractPhixIT;


import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignmentIT extends AbstractPhixIT {

    private static final String VALID_ASSIGNMENT_ID = "assignment-id";
    private static final String VALID_ASSIGNMENT_TITLE = "assignment-title";
    private static final String VALID_ASSIGNMENT_TYPE = "assignment-type";
    private static final String VALID_ASSIGNMENT_DESCRIPTION = "assignment-description*#@?.,!~";
    private static final String VALID_INITIAL_CODE = "initial-code{}<>/";
    private static final String TOO_SHORT_ASSIGNMENT_ID = "id";
    private static final int ASSIGNMENT_ID_TOO_LONG_LEN = 65;
    private static final String VALID_ASSIGNMENT_ID_CHAR = "a";
    private static final String ASSIGNMENT_ID_WITH_INVALID_CHARACTERS = "abcdefgh*ijk";
    private static final String ASSIGNMENT_INVALID_CHAR = "*";
    private static final String ASSIGNMENT_ID_ALL_VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDE-FGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ASSIGNMENT_TITLE_TOO_SHORT = "abc";
    private static final int ASSIGNMENT_TITLE_TOO_LONG_LEN = 129;
    private static final int LONG_TEXT_SIZE = 10240;

    @Autowired
    Validator validator;

    @Test
    public void testAllFieldsIsValid() {
        Assignment assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);
        assignment.setDescription(VALID_ASSIGNMENT_DESCRIPTION);
        assignment.setInitialCode(VALID_INITIAL_CODE);

        assertThat(validator.validate(assignment)).isEmpty();
    }

    @Test
    public void testAllOptionalFieldsEmptyIsValid() {
        Assignment assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).isEmpty();
    }

    @Test
    public void testIdValidation() {
        // Празен символен низ
        Assignment assignment = new Assignment();
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Прекалено къс символен низ
        assignment = new Assignment();
        assignment.setId(TOO_SHORT_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Прекалено дълъг символен низ
        assignment = new Assignment();
        assignment.setId(StringUtils.leftPad(VALID_ASSIGNMENT_ID, ASSIGNMENT_ID_TOO_LONG_LEN, VALID_ASSIGNMENT_ID_CHAR));
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Съдържа невалидни символи
        assignment = new Assignment();
        assignment.setId(ASSIGNMENT_ID_WITH_INVALID_CHARACTERS);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Съдържа невалидни символи и прекалено дълъг низ
        assignment = new Assignment();
        assignment.setId(StringUtils.leftPad(VALID_ASSIGNMENT_ID, ASSIGNMENT_ID_TOO_LONG_LEN, ASSIGNMENT_INVALID_CHAR));
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(2);

        // Съдържа само валидни символи
        assignment = new Assignment();
        assignment.setId(ASSIGNMENT_ID_ALL_VALID_CHARACTERS);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).isEmpty();
    }

    @Test
    public void testTitleValidation() {
        // Празен символен низ
        Assignment assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Прекалено къс символен низ
        assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(ASSIGNMENT_TITLE_TOO_SHORT);
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Прекалено дълъг символен низ
        assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(StringUtils.leftPad(VALID_ASSIGNMENT_TITLE, ASSIGNMENT_TITLE_TOO_LONG_LEN));
        assignment.setType(VALID_ASSIGNMENT_TYPE);

        assertThat(validator.validate(assignment)).hasSize(1);
    }

    @Test
    public void testTypeValidation() {
        // Задължително поле
        Assignment assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);

        assertThat(validator.validate(assignment)).hasSize(1);

        // Празен символен низ
        assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType("");

        assertThat(validator.validate(assignment)).hasSize(1);

        // Сомволен низ само от интервали
        assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType("       ");

        assertThat(validator.validate(assignment)).hasSize(1);
    }

    @Test
    public void testDescriptionAndInitialCodeLongStringsAreValid() {
        Assignment assignment = new Assignment();
        assignment.setId(VALID_ASSIGNMENT_ID);
        assignment.setTitle(VALID_ASSIGNMENT_TITLE);
        assignment.setType(VALID_ASSIGNMENT_TYPE);
        assignment.setDescription(StringUtils.leftPad(VALID_ASSIGNMENT_DESCRIPTION, LONG_TEXT_SIZE));
        assignment.setInitialCode(StringUtils.leftPad(VALID_INITIAL_CODE, LONG_TEXT_SIZE));

        assertThat(validator.validate(assignment)).isEmpty();
    }

}