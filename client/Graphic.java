package client;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Graphic {

    private static BorderPane root = new BorderPane();
    private final Stage stage;
    private final int WIDTH = 500;
    private final int BTN_WIDTH = 100;
    private final int BOTTOM_HEIGHT = 100;
    private final int TOP_HEIGHT = 50;
    private final int DROP_WIDTH = 100;

    public Graphic(Stage primaryStage) {
        this.stage = primaryStage;
    }

    void setup() {
        stage.setTitle("Hello World!");

        root = sendErrorMessage("You are not logged in - type your username above.");
        root = setUpTop();
        root = setUpRight();
        root = setUpCenter();

        int HEIGHT = 500;
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }

    public BorderPane sendErrorMessage(String s) {
        Label heading = new Label(s);
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));
        root.setBottom(heading);
        return root;
    }

    private BorderPane setUpTop() {
        Button btn = createButton("Login", TOP_HEIGHT);

        TextField top = createTextField();

        top.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().equals(" ")) {
                sendErrorMessage("Your username should be 1 word - no spaces!");
                change.setText("");
            }
            return change;
        }));

        top.setOnAction(e -> {
            handleMessage(top);
        });

        btn.setOnAction(e -> {
            handleMessage(top);
        });

        HBox topFrame = new HBox(2);
        topFrame.getChildren().addAll(top, btn);
        root.setTop(topFrame);
        return root;
    }

    private Button createButton(String s, int top_height) {
        Button btn = new Button(s);
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(top_height);
        return btn;
    }

    private TextField createTextField() {
        TextField top = new TextField();
        top.setPrefWidth(WIDTH - BTN_WIDTH);
        top.setPrefHeight(TOP_HEIGHT);
        top.setPromptText("What is your username?");
        return top;
    }

    private void handleMessage(TextField top) {
        sendMessage("IDEN " + top.getText());
        top.setText("");
        setUpRight();
    }

    private void sendMessage(String msg) {
        Client.getWriter().println(msg);
        Client.getWriter().flush();
    }

    public BorderPane setUpRight() {
        Label heading = new Label("Online users");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.getChildren().add(heading);
        vbox.setPadding(new Insets(20, 10, 20, 20));
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        root.setRight(vbox);
        return root;
    }

    private BorderPane setUpCenter() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        VBox center = new VBox(10);
        center.setId("center");

        Label heading = new Label("Chat");
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(2, 10, 5, 10));

        center.getChildren().add(heading);

        center.setPadding(new Insets(50, 10, 50, 20));
        scroll.setContent(center);
        scroll.setVvalue(1.0);

        root.setCenter(scroll);
        return root;
    }

    public void topLoggedIn() {
        Label userName = createLabel();
        userName.setPadding(new Insets(10, 10, 10, 10));
        HBox topFrame = new HBox(2);

        Button btn = createButton("Log out", TOP_HEIGHT);

        btn.setOnAction(e -> {
            Client.setLoggedIn(false);
            Client.getWriter().println("QUIT");
            Client.getWriter().flush();
            setUpTop();
            sendErrorMessage("You have been logged out successfully");
            setUpRight();

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

    private Label createLabel() {
        Label userName = new Label("Your username is:   " + Client.getUsername());
        userName.setPrefHeight(TOP_HEIGHT);
        userName.setPrefWidth(WIDTH - BTN_WIDTH);
        return userName;
    }

    public void setUpBottom() {
        //bottom
        Button btn = createButton("Submit", BOTTOM_HEIGHT);

        TextArea bottom = createTextArea();

        ComboBox<String> user = createComboBox();
        user.getItems().add("BROADCAST");

        bottom.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.isControlDown()) {
                handleSubmit(user, bottom);
            }
        });

        btn.setOnAction(e -> handleSubmit(user, bottom));

        HBox bottomFrame = new HBox(2);
        bottomFrame.getChildren().addAll(user, bottom, btn);
        root.setBottom(bottomFrame);
    }

    private TextArea createTextArea() {
        TextArea bottom = new TextArea();
        bottom.setPrefWidth(WIDTH - DROP_WIDTH - BTN_WIDTH);
        bottom.setPrefHeight(BOTTOM_HEIGHT);
        bottom.setPromptText("Write your message");
        return bottom;
    }

    private ComboBox<String> createComboBox() {
        ComboBox<String> user = new ComboBox<>();
        user.setPrefWidth(DROP_WIDTH);
        user.setPrefHeight(BOTTOM_HEIGHT);
        user.setPromptText("Recipient");
        return user;
    }

    private void handleSubmit(ComboBox<String> user, TextArea bottom) {
        String value = user.getValue();
        String bottomText = bottom.getText().trim();

        if (bottomText.length() <= 0) {
            bottom.setText("");
            return;
        }

        if (value.equals("BROADCAST")) {
            bottomText = bottomText.replace("\r", "$r").replace("\n", "$n");
            sendMessage("HAIL" + " " + bottomText);
            bottom.setText("");
            return;
        }

        addMessageToPane(Client.getUsername() + ":" + bottomText);
        bottomText = bottomText.replace("\r", "$r").replace("\n", "$n");
        sendMessage("MESG " + value + " " + bottomText);
        bottom.setText("");
    }

    public void addMessageToPane(String message) {
        Node center = root.getCenter();
        Label label = new Label(message);
        label.setPadding(new Insets(2, 10, 2, 10));
        label.setWrapText(true);
        if (center instanceof ScrollPane) {
            ScrollPane s = (ScrollPane) center;
            Node v = s.getContent();
            if (v instanceof VBox) {
                VBox vBox = (VBox) v;
                vBox.getChildren().add(label);
            }
        }
    }

    public void populateComboBox(String[] list, String username) {
        Node bottom = root.getBottom();
        if (!(bottom instanceof HBox)) {
            return;
        }


        HBox h = (HBox) bottom;
        Node combo = h.getChildren().get(0);
        if (!(combo instanceof ComboBox)) {
            return;
        }
        ComboBox<String> c = (ComboBox<String>) combo;
        c.getItems().remove(1, c.getItems().size());

        for (String recipient : list) {
            if (username.equals(recipient)) {
                continue;
            }
            c.getItems().add(recipient);

            if (list.length != 2) {
                c.setValue(recipient);
            }
        }

        if (list.length == 1) {
            c.setValue("BROADCAST");
        }

    }

    public void drawUserList(String[] users) {
        Node right = root.getRight();
        if (!(right instanceof VBox)) {
            return;
        }

        VBox vbox = (VBox) right;
        if (vbox.getChildren().size() >= 2) {
            vbox.getChildren().remove(1, vbox.getChildren().size());
        }

        if (users.length <= 1) {
            vbox.getChildren().add(new Label("You are alone"));
            return;
        }

        for (String name : users) {
            if (name.equals(Client.getUsername())) {
                continue;
            }

            Label label = new Label(name);
            vbox.getChildren().add(label);
        }

    }

}
