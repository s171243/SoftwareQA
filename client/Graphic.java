package client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Graphic {

    private Stage stage;
    private static BorderPane root = new BorderPane();

    public Graphic(Stage primaryStage){
        this.stage = primaryStage;
    }


    public void setup(){
        stage.setTitle("Hello World!");

        //bottom
        root = setUpBottom();

        //top
        root = setUpTop();

        //right
        String[] names = {"You're not logged in"};
        root = setUpRight(names);

        // center
        String[] messages = {"Hello, world!", "The world says hello back", "Oh wow - I never tried that!", "Yeah. That's life"};
        root = setUpCenter(Client.getMessages());

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    private BorderPane setUpTop() {
        //bottom
        Button btn = new Button("Login");
        btn.setPrefWidth(100);
        btn.setPrefHeight(50);

        TextField top = new TextField();
        top.setPrefWidth(400);
        top.setPrefHeight(50);
        top.setPromptText("What is your username?");

        top.setOnAction(e -> {
            sendMessage("IDEN " + top.getText());
            top.setText("");
        });
        btn.setOnAction(e -> {
            sendMessage("IDEN " + top.getText());
            top.setText("");
        });

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(top, btn);
        root.setTop(bottomFrame);
        return root;
    }

    public void emptyTop(){
        root.setTop(null);
    }

    public BorderPane setUpBottom(){
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(100);
        btn.setPrefHeight(100);

        TextField bottom = new TextField();
        bottom.setPrefWidth(300);
        bottom.setPrefHeight(100);
        bottom.setPromptText("Write your message");

        ComboBox user = new ComboBox();
        user.setPrefWidth(100);
        user.setPrefHeight(100);
        user.setPromptText("Recipient");

        bottom.setOnAction(e -> {
            sendMessage("MESG " + bottom.getText());
            bottom.setText("");
        });
        btn.setOnAction(e -> {
            sendMessage("MESG " + bottom.getText());
            bottom.setText("");
        });

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(user, bottom, btn);
        root.setBottom(bottomFrame);
        return root;
    }

    public BorderPane setUpBottom(String[] list){
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(100);
        btn.setPrefHeight(100);

        TextField bottom = new TextField();
        bottom.setPrefWidth(300);
        bottom.setPrefHeight(100);
        bottom.setPromptText("Write your message");

        ComboBox<String> user = new ComboBox<>();
        user.setPrefWidth(100);
        user.setPrefHeight(100);
        user.setPromptText("Recipient");
        for (String recipient : list){
            user.getItems().add(recipient);
        }

        bottom.setOnAction(e -> {
            String value = user.getValue();
            String bottomText = bottom.getText();
            sendMessage("MESG " + value + " " + bottomText);
            Client.addMessages(Client.getUsername() + ":" + value);
            bottom.setText("");
        });

        btn.setOnAction(e -> {
            String value = user.getValue();
            String bottomText = bottom.getText();
            sendMessage("MESG " + value + " " + bottomText);
            Client.addMessages(Client.getUsername() + ":" + value);
            bottom.setText("");
        });

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(user, bottom, btn);
        root.setBottom(bottomFrame);
        return root;
    }

    public BorderPane setUpBottom(String[] list, String username){
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(100);
        btn.setPrefHeight(100);

        TextField bottom = new TextField();
        bottom.setPrefWidth(300);
        bottom.setPrefHeight(100);
        bottom.setPromptText("Write your message");

        ComboBox<String> user = new ComboBox<>();
        user.setPrefWidth(100);
        user.setPrefHeight(100);
        user.setPromptText("Recipient");
        for (String recipient : list){
            if(!username.equals(recipient)) {
                user.getItems().add(recipient);
            }
        }

        bottom.setOnAction(e -> {
            String value = user.getValue();
            String bottomText = bottom.getText();
            sendMessage("MESG " + value + " " + bottomText);
            Client.addMessages(Client.getUsername() + ":" + bottomText);
            bottom.setText("");
        });

        btn.setOnAction(e -> {
            String value = user.getValue();
            String bottomText = bottom.getText();
            sendMessage("MESG " + value + " " + bottomText);
            Client.addMessages(Client.getUsername() + ":" + bottomText);
            bottom.setText("");
        });

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(user, bottom, btn);
        root.setBottom(bottomFrame);
        return root;
    }

    public void sendMessage(String msg){
        Client.getWriter().println(msg);
        Client.getWriter().flush();
    }

    public BorderPane setUpRight(String[] names){
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

    public BorderPane setUpCenter(String[] messages){
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

    public BorderPane setUpCenter(ArrayList<String> messages){
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
