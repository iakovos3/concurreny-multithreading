package org.iakovos.part01.p03;

public class ThreadInfo {
    void main() throws InterruptedException {

        Thread worker = Thread.ofPlatform()
                .name("report-generator")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .unstarted(() -> IO.println("generating report..."));

        printInfo(Thread.currentThread());
        printInfo(worker);
        worker.start();
        printInfo(worker);
        worker.join();
    }

    //1. The IDs between 3 and 25 belong to threads the JVM started for itself (garbage collector helpers, the JIT compiler and so on).
    //2. Priority ranges from 1 (MIN_PRIORITY) to 10 (MAX_PRIORITY), with 5 as the default.
    //3. Priority is only a hint to the operating system scheduler, and on Linux it is normally ignored altogether.
    //    Never use priority to make a program correct

    void printInfo(Thread thread) {
        IO.println("name=" + thread.getName()
                + " id=" + thread.threadId()
                + " state=" + thread.getState()
                + " daemon=" + thread.isDaemon()
                + " priority=" + thread.getPriority()
                + " virtual=" + thread.isVirtual());
    }
}
