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
package phix.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeLimitedExecutor {

    private final ExecutorService executorService;
    private final ScheduledExecutorService cancellerExecutorService;

    public TimeLimitedExecutor() {
        this(Executors.newCachedThreadPool());
    }

    public TimeLimitedExecutor(ExecutorService executorService) {
        this.executorService = executorService;
        this.cancellerExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void execute(Runnable command, long timeLimit) {
        executorService.execute(new TimeLimitedFutureTask(command, timeLimit));
    }

    public void shutdown() {
        executorService.shutdown();

        cancellerExecutorService.shutdown();
    }

    private class TimeLimitedFutureTask extends FutureTask<Void> {

        private final long timeLimit;

        TimeLimitedFutureTask(Runnable runnable, long timeLimit) {
            super(runnable, null);
            this.timeLimit = timeLimit;
        }

        @Override
        public void run() {
            cancellerExecutorService.schedule(() -> cancel(true), timeLimit, TimeUnit.MILLISECONDS);
            super.run();
        }
    }

}
