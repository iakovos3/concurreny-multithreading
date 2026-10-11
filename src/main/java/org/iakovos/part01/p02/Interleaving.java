package org.iakovos.part01.p02;

public class Interleaving {

    void main() throws InterruptedException {
        Thread letters = new Thread(() -> {
            for (char letter = 'A'; letter <= 'E'; letter++) {
                IO.println(Thread.currentThread().getName()+ " -> letters: " + letter);
            }
        }, "letters-thread");

        Thread numbers = new Thread(() -> {
            for (int number = 1; number <= 5; number++) {
                IO.println(Thread.currentThread().getName() + " -> numbers: " + number);
            }
        }, "numbers-thread");

        //This will print all the letters first and then all the numbers.
        //The code runs on the calling thread (main), and NO new thread is created.
        letters.run();
        numbers.run();
        IO.println(Thread.currentThread().getName()+ " : Within one thread, statements happen in order.");

        //start() asks the JVM for a new thread, which then calls run()
        letters.start();
        numbers.start();

        letters.join();
        numbers.join();
        IO.println(Thread.currentThread().getName()+ " : Between threads, you have no ordering guarantee unless you create one.!");
    }
}
