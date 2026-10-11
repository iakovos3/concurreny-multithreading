package org.iakovos.part01.p01;

import java.util.ArrayList;
import java.util.List;

public class CpuBound {

    void main() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        IO.println("Cores: " + cores);

        long start = System.currentTimeMillis();
        for(int i=0; i<4; i++){
            countPrimes(2_000_000);
        }
        IO.println("Sequential, 4 jobs: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        for(int i=0; i<4; i++){
            Thread thread = new Thread(() -> countPrimes(2_000_000));
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        IO.println("Parallel, 4 jobs:   " + (System.currentTimeMillis() - start) + " ms");

    }


    long countPrimes(int limit){
        IO.println(Thread.currentThread().getName() + " counting primes");
        long count = 0;
        for(int n=2; n< limit; n++){
            if(isPrime(n)){
                count++;
            }
        }
        return count;
    }

    boolean isPrime(int n) {
        for (int divisor = 2; (long) divisor * divisor <= n; divisor++) {
            if (n % divisor == 0) {
                return false;
            }
        }
        return true;
    }
}
