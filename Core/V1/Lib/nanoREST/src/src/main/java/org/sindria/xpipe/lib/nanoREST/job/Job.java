package org.sindria.xpipe.lib.nanoREST.job;


// Job class representing a task
public class Job implements Comparable<Job> {
    protected final String name;
    protected final int priority;

    public Job(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public void execute() {
        System.out.println("Executing job: " + name + " with priority " + priority);
        this.handle();
        System.out.println("Job " + name + " completed.");
    }

    @Override
    public int compareTo(Job other) {
        return Integer.compare(other.priority, this.priority); // Higher priority jobs first
    }

    public void handle() {
        try {
            Thread.sleep(1000); // Simulate job execution time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}