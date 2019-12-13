package server.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

class ServerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    Server s;
    Thread t;
    private String sep = System.getProperty("line.separator");
    private String initial = "Server has been initialised on port 9000" + sep;
    private String probably = "A connection has probably been set up" + sep;
    private ServerRunner runner;

    @BeforeEach
    public void initialize() {
        runner = new ServerRunner(9000);
        //System.setOut(new PrintStream(outContent));
        t = new Thread(runner);
        t.start();
        //runner.run();
        //this.runner.start();
        // t = new Thread(runner);
        // t.start();
    }

    @AfterEach
    public void end() throws IOException {
        runner.quitServer();
    }


    @Test
    void init() throws IOException {
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(initial, outContent.toString());
    }

    @Test
    void shouldConnect() throws IOException {
        initialiseClient();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(initial + probably + welcome(runner), outContent.toString());

    }

    @Test
    void shouldLogIn() throws IOException {
        String username = "abc";
        loginWithUsername(username);
        assertEquals(initial + probably +
                welcome(runner) +
                "OK Welcome to the chat server " + username + sep, outContent.toString());
    }

    @Test
    void shouldNotLogIn() throws IOException {
        String username = "   ";
        loginWithUsername(username);
        assertEquals(initial + probably +
                welcome(runner) +
                "BAD command not recognised: IDEN" + username + " " + sep, outContent.toString());
    }


    private void loginWithUsername(String username) throws IOException {
        PrintWriter i = initialiseClient();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i.println("IDEN " + username);
        i.flush();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String welcome(ServerRunner runner) {
        return "OK Welcome to the chat server, there are currently "
                + runner.getServer().getNumberOfUsers() + " user(s) online" + sep;
    }

    @Test
    void getUserNumber() throws IOException {
        initialiseClient();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(1, runner.getServer().getNumberOfUsers());
    }

    private PrintWriter initialiseClient() throws IOException {
        String host = "localhost";
        int port = 9000;
        InetAddress address = InetAddress.getByName(host);
        Socket socket = new Socket(address, port);

        //Send the message to the server
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        PrintWriter writer = new PrintWriter(osw);

        //String sendMessage = "I am connected";
        //writer.println(sendMessage);
        //writer.flush();
        return writer;
    }


    class ServerRunner implements Runnable {

        private final int port;
        private Server server;

        ServerRunner(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            System.setOut(new PrintStream(outContent));
            this.server = new Server(port);
            server.start();
        }

        public void quitServer() throws IOException {
            server.quit();
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