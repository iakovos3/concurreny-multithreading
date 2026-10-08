package org.iakovos.part00;

public class Sleepy {

    void main() throws InterruptedException {

        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(20_000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "sleepy-worker"); // Thread's name= "sleepy-worker". If no name is given, name = "Thread-0"

        worker.start();
        worker.join(); //  The main thread is WAITING inside join()
        IO.println("Goodbye");
    }
}
