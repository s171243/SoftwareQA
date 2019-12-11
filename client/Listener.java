package client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

class Listener extends Thread {

    private final Graphic g;
    private final BufferedReader br;
    private boolean isRunning = true;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }


    // Constructor
    public Listener(BufferedReader br, Graphic g) {
        this.br = br;
        this.g = g;
    }

    private void validateMessage(String msg) {

        // receive
        if (msg.startsWith("Broadcast from")) {
            receive(msg.substring(14));
        }

        // connected
        else if (msg.startsWith("OK Welcome to the chat server,")) {
            connected();
        }

        // receive
        else if (msg.startsWith("PM from")) {
            pm(msg.substring(8));
        }

        //listing
        else if (msg.startsWith("OK, the following users are online")) {
            listing(msg.substring(35));
        } else if (msg.startsWith("OK Welcome to the chat server")) {
            loggedIn(msg.substring(30));
        } else if (msg.startsWith("OK your message has been sent")) {
            sent();
        } else if (msg.startsWith("BAD username is already taken")) {

           Platform.runLater(() -> g.setUpBottom("The username is already taken. Try again!"));

        } else {
            System.out.println("ehh... " + msg);
        }
    }

    private static void receive(String msg) {
        System.out.println("Message received: " + msg);
    }

    private void pm(String msg) {
        msg = msg.replace("$r", "\r").replace("$n", "\n");
        Client.addMessages(msg);
        Platform.runLater(() -> g.setUpCenter(Client.getMessages()));

    }

    private static void connected() {
        System.out.println("We are online");
    }

    private static void sent() {
        System.out.println("Message sent");
    }

    private void listing(String msg) {
        String[] list = msg.split(",");
        list = Arrays.copyOf(list, list.length - 1);
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
        }
        // System.out.println(Arrays.toString(list));
        String[] finalList = list;

        if (!Arrays.equals(list, Client.getUsers())) {
            Platform.runLater(() -> {
                g.setUpBottom(finalList, Client.getUsername());
                g.setUpRight(finalList);
                g.setUpCenter(Client.getMessages());
            });
        }

        if (Client.getMessages().size() != Client.getNumMessages()) {
            Platform.runLater(() -> g.setUpCenter(Client.getMessages()));

            Client.incNumMessages();
        }

        Client.setUsers(finalList);
        // g.setUpCenter(list);
    }

    private void loggedIn(String msg) {
        System.out.println("We are logged in with username " + msg);
        Client.setUsername(msg);
        Client.setLoggedIn(true);
        Platform.runLater(g::topLoggedIn);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String response = br.readLine();
                if (response != null) {
                    validateMessage(response);
                }
                // System.out.println("Server : " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
