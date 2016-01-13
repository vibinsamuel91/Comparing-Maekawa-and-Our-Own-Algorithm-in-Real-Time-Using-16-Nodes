import java.net.*;
import java.io.*;

public class MutexNode implements Runnable {  
	private Socket socket              = null;
   	private Thread thread              = null;
   	private DataInputStream  console   = null;
   	private DataOutputStream d_output = null;
   	private MutexNodeThread node_client    = null;
	public  static int PORT = 9977; 

	static Quorum q = new Quorum();

   	public MutexNode(String serverName, int serverPort) {  
		System.out.println("Establishing connection. Please wait ...");
      	try {  
			socket = new Socket(serverName, serverPort);
        	System.out.println("Connected: " + socket);
         	start();
      	} 	
		catch(UnknownHostException uhe) {  
			System.out.println("Host unknown: " + uhe.getMessage()); 
	  	} 	
		catch(IOException ioe) {  
			System.out.println("Unexpected exception: " + ioe.getMessage()); 
		}
   	}
   	public void run()   {  
		while (thread != null) {  
			try {  
				d_output.writeUTF(console.readLine());
            	d_output.flush();
         	} 	
			catch(IOException ioe) {  
				System.out.println("Sending error: " + ioe.getMessage());
           		stop();
           }
      	}
   	}
   	public void handle(String msg) {  
		if (msg.equals("-1")) {  
			System.out.println("Good bye. Press RETURN to exit ...");
         	stop();
      	} else {
      		System.out.println(msg);
      		MutexServer.rcvMessage = msg;
      	}
   	}
	/* send messages format should be "destinationNode;message" */
   	public void start() throws IOException {  
		console   = new DataInputStream(System.in); // input from console FOR TESTING
      	d_output = new DataOutputStream(socket.getOutputStream()); /* to server */
      	if (thread == null) {  
			node_client = new MutexNodeThread(this, socket);
         	thread = new Thread(this);                   
         	thread.start();
      	}
   	}
   	public void stop() {  
		if (thread != null) {  
			thread.stop();  
         	thread = null;
      	} 
		try {  
			if (console   != null)  console.close();
         	if (d_output != null)  d_output.close();
         	if (socket    != null)  socket.close();
      	} 	
		catch(IOException ioe) {  
			System.out.println("Error closing ..."); 
	  	}
      	node_client.close();  
      	node_client.stop();
   	}
   	public static void main(String args[]) {  
		MutexNode node_client = null;
      	if (args.length != 1)
         	System.out.println("Usage: java MutexNode host port");
      	else
         	node_client = new MutexNode(args[0], PORT);

   	}
}
