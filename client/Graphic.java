package client;

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

        //right
        String[] names = {"a", "b", "c", "d"};
        root = setUpRight(names);

        // center
        String[] messages = {"Hello, world!", "The world says hello back", "Oh wow - I never tried that!", "Yeah. That's life"};
        root = setUpCenter(Client.getMessages());

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    public BorderPane setUpBottom(){
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(100);
        btn.setPrefHeight(100);

        TextField bottom = new TextField();
        bottom.setPrefWidth(400);
        bottom.setPrefHeight(100);
        bottom.setPromptText("Write your message");


        bottom.setOnAction(e -> {
            sendMessage(bottom.getText());
            bottom.setText("");
        });
        btn.setOnAction(e -> {
            sendMessage(bottom.getText());
            bottom.setText("");
        });

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(bottom, btn);
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
