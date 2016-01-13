import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class MutexServer implements Runnable {  	

	private MutexServerThread nodes[] = new MutexServerThread[50];
   	private ServerSocket server = null;
   	private Thread thread = null;
   	private int nodeCount = 0;
	public static ArrayList<String> all_Hosts = null;	// will hold all hostnames
	public static ArrayList<String> quorum_Hosts = null; // TEST FOR CLIENT/SERVER
	public static int totalNodes = 0;		// total # of nodes
	public static int PORT = 9988; 
	public static  ArrayList<String> rcvMessage = new ArrayList<String>();
	
   	public MutexServer(int PORT) {  
		try {  
			int n = 0;
			System.out.println("Binding to port " + PORT + "...\n");
        	server = new ServerSocket(PORT);  
        	System.out.println("Server started: " + server);
        	start(); 
		} 
		catch(IOException ioe) {  
			System.out.println("Cannot bind to port " + PORT + ": " + ioe.getMessage()); }
		}
	
	public int getNodeID(Socket socket) {
		int nodeID = 0;
		String host = null;
		host = socket.getInetAddress().getHostName(); 	// get hostname	
		nodeID = ClientServer.all_Hosts.indexOf(host) + 1;	
		return nodeID;
	}	

   	private void addThread(Socket socket) {  
		
		Socket sock = socket;	
		int id = getNodeID(socket);
		String host = socket.getInetAddress().getHostName();
		if (nodeCount < nodes.length) { 	
			id = getNodeID(sock);
			System.out.println("Client accepted: " + host);			
	     	nodes[nodeCount] = new MutexServerThread(this, socket, id); 
        	try {  
				nodes[nodeCount].open(); 
            	nodes[nodeCount].start();  
            	nodeCount++; 
				System.out.println(nodeCount + "\n");
			} 
			catch(IOException ioe){  
				System.out.println("Error opening thread: " + ioe); 
			} 
		} else
        	System.out.println("Client refused: maximum " + nodes.length + " reached.");
   	}

   	public void run() {  
		while (thread != null) {  
			try {  
            	addThread(server.accept()); 
			} 
			catch(IOException ioe) {  
				System.out.println("Server accept error: " + ioe); stop(); }
        	}
   	}
	/* start node thread */
   	public void start()  { 
		if (thread == null){  
			thread = new Thread(this); 
        	thread.start();
      	} 
	}
	/* stop node thread */
   	public void stop()   {  
		if (thread != null)   {  
			thread.stop(); 
        	thread = null;
    	} 
	}
	/* finds nodeID for msg sending and socket closure */
   	public int findNode(int nid) {  
		for (int i = 0; i < nodeCount; i++)  
			if((nodes[i].get_nid() + 1) == nid)
	            return i;
      return -1;
   	}

   	public synchronized void handle(int nid, String input) {  
		
		int n=0;		
		Scanner msg = new Scanner (input);
			/* parse msg */
		msg.useDelimiter(";");				
		String to_node = msg.next();		/* destination node */
		String from_node = msg.next();			/* TEST PARSING */ 
		n = Integer.parseInt(to_node) - 1;		/* String to int */ 
		
		String fullMsg = to_node+";"+from_node;
		if (from_node.equals("-1")) {		
			//nodes[findNode(nid)].send(from_node);
			nodes[findNode(nid)].send(fullMsg);
			remove(nid);
		} 
		else {
			//nodes[n].send(from_node);
			nodes[n].send(fullMsg);
		}
		msg.close();
   	}

   	public synchronized void remove(int nid){  
		int pos = findNode(nid);
		MutexServerThread toTerminate = nodes[pos];
         	System.out.println("Removing node_client thread " + nid + "\n");
			if (pos < nodeCount-1) {
				for (int i = pos+1; i < nodeCount; i++) {
    				try {  
						toTerminate.close(); 
					}	 
					catch(IOException ioe) {  
						System.out.println("Error closing thread: " + ioe); 
					}
       				toTerminate.stop();
				} 
			}	
   	}

/*   	public static void main(String args[]) { 
		
			
   		if (args.length != 1)	
       		System.out.println("Need number of nodes\n");
	
		Quorum q = new Quorum();		
		
		int nodes = 0;		      					
		nodes = Integer.parseInt(args[0]); // get # of nodes			
		totalNodes = nodes;		// set global nodes variable

		all_Hosts = q.getAllHosts(nodes);	

		MutexServer server = null;
       	server = new MutexServer(PORT);	// create socket	
   	}
*/
}

