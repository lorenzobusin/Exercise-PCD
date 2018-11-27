package merkleServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class merkleServer {
public static void main(String[] args) throws IOException{
    
    ServerSocket listener = new ServerSocket(2323); 
        try {
            while (true) {
                Socket s = listener.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String transaction = input.readLine(); // receive transaction to validate
                
                try {
                   PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                   List<String> nodesToSend = new ArrayList(); // example nodes
                   nodesToSend.add("3");
                   nodesToSend.add("01");
                   nodesToSend.add("4567");
                 
                   nodesToSend.stream().forEach(node -> out.println(node)); // send merkle nodes
                   
                    s.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        
        }catch(IOException e){
            e.printStackTrace();
        }
    listener.close();
}
}