package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Graphic {

    private final Stage stage;
    private static BorderPane root = new BorderPane();
    private final int WIDTH = 800;
    private final int HEIGHT = 500;
    private final int RIGHT_WIDTH = 200;
    private final int BTN_WIDTH = 100;
    private final int BOTTOM_HEIGHT = 100;
    private final int TOP_HEIGHT = 50;
    private final int DROP_WIDTH = 100;

    public Graphic(Stage primaryStage) {
        this.stage = primaryStage;
    }


    public void setup() {
        stage.setTitle("Hello World!");

        //bottom
        root = setUpBottom();

        //top
        root = setUpTop();

        //right
        String[] names = {"You're not logged in"};
        root = setUpRight(names);

        // center
        root = setUpCenter(Client.getMessages());

        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    private BorderPane setUpTop() {
        //bottom
        Button btn = new Button("Login");
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(TOP_HEIGHT);

        TextField top = new TextField();
        top.setPrefWidth(WIDTH - BTN_WIDTH);
        top.setPrefHeight(TOP_HEIGHT);
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

    public void emptyTop() {
        root.setTop(null);
    }

    private BorderPane setUpBottom() {
        Label heading = new Label("You are not logged in. Please log in above.");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));
        root.setBottom(heading);
        return root;
    }

    public void setUpBottom(String[] list, String username) {
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(BOTTOM_HEIGHT);

        TextField bottom = new TextField();
        bottom.setPrefWidth(WIDTH - DROP_WIDTH - BTN_WIDTH);
        bottom.setPrefHeight(BOTTOM_HEIGHT);
        bottom.setPromptText("Write your message");

        ComboBox<String> user = new ComboBox<>();
        user.setPrefWidth(DROP_WIDTH);
        user.setPrefHeight(BOTTOM_HEIGHT);
        user.setPromptText("Recipient");

        for (String recipient : list) {
            if (!username.equals(recipient)) {
                user.getItems().add(recipient);
                if (list.length == 2) {
                    user.setValue(recipient);
                }
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
    }

    private void sendMessage(String msg) {
        Client.getWriter().println(msg);
        Client.getWriter().flush();
    }

    public BorderPane setUpRight(String[] names) {
        Label heading = new Label("Online users");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.setPrefWidth(RIGHT_WIDTH);
        vbox.getChildren().add(heading);

        for (String name : names) {
            Label label = new Label(name);
            vbox.getChildren().add(label);
        }

        vbox.setPadding(new Insets(20, 10, 20, 20));
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        root.setRight(vbox);
        return root;
    }

    BorderPane setUpCenter(ArrayList<String> messages) {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        VBox center = new VBox(10);

        Label heading = new Label("Chat");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(2, 10, 5, 10));

        center.getChildren().add(heading);

        for (String message : messages) {
            center = addMessage(message, center);
        }

        center.setPadding(new Insets(50, 10, 50, 20));
        scroll.setContent(center);
        scroll.setVvalue(1.0);
        root.setCenter(scroll);
        return root;
    }

    VBox addMessage(String message, VBox center) {
        HBox messageUI = new HBox(2);

        String[] information = message.split(":", 2);
        String username = information[0];
        String text = information[1];

        Label sender = new Label(username);
        sender.setPadding(new Insets(0, 0, 0, 0));
        sender.setPadding(new Insets(2, 0,0,0));
        sender.setPrefWidth(100);
        sender.setAlignment(Pos.BASELINE_RIGHT);
        messageUI.getChildren().add(sender);

        Label label = new Label(text);
        label.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), new CornerRadii(10), Insets.EMPTY)));
        label.setPadding(new Insets(2, 10, 2, 10));
        label.setPrefWidth(WIDTH - RIGHT_WIDTH - 200);
        label.setMaxWidth(WIDTH - RIGHT_WIDTH - 200);
        label.setWrapText(true);

        messageUI.getChildren().add(label);

        messageUI.setAlignment(Pos.BASELINE_LEFT);

        center.getChildren().add(messageUI);
        return center;
    }

}
