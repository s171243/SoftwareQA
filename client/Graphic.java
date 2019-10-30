package client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedList;

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
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(BOTTOM_HEIGHT);

        TextField bottom = new TextField();
        bottom.setPrefWidth(WIDTH - BTN_WIDTH - DROP_WIDTH);
        bottom.setPrefHeight(BOTTOM_HEIGHT);
        bottom.setPromptText("Write your message");

        ComboBox user = new ComboBox();
        user.setPrefWidth(DROP_WIDTH);
        user.setPrefHeight(BOTTOM_HEIGHT);
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

    public BorderPane setUpCenter(LinkedList<String> messages) {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        VBox center = new VBox(10);

        Label heading = new Label("Chat");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(2, 10, 5, 10));

        center.getChildren().add(heading);

        for (String message : messages) {
            Label label = new Label(message);
            label.setPadding(new Insets(2, 10, 2, 10));
            center.getChildren().add(label);
        }

        center.setPadding(new Insets(50, 10, 50, 20));
        scroll.setContent(center);
        scroll.setVvalue(1.0);
        root.setCenter(scroll);
        return root;
    }
}
