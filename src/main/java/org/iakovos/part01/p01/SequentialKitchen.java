package org.iakovos.part01.p01;

//Single thread

public class SequentialKitchen {

    void main() throws InterruptedException {
        long start = System.currentTimeMillis();
        cook("Chopping vegetables", 600);
        cook("Grilling steak", 800);
        cook("Plating dessert", 400);

        IO.println("Total: " + (System.currentTimeMillis() - start) + " ms");
    }

    void cook(String task, long millis) throws InterruptedException {
        IO.println(Thread.currentThread().getName() +": "+ task + "...");
        Thread.sleep(millis);
        IO.println(Thread.currentThread().getName() +": "+ task + " done!");
    }
}
