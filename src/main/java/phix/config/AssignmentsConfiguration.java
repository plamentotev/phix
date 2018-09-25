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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import phix.assignment.AssignmentsRepository;
import phix.repository.filesystem.AssignmentParser;
import phix.repository.filesystem.FileSystemAssignmentsRepository;
import phix.repository.filesystem.YamlAssignmentParser;
import phix.service.AssignmentsService;

import javax.validation.Validator;

@Configuration
class AssignmentsConfiguration {

    private final AssignmentsProperties assignmentsProperties;

    @Autowired
    AssignmentsConfiguration(AssignmentsProperties assignmentsProperties) {
        this.assignmentsProperties = assignmentsProperties;
    }

    @Bean
    @Autowired
    AssignmentParser getAssignmentParser(Validator validator) {
        return new YamlAssignmentParser(validator);
    }

    @Bean
    @Autowired
    AssignmentsRepository getAssignmentsRepository(AssignmentParser assignmentParser) {
        return new FileSystemAssignmentsRepository(
                assignmentsProperties.getBaseDirectory(),
                assignmentParser);
    }


    @Bean
    @Autowired
    AssignmentsService getAssignmentsService(AssignmentsRepository assignmentsRepository) {
        return new AssignmentsService(assignmentsRepository);
    }
    
}
