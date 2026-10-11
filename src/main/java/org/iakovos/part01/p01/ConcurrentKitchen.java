package org.iakovos.part01.p01;

public class ConcurrentKitchen {

    void main() throws InterruptedException {
        long start = System.currentTimeMillis();

        Thread chop = new Thread(() -> cook("Chopping vegetables", 600), "Thread-Chop");
        Thread grill = new Thread(() -> cook("Grilling steak", 800), "Thread-Grill");
        Thread plate = new Thread(() -> cook("Plating dessert", 400), "Thread-Plate");

        chop.start();
        grill.start();
        plate.start();

        chop.join();
        grill.join();
        plate.join();

        IO.println("Total: " + (System.currentTimeMillis() - start) + " ms");

    }

    void cook(String task, long millis){
        IO.println(Thread.currentThread().getName() +": "+ task + "...");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        IO.println(Thread.currentThread().getName() +": "+ task + " done");
    }
}
