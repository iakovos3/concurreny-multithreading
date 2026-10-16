package org.iakovos.part01.p04;

/*
 * A thread notices an interrupt in one of two ways:
 *
 *   1. A blocking method throws InterruptedException.
 *      Happens when the thread is blocked in sleep(), wait(), join(), take(), get(), etc.
 *      Those methods wake up immediately, throw, AND CLEAR the interrupt flag as they do so.
 *
 *   2. The thread polls Thread.currentThread().isInterrupted().
 *      Happens when the thread is busy running ordinary code and never blocks.
 *      Nothing throws; the flag just stays set until the thread reads it.
 *
 * Which one fires depends on what the thread is doing when interrupt() is called.
 *
 * This demo covers CASE 1: the worker is blocked in Thread.sleep() when the
 * interrupt arrives, so sleep() throws InterruptedException, and we handle it
 * in the catch block. See InterruptBusyLoop for case 2.
 */
public class InterruptSleeping {

    void main() throws InterruptedException {

        /*
        * The worker sleeps for 10s.
        * if it is never interrupted, it just returns after the sleep.
        * if it is interrupted while blocked in sleep(), sleep() throws InterruptedException and clears the interrupt flag
        * the catch block is where the worker decides to stop (here, by returning from run()).
        */

        Thread worker = new Thread(() -> {
            try {
                IO.println("worker: going to sleep for 10 seconds");
                Thread.sleep(10_000);
                IO.println("worker: woke up normally");
            } catch (InterruptedException e) {
                IO.println("worker: interrupted while sleeping, cleaning up and exiting");
            }
        });

        worker.start();
        //give the worker time to actually enter sleep() before we interrupt it,
        //so we know the interrupt hits a blocked thread (not one still running).
        Thread.sleep(500);

        IO.println("main: asking worker to stop");
        // interrupt() only sets the flag (and wakes the worker out of sleep)
        //it does not stop the thread; the worker chooses to stop in its catch block.
        worker.interrupt();

        //wait for the worker to finish before we print the final line, so the
        //output order is deterministic, and we don't exit while it is still running.
        worker.join();
        IO.println("main: worker has stopped");
    }
}
