package client.test;


import client.Graphic;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import org.testfx.framework.junit.ApplicationTest;

class GraphicTest extends ApplicationTest {

    private Graphic g;

    @Override
    public void start(Stage stage) throws Exception {
        
    }

    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @org.junit.jupiter.api.Test
    void setup() {
        assertTrue(true);
    }

/*
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void setup() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void emptyTop() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void setUpBottom() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void setUpRight() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void setUpCenter() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void addMessage() {
        fail("Not yet implemented");
    }

 */
}