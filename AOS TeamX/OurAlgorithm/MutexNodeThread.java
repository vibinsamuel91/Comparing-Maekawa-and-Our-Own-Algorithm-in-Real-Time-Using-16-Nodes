import java.net.*;
import java.io.*;

public class MutexNodeThread extends Thread {  
	private Socket           socket   = null;
   	private MutexNode       node_client   = null;
   	private DataInputStream  d_input = null;

   	public MutexNodeThread(MutexNode _node_client, Socket _socket) {  
		node_client   = _node_client;
    	socket   = _socket;
      	open();  
      	start();
   	}
   public void open() {  
		try {  /* input from server */
			d_input  = new DataInputStream(socket.getInputStream());
      	} 	
		catch(IOException ioe) {  
			System.out.println("Error getting input stream: " + ioe);
        	node_client.stop();
      	  	}
   	}
   public void close() {  
		try {  
			if (d_input != null) d_input.close();
      	}
		catch(IOException ioe) {  
			System.out.println("Error closing input stream: " + ioe);
      	  	}
   	}
   public void run() {  
		while (true) {  
			try {  
				node_client.handle(d_input.readUTF());
         	} 	
			catch(IOException ioe) {  
				System.out.println("Listening error: " + ioe.getMessage());
           		node_client.stop();
       		}
      	}
   	}
}
