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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

public class Assignment {

    @NotBlank(message="The assignment id should not be empty")
    @Size(min=5, max=64, message="The assignment id should be between 5 and 64 characters long.")
    @Pattern(regexp="[a-zA-Z0-9-]*", message="The assignment id should contain only alphanumeric characters or hyphens.")
    private String id;

    @NotBlank(message="The assignment type should not be empty")
    private String type;

    @NotBlank(message="The assignment title should not be empty")
    @Size(min=5, max=128, message="The assignment title should be between 5 and 128 characters long.")
    private String title;

    private String description;

    private String initialCode;

    @JsonIgnore
    private Map<String, String> executorParams;

    public Assignment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitialCode() {
        return initialCode;
    }

    public void setInitialCode(String initialCode) {
        this.initialCode = initialCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getExecutorParams() {
        return executorParams;
    }

    public void setExecutorParams(Map<String, String> executorParams) {
        this.executorParams = executorParams;
    }

    public AssignmentSummary createAssignmentSummary() {
        return new AssignmentSummary(getId(), getTitle(), getType());
    }

    public static class AssignmentSummary {
        private final String id;
        private final String title;
        private final String type;

        protected AssignmentSummary(String id, String title, String type) {
            this.id = id;
            this.title = title;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }
    }
}
