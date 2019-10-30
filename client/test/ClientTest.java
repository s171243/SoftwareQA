package client.test;

import client.Client;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import server.Runner;
import server.Server;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    Thread thread;
    Thread server;

    @BeforeEach
    void setUp() throws InterruptedException {
        JFXPanel fxPanel = new JFXPanel();
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        Stage stage = new Stage();
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

    @AfterEach
    void tearDown() {
        server.interrupt();
        thread.interrupt();
    }

    @org.junit.jupiter.api.Test
    void getWriter() {
        assertThat(Client.getWriter(), instanceOf(PrintWriter.class));
    }

    @org.junit.jupiter.api.Test
    void getMessages() {
        Client.addMessages("Test 1");
        Client.addMessages("Test 2");

        assertTrue(Client.getMessages().contains("Test 1"));
        assertTrue(Client.getMessages().contains("Test 2"));
    }

    @org.junit.jupiter.api.Test
    void addMessages() {
        Client.addMessages("Test 1");
        Client.addMessages("Test 2");

        assertTrue(Client.getMessages().contains("Test 1"));
        assertTrue(Client.getMessages().contains("Test 2"));
    }

    @org.junit.jupiter.api.Test
    void getUsers() {
        Client.setUsers(new String[]{"User 1", "User 2"});
        assertTrue(Arrays.asList(Client.getUsers()).contains("User 1"));
        assertTrue(Arrays.asList(Client.getUsers()).contains("User 2"));
    }

    @org.junit.jupiter.api.Test
    void setUsers() {
        Client.setUsers(new String[]{"User 1", "User 2"});
        assertTrue(Arrays.asList(Client.getUsers()).contains("User 1"));
        assertTrue(Arrays.asList(Client.getUsers()).contains("User 2"));
    }

    @org.junit.jupiter.api.Test
    void getUsername() {
        Client.setUsername("abc");
        assertEquals("abc", Client.getUsername());
    }

    @org.junit.jupiter.api.Test
    void setUsername() {
        Client.setUsername("abc");
        assertEquals("abc", Client.getUsername());
    }

    @org.junit.jupiter.api.Test
    void getNumMessages() {
        assertEquals(0, Client.getNumMessages());
        Client.incNumMessages();
        assertEquals(1, Client.getNumMessages());
    }

    @org.junit.jupiter.api.Test
    void incNumMessages() {
        assertEquals(0, Client.getNumMessages());
        Client.incNumMessages();
        assertEquals(1, Client.getNumMessages());
    }

    /*
    @org.junit.jupiter.api.Test
    void start() {
        fail("Not yet implemented");
    }
    */

    @org.junit.jupiter.api.Test
    void setLoggedIn() {
        Client.setLoggedIn(true);
        assertTrue(Client.getLoggedIn());
        Client.setLoggedIn(false);
        assertFalse(Client.getLoggedIn());
    }

    @org.junit.jupiter.api.Test
    void getLoggedIn() {
        Client.setLoggedIn(true);
        assertTrue(Client.getLoggedIn());
        Client.setLoggedIn(false);
        assertFalse(Client.getLoggedIn());
    }
}