package client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class Listener extends Thread {

    private final Graphic g;
    private final BufferedReader br;

    // Constructor
    public Listener(BufferedReader br, Graphic g) {
        this.br = br;
        this.g = g;
    }

    public String validateMessage(String msg) {
        // receive
        if (msg.startsWith("Broadcast from")) {
            receive(msg.substring(14));
            return "broadcast";
        }

        // connected
        else if (msg.startsWith("OK Welcome to the chat server,")) {
            connected();
            return "welcome";
        }

        // receive
        else if (msg.startsWith("PM from")) {
            pm(msg.substring(8));
            return "pm";
        }

        //listing
        else if (msg.startsWith("OK, the following users are online")) {
            listing(msg.substring(35));
            return "list";
        } else if (msg.startsWith("OK Welcome to the chat server")) {
            loggedIn(msg.substring(30));
            return "logged in";
        } else if (msg.startsWith("OK your message has been sent")) {
            sent();
            return "sent";
        } else if (msg.equals("")) {
            return "";
        } else {
            System.out.println("ehh... " + msg);
            return "error";
        }
    }

    private static void receive(String msg) {
        System.out.println("Message received: " + msg);
    }

    private void pm(String msg) {
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
        Platform.runLater(g::emptyTop);
    }

    @Override
    public void run() {
        while (this.isAlive()) {
            try {
                String response = br.readLine();
                validateMessage(response);
                // System.out.println("Server : " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
