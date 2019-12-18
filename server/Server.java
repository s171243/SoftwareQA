package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;


public class Server {

	public ServerSocket server;
	private ArrayList<Connection> list;
	public boolean isRunning = true;

	public Server (int port) {
		try {
			server = new ServerSocket(port);
			System.out.println("Server has been initialised on port " + port);
		} catch (IOException e) {
			System.err.println("error initialising server");
			e.printStackTrace();
		}
	}

	public void start(){
		list = new ArrayList<Connection>();
		while(isRunning) {
				Connection c = null;
				try {
					c = new Connection(server.accept(), this);
					System.out.println("A connection has probably been set up");
				}
				catch(SocketException e){
					continue;
				}
				catch (IOException e) {
					System.err.println("error setting up new client connection");
					e.printStackTrace();
				}
				Thread t = new Thread(c);
				t.start();
				list.add(c);
		}
	}
	
	public ArrayList<String> getUserList() {
		ArrayList<String> userList = new ArrayList<String>();
		for( Connection clientThread: list){
			if(clientThread.getState() == Connection.STATE_REGISTERED) {
				userList.add(clientThread.getUserName());
			}
		}
		return userList;
	}
	
	public boolean doesUserExist(String newUser) {
		boolean result = false;
		for( Connection clientThread: list){
			if(clientThread.getState() == Connection.STATE_REGISTERED) {
				result = clientThread.getUserName().compareTo(newUser)==0;
			}
		}
		return result;
	}
	
	public void broadcastMessage(String theMessage){
		//System.out.println(theMessage);
		for( Connection clientThread: list){
			clientThread.messageForConnection(theMessage + System.lineSeparator());	
		}
	}
	
	public boolean sendPrivateMessage(String message, String user) {
		for( Connection clientThread: list) {
			if(clientThread.getState() == Connection.STATE_REGISTERED) {
				if(clientThread.getUserName().compareTo(user)==0) {
					clientThread.messageForConnection(message + System.lineSeparator());
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeDeadUsers(){
		list.removeIf(c -> !c.isRunning());
	}
	
	public int getNumberOfUsers() {
		return list.size();
	}

	public void quit() throws IOException{
		this.isRunning = false;
		for(Connection c : list){
			c.setRunning(false);
		}
		server.close();
	}
		
}
