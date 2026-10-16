package org.iakovos.part01.p04;

/*
 * Sometimes a plain boolean flag is easier to read than interruption.
 *
 * The stop mechanism here is a `volatile boolean running` field that main sets
 * to false via poller.stop(). The poller's loop reads it each iteration and
 * exits when it sees false.
 *
 * Why `volatile` (the short version; Lesson 07 explains the full why):
 *   Each thread may cache fields in CPU/JIT caches. Without `volatile`, the
 *   poller thread could keep reading a cached `true` forever and NEVER see the
 *   `false` write done by main. `volatile` forces every read to main memory and
 *   every write to flush, so the change is visible across threads.
 *   It is a VISIBILITY guarantee, not mutual exclusion or atomicity.
 *
 * volatile flag vs interrupt() — the precise difference:
 *
 *   interrupt() does TWO things: (1) sets the interrupt flag, and (2) UNPARKS
 *   the target thread if it is currently blocked in sleep()/wait()/join()/
 *   take()/get()/etc. Those blocking methods are written to check the flag
 *   when they wake, see it set, and throw InterruptedException (clearing it).
 *   So a blocked thread is pulled out of the call right away.
 *
 *   A `volatile` write does only the first half: it changes a value in memory
 *   and nothing more. It does NOT unpark the thread. So a thread blocked in
 *   sleep()/wait()/take() stays parked until that call returns on its own
 *   (timer fires, a message arrives, etc.) — only then does it loop back,
 *   read the flag, and exit.
 *
 *   Dramatic case: a worker blocked in `queue.take()` waiting for a message
 *   that never arrives. Setting `running = false` does nothing — `take()`
 *   isn't watching your boolean, and it never returns, so the loop never
 *   re-checks the flag. The thread is stuck forever. interrupt() unparks
 *   `take()` immediately, it throws, and the thread exits.
 *
 *   Neither mechanism forces the thread to stop. Both are cooperative. The
 *   difference is that interrupt() wakes blocked methods so they can cooperate
 *   now; a volatile flag can only be read when the thread is already awake.
 *
 * This file still handles InterruptedException inside run(): see the rule below.
 */
public class VolatileFlagStop {

    void main() throws InterruptedException {
        Poller poller = new Poller();
        Thread thread = new Thread(poller);
        thread.start();

        Thread.sleep(350);
        //flip the flag from the outside. The poller will see it on the next
        //loop iteration — but only AFTER its current sleep(100) returns on its
        //own. That delay is the whole "volatile flag doesn't wake blocked
        //methods" point in action. interrupt() would have cut the sleep short.
        poller.stop();
        thread.join();

    }


    /*
    * The rule for InterruptedException inside Runnable.run():
    *
    *   Runnable.run() is declared as `public void run()` with NO throws clause,
    *   and you can't add one on an @Override. InterruptedException is checked,
    *   so it CANNOT escape run(). "Propagate it" (option 1 of the rule) is off
    *   the table. Therefore when run() catches one:
    *     - restore the flag with Thread.currentThread().interrupt()
    *       (sleep() cleared it when it threw; without restoring, the signal is lost),
    *     - then end the task (return / break).
    *   Restoring matters: the owner of the thread (e.g. an ExecutorService that
    *   called shutdownNow()) can still see that an interrupt happened.
    *
    *   Do NOT do `throw new RuntimeException(e)` (commented out below): unless
    *   you restore the flag first, it erases the interrupt AND turns a polite
    *   stop request into an uncaught exception that crashes the task noisily.
    */
    class Poller implements Runnable{
        //volatile: without it, the poller thread may cache `running` and never
        //see main's write of false. Visibility, not atomicity (Lesson 07).
        private volatile boolean running = true;

        void stop(){
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                IO.println("polling for new messages...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //sleep() cleared the flag when it threw; set it again so the
                    //thread's owner can still tell an interrupt happened, then exit.
                    Thread.currentThread().interrupt();
                    return;
                    //throw new RuntimeException(e);
                }
            }
            IO.println("poller stopped");
        }
    }
}
