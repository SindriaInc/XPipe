package org.sindria.xpipe.lib.nanoREST.job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import java.util.concurrent.*;
import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.CronType;
import java.time.ZonedDateTime;
import java.util.Optional;


// Job Dispatcher
public class JobDispatcher {
    private final BlockingQueue<Job> jobQueue = new PriorityBlockingQueue<>();
    private final Thread workerThread;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean running = true;
    private final CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    public JobDispatcher() {
        workerThread = new Thread(() -> {
            while (running || !jobQueue.isEmpty()) {
                try {
                    Job job = jobQueue.take(); // Take job from queue
                    job.execute();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        workerThread.start();
    }

    public void submitJob(Job job) {
        jobQueue.offer(job);
    }

    public void scheduleRecurringJob(String cronExpression, Job job) {
        Cron cron = cronParser.parse(cronExpression);
        scheduler.scheduleAtFixedRate(() -> {
            ZonedDateTime now = ZonedDateTime.now();
            Optional<ZonedDateTime> nextExecution = ExecutionTime.forCron(cron).nextExecution(now);
            if (nextExecution.isPresent() && nextExecution.get().isBefore(now.plusSeconds(1))) {
                submitJob(job);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        running = false;
        workerThread.interrupt();
        scheduler.shutdown();
    }
}








// V2
//// Job Dispatcher
//class JobDispatcher {
//    private final BlockingQueue<Job> jobQueue = new PriorityBlockingQueue<>();
//    private final Thread workerThread;
//    private volatile boolean running = true;
//
//    public JobDispatcher() {
//        workerThread = new Thread(() -> {
//            while (running || !jobQueue.isEmpty()) {
//                try {
//                    Job job = jobQueue.take(); // Take job from queue
//                    job.execute();
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        });
//        workerThread.start();
//    }
//
//    public void submitJob(Job job) {
//        jobQueue.offer(job);
//    }
//
//    public void shutdown() {
//        running = false;
//        workerThread.interrupt();
//    }
//}



//// Job Dispatcher
//class JobDispatcher {
//    private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();
//    private final Thread workerThread;
//    private volatile boolean running = true;
//
//    public JobDispatcher() {
//        workerThread = new Thread(() -> {
//            while (running || !jobQueue.isEmpty()) {
//                try {
//                    Job job = jobQueue.take(); // Take job from queue
//                    job.execute();
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        });
//        workerThread.start();
//    }
//
//    public void submitJob(Job job) {
//        jobQueue.offer(job);
//    }
//
//    public void shutdown() {
//        running = false;
//        workerThread.interrupt();
//    }
//}
