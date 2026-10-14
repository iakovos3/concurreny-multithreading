package org.iakovos.part01.p03;

import java.time.Duration;

public class JoinDemo {
    /*
    * join() puts the caller in WAITING until the target thread reaches TERMINATED.
    * join(Duration) (Java 19+) waits at most that long and returns true if the thread finished.
    * Prefer the timed form in real code. A plain join() on a thread that never ends hangs the caller forever.
join also creates a happens-before relationship: everything the finished thread wrote is visible to the thread that joined it.
    *
    * */
    void main() throws InterruptedException {
        Thread download = new Thread(() -> {
            for (int percent = 25; percent <= 100; percent += 25) {
                pause(500);
                IO.println("download " + percent + "%");
            }
        });

        download.start();
        IO.println("main: waiting for the download");
        download.join();
        IO.println("main: download finished, opening file");


        Thread slow = new Thread(() -> pause(3_000));
        slow.start();
        boolean finished = slow.join(Duration.ofMillis(300));
        if(!finished){
            IO.println("slow thread finished in time:  " + finished);
            IO.println("going to interrupt the slow thread!");
            slow.interrupt();
        }


    }

    void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
