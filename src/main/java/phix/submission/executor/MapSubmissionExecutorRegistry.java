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
package phix.submission.executor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapSubmissionExecutorRegistry implements SubmissionExecutorsRegistry {

    private final Map<String, SubmissionExecutor> submissionExecutors;

    public MapSubmissionExecutorRegistry(Map<String, String> submissionExecutorsClasses,
                                         Map<String, Map<String, String>> submissionExecutorsConfig) {
        submissionExecutors = new HashMap<>();

        submissionExecutorsClasses.forEach((assignmentType, submissionExecutorClassName) -> {
            try {
                SubmissionExecutor submissionExecutor = getSubmissionExecutor(submissionExecutorClassName);
                @SuppressWarnings("unchecked")
                Map<String, String> config = submissionExecutorsConfig.getOrDefault(assignmentType, Collections.EMPTY_MAP);
                submissionExecutor.init(config);
                submissionExecutors.put(assignmentType, submissionExecutor);
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException("Illegal submission executors configuration.");
            }
        });
    }

    @Override
    public SubmissionExecutor getExecutorByAssignmentType(String assignmentType) {
        SubmissionExecutor submissionExecutor = submissionExecutors.get(assignmentType);
        if (submissionExecutor == null) {
            throw new IllegalArgumentException("Not a valid assignment type: " + assignmentType);
        }

        return submissionExecutor;
    }

    @Override
    public void shutdown() {
        submissionExecutors.values().forEach(SubmissionExecutor::shutdown);
    }

    private SubmissionExecutor getSubmissionExecutor(String submissionExecutorClassName)
            throws ReflectiveOperationException
    {
        Class<?> submissionExecutorClass = Class.forName(submissionExecutorClassName);

        if (!SubmissionExecutor.class.isAssignableFrom(submissionExecutorClass)) {
            throw new IllegalArgumentException(submissionExecutorClass.getCanonicalName() + " does not implement " +
                    SubmissionExecutor.class.getCanonicalName());
        }

        return (SubmissionExecutor)submissionExecutorClass.newInstance();
    }

}
