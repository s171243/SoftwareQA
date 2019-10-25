package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;

public class Client extends Application {

    private static Socket socket;
    private Thread listener;
    private Thread sender;
    private static PrintWriter writer;
    private static BufferedReader br;
    private Graphic g;
    private static Boolean loggedIn = false;
    private static ArrayList<String> messages = new ArrayList<String>();
    private static String[] users;
    private static String username;

    public static void main(String args[]) {
        launch(args);
    }

    public static PrintWriter getWriter(){
        return writer;
    }

    public static ArrayList<String> getMessages() {
        return messages;
    }

    public static void addMessages(String message) {
        Client.messages.add(message);
    }

    public static String[] getUsers() {
        return users;
    }

    public static void setUsers(String[] users) {
        Client.users = users;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Client.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        g = new Graphic(primaryStage);
        g.setup();

        try {
            String host = "localhost";
            int port = 9000;
            Scanner myScanner = new Scanner(System.in);
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            writer = new PrintWriter(osw);

            String number = "LIST";

            String sendMessage = "I am connected";
            writer.println(sendMessage);
            writer.flush();

            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            br.readLine();

            // create a new thread object
            Thread listener = new Listener(br, g);
            Thread sender = new Sender(writer, g);

            // Invoking the start() method
            listener.start();
            sender.start();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void setLoggedIn(Boolean loggedIn) {
        Client.loggedIn = loggedIn;
    }

    public static Boolean getLoggedIn() {
        return loggedIn;
    }
}