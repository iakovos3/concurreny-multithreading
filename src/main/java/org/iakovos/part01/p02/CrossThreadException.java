package org.iakovos.part01.p02;

public class CrossThreadException {

    void main(){

        Thread failingThread = new Thread(() -> {
            IO.println(Thread.currentThread().getName()+" : This thread will crush and throw exception");
            throw new IllegalStateException("boom");
        }, "failing-worker");

        //An exception thrown inside a thread ends that thread only.
        // It doesn't reach the code that called start()

        try {
            failingThread.start();
            failingThread.join();
        } catch (InterruptedException e) {
            //This will never print, because the exception belongs to failing-worker, and the JVM's default handler prints it to stderr
            IO.println("caught in main? never printed");
        }
        IO.println("main carries on");
    }
}
