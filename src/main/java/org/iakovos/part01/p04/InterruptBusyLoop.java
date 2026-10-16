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
 * This demo covers CASE 2: the counter never blocks, so no exception is ever
 * thrown. It must poll isInterrupted() itself, once per loop iteration, to
 * notice the interrupt and exit. Interruption is cooperative:
 * if the loop used `while (true)` instead, interrupt() would do nothing at all.
 * See InterruptSleeping for case 1.
 */
public class InterruptBusyLoop {

    void main() throws InterruptedException {

        /*
        * The counter just increments forever.
        * It never blocks, so interrupt() will NOT throw anything here.
        * Instead, it polls isInterrupted() each iteration: when the flag is set
        * the loop condition becomes false and the thread returns from run().
        * Note isInterrupted() (instance, non-clearing) is used here, not the
        * static Thread.interrupted() which would clear the flag as it reads it.
        */
        Thread counter = new Thread(() -> {
            long count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                count++;
            }
            IO.println("counter: stopped after " + count + " increments");
        });

        counter.start();
        //give the counter time to get spinning before we interrupt it,
        //so the flag is observed as set on the next loop iteration.
        Thread.sleep(500);

        IO.println("main: asking worker to stop");
        // interrupt() only sets the flag. Nothing throws here because the
        // counter never blocks; it will be read by the loop condition instead.
        counter.interrupt();

        //wait for the counter to finish so the output order is deterministic
        //and we don't exit while it is still running.
        counter.join();
        IO.println("main: done");

    }
}
