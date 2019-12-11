package client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.Key;
import java.util.LinkedList;

// import static jdk.internal.org.jline.terminal.Terminal.MouseTracking.Button;

class Graphic {

    private final Stage stage;
    private static BorderPane root = new BorderPane();
    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    private final int BTN_WIDTH = 100;
    private final int BOTTOM_HEIGHT = 100;
    private final int TOP_HEIGHT = 50;
    private final int DROP_WIDTH = 100;


    Graphic(Stage primaryStage) {
        this.stage = primaryStage;
    }


    void setup() {
        stage.setTitle("Hello World!");

        //bottom
        root = setUpBottom("You are not logged in - type your username above.");

        //top
        root = setUpTop();

        //right
        String[] names = {"You're not logged in"};
        root = setUpRight(names);

        // center
        root = setUpCenter();

        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }

    private BorderPane setUpTop() {
        //bottom
        Button btn = new Button("Login");
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(TOP_HEIGHT);

        TextField top = new TextField();

        top.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().equals(" ")) {
                setUpBottom("Your username should be 1 word - no spaces!");
                change.setText("");
            }
            return change;
        }));

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

        HBox topFrame = new HBox(2);
        topFrame.getChildren().addAll(top, btn);
        root.setTop(topFrame);
        return root;
    }

    public void topLoggedIn() {
        Label userName = new Label("Your username is:   " + Client.getUsername());
        userName.setPadding(new Insets(10, 10, 10, 10));
        HBox topFrame = new HBox(2);

        Button btn = new Button("Submit");
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(TOP_HEIGHT);

        btn.setOnAction(e -> {
            Client.setLoggedIn(false);
            Client.getWriter().println("QUIT");
            Client.getWriter().flush();
            setUpTop();
            setUpBottom("You have been logged out succesfully");
            setUpRight(Client.getUsers());

            Client.setUsername("");
            try {
                Client.restartConnection(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        topFrame.getChildren().addAll(userName, btn);
        root.setTop(topFrame);
    }

    public BorderPane setUpBottom(String s) {
        Label heading = new Label(s);
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

        TextArea bottom = new TextArea();
        bottom.setPrefWidth(WIDTH - DROP_WIDTH - BTN_WIDTH);
        bottom.setPrefHeight(BOTTOM_HEIGHT);
        bottom.setPromptText("Write your message");

        ComboBox<String> user = new ComboBox<>();
        user.setPrefWidth(DROP_WIDTH);
        user.setPrefHeight(BOTTOM_HEIGHT);
        user.setPromptText("Recipient");
        user.getItems().add("BROADCAST");

        for (String recipient : list) {
            if (!username.equals(recipient)) {
                user.getItems().add(recipient);
                if (list.length == 2) {
                    user.setValue(recipient);
                }
            }
        }

        bottom.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.isControlDown()) {
                    handleSubmit(user, bottom);
                }
            }
        });

        btn.setOnAction(e -> {
            handleSubmit(user, bottom);
        });

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(user, bottom, btn);
        root.setBottom(bottomFrame);
    }

    private void handleSubmit(ComboBox<String> user, TextArea bottom) {
        String value = user.getValue();
        String bottomText = bottom.getText().trim();
        if (bottomText.length() > 0) {
            if(value.equals("BROADCAST")){
                bottomText = bottomText.replace("\r", "$r").replace("\n", "$n");
                sendMessage("HAIL" + " " + bottomText);
            } else {
                addMessageToPane(Client.getUsername() + ":" + bottomText);
                bottomText = bottomText.replace("\r", "$r").replace("\n", "$n");
                sendMessage("MESG " + value + " " + bottomText);
            }
            bottom.setText("");
        } else {
            bottom.setText("");
        }
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

    public void addMessageToPane(String message){
        Node center = root.getCenter();
        Label label = new Label(message);
        label.setPadding(new Insets(2, 10, 2, 10));
        label.setWrapText(true);
        if(center instanceof ScrollPane){
            System.out.println("IT is a ScrollPane!!!");
            ScrollPane s = (ScrollPane) center;
            Node v = s.getContent();
            if (v instanceof VBox){
                System.out.println("IT is a VBOX!!!");
                VBox vBox = (VBox) v;
                vBox.getChildren().add(label);
            }
            //s.getContent();
        }
    }

    public BorderPane setUpCenter() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        VBox center = new VBox(10);
        center.setId("center");
        Label heading = new Label("Chat");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(2, 10, 5, 10));

        center.getChildren().add(heading);

        /*
        for (String message : messages) {
            Label label = new Label(message);
            label.setPadding(new Insets(2, 10, 2, 10));
            label.setWrapText(true);
            center.getChildren().add(label);
        }
        */
        center.setPadding(new Insets(50, 10, 50, 20));
        scroll.setContent(center);
        scroll.setVvalue(1.0);
        root.setCenter(scroll);
        return root;
    }
}
