package server;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Runner
{
	static Server server;
	final static int PORT = 9000;
	
	public static void main(String[] args){
		server = new Server(PORT);
		server.start();
	}

}
