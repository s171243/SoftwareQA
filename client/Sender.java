package client;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Sender extends Thread {

    private final Graphic g;
    PrintWriter pw;
    Scanner myScanner = new Scanner(System.in);
    BorderPane root;

    // Constructor
    public Sender(PrintWriter pw, Graphic g) {
        this.pw = pw;
        this.g = g;
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
