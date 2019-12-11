package client;

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
        root = sendErrorMessage("You are not logged in - type your username above.");

        //top
        root = setUpTop();

        //right
        String[] names = {"You're not logged in"};
        root = setUpRight();

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
                sendErrorMessage("Your username should be 1 word - no spaces!");
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
            setUpRight();
        });

        btn.setOnAction(e -> {
            sendMessage("IDEN " + top.getText());
            top.setText("");
            setUpRight();
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
            sendErrorMessage("You have been logged out succesfully");
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

    public void addUserDropdown(String user) {

    }

    public BorderPane sendErrorMessage(String s) {
        Label heading = new Label(s);
        heading.setStyle("-fx-font-weight: bold");
        heading.setPadding(new Insets(10, 10, 10, 10));
        root.setBottom(heading);
        return root;
    }


    public void setUpBottom() {
        //bottom
        Button btn = new Button("Submit");
        btn.setPrefWidth(BTN_WIDTH);
        btn.setPrefHeight(BOTTOM_HEIGHT);

        TextArea bottom = new TextArea();
        bottom.setPrefWidth(WIDTH - DROP_WIDTH - BTN_WIDTH);
        bottom.setPrefHeight(BOTTOM_HEIGHT);
        bottom.setPromptText("Write your message");

        ComboBox<String> user = new ComboBox<String>();
        user.setPrefWidth(DROP_WIDTH);
        user.setPrefHeight(BOTTOM_HEIGHT);
        user.setPromptText("Recipient");
        user.getItems().add("BROADCAST");

        //user = populateComboBox(list, username, user);

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

    public void populateComboBox(String[] list, String username) {
        Node bottom = root.getBottom();
        if (bottom instanceof HBox) {
            HBox h = (HBox) bottom;
            Node combo = h.getChildren().get(0);
            if (combo instanceof ComboBox){
                System.out.println("It IS a COMBOBOX!!!");
                ComboBox<String> c = (ComboBox<String>) combo;
                c.getItems().remove(1, c.getItems().size());
                c.setPrefWidth(DROP_WIDTH);
                c.setPrefHeight(BOTTOM_HEIGHT);

                for (String recipient : list) {
                    if (!username.equals(recipient)) {
                        c.getItems().add(recipient);
                        if (list.length == 2) {
                            c.setValue(recipient);
                        }
                    }
                }
            }
        }
    }

    private void handleSubmit(ComboBox<String> user, TextArea bottom) {
        String value = user.getValue();
        String bottomText = bottom.getText().trim();
        if (bottomText.length() > 0) {
            if (value.equals("BROADCAST")) {
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

    public void drawUserList(String[] users) {
        Node right = root.getRight();
            if (right instanceof VBox) {
                VBox vbox = (VBox) right;
                for (String na : users) {
                    if (vbox.getChildren().size() >= 2) {
                        vbox.getChildren().remove(1, vbox.getChildren().size());
                    }
                }
                for (String name : users) {
                    Label label = new Label(name);
                    vbox.getChildren().add(label);
                }
        }
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

    public void addMessageToPane(String message) {
        Node center = root.getCenter();
        Label label = new Label(message);
        label.setPadding(new Insets(2, 10, 2, 10));
        label.setWrapText(true);
        if (center instanceof ScrollPane) {
            System.out.println("IT is a ScrollPane!!!");
            ScrollPane s = (ScrollPane) center;
            Node v = s.getContent();
            if (v instanceof VBox) {
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
