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
import phix.assignment.Assignment;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

public class YamlAssignmentParser implements AssignmentParser {

    private final Validator validator;

    public YamlAssignmentParser(Validator validator) {
        this.validator = validator;
    }

    @Override
    public Assignment parseAssignment(InputStream manifest) throws AssignmentParserException {
        try {
            Yaml yaml = new Yaml();

            // Превръщане на YAML документ в обект
            Assignment assignment = yaml.loadAs(manifest, Assignment.class);

            // Валидиране на получения обект
            if (assignment == null) {
                throw new AssignmentParserException("The manifest file is empty or not valid.");
            }
            Set<ConstraintViolation<Assignment>> errors = validator.validate(assignment);

            // Ако са намерени грешки при валидацията
            if (!errors.isEmpty()) {
                String errorMessage = errors
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", ", "[", "]"));

                throw new AssignmentParserException("The following errors occurred while parsing assignment: "
                        + errorMessage);
            }

            return assignment;
        } catch (YAMLException | ValidationException e) {
            throw new AssignmentParserException("Exception thrown while trying to parse assignment.", e);
        }
    }

}
