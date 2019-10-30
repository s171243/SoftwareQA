package client.test;

import client.Client;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    @BeforeEach
    void setUp() throws InterruptedException {
        JFXPanel fxPanel = new JFXPanel();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        new Client().start(new Stage());
                    }
                });
            }
        });
        thread.start();// Initi alize the thread
        Thread.sleep(10000);

    }

    @AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getWriter() {
        assertThat(Client.getWriter(), instanceOf(PrintWriter.class));
    }

    @org.junit.jupiter.api.Test
    void getMessages() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void addMessages() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void getUsers() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void setUsers() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void getUsername() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void setUsername() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void getNumMessages() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void incNumMessages() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void start() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void setLoggedIn() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void getLoggedIn() {
        fail("Not yet implemented");
    }
}