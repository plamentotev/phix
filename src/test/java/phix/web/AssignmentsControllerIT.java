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

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static phix.web.AssignmentsController.POST_SUBMISSION_FILE_KEY;

@TestPropertySource(properties="submissions.maxSubmissionSize=150")
public class AssignmentsControllerIT extends AbstractWebIT {

    private static final String GET_ASSIGNMENTS_RESOURCE_URL = "/assignments";
    private static final String GET_HELLO_WORLD_ASSIGNMENT_RESOURCE_URL = "/assignments/hello-world";
    private static final String GET_DOCKER_ASSIGNMENT_RESOURCE_URL = "/assignments/docker-assignment";
    private static final String POST_HELLO_WORLD_SUBMISSION_RESOURCE_URL = GET_HELLO_WORLD_ASSIGNMENT_RESOURCE_URL +
            "/submissions/";
    private static final String GET_NOT_EXISTING_ASSIGNMENT_RESOURCE_URL = "/assignments/no-such-assignment";

    private static final String EXPECTED_HELLO_WORLD_ASSIGNMENT_JSON = "{" +
            "\"id\": \"hello-world\"," +
            "\"title\": \"Hello World Title\"," +
            "\"type\": \"manual\"" +
            "}";
    private static final String EXPECTED_CUSTOM_ASSIGNMENT_JSON = "{" +
            "\"id\": \"docker-assignment\"," +
            "\"title\": \"Docker Assignment\"," +
            "\"type\": \"docker\"" +
            "}";
    private static final String EXPECTED_ASSIGNMENTS_LIST_JSON = "[" + EXPECTED_HELLO_WORLD_ASSIGNMENT_JSON +
            "," + EXPECTED_CUSTOM_ASSIGNMENT_JSON + "]";

    private static final String SUBMISSION_FOLDER = "submissions_test_request_files/";
    private static final String HELLO_WORLD_SUBMISSION_FILE_PATH = SUBMISSION_FOLDER + "hello-world";
    private static final String TOO_LARGE_SUBMISSION_FILE_PATH = SUBMISSION_FOLDER + "large-submission";

    @Test
    public void testGetAssignments() {
        String assignmentsResponse = rest().getForObject(GET_ASSIGNMENTS_RESOURCE_URL, String.class);

        JSONAssert.assertEquals(EXPECTED_ASSIGNMENTS_LIST_JSON, assignmentsResponse, false);
    }

    @Test
    public void testGetAssignment() {
        String assignmentResponse = getResponseAndCheckResponseCodeIsOk(GET_HELLO_WORLD_ASSIGNMENT_RESOURCE_URL);

        JSONAssert.assertEquals(EXPECTED_HELLO_WORLD_ASSIGNMENT_JSON, assignmentResponse, false);
    }

    @Test
    public void testGetCustomAssignment() {
        String assignmentResponse = getResponseAndCheckResponseCodeIsOk(GET_DOCKER_ASSIGNMENT_RESOURCE_URL);

        JSONAssert.assertEquals(EXPECTED_CUSTOM_ASSIGNMENT_JSON, assignmentResponse, false);
    }

    @Test
    public void testGetNotExistingAssignment() {
        getResponseAndCheckResponseCode(GET_NOT_EXISTING_ASSIGNMENT_RESOURCE_URL, HttpStatus.NOT_FOUND);
    }

    @Test
    public void testPostSubmission() {
        getResponseAndCheckResponseCode(POST_HELLO_WORLD_SUBMISSION_RESOURCE_URL, HttpMethod.POST,
                getHelloWorldSubmissionRequestMap(), HttpStatus.CREATED);
    }

    @Test
    public void testPostSubmissionExceedingSizeLimit() {
        getResponseAndCheckResponseCode(POST_HELLO_WORLD_SUBMISSION_RESOURCE_URL, HttpMethod.POST,
                getSubmissionRequestMap(TOO_LARGE_SUBMISSION_FILE_PATH), HttpStatus.BAD_REQUEST);
    }

    private MultiValueMap<String, Object> getHelloWorldSubmissionRequestMap() {
        return getSubmissionRequestMap(HELLO_WORLD_SUBMISSION_FILE_PATH);
    }

    private MultiValueMap<String, Object> getSubmissionRequestMap(String submissionPath) {
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        ClassPathResource submissionResource = new ClassPathResource(submissionPath);
        requestMap.add(POST_SUBMISSION_FILE_KEY, submissionResource);

        return requestMap;
    }

}
