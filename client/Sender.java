package client;

import java.io.PrintWriter;

class Sender extends Thread {

    private final PrintWriter pw;

    // Constructor
    public Sender(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void run() {
        while (this.isAlive()) {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Client.getLoggedIn()) {
                pw.println("LIST");
                pw.flush();
            }
        }
    }
}
