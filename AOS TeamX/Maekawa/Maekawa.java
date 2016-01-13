import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.net.*;
import java.io.*;

public class Maekawa {
	static String logPath;
	static String mutexFile;
	static String logFile;
	static String syncFile;

	private static int seqNumber;
	private static int nodeID;
	private static int noOfNodes;
	private static int criticalSectionDelay;

	private static int inSeqNumber;
	private static int inNodeID;

	private static int highestSeqNumber;

	private static ArrayList<Integer> nodeQuorum;
	private static ArrayList<Message> requestQueue;
	private static ArrayList<Integer> sentRequest;

	private static boolean using;
	private static boolean nodeLocked;
	static int outstandingLocked;
	// boolean sentRequest[];
	static boolean failed;
	static boolean inquired;

	static String startSynchronizer = "Node 1 has started";
	static String terminationSynchronizer = "Request Complete";
	static ArrayList<Integer> timestamps = new ArrayList<Integer>();
	int counter = 0;
	public static boolean initiateTermination = false;
	public static boolean terminationNode = false;
	static SendOutRequests sendOutMyRequests;
	static ReceiveMessages receiveMyMessages;
	Timer timer;
	Timer rcvTimer;
	static ClientServer mySockets;
	public static int PORT = 9977;
	public static ArrayList<String> all_Hosts = null;
	public static ArrayList<String> receivedMessages = null;

	public Maekawa() {
	}

	private void initialize() {
		noOfNodes = 0;
		seqNumber = 0;
		nodeID = 0;
		inSeqNumber = 0;
		inNodeID = 0;
		criticalSectionDelay = 0;
		nodeQuorum = new ArrayList<Integer>();
		requestQueue = new ArrayList<Message>();
		sentRequest = new ArrayList<Integer>();
		using = false;
		nodeLocked = false;
		outstandingLocked = 0;
		failed = false;
		inquired = false;
		highestSeqNumber = 0;
		sendOutMyRequests = new SendOutRequests();
		receiveMyMessages = new ReceiveMessages();
	}

	private static void read(String msg) {
		/*if(using){
			try {
				TimeUnit.SECONDS.sleep(criticalSectionDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in read() timer.");
			}
		}*/
		if (msg.toLowerCase().contains("request"))
			receiveRequest(msg);
		if (msg.toLowerCase().contains("locked"))
			receiveLocked(msg);
		if (msg.toLowerCase().contains("inquire"))
			receiveInquire(msg);
		if (msg.toLowerCase().contains("failed"))
			receiveFailed(msg);
		if (msg.toLowerCase().contains("relinquish"))
			receiveRelinquish(msg);
		if (msg.toLowerCase().contains("release"))
			receiveRelease(msg);
	}

