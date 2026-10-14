package org.iakovos.part01.p03;

public class ThreadStates {
    // A plain Object used as a monitor (intrinsic lock). Any object can act as one.
    // Two threads that synchronize on the SAME object are guaranteed not to run the
    // guarded blocks at the same time; one must wait for the other to release the lock.
    final Object lock = new Object();


    void main() throws InterruptedException {

        //create a thread but don't start it yet.
        //a freshly constructed Thread has not been scheduled by the JVM, so its state is NEW.
        Thread sleeper = new Thread(() -> {
            pause(500);   // will call Thread.sleep(500) once running
        }, "thread-sleeper");
        IO.println(Thread.currentThread().getName()+" : After creating a new thread:\t" + sleeper.getState());

        /*
          start() registers the thread with the JVM and lets the OS scheduler run it.
          its state becomes RUNNABLE, meaning "eligible to run" (actually running or in the ready queue)
          RUNNABLE does NOT mean it's executing right now.
         */
        sleeper.start();
        IO.println(Thread.currentThread().getName()+ " : After start():\t\t\t" + sleeper.getState());

        /*
          pause(100) on main is a deliberate timing trick.
          Timing trick: main sleeps 100ms, enough to guarantee that sleeper has start and reach its own sleep(500) on its core.
          since 100 < 500, main wakes first and catches sleeper still mid-sleep -> TIMED_WAITING.
        */
        pause(100);
        IO.println(Thread.currentThread().getName()+" : While sleeping:\t\t" + sleeper.getState());

        // join() makes main block until sleeper's run() finishes.
        // After the thread dies it can never run again, so its state becomes TERMINATED.
        sleeper.join();
        IO.println(Thread.currentThread().getName()+" : After finish:\t\t\t" + sleeper.getState());



        // WAITING, the thread froze itself with wait() and waits for a signal.:
        //   1. Thread picks up the lock (synchronized) -> the "key" to the room.
        //   2. wait() puts the key down (releases the lock) and freezes the thread.
        //      It does nothing -- no CPU, no countdown -- until woken by notify().
        //   3. notify()/notifyAll() taps it on the shoulder. It then has to pick the
        //      key back up (re-acquire the lock) before wait() returns and it can run.
        //synchronized: You can't put down a key you never picked up.
        //So wait() MUST be called inside synchronized(lock), or the JVM throws.
        Thread threadWait = new Thread(() -> {
            synchronized (lock) {           // pick up the key
                try {
                    lock.wait();            // put key down + freeze (WAITING); pick up again on wakeup
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();   // restore the interrupted flag
                }
            }
        },"thread-wait");


        threadWait.start();
        //pause(100) gives threadWait time to reach wait() and freeze, so getState() returns WAITING instead of RUNNABLE.
        pause(100);

        // threadWait is now frozen inside wait(). It put the key down, so the lock
        // is FREE. State = WAITING: frozen on purpose, waiting for a notify().
        IO.println(Thread.currentThread().getName()+" : Inside wait():\t\t\t" + threadWait.getState());

        Thread blocked;
        // BLOCKED, the thread wants to enter a synchronized block but the lock is held by someone else, so it can't get in:
        //   Main picks up the key (synchronized) and keeps it, so blocked-thread can't
        //   get in and stands at the door -> BLOCKED until main leaves (releases the lock).
        synchronized (lock) {
            // blocked-thread tries to enter its own synchronized(lock) but main holds
            // the lock, so it immediately goes BLOCKED.
            blocked = new Thread(() -> {
                synchronized (lock) {
                    IO.println(Thread.currentThread().getName()+" : blocked thread finally got the lock\t\t");
                }
            }, "blocked-thread");

            blocked.start();
            pause(100);
            //  blocked-thread is now stuck at the door while main holds the key -> BLOCKED.
            IO.println(Thread.currentThread().getName()+" : Lock held by main:\t\t" + blocked.getState());

            // notifyAll() wakes threadWait but does NOT hand it the key: it goes
            // WAITING -> BLOCKED, now standing at the door with blocked-thread.
            // When main exits this block, the OS lets one in; the other stays BLOCKED.
            lock.notifyAll();

        }   // <-- main releases the lock; OS lets one of the two in, the other stays BLOCKED.
        threadWait.join();
        blocked.join();
    }



    void pause(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
