package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Sender extends Thread {

    PrintWriter pw;
    Scanner myScanner = new Scanner(System.in);

    // Constructor
    public Sender(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void run() {
        String response = null;
        while(true) {
            String msg = myScanner.nextLine();

            pw.println(msg);
            pw.flush();
        }
    }
}
