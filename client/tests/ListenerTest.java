package client.tests;


import client.Client;
import client.Graphic;
import client.Listener;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.Runner;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ListenerTest {
    Thread thread;
    Thread server;
    private Stage stage;

    @AfterEach
    void tearDown() {
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        JFXPanel fxPanel = new JFXPanel();


        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        stage = new Stage();
                        new Client().start(stage);
                    }
                });
            }
        });

        server = new Thread(new Runnable() {
            @Override
            public void run() {
                Runner.main(new String[]{""});
            }
        });
        thread.setDaemon(true);
        server.setDaemon(true);
        server.start();
        thread.start();
        Thread.sleep(10000);
    }

    @org.junit.jupiter.api.Test
    void validateMessage() throws IOException {
        String host = "localhost";
        int port = 9000;
        InetAddress address = null;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Socket socket = new Socket(address, port);

        //Send the message to the server
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Listener l = new Listener(br, new Graphic(stage));
        /*assertEquals(l.validateMessage("OK, the following users are online christian"), "list");
        assertEquals(l.validateMessage("OK Welcome to the chat server abc"), "logged in");
        assertEquals(l.validateMessage("OK your message has been sent"), "sent");
        assertEquals(l.validateMessage(""), "");
        assertEquals(l.validateMessage("Broadcast from abc"), "broadcast");
        assertEquals(l.validateMessage("OK Welcome to the chat server, james"), "welcome");
        assertEquals(l.validateMessage("PM from abc"), "pm");*/
    }
}

