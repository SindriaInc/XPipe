package org.sindria.xpipe.lib.nanoREST.job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.*;

public class JobDispatcher {
    private final BlockingQueue<Job> jobQueue = new PriorityBlockingQueue<>();
    private final Thread workerThread;
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean running = true;

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

    public void shutdown() {
        running = false;
        workerThread.interrupt();
        scheduler.shutdown();
    }
}