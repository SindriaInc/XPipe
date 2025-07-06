package org.sindria.xpipe.core.lib.nanorest.job;


// Main class to test the dispatcher
public class JobDispatcherExample {
    public static void main(String[] args) {
        JobDispatcher dispatcher = new JobDispatcher();

        dispatcher.submitJob(new Job("Task 1", 2));
        dispatcher.submitJob(new Job("Task 2", 1));
        dispatcher.submitJob(new Job("Task 3", 3));

        //dispatcher.scheduleRecurringJob("*/5 * * * * *", new Job("Cron Recurring Task", 2));

        try {
            Thread.sleep(20000); // Allow jobs to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        dispatcher.shutdown();
    }
}




// V2
// Main class to test the dispatcher
//public class JobDispatcherExample {
//    public static void main(String[] args) {
//        JobDispatcher dispatcher = new JobDispatcher();
//
//        dispatcher.submitJob(new Job("Task 1", 2));
//        dispatcher.submitJob(new Job("Task 2", 1));
//        dispatcher.submitJob(new Job("Task 3", 3));
//
//        try {
//            Thread.sleep(5000); // Allow jobs to complete
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        dispatcher.shutdown();
//    }
//}






// V1
//// Main class to test the dispatcher
//public class JobDispatcherExample {
//    public static void main(String[] args) {
//        JobDispatcher dispatcher = new JobDispatcher();
//
//        dispatcher.submitJob(new Job("Task 1"));
//        dispatcher.submitJob(new Job("Task 2"));
//        dispatcher.submitJob(new Job("Task 3"));
//
//        try {
//            Thread.sleep(5000); // Allow jobs to complete
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        dispatcher.shutdown();
//    }
//}
