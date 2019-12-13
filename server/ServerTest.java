package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {
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
        t = new Thread(runner);
        t.start();
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
        assertEquals(initial + probably + welcome(1), outContent.toString());

    }


    @Test
    void shouldSendBroadcast() throws IOException {
        String username1 = "abc";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        String message = "Hello, world! This is a broadcast";
        sendMessage("HAIL " + message, i);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        "Broadcast from " + username1 + ": " + message + sep + sep
                , outContent.toString());
    }

    @Test
    void shouldNotSendMessageToNonexistentUser() throws IOException {
        String username1 = "abc";
        String username2 = "def";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseAndSendMessage("IDEN " + username2);
        String message = "Hello, world!";
        sendMessage("MESG wow " + message, j);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        probably +
                        welcome(2) +
                        "OK Welcome to the chat server " + username2 + sep +
                        "BAD the user does not exist" + sep
                , outContent.toString());
    }


    @Test
    void shouldNotSendNotLoggedinBroadcast() throws IOException {
        String username1 = "abc";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseClient();
        String message = "Hello, world!";
        sendMessage("HAIL " + message, j);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        probably +
                        welcome(2) +
                        "BAD You have not logged in yet" + sep
                , outContent.toString());
    }


    @Test
    void shouldNotSendNotLoggedinMessage() throws IOException {
        String username1 = "abc";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseClient();
        String message = "Hello, world!";
        sendMessage("MESG abc " + message, j);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        probably +
                        welcome(2) +
                        "BAD You have not logged in yet" + sep
                , outContent.toString());
    }

    @Test
    void shouldNotSendBadlyFormattedMessage() throws IOException {
        String username1 = "abc";
        String username2 = "def";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseAndSendMessage("IDEN " + username2);
        String message = "Hello,world!";
        sendMessage("MESG abc" + message, j);
        assertEquals(initial +
                probably +
                welcome(1) +
                "OK Welcome to the chat server " + username1 + sep +
                probably +
                welcome(2) +
                "OK Welcome to the chat server " + username2 + sep +
                "BAD Your message is badly formatted" + sep, outContent.toString());
    }

    @Test
    void shouldSendMessage() throws IOException {
        String username1 = "abc";
        String username2 = "def";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseAndSendMessage("IDEN " + username2);
        String message = "Hello, world!";
        sendMessage("MESG abc " + message, j);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        probably +
                        welcome(2) +
                        "OK Welcome to the chat server " + username2 + sep +
                        "PM from " + username2 + ":" + message + sep + sep +
                        "OK your message has been sent" + sep
                , outContent.toString());
    }

    @Test
    void shouldNotLoginWithSameUsername() throws IOException {
        String username1 = "abc";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseAndSendMessage("IDEN " + username1);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        probably +
                        welcome(2) +
                        "BAD username is already taken" + sep
                , outContent.toString());
    }

    @Test
    void shouldLoginTwice() throws IOException {
        String username1 = "abc";
        String username2 = "def";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username1);
        PrintWriter j = initialiseAndSendMessage("IDEN " + username2);
        assertEquals(initial +
                        probably +
                        welcome(1) +
                        "OK Welcome to the chat server " + username1 + sep +
                        probably +
                        welcome(2) +
                        "OK Welcome to the chat server " + username2 + sep
                , outContent.toString());
    }

    @Test
    void shouldNotLogInAgain() throws IOException {
        String username = "abc";
        PrintWriter i = initialiseAndSendMessage("IDEN " + username);
        sendMessage("IDEN wow", i);
        assertEquals(initial + probably +
                welcome(1) +
                "OK Welcome to the chat server " + username + sep +
                "BAD you are already registered with username " + username + sep, outContent.toString());
    }

    @Test
    void shouldLogIn() throws IOException {
        String username = "abc";
        initialiseAndSendMessage("IDEN " + username);
        assertEquals(initial + probably +
                welcome(1) +
                "OK Welcome to the chat server " + username + sep, outContent.toString());
    }


    @Test
    void shouldNotLogIn() throws IOException {
        String username = "   ";
        initialiseAndSendMessage("IDEN " + username);
        assertEquals(initial + probably +
                welcome(1) +
                "BAD command not recognised: IDEN" + username + " " + sep, outContent.toString());
    }

    @Test
    void userShouldNotExist() throws IOException {
        String username = "abc";
        initialiseAndSendMessage("IDEN " + username);
        assertEquals(initial + probably +
                welcome(1) +
                "OK Welcome to the chat server " + username + sep, outContent.toString());
        assertFalse(runner.getServer().doesUserExist("bcd"));
    }

    @Test
    void shouldQuit() throws IOException {
        String command = "QUIT";
        initialiseAndSendMessage(command);
        assertEquals(initial + probably +
                welcome(1) + "OK goodbye" + sep, outContent.toString());
    }

    @Test
    void shouldQuitLoggedIn() throws IOException {
        String command = "IDEN abc";
        PrintWriter i = initialiseAndSendMessage(command);
        sendMessage("QUIT", i);
        assertEquals(initial + probably +
                        welcome(1) + "OK Welcome to the chat server abc" + sep +
                        "OK thank you for sending 0 message(s) with the chat service, goodbye. " + sep
                , outContent.toString());
    }

    @Test
    void commandShouldNotWork() throws IOException {
        String command = "abc";
        initialiseAndSendMessage(command);
        assertEquals(initial + probably +
                welcome(1) +
                command.length() + sep + "BAD invalid command to server" + sep, outContent.toString());
    }

    @Test
    void longerCommandShouldNotWork() throws IOException {
        String command = "WOOOOW";
        initialiseAndSendMessage(command);
        assertEquals(initial + probably +
                welcome(1) +
                "BAD command not recognised: " + command + sep, outContent.toString());
    }

    @Test
    void shouldNotShowList() throws IOException {
        String command = "LIST";
        PrintWriter i = initialiseClient();
        sendMessage(command, i);
        assertEquals(initial + probably +
                        welcome(1) +
                        "BAD You have not logged in yet" + sep
                , outContent.toString());
    }

    @Test
    void shouldReturnLoggedinList() throws IOException {
        String command = "LIST";
        PrintWriter i = initialiseAndSendMessage("IDEN abc");
        sendMessage(command, i);
        assertEquals(initial + probably +
                welcome(1) +
                "OK Welcome to the chat server abc" + sep +
                "OK, the following users are online abc, " + sep, outContent.toString());
    }

    @Test
    void shouldReturnStatLoggedIn() throws IOException {
        String command = "STAT";
        PrintWriter i = initialiseAndSendMessage("IDEN abc");
        sendMessage(command, i);
        assertEquals(initial + probably +
                welcome(1) +
                "OK Welcome to the chat server abc" + sep +
                "OK There are currently 1 user(s) on the server" + sep +
                "You are logged in and have sent 0 message(s)" + sep, outContent.toString());
    }

    @Test
    void shouldReturnStat() throws IOException {
        String command = "STAT";
        initialiseAndSendMessage(command);
        assertEquals(initial + probably +
                welcome(1) +
                "OK There are currently 1 user(s) on the server" + sep +
                "You have not logged in yet" + sep, outContent.toString());
    }

    @Test
    void userShouldExist() throws IOException {
        String username = "abc";
        initialiseAndSendMessage("IDEN " + username);
        assertEquals(initial + probably +
                welcome(1) +
                "OK Welcome to the chat server " + username + sep, outContent.toString());
        assertTrue(runner.getServer().doesUserExist(username));
    }

    private PrintWriter initialiseAndSendMessage(String message) throws IOException {
        PrintWriter i = initialiseClient();
        sendMessage(message, i);
        return i;
    }

    private void sendMessage(String message, PrintWriter i) {
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i.println(message);
        i.flush();
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String welcome(int users) {
        return "OK Welcome to the chat server, there are currently "
                + users + " user(s) online" + sep;
    }

    @Test
    void getUserNumber() throws IOException {
        initialiseClient();
        try {
            sleep(400);
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
    }
}