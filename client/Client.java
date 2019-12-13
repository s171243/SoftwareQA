package client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.System.exit;

public class Client extends Application {

    public static char separator = ((char) 007);
    private static PrintWriter writer;

    private static Boolean loggedIn = false;
    private static String username;

    public Listener getListener() {
        return listener;
    }

    private static Listener listener;
    private static Sender sender;
    private static Socket socket;
    private Graphic graphic;

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private boolean isRunning = true;

    public static void main(String[] args) {
        launch(args);
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Client.username = username;
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
        socket = new Socket(address, port);

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

    public static Boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(Boolean loggedIn) {
        Client.loggedIn = loggedIn;
    }

    public static PrintWriter getWriter() {
        return writer;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    @Override
    public void start(Stage primaryStage) {
        graphic = new Graphic(primaryStage);
        primaryStage.setOnHiding(
                e -> {
                    Client.getWriter().println("QUIT");
                    Client.getWriter().flush();
                    exit(0);
                }
        );

        graphic.setup();

        while (isRunning) {
            try {
                setupConnection(graphic);
                break;
            } catch (ConnectException e) {
                int sleepTime = 10;
                graphic.sendErrorMessage("The server is not online yet. Trying again in " + sleepTime + " seconds");
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}