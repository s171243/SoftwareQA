package client;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import static java.lang.System.exit;

public class Client extends Application {

    private static PrintWriter writer;
    private static Boolean loggedIn = false;
    private static final LinkedList<String> messages = new LinkedList<>();
    private static String[] users;
    private static String username;
    private static int numMessages = 0;
    private static int sleepTime = 10;
    private static Listener listener;
    private static Sender sender;
    public static char separator = ((char) 007);

    public static void main(String[] args) {
        launch(args);
    }

    public static PrintWriter getWriter() {
        return writer;
    }

    public static LinkedList<String> getMessages() {
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
        primaryStage.setOnHiding(
                e -> {
                    Client.getWriter().println("QUIT");
                    Client.getWriter().flush();
                    exit(0);
                }
        );

        g.setup();

        while (true) {
            try {
                setupConnection(g);
                break;
            } catch (ConnectException e) {
                System.out.println("The server is not online yet. Trying again in " + sleepTime + " seconds");
                //Thread.sleep(sleepTime * 1000);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void restartConnection(Graphic g) throws IOException {
        listener.setRunning(false);
        sender.setRunning(false);
        setupConnection(g);
    }

    private static void setupConnection(Graphic g) throws IOException {
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
        listener = new Listener(br, g);
        sender = new Sender(writer);

        // Invoking the start() method
        listener.start();
        sender.start();
    }

    public static void setLoggedIn(Boolean loggedIn) {
        Client.loggedIn = loggedIn;
    }

    public static Boolean getLoggedIn() {
        return loggedIn;
    }
}