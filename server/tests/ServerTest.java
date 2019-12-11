package server.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    Server s;
    private ServerRunner runner;
    Thread t;

    @BeforeEach
    void initialize(){
        this.runner = new ServerRunner(9000);
        //System.setOut(new PrintStream(outContent));
        // t = new Thread(runner);
        this.runner.start();
    }

    @AfterEach
    void end() throws IOException {
        //this.runner.getServer().quit();
    }

    @Test
    void init() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("Server has been initialised on port 9000", new String(outContent.toByteArray(), Charset.defaultCharset()));
    }

    @Test
    void shouldConnect() throws IOException {
        initialiseClient();
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(outContent.toString().contains("A connection has probably been set up"));
    }

    /*
    
    @Test
    void getUserNumber() throws IOException {
        initialiseClient();
        Assertions.assertEquals(0, runner.getServer().getNumberOfUsers());
    }

     */

    private PrintWriter initialiseClient() throws IOException {
        String host = "localhost";
        int port = 9000;
        InetAddress address = InetAddress.getByName(host);
        Socket socket = new Socket(address, port);

        //Send the message to the server
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        PrintWriter writer = new PrintWriter(osw);

        String sendMessage = "I am connected";
        writer.println(sendMessage);
        writer.flush();
        return writer;
    }


    class ServerRunner extends Thread {

        private final int port;
        private Server server;

        ServerRunner(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            System.setOut(new PrintStream(outContent));
            this.server = new Server(port);
        }

        public Server getServer() {
            return server;
        }

        public void setServer(Server server) {
            this.server = server;
        }
    }
/*
    @Test
    void getUserList() {

    }

    @Test
    void doesUserExist() {
    }

    @Test
    void broadcastMessage() {
    }

    @Test
    void sendPrivateMessage() {
    }

    @Test
    void removeDeadUsers() {
    }

    @Test
    void getNumberOfUsers() {
    }

    @Test
    void testFinalize() {
    }

 */
}