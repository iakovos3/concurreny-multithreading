package org.iakovos.part01.p03;

//Daemon threads: are background helpers that the JVM doesn't wait for.
//The JVM exits when every non-daemon thread has finished.
//When the last non-daemon thread ends, the JVM exits and any daemon threads stop mid-step.


public class DaemonDemo {

    void main() throws InterruptedException {
        /*
         * we have 2 threads: main & heartbeat
         *
         * Case 1:
         * if  heartbeat.setDaemon(true); then
         * non-deamon = main
         * deamon     = heartbeat
         *
         * when main finishes, JVM exits because:
         * "When the last non-daemon thread ends, the JVM exits and any daemon threads stop mid-step."
         *
         *
         * Case 2:
         * if heartbeat.setDaemon(false); [this is the default] then
         * non-deamon = main, heartbeat
         *
         *
         * when main finishes, JVM does NOT exit  because:
         * "The JVM exits when every non-daemon thread has finished."
         *  heartbeat has a  while (true)
         */


        Thread heartbeat = new Thread(() -> {
            while (true) {
                IO.println("heartbeat");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        /*
        *
        * Remove setDaemon(true) and the program never exits. Rules:
        *
        * 1. setDaemon must be called before start(), or it throws IllegalThreadStateException.
        * 2. A new thread inherits the daemon flag of the thread that created it.
        * 3. Daemon threads get no cleanup: no finally blocks, no flushing.
        * 4. Never give a daemon thread work that must finish, such as writing a file or committing a transaction.
        * Virtual threads are always daemon threads.
        *
        * */
        boolean isHeartbeatDaemon = true;
        heartbeat.setDaemon(isHeartbeatDaemon);
        heartbeat.start();

        Thread.sleep(700);
        if(isHeartbeatDaemon){
            IO.println("main finished, JVM exits and heartbeat stops as well");
        }else{
            IO.println("main finished, JVM exits even though heartbeat never stops");
        }

    }

}
