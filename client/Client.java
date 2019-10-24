package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application {

    private static Socket socket;

    public static void main(String args[]) {
        try {
            String host = "localhost";
            int port = 9000;
            Scanner myScanner = new Scanner(System.in);
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            PrintWriter bw = new PrintWriter(osw);

            String number = "LIST";

            String sendMessage = "I am connected";
            bw.println(sendMessage);
            bw.flush();
            System.out.println("Me: " + sendMessage);

            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            br.readLine();
            System.out.println("Server: " + message);

            // create a new thread object
            Thread t = new ClientListener(br);

            // Invoking the start() method
            t.start();

            Thread sender = new Sender(bw);

            sender.start();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            //Closing the socket
            /*
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
             */
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        primaryStage.setTitle("Hello World!");

        //bottom
        root = setUpBottom(root);

        //right
        String[] names = {"a", "b", "c", "d"};
        root = setUpRight(root, names);

        // center
        String[] messages = {"Hello, world!", "The world says hello back", "Oh wow - I never tried that!", "Yeah. That's life"};
        root = setUpCenter(root, messages);

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
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
}