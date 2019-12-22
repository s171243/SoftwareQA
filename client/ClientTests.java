package client.tests;

import client.Client;
import client.Listener;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTests {
    // Client client;
    Thread serverThread;
    Thread clientThread;
    private ClientRunner clientRunner;
    private ServerRunner serverRunner;

    @BeforeEach
    void setUp() {
        JFXPanel fxPanel = new JFXPanel();
        serverRunner = new ServerRunner(9000);
        serverThread = new Thread(serverRunner);
        serverThread.start();

        clientRunner = new ClientRunner();
        clientThread = new Thread(clientRunner);
        clientThread.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientRunner.quitClient();
        serverRunner.quitServer();
        clientRunner.exit();
    }


    @Test
    void testSending() throws IOException {
    	PrintWriter w = clientRunner.getClient().getWriter();
    	w.println("IDEN jack");
    	w.println("MESG jack weewee");
    	w.println("HAIL woowooo");
    	w.println("QUIT");
    }
    @Test
    void validateMessages() throws IOException {
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Listener l = clientRunner.getClient().getListener();

        assertEquals("this is a test broadcast", l.validateMessage("Broadcast from this is a test broadcast"));
        assertEquals("We are online", l.validateMessage("OK Welcome to the chat server, awa"));
        assertEquals("this is a test pm", l.validateMessage("PM from this is a test pm"));
        assertEquals("jack,james,wank", l.validateMessage("OK, the following users are online jack,james,wank"));
        assertEquals("awa", l.validateMessage("OK Welcome to the chat server awa"));
        assertEquals("Message sent", l.validateMessage("OK your message has been sent"));
        assertEquals("error duplicate names", l.validateMessage("OK Welcome to the chat server awa"));
        assertEquals("ehh... random stuff", l.validateMessage("random stuff"));
        
    }

    /*
    @Test
    void logOutButton() throws IOException {
        Platform.runLater(() -> {
            clientRunner.getClient().getGraphic().logOutButton();
            //clientRunner.getClient().getGraphic().logOutButton();
        });
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse(Client.isLoggedIn());
    }
    */

    class ClientRunner implements Runnable {

        private Client client;

        ClientRunner() {
        }

        @Override
        public void run() {
            this.client = new Client();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Stage clientStage = new Stage();
                    client.start(clientStage);
                }
            });
        }

        void quitClient() throws IOException {
            client.getSender().setRunning(false);
            /*Platform.runLater(() -> {
                client.getGraphic().stage.close();
            });*/
        }


        void exit() {
            Platform.exit();
        }

        public Client getClient() {
            return client;
        }
    }

    class ServerRunner implements Runnable {

        private final int port;
        private Server server;

        ServerRunner(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            //System.setOut(new PrintStream(outContent));
            this.server = new Server(port);
            server.start();
        }

        public void quitServer() throws IOException {
            this.server.quit();
        }

        public Server getServer() {
            return server;
        }
    }
}

