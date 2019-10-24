package client;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Listener extends Thread {

    private Graphic g;
    BufferedReader br;
    Scanner myScanner = new Scanner(System.in);
    static BorderPane root;

    // Constructor
    public Listener(BufferedReader br, Graphic g) {
        this.br = br;
        this.g = g;
    }

    public void validateMessage(String msg){
        // receive
        if(msg.startsWith("Broadcast from")){
            receive(msg.substring(14));
        }

        // connected
        else if(msg.startsWith("OK Welcome to the chat server,")){
            connected();
        }

        // receive
        else if(msg.startsWith("PM from")){
            pm(msg.substring(8));
        }

        //listing
        else if(msg.startsWith("OK, the following users are online")){
            listing(msg.substring(35));
        }

        else if(msg.startsWith("OK Welcome to the chat server")){
            loggedIn(msg.substring(30));
        }

        else if (msg.startsWith("OK your message has been sent")){
            sent();
        }

        else if (msg.equals("")){
            //System.out.println("No message");
        }

        else {
            System.out.println("I AM CONFUSED");
        }
    }

    public static void receive(String msg){
        System.out.println("Message received: " + msg);
    }

    public static void pm(String msg){
        System.out.println("PM received: " + msg);
    }

    public static void connected(){
        System.out.println("We are online");
    }

    public static void sent(){
        System.out.println("Message sent");
    }

    public void listing(String msg){
        String[] list = msg.split(",");
        list = Arrays.copyOf(list, list.length-1);
        System.out.println(Arrays.toString(list));
        String[] finalList = list;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                g.setUpRight(finalList);
            }
        });
        // g.setUpCenter(list);
    }

    public static void loggedIn(String msg){
        System.out.println("We are logged in with username " + msg);
        Client.setLoggedIn(true);
    }

    @Override
    public void run() {
        String response = null;
        while(true) {
            try {
                response = br.readLine();
                validateMessage(response);
                // System.out.println("Server : " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
