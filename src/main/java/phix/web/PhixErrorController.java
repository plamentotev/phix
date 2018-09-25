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

import phix.web.annotation.PhixApiController;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@PhixApiController(path="${server.error.path:${error.path:/error}}")
public class PhixErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    public PhixErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes);

        this.errorProperties = serverProperties.getError();
    }

    @RequestMapping
    public ResponseEntity<Error> error(HttpServletRequest request) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, false);

        Error errorResponse = new Error(
                errorAttributes.get("status").toString(),
                errorAttributes.get("error").toString(),
                errorAttributes.get("message").toString());

        return new ResponseEntity<>(errorResponse, getStatus(request));
    }

    @Override
    public String getErrorPath() {
        return errorProperties.getPath();
    }

}
