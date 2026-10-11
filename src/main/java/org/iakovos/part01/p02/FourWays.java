package org.iakovos.part01.p02;

public class FourWays {

    void main() throws InterruptedException {

        /* Style 1: extends Thread. Avoid.

         * The Javadoc says Thread "may be extended for customization and other
         * advanced reasons although most applications should have little need to
         * do this," and the no-arg constructor "is only useful when extending
         * Thread to override the run() method." Two practical problems:
         *   1.Java has single inheritance: this class can't extend anything else.
         *      If you wanted PrinterTask extends SomeBaseClass, you can't.
         *      You've spent your one inheritance slot on Thread for no real benefit.
         *   2.The job is glued to the worker. A Thread is a worker. A Runnable is a job.
         *      When you subclass Thread, the job is glued permanently to one specific worker.
         *      In thread pools, a pool keeps a few workers alive and feeds them different jobs.
         *      That only works if the job is a separate Runnable object you can pass around.
         *      A PrinterThread can't be passed to a pool — it is the worker.
         */
        Thread first = new PrinterThread();

        /* Style 2: new Thread(Runnable) with a named class.

         * "Hire a worker and hand them a job."
         *  - new PrinterTask() : you create the job. It's an object whose class implements Runnable, meaning it has a run() method describing what to do.
         *  - new Thread(...) : you create the worker, a fresh OS thread, and give it that job.
         *  - second.start() : the worker starts doing the job on its own thread.
         * The Javadoc notes this constructor "is equivalent to Thread.ofPlatform().unstarted(task)."
         * PrinterTask is the job (a Runnable
         * with a run() method), new Thread(...) is the worker. The Javadoc notes
         *
         * Fine when the task has state or is reused. ->
         * When to prefer this over a lambda (Style 3):
         *   A named class is a real object you can put data inside and use again.
         *   A lambda is an one-line, throwaway instruction.
         *
         *   - No state to remember: "print hello" -> nothing to remember, nothing to configure. A lambda is perfect
         *
         *   - State to remember: a counter, buffer, or partial result. A lambda
         *     can only read variables that are final or never reassigned, so you
         *     end up hacking with int[] just to update a value. That works but
         *     it's ugly. A named class has fields it can update freely.
         *   - Reused: same logic in several places? A named class is written once
         *     and instantiated anywhere -- including a thread pool (Lesson 10).
         *
         * Job and worker are separate objects, so the same task could go to a pool or a different thread.
         */
        Thread second = new Thread(new PrinterTask());

        /* Style 3: new Thread(() -> ...). The usual choice for short tasks.
         * Runnable is @FunctionalInterface, so a lambda is just style 2 with the
         * class inlined. Same separation of job and worker, less boilerplate.
         */
        Thread third = new Thread(() -> printFrom("lambda"));

        /* Style 4: Thread.ofPlatform() builder. The modern API (Java 21+).
         * "Configure a worker step by step, then build it."
         *
         * The Javadoc: "Returns a builder for creating a platform Thread or ThreadFactory."
         * Sets name, daemon flag, priority, group and stack size in one readable chain
         * The same shape with Thread.ofVirtual() yields virtual threads.
         *
         * .unstarted(...) creates the thread but does NOT start it;
         * .start(...) would create and start in one call.
         *  Use unstarted when you want to start later or set something else first.
         *
         * - Thread.ofPlatform() : "I want a regular (platform) thread." Returns a builder, a helper object you configure before the real thread exists.
         * - .name("builder-thread") : set its name (so thread dumps are readable).
         * - .unstarted(...) — attach the job and create the thread, but don't start it yet. You get back a Thread you start() yourself when ready.
         */

        Thread fourth = Thread.ofPlatform()
                .name("builder-thread")
                .unstarted(() -> printFrom("builder"));


        first.start();
        second.start();
        third.start();
        fourth.start();


        // join(): Javadoc -> "Waits for this thread to terminate."
        // main reaches "main is done" only after all four have finished,
        // regardless of the order they printed in.
        first.join();
        second.join();
        third.join();
        fourth.join();
        IO.println("main is done");
    }

    void printFrom(String style) {
        IO.println(style + " running on " + Thread.currentThread().getName());
    }
    
    class PrinterThread extends Thread {
        @Override
        public void run() {
            IO.println("subclass running on " + getName());
        }
    }

    class PrinterTask implements Runnable{

        @Override
        public void run() {
            IO.println("runnable running on " + Thread.currentThread().getName());
        }
    }
}
