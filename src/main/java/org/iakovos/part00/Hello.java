package org.iakovos.part00;


public class Hello {
    void main(){
        IO.println("Hello from " + Thread.currentThread().getName() +
                   " my id is " + Thread.currentThread().threadId() +
                   " and my state is " + Thread.currentThread().getState().name()
        );
        IO.println("The system has " + Runtime.getRuntime().availableProcessors() + " CPU cores"  );

        greet("threads");

        Counter counter = new Counter();
        counter.increment();
        IO.println("Counter number = " + counter.getValue());

    }

    void greet(String topic){
        IO.println("Let's learn " + topic);
    }

    class Counter {
        private int value;

        void increment(){
            value++;
        }

        int getValue(){
            return value;
        }
    }
}
