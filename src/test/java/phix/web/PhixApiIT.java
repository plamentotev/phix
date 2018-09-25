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

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;
import org.springframework.http.*;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class PhixApiIT extends AbstractWebIT {

    private static final String VERSION_RESOURCE_URL = "/version";
    private static final String NOT_EXISTING_URL = "/no-such-url";

    private static final String ERROR_FIELD_NAME_JSON = "$.error";
    private static final String MESSAGE_FIELD_NAME_JSON = "$.message";
    private static final String STATUS_FIELD_NAME_JSON = "$.status";

    private static final String ERROR_FIELD_NAME_XML = "/Error/error";
    private static final String MESSAGE_FIELD_NAME_XML = "/Error/message";
    private static final String STATUS_FIELD_NAME_XML = "/Error/status";

    private static final MediaType[] COMMON_MEDIA_TYPES =
            new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };

    @Test
    public void errorJsonTest() {
        ResponseEntity<String> response = get(NOT_EXISTING_URL, MediaType.APPLICATION_JSON);
        ReadContext ctx = JsonPath.parse(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ctx.read(ERROR_FIELD_NAME_JSON, String.class)).isNotEmpty();
        assertThat(ctx.read(MESSAGE_FIELD_NAME_JSON, String.class)).isNotEmpty();
        assertThat(ctx.read(STATUS_FIELD_NAME_JSON, Integer.class)).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void errorXMLTest() throws XPathExpressionException {
        ResponseEntity<String> response = get(NOT_EXISTING_URL, MediaType.APPLICATION_XML);
        String responseBody = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(evaluateXPath(responseBody, ERROR_FIELD_NAME_XML)).isNotEmpty();
        assertThat(evaluateXPath(responseBody, MESSAGE_FIELD_NAME_XML)).isNotEmpty();
        assertThat(evaluateXPath(responseBody, STATUS_FIELD_NAME_XML)).isNotEmpty();
    }

    @Test
    public void assertCommonMediaTypeAreReturned() {
        for (MediaType mediaType : COMMON_MEDIA_TYPES) {
            ResponseEntity<String> response = get(VERSION_RESOURCE_URL, mediaType);

            assertThat(response.getStatusCodeValue()).as("Phix must be able to return %s", mediaType)
                    .isEqualTo(HttpStatus.OK.value());
        }
    }

    private ResponseEntity<String> get(String url, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(mediaType));
        HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);

        return rest().exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    private String evaluateXPath(String source, String xPathExpression) throws XPathExpressionException {
        InputSource inputSource = new InputSource(new StringReader(source));
        XPath xPath = XPathFactory.newInstance().newXPath();

        return xPath.evaluate(xPathExpression, inputSource);
    }

}
