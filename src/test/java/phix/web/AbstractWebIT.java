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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import phix.AbstractPhixIT;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractWebIT extends AbstractPhixIT {

    @Autowired
    private TestRestTemplate restTemplate;

    protected TestRestTemplate rest() {
        return restTemplate;
    }

    protected String getResponseAndCheckResponseCodeIsOk(String url) {
        return getResponseAndCheckResponseCode(url, HttpStatus.OK);
    }

    protected String getResponseAndCheckResponseCode(String url, HttpStatus status) {
        ResponseEntity<String> response = rest().getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(status);

        return response.getBody();
    }

    protected <T> String getResponseAndCheckResponseCode(String url, HttpMethod method,
                                                         T request, HttpStatus status)
    {
        ResponseEntity<String> response = rest().exchange(url, method, new HttpEntity<>(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(status);

        return response.getBody();
    }

}