	private static void sendSocketMessage(String msg, int nodeTo) {
		try {
			int nid = mySockets.server.findNode(nodeID);
			TimeUnit.SECONDS.sleep(1);
			mySockets.server.handle(nid, Integer.toString(nodeTo) + ";" + msg);
			//mySockets.node.handle(Integer.toString(nodeTo) + ";" + msg);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private static void receiveSocketMessage() {
		try {
			if (mySockets.server.rcvMessage != null
					&& !mySockets.server.rcvMessage.isEmpty()) {
				System.out.println("rcvMessage is "
						+ mySockets.server.rcvMessage);
				String newMsg = mySockets.server.rcvMessage.remove(0);
				read(newMsg);
			}
		} catch (Exception e) {
			System.out.println("Error reading from socket");
		}
	}

	private static void write(String msg, int i, String logMsg) {
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		myReader.writeLine(logMsg, logFile, true);
		sendSocketMessage(msg, i);
	}
	
    public static void receiveRequest(String msg) {
    	System.out.println("receiveRequest " + msg);
        inSeqNumber = Integer.parseInt(msg.split(" ")[1]);
        inNodeID = Integer.parseInt(msg.split(" ")[2]);
       
        Message a1 = new Message(inSeqNumber, inNodeID);
        highestSeqNumber = Math.max(inSeqNumber, seqNumber);

        if (!nodeLocked && requestQueue.isEmpty()) {
               String logMsg = "Node " + nodeID + " is sending LOCKED message to " + inNodeID;
               write("LOCKED " + nodeID, inNodeID, logMsg);
               nodeLocked = true;
               requestQueue.add(a1);
        } else if(!requestQueue.isEmpty()){
               if(nodeLocked){
                     Message a = requestQueue.get(0);
                     if (a.getSeqNum() < inSeqNumber || (a.getSeqNum() == inSeqNumber && a.getNodeID() < inNodeID)){
                            String logMsg = "Node " + nodeID + " is sending FAILED message to " + inNodeID;
                            write("FAILED " + nodeID, inNodeID, logMsg); 
                            boolean added = false;
                            for (int i = 0; i < requestQueue.size(); i++) {
                                  Message a2 = requestQueue.get(i);
                                  if (!added && (a2.getSeqNum() > inSeqNumber
                                               || (a2.getSeqNum() == inSeqNumber && a2.getNodeID() > inNodeID))) {
                                         requestQueue.add(i, a1);
                                         added = true;
                                         break;
                                  }
                            }
                            if (!added)
                                  requestQueue.add(a1);
                     } else{
                            String logMsg = "Node " + nodeID + " is sending INQUIRE message to " + a.getNodeID();
                            write("INQUIRE " + nodeID, a.getNodeID(), logMsg);
                            requestQueue.add(1, a1);
                     }
               }else{
                     Message a = requestQueue.get(0);
                     if (a.getSeqNum() < inSeqNumber || (a.getSeqNum() == inSeqNumber && a.getNodeID() < inNodeID)){
                            String logMsg = "Node " + nodeID + " is sending FAILED message to " + inNodeID;
                            write("FAILED " + nodeID, inNodeID, logMsg); 
                            boolean added = false;
                            for (int i = 0; i < requestQueue.size(); i++) {
                                  Message a2 = requestQueue.get(i);
                                  if (!added && (a2.getSeqNum() > inSeqNumber
                                               || (a2.getSeqNum() == inSeqNumber && a2.getNodeID() > inNodeID))) {
                                         requestQueue.add(i, a1);
                                         added = true;
                                         break;
                                  }
                            }
                            if (!added)
                                  requestQueue.add(a1);
                     } else{
                            String logMsg = "Node " + nodeID + " is sending LOCKED message to " + inNodeID;
                            write("LOCKED " + nodeID, inNodeID, logMsg);
                            nodeLocked = true;
                            requestQueue.add(0, a1);
                     }                        
               }
        }
  }


	private static void receiveLocked(String msg) {
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		inNodeID = Integer.parseInt(msg.split(" ")[1]);
		outstandingLocked -= 1;
		if (outstandingLocked == 0) {
			using = true;
			String temp = "Node " + nodeID
					+ " is entering CRITICAL_SECTION now";
			System.out.println(temp);
			myReader.writeLine(temp, mutexFile, true);
			try {
				TimeUnit.SECONDS.sleep(criticalSectionDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in Critical Section");
			}
			// critical section
			temp = "Node " + nodeID + " is exiting CRITICAL_SECTION now";
			System.out.println(temp);
			myReader.writeLine(temp, mutexFile, true);
			if (terminationNode && timestamps.isEmpty()) {
				myReader.writeLine(terminationSynchronizer, syncFile, false);
				initiateTermination = true;
			} else if (timestamps.isEmpty()) {
				initiateTermination = true;
			}
			for(Message m : requestQueue){
					System.out.println("RQ : " + m.getNodeID());
			}
			requestQueue.remove(0);
			using = false;
			outstandingLocked = nodeQuorum.size() - 1;
			failed = false;
			nodeLocked = false;
			sentRequest = new ArrayList<Integer>();
			for (int i = 0; i < nodeQuorum.size(); i++) {
				if (nodeQuorum.get(i) != nodeID) {
					String logMsg = "Node " + nodeID
							+ " is sending RELEASE message to "
							+ nodeQuorum.get(i);
					write("RELEASE " + nodeID, nodeQuorum.get(i), logMsg);
				}
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in Recieve Locked.");
			}
			if (!requestQueue.isEmpty()) {
				Message a = requestQueue.get(0);
				String logMsg = "Node " + nodeID
						+ " is sending LOCKED message to " + a.getNodeID();
				write("LOCKED " + nodeID, a.getNodeID(), logMsg);
				nodeLocked = true;
			}
		}
		// send release message
	}

	private static void receiveInquire(String msg) {
		//inSeqNumber = Integer.parseInt(msg.split(" ")[1]);
		inNodeID = Integer.parseInt(msg.split(" ")[1]);
		if (failed) {
			String logMsg = "Node " + nodeID
					+ " is sending RELINQUISH message to " + inNodeID;
			write("RELINQUISH " + nodeID, inNodeID, logMsg);
		}
	}

	private static void receiveFailed(String msg) {
		inNodeID = Integer.parseInt(msg.split(" ")[1]);
		if (!failed && inquired) {
			for (int i = 0; i < nodeQuorum.size(); i++)
				if (nodeQuorum.get(i) != nodeID
				// && nodeQuorum.get(i) != inNodeID
						&& sent(nodeQuorum.get(i))) {
					String logMsg = "Node " + nodeID
							+ " is sending RELINQUISH message to "
							+ nodeQuorum.get(i);
					write("RELINQUISH " + nodeID, nodeQuorum.get(i), logMsg);
				}
			failed = true;
		}
	}

	private static boolean sent(int node) {
		for (int i = 0; i < sentRequest.size(); i++) {
			if (node == sentRequest.get(i))
				return true;
			;
		}
		return false;
	}

	private static void receiveRelinquish(String msg) {
		Collections.swap(requestQueue, 0, 1);

		Message a = requestQueue.get(0);
		// requestQueue.remove();
		Message a1 = requestQueue.get(1);
		// requestQueue.addFirst(a);
		// requestQueue.addFirst(a1);

		String logMsg = "Node " + nodeID + " is sending LOCKED message to "
				+ a.getNodeID();
		write("LOCKED " + nodeID, a.getNodeID(), logMsg);
		nodeLocked = true;
	}

	private static void receiveRelease(String msg) {
		inNodeID = Integer.parseInt(msg.split(" ")[1]);
		for (int i = 0; i < requestQueue.size(); i++) {
			Message a = requestQueue.get(i);
			if (inNodeID == a.getNodeID()) {
				requestQueue.remove(i);
				nodeLocked = false;
				if (!requestQueue.isEmpty()) {
					a = requestQueue.get(0);
					if (nodeID != a.getNodeID()) {
						String logMsg = "Node " + nodeID
								+ " is sending LOCKED message to "
								+ a.getNodeID();
						write("LOCKED " + nodeID, a.getNodeID(), logMsg);
						nodeLocked = true;
					}
				}
				break;
			}
		}
	}

	public void run(String[] args) {
		nodeID = Integer.parseInt(args[0]);

		noOfNodes = Integer.parseInt(args[1]);
		// set critical section delay (in seconds)
		criticalSectionDelay = Integer.parseInt(args[2]);
		QuorumAssignment q = new QuorumAssignment(noOfNodes);
		q.createQuorums();
		// quorum.printQuorums();
		mySockets = new ClientServer(Integer.parseInt(args[1]),
				Integer.parseInt(args[0]));

		nodeQuorum = q.getQuorum(nodeID - 1);
		Collections.sort(nodeQuorum);
		// all_Hosts = q.getAllHosts(noOfNodes);
		outstandingLocked = nodeQuorum.size() - 1;
		logPath = args[3] + "//";
		readConfigFile(logPath + args[4]);
		mutexFile = logPath + args[5];
		logFile = logPath + args[6];
		syncFile = logPath + args[7];
		ReadWriteHelperClass myFile = new ReadWriteHelperClass();
		myFile.zeroOutFile(mutexFile);
		myFile.zeroOutFile(logFile);
		myFile.zeroOutFile(syncFile);

	}

	private static void readConfigFile(String configFile) {

		ArrayList<String> aLine = new ArrayList<>();
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		aLine = myReader.readFile(configFile);
		for (int i = 0; i < aLine.size(); i++) {
			String sLine = aLine.get(i);
			String[] str_array = sLine.split(",");
			terminationNode = Integer.parseInt(str_array[1]) == nodeID
					&& sLine.indexOf('|') > 0;
			if (Integer.parseInt(str_array[1]) == nodeID) {
				timestamps.add(Integer.parseInt(str_array[0]));
			}
		}
		if (!timestamps.isEmpty()) {
			Collections.sort(timestamps);
		}
	}

	public static void main(String[] args) {

		try {
			Maekawa m = new Maekawa();
			ReadWriteHelperClass myReader = new ReadWriteHelperClass();
			String sLine = null;
			m.initialize();
			m.run(args);

			// start synchronization
			// TODO: IF NET01 IS KICKED OFF FIRST THEN BELOW CONDITION WILL
			// CHANGE TO
			// if(nodeID != noOfNodes) TO START AFTER LAST NODE IS RUNNING
			if (nodeID != noOfNodes) {
				do {
					sLine = myReader.readLine(syncFile);
					if (sLine == null)
						continue;
					if (sLine.contains(startSynchronizer))
						break; // start program
				} while (1 == 1);
			} else {
				myReader.writeLine(startSynchronizer, syncFile, false);
			}
			TimeUnit.MILLISECONDS.sleep(nodeID*33);
			// System.out.println("Start synchronization complete"); //test
			sendOutMyRequests.startTimer();
			receiveMyMessages.startTimer();
			do {
				if (initiateTermination) {
					sLine = myReader.readLine(syncFile);
					if (sLine == null)
						continue;
					if (sLine.contains(terminationSynchronizer)) {
						receiveMyMessages.stopTimer();
						System.out.println("Node : " + nodeID
								+ " Termination Complete.");
						break;
					}
				}
				TimeUnit.SECONDS.sleep(2);
			} while (1 == 1);
			// TODO close mySockets
			mySockets.server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	class SendOutRequests extends TimerTask {
		public SendOutRequests() {
		}

		public void run() {
			counter++;
			if (!timestamps.isEmpty()) {
				if (counter == timestamps.get(0)) {
					int ts = timestamps.get(0);
					timestamps.remove(0);

					seqNumber = ++highestSeqNumber;
					Message a = new Message(seqNumber, nodeID);
					ReadWriteHelperClass writer = new ReadWriteHelperClass();

					requestQueue.add(a);
					String msg = "REQUEST " + a.getSeqNum() + " "
							+ a.getNodeID();
					for (int i = 0; i < nodeQuorum.size(); i++)
						if (nodeID != nodeQuorum.get(i)) {
							String logMsg = "Node " + nodeID
									+ " is sending REQUEST message to "
									+ nodeQuorum.get(i);
							write(msg, nodeQuorum.get(i), logMsg);
							sentRequest.add(nodeQuorum.get(i));
						}

					writer.writeLine(msg, logFile, true);
				}
			} else {
				// System.out.println("All Request Messages have been sent!");
				ReadWriteHelperClass writer = new ReadWriteHelperClass();
				String sLine = "Node: " + nodeID
						+ " Request Messages have been sent!";
				writer.writeLine(sLine, logFile, true);
				if (!terminationNode) {
					initiateTermination = true;
				}
				timer.cancel();
			}
		}

		private void startTimer() {
			timer = new Timer();
			timer.schedule(new SendOutRequests(), 0, 10 * 1000);
		}
	}

	class ReceiveMessages extends TimerTask {
		public ReceiveMessages() {
		}

		public void run() {
			if (mySockets.server.rcvMessage != null
					&& !mySockets.server.rcvMessage.isEmpty()) {
				System.out.println("rcvMessage is "
						+ mySockets.server.rcvMessage);
				String newMsg = mySockets.server.rcvMessage.remove(0);
				read(newMsg);
			}
		}

		// start timer only after start synchronization
		public void startTimer() {
			rcvTimer = new Timer();
			rcvTimer.schedule(new ReceiveMessages(), 0, // initial delay
					1 * 1); // subsequent rate, every second
		}

		//
		public void stopTimer() {
			ReadWriteHelperClass writer = new ReadWriteHelperClass();
			String sLine = "All Messages have been received!";
			System.out.println(sLine);
			writer.writeLine(sLine, logFile, true);
			rcvTimer.cancel();
		}
	}

}
