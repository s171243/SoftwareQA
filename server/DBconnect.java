/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author kek
 */
public class DBconnect {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    
    public DBconnect(){
        try{
            //Class.forName("com.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/chatdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","");
            st = con.createStatement();
        }catch(Exception e){
            System.err.println(e);
        }
    }
    
    public void getChat(){
        try{
            String q = "select * from chathistory;";
            rs = st.executeQuery(q);
            System.out.println("retrieving chat");
            
            while(rs.next()){
                String msg = rs.getString("msg");
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                
                System.err.println(sender+" says "+msg+" to "+receiver);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void saveChat(String msg, String sender, String receiver){
        try{
            String q = "insert into chathistory (msg, sender, receiver)"+
                        "values ( \""+msg+"\" , \""+sender+"\" , \""+receiver+"\");";
            st.executeUpdate(q);
            System.out.println("sending chat");
            
        }catch(Exception e){
            System.out.println(e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) { System.out.println(e);}
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) { System.out.println(e);}
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {System.out.println(e);}
            }
        }
    }
}
