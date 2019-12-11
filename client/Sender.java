package client;

import java.io.PrintWriter;

class Sender extends Thread {

    private final PrintWriter pw;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    private boolean isRunning = true;

    // Constructor
    public Sender(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void run() {
        while (this.isRunning) {
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
