package org.iakovos.part01.p02;

public class StartTwice {

    void main() throws InterruptedException {
        Thread worker = new Thread(() -> IO.println("working"), "worker-thread");

        worker.start();
        worker.join();
        // A Thread object in Java is a one-shot state machine. Once a thread has finished running, it cannot be restarted
        // a Thread is a single execution, not a reusable worker. The Runnable is reusable; the Thread is not.
        // To run the job again, create a new Thread with the same Runnable
        worker.start();
    }
}
