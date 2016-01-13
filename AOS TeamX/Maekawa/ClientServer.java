import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class ClientServer {
	

   	//private ServerSocket server = null;
   
   	//private int nodeCount = 0;
	public static ArrayList<String> all_Hosts = null;	// will hold all hostnames
	public static ArrayList<String> quorum_Hosts = null; // TEST FOR CLIENT/SERVER
	public static int totalNodes = 0;		// total # of nodes
	public static int PORT = 9977; 
	public static int nodeID = 0;
	
	//private Socket socket              		= null;
   	
   	//private DataInputStream  console   		= null;
   	//private DataOutputStream d_output 		= null;
   	//private MutexNodeThread node_client    	= null;
	MutexServer server = null;  
	
	static Quorum q = new Quorum();

	public ClientServer(int numNodes, int nid) {
		//Quorum q = new Quorum();	
        InetAddress ip;
        String hostname = null;
		server = new MutexServer(PORT);	// create socket	
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();

            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
 
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

		Console cnsl = null;	
		cnsl = System.console();
		
		totalNodes = numNodes;		// set global nodes variable
		nodeID = nid;
		/* list all hostnames */	
		all_Hosts = q.getAllHosts(numNodes);	

		String foo = cnsl.readLine("\"Enter\" starts the program: ");
		
		MutexNode node_client = null;
		for (int b=0; b<all_Hosts.size(); b++) 
			node_client = new MutexNode(all_Hosts.get(b), PORT);
	}
	
//	public static void main(String args[]) {  
//		Quorum q = new Quorum();	
//        InetAddress ip;
//        String hostname = null;
//
//		MutexNode node_client = null;
//		MutexServer server = null;  
//
//
//		server = new MutexServer(PORT);	// create socket	
//
//
//        try {
//            ip = InetAddress.getLocalHost();
//            hostname = ip.getHostName();
//
//            System.out.println("Your current IP address : " + ip);
//            System.out.println("Your current Hostname : " + hostname);
// 
//        } catch (UnknownHostException e) {
// 
//            e.printStackTrace();
//        }
//
//		Console cnsl = null;
//		
//		cnsl = System.console();
//
//		String foo = null;
//
//   	
//
//
//		/* check if # nodes given */		
//   		if (args.length != 1)	
//      		System.out.println("Need number of nodes\n");
//	
//			
//		
//		int nodes = 0;		      					
//		nodes = Integer.parseInt(args[0]); // get # of nodes			
//		totalNodes = nodes;		// set global nodes variable
//
//		/* list all hostnames */	
//		all_Hosts = q.getAllHosts(nodes);	
//
//		foo = cnsl.readLine("\"Enter\" starts the program: ");
//
//		for (int b=0; b<all_Hosts.size(); b++) 
//			node_client = new MutexNode(all_Hosts.get(b), PORT);
//		     	
//   	}


}
