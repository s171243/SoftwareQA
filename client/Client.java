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
            System.out.println("Message sent to the server : " + sendMessage);

            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            br.readLine();
            System.out.println("Message received from the server : " + message);

            while (true) {
                String msg = myScanner.nextLine();

                if (msg.equals("QUIT")) {
                    socket.close();
                    break;
                }
                bw.println(msg);
                System.out.println("Message sent to the server : " + msg);
                bw.flush();
                String response = br.readLine();
                validateMessage(response);
                System.out.println("Message received from the server : " + response);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            //Closing the socket
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        launch(args);
    }

    public static void validateMessage(String msg){
        // receive
        if(msg.startsWith("Broadcast from")){
            receive(msg.substring(14));
        }

        // connected
        else if(msg.startsWith("OK Welcome to the chat server,")){
            connected();
        }

        // receive
        else if(msg.startsWith("PM from ")){
            pm(msg.substring(8));
        }

        //listing
        else if(msg.startsWith("OK, the following users are online")){
            listing(msg.substring(35));
        }

        else if(msg.startsWith("OK Welcome to the chat server")){
            loggedIn(msg.substring(30));
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

    public static void listing(String msg){
        String[] list = msg.split(",");
        list = Arrays.copyOf(list, list.length-1);
        System.out.println(Arrays.toString(list));
    }

    public static void loggedIn(String msg){
        System.out.println("We are logged in with username " + msg);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        primaryStage.setTitle("Hello World!");

        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(100);
        btn.setPrefHeight(100);

        TextField bottom = new TextField();
        bottom.setPrefWidth(400);
        bottom.setPrefHeight(100);
        bottom.setPromptText("This is a test");

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(bottom, btn);
        root.setBottom(bottomFrame);

        //right
        Label label = new Label("Online users");
        label.setStyle("-fx-font-weight: bold");
        label.setPadding(new Insets(10, 10, 10, 10));

        Label label2 = new Label("User 1");
        label.setPadding(new Insets(10, 10, 10, 10));

        Label label3 = new Label("User 2");
        label.setPadding(new Insets(10, 10, 10, 10));

        Label label4 = new Label("User 3");
        label.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(label, label2, label3, label4);
        vbox.setPadding(new Insets(20, 10, 20, 20));
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        root.setRight(vbox);

        // center
        VBox center = new VBox(10);

        Label label5 = new Label("Chat");
        label5.setStyle("-fx-font-weight: bold");
        label.setPadding(new Insets(10, 10, 10, 10));

        Label label6 = new Label("Message");
        label.setPadding(new Insets(10, 10, 10, 10));

        Label label7 = new Label("Message");
        label.setPadding(new Insets(10, 10, 10, 10));

        Label label8 = new Label("Message");
        label.setPadding(new Insets(10, 10, 10, 10));

        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(label5, label6, label7, label8);
        centerBox.setPadding(new Insets(50, 10, 50, 20));

        root.setCenter(centerBox);

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }
}