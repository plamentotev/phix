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
package phix.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import phix.Version;

import javax.servlet.MultipartConfigElement;

@Configuration
class WebConfiguration {

    private static final int MAJOR_API_VERSION = 1;
    private static final int MINOR_API_VERSION = 0;

    @Bean
    Version getVersion() {
        return new Version(MAJOR_API_VERSION, MINOR_API_VERSION);
    }

    @Bean
    MultipartConfigElement createMultipartConfig(SubmissionsProperties submissionsProperties) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(submissionsProperties.getMaxSubmissionSize());

        return factory.createMultipartConfig();
    }

}
