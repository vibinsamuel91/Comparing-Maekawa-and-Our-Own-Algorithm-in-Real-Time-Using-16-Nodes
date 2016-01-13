import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class MutexServerThread extends Thread {  
	private MutexServer      server    = null;
   	private Socket           socket    = null;
   	private int              nid        = -1;
   	private DataInputStream  d_input  = null;
   	private DataOutputStream d_output = null;


   	public MutexServerThread(MutexServer _server, Socket _socket, int node_ID)   {  
		super();
      	server = _server;
      	socket = _socket;
     	nid     = node_ID; 		 
   	}
	/* send message */
	public void send(String msg)   {   
		try {  
			d_output.writeUTF(msg); // write to outputstream
          	d_output.flush();
       	} 	
		catch(IOException ioe) {  
			System.out.println(nid + " ERROR sending: " + ioe.getMessage());
       		server.remove(nid);
       		stop();
   	  	}
    }
	/* get nodeID */
	public int get_nid() {
		return nid;
	}

   	public void run() {  
		System.out.println("Connected to node " + nid + "...\n");
      	while (true) {  
			try {  
				server.handle(nid, d_input.readUTF());
         	} 	
			catch(IOException ioe) {  
				System.out.println(nid + " ERROR reading: " + ioe.getMessage());
           		server.remove(nid);
           		stop();
       	  }
      	}
   	}
   	public void open() throws IOException {  
		d_input = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
      	d_output = new DataOutputStream(
						new BufferedOutputStream(socket.getOutputStream()));
   	}
   	public void close() throws IOException {  
		if (socket != null)    socket.close();
      	if (d_input != null)  d_input.close();
      	if (d_output != null) d_output.close();
   	}
}
