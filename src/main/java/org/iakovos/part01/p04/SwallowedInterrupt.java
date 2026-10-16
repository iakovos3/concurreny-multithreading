package org.iakovos.part01.p04;

import java.time.Duration;

/*
 * The rule for InterruptedException: never swallow it.
 *
 * This demo shows the bug that lives in real codebases. Two workers run the
 * same loop, which sleeps inside a try/catch. When interrupt() arrives, the
 * worker is almost certainly blocked in sleep(), so sleep() throws
 * InterruptedException AND CLEARS THE FLAG.
 * The catch block decides whether the thread can ever stop:
 *
 *   - broken: catches the exception and does nothing. The flag is now cleared,
 *     the loop condition sees "not interrupted", and the loop runs forever.
 *     The request to stop was silently erased.
 *
 *   - fixed: calls Thread.currentThread().interrupt() to SET THE FLAG AGAIN,
 *     so the loop condition sees it on the next iteration and exits.
 *
 * General rule when you catch InterruptedException:
 *   1. Propagate it (declare throws InterruptedException), or
 *   2. Restore the flag with Thread.currentThread().interrupt(), then stop
 *      (return/break). This is the only option inside Runnable.run(), which
 *      cannot throw checked exceptions.
 * Never leave the catch empty, and never only log it.
 */
public class SwallowedInterrupt {

    void main() throws InterruptedException {
        // broken swallows the interrupt; fixed restores it. Same loop, different outcome.
        Thread broken = new Thread(() -> worker("broken", false));
        Thread fixed = new Thread(() -> worker("fixed", true));

        broken.start();
        fixed.start();
        //let both workers get inside sleep() before we interrupt, so the
        //interrupt hits a blocked thread (case 1: throws + clears the flag).
        Thread.sleep(300);

        broken.interrupt();
        fixed.interrupt();

        //fixed stops on its own; broken never will, so we join it with a timeout
        //and then check isAlive() to prove it is still stuck.
        fixed.join();
        broken.join(Duration.ofSeconds(2));
        IO.println("broken thread still alive after 2s? " + broken.isAlive());
        //System.exit is only needed because broken would otherwise keep the JVM alive.
        System.exit(0);
    }

    /*
    * The loop combines both ways of noticing an interrupt:
    *   - it polls isInterrupted() as the loop condition (handles busy moments), and
    *   - it sleeps inside try/catch (handles blocked moments via the exception).
    * The catch block is where the flag must be restored, because sleep() cleared it.
    * restoreInterrupt=false demonstrates the swallow-the-interrupt bug.
    */
    void worker(String name, boolean restoreInterrupt) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                if (restoreInterrupt) {
                    //sleep() cleared the flag when it threw; set it again so the
                    //loop condition sees it and exits. Without this, the loop runs forever.
                    Thread.currentThread().interrupt();
                }
            }
        }
        IO.println(name + ": stopped cleanly");
    }

}
