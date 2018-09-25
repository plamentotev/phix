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
import org.yaml.snakeyaml.Yaml;
import phix.assignment.AssignmentsRepository;
import phix.repository.filesystem.SubmissionParser;
import phix.repository.filesystem.YamlSubmissionParser;
import phix.service.SubmissionsService;
import phix.submission.executor.MapSubmissionExecutorRegistry;
import phix.submission.executor.SubmissionExecutorsRegistry;
import phix.submission.SubmissionsRepository;
import phix.repository.filesystem.FileSystemSubmissionsRepository;

import java.util.HashMap;
import java.util.Map;

@Configuration
class SubmissionsConfiguration {

    private final SubmissionsProperties submissionsProperties;

    @Autowired
    SubmissionsConfiguration(SubmissionsProperties submissionsProperties) {
        this.submissionsProperties = submissionsProperties;
    }

    @Bean
    @Autowired
    SubmissionParser getSubmissionParser(Yaml yamlProcessor) {
        return new YamlSubmissionParser(yamlProcessor);
    }

    @Bean
    @Autowired
    SubmissionsRepository getSubmissionsRepository(SubmissionParser submissionParser) {
        return new FileSystemSubmissionsRepository(submissionsProperties.getBaseDirectory(), submissionParser);
    }

    @Bean
    SubmissionExecutorsRegistry getSubmissionExecutorsRegistry() {
        Map<String, String> submissionExecutorsClasses = new HashMap<>();
        Map<String, Map<String, String>> submissionExecutorsConfig = new HashMap<>();

        submissionsProperties.getSubmissionExecutors().forEach((submissionType, submissionExecutorConfiguration) -> {
            submissionExecutorsClasses.put(submissionType, submissionExecutorConfiguration.getClassName());
            submissionExecutorsConfig.put(submissionType, submissionExecutorConfiguration.getConfig());
        } );

        return new MapSubmissionExecutorRegistry(submissionExecutorsClasses, submissionExecutorsConfig);
    }

    @Bean
    @Autowired
    SubmissionsService getSubmissionsService(SubmissionsRepository submissionsRepository,
                                             SubmissionExecutorsRegistry submissionExecutorsRegistry,
                                             AssignmentsRepository assignmentsRepository)
    {
        return new SubmissionsService(submissionsRepository, submissionExecutorsRegistry, assignmentsRepository);
    }

}
