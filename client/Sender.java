package client;

import java.io.PrintWriter;

class Sender extends Thread {

    private final PrintWriter pw;
    private boolean isRunning = true;

    public Sender(PrintWriter pw) {
        this.pw = pw;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        while (this.isRunning) {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Client.isLoggedIn()) {
                pw.println("LIST");
                pw.flush();
            }
        }
    }

}
