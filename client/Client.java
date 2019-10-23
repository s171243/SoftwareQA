package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client
{

    private static Socket socket;

    public static void main(String args[])
    {
        try
        {
            String host = "localhost";
            int port = 9000;
            Scanner myScanner = new Scanner(System.in);
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            PrintWriter bw = new PrintWriter(osw);

            String number = "LIST";

            //String sendMessage = number + "\n";
            //bw.write(sendMessage);
            //bw.flush();
            //System.out.println("Message sent to the server : " + sendMessage);

            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            //String message = br.readLine();
            //System.out.println("Message received from the server : " + message);

            while(true){
                String msg = myScanner.nextLine();

                if(msg.equals("QUIT")){
                    socket.close();
                    break;
                }
                //String msg =  + "\n";
                bw.println(msg + "\n");
                System.out.println("Message sent to the server : " + msg);
                bw.flush();
                String response = br.readLine();
                System.out.println("Message received from the server : " + response);
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            //Closing the socket
            try
            {
                socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}