package client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class Listener extends Thread {

    private final Graphic g;
    private final BufferedReader br;
    private String[] lastList = new String[5];
    private boolean isRunning = true;

    // Constructor
    public Listener(BufferedReader br, Graphic g) {
        this.br = br;
        this.g = g;
    }

    private static String connected() {
        System.out.println("We are online");
        return "We are online";
    }

    private static String sent() {
        System.out.println("Message sent");
        return "Message sent";
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String response = br.readLine();
                if (response != null) {
                    validateMessage(response);
                }
            } catch (IOException e) {
                System.out.println("The socket is down....");
                try {
                    Platform.runLater(() -> g.sendErrorMessage("The server is currently down. Please close the window and retry"));
                    sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public String validateMessage(String msg) {

        String res = "";
    	if (msg.startsWith("Broadcast from")) {
        	res = receive(msg.substring(14));
        } else if (msg.startsWith("OK Welcome to the chat server,")) {
        	res = connected();
        } else if (msg.startsWith("PM from")) {
        	res = pm(msg.substring(8));
        } else if (msg.startsWith("OK, the following users are online")) {
            res = listing(msg.substring(35));
        } else if (msg.startsWith("OK Welcome to the chat server")) {
        	res = loggedIn(msg.substring(30));
        } else if (msg.startsWith("OK your message has been sent")) {
        	res = sent();
        } else if (msg.startsWith("BAD username is already taken")) {
        	res = "error duplicate names";
        	Platform.runLater(() -> g.sendErrorMessage("The username is already taken. Try again!"));
        } else {
            System.out.println("ehh... " + msg);
            res = "not recognised";
        }
        
        return res;
    }

    // broadcast
    private String receive(String msg) {
        msg = "*" + msg;
        String finalMsg = msg;
        if(Client.isLoggedIn()) Platform.runLater(() -> g.addMessageToPane(finalMsg));
        return msg;
    }

    private String pm(String msg) {
        msg = msg.replace("$r", "\r").replace("$n", "\n");
        String finalMsg = msg;
        Platform.runLater(() -> g.addMessageToPane(finalMsg));
        return msg;
    }

    private String listing(String msg) {
        String[] list = msg.split(",");
        list = Arrays.copyOf(list, list.length - 1);
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
        }

        String[] finalList = list;

        if (!Arrays.equals(finalList, lastList)) {
            Platform.runLater(() -> {
                g.populateComboBox(finalList, Client.getUsername());
                g.drawUserList(finalList);
            });
        }

        lastList = finalList;
        return msg;
    }

    private String loggedIn(String msg) {
        System.out.println("We are logged in with username " + msg);
        Client.setUsername(msg);
        Client.setLoggedIn(true);
        Platform.runLater(g::topLoggedIn);
        Platform.runLater(g::setUpRight);
        Platform.runLater(g::setUpBottom);
        return msg;
    }

}
