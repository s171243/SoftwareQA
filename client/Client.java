package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.stage.Stage;

public class Client extends Application {

    private static PrintWriter writer;
    private static Boolean loggedIn = false;
    private static final ArrayList<String> messages = new ArrayList<>();
    private static String[] users;
    private static String username;
    private static int numMessages = 0;

    public static void main(String[] args) {
        launch(args);
    }

    public static PrintWriter getWriter() {
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

    public static int getNumMessages() {
        return numMessages;
    }

    public static void incNumMessages() {
        Client.numMessages++;
    }

    @Override
    public void start(Stage primaryStage) {
        Graphic g = new Graphic(primaryStage);
        g.setup();

        try {
            String host = "localhost";
            int port = 9000;
            InetAddress address = InetAddress.getByName(host);
            Socket socket = new Socket(address, port);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            writer = new PrintWriter(osw);

            String sendMessage = "I am connected";
            writer.println(sendMessage);
            writer.flush();

            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            // Reading twice to get welcome message
            br.readLine();
            br.readLine();

            // create a new thread object
            Thread listener = new Listener(br, g);
            Thread sender = new Sender(writer);

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