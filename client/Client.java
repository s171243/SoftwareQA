package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application {

    private static Socket socket;
    private Thread listener;
    private Thread sender;
    private static PrintWriter writer;
    private static BufferedReader br;
    private Graphic g;
    private static Boolean loggedIn = false;

    public static void main(String args[]) {
        launch(args);
    }

    public static PrintWriter getWriter(){
        return writer;
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

    public static BorderPane setUpBottom(BorderPane root){
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(100);
        btn.setPrefHeight(100);

        TextField bottom = new TextField();
        bottom.setPrefWidth(400);
        bottom.setPrefHeight(100);
        bottom.setPromptText("Write your message");

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(bottom, btn);
        root.setBottom(bottomFrame);
        return root;
    }

    public static BorderPane setUpRight(BorderPane root, String[] names){
        Label heading = new Label("Online users");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.getChildren().add(heading);

        for (String name : names){
            Label label = new Label(name);
            vbox.getChildren().add(label);
        }

        vbox.setPadding(new Insets(20, 10, 20, 20));
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        root.setRight(vbox);
        return root;
    }

    public static BorderPane setUpCenter(BorderPane root, String[] messages){
        VBox center = new VBox(10);

        Label heading = new Label("Chat");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));

        center.getChildren().add(heading);

        for(String message : messages){
            Label label = new Label(message);
            label.setPadding(new Insets(10, 10, 10, 10));
            center.getChildren().add(label);
        }

        center.setPadding(new Insets(50, 10, 50, 20));

        root.setCenter(center);
        return root;
    }

    public static void setLoggedIn(Boolean loggedIn) {
        Client.loggedIn = loggedIn;
    }

    public static Boolean getLoggedIn() {
        return loggedIn;
    }
}