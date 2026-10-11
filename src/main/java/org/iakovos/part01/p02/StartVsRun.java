package org.iakovos.part01.p02;

public class StartVsRun {

    void main() throws InterruptedException {

        Thread worker = new Thread(() -> IO.println("Running on: " + Thread.currentThread().getName()), "worker");

        //is a plain method call. The code runs on the calling thread (main), and no new thread is created.
        worker.run();

        //Only start() asks the JVM for a new thread, which then calls run() for you
        worker.start();
        worker.join();
    }
}
