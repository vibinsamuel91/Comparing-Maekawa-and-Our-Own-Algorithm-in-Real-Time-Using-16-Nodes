import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


//see README file

public class OurAlgorithm {
	
	static int seqNum;
	static int nodeID;
	static int lowestQuorumMember;
	static int criticalSectionDelay; //in seconds
	static String mutexFile;  //log file for MutEX
	static String logFile;    //log file for Node statistics
	static String syncFile;   //log file for Synchronization
	static String startSynchronizer;
	static ArrayList<Integer> nodeQuorum;
	static int holder;
	static Boolean using;
	//static Boolean expectPrivilegeReturned;
	static ArrayList<Message> requestQ;
	static SendOutRequests sendOutMyRequests; //tracks the REQUEST msgs that this Node needs to send
	static ReceiveMessages receiveMyMessages; //tracks the incoming messages
	static ClientServer mySockets;
	
	//for sending our this Node's request messages
	static ArrayList<Integer> timestamps = new ArrayList<Integer>();
	Timer timer;
	Timer rcvTimer;
	int counter = 0; 
	
	public OurAlgorithm () {
		seqNum = 0;
		nodeID = 0;
		lowestQuorumMember = 0;
		criticalSectionDelay = 0;
		nodeQuorum = new ArrayList<Integer>();
		holder = 0;
		using = false;
		//expectPrivilegeReturned = false;
		requestQ = new ArrayList<Message>();
		sendOutMyRequests = new SendOutRequests();
		receiveMyMessages = new ReceiveMessages();
	}
	
	public static void main (String[] args) throws IOException {
		if (args.length != 8){
			System.out.println("See README files; Inputs to algorithm incorrect");
		}
		OurAlgorithm myAlg = new OurAlgorithm();
		//Node initialization
		myAlg.initialize(args);
		//myAlg.printQuorum(); //test
		//System.out.println("lowest quorum member = " + lowestQuorumMember); //test
		printTimestamps(); //test
		
		//start synchronization - wait for last node to start
		if(nodeID != Integer.parseInt(args[1])){
			String sLine = null;
			ReadWriteHelperClass myReader = new ReadWriteHelperClass();
			do{
				sLine = myReader.readLine(syncFile);
				if(sLine == null) continue;
				if(sLine.endsWith(startSynchronizer)) break; //start program
			}while(1==1);		
		}
		else{
			ReadWriteHelperClass myReader = new ReadWriteHelperClass();
			myReader.writeLine(startSynchronizer, syncFile, false);
		}
		System.out.println("Start synchronization complete"); //test	
		//need to add delay b/c when all messages are sent at the same time, we cannot receive them that quickly
		try {
			TimeUnit.MILLISECONDS.sleep(nodeID*10);
		} catch (InterruptedException e) {
			System.out.println("Error in synchronization, could not sleep");
		}
		sendOutMyRequests.startTimer();
		receiveMyMessages.startTimer();
		//TODO finish!!
		while(true){
			
		}
		
	}
	
	public void initialize(String[] args) {
		
		//get nodeNum from startup script
		nodeID = Integer.parseInt(args[0]);
		
		//Quorum Assignment
		QuorumAssignment myQuorum = new QuorumAssignment(Integer.parseInt(args[1]));
		myQuorum.createQuorums();
		myQuorum.printQuorums(); //test
		//myQuorum.printAQuorums(0); //test
		
		//get the quorum for this Node ID
		nodeQuorum = myQuorum.getQuorum(nodeID - 1);
		Collections.sort(nodeQuorum); //sort quorum
		
		//find the lowest numbered node quorum member for this Node ID
		lowestQuorumMember = nodeQuorum.get(0);
		
		//set critical section delay (in seconds)
		criticalSectionDelay = Integer.parseInt(args[2]);
		
		//set holder
		if(nodeID == 1) {
			holder = 1;
		}
		else{
			holder = lowestQuorumMember; //TODO
		}
		//set the log files and zero them out
		mutexFile = args[3] + "//" + args[5];
		logFile = args[3] + "//" + args[6];
		syncFile = args[3] + "//" + args[7];
		ReadWriteHelperClass myFile = new ReadWriteHelperClass();
		myFile.zeroOutFile(mutexFile);
		myFile.zeroOutFile(logFile);
		myFile.zeroOutFile(syncFile);
		
		//set synchronization string
		startSynchronizer = "Node " + args[1] + " has started";
		
		//read in config file
		readConfigFile(args[4]);
		
		//initiate sockets
		mySockets = new ClientServer(Integer.parseInt(args[1]), Integer.parseInt(args[0])); 
	}
	
	//print the quorum
	public void printQuorum() {
		for(int i=0; i<nodeQuorum.size(); i++){
			System.out.print("S[" +i+"]"+":\t");
			System.out.println(nodeQuorum.get(i) + "\n");
		}
	}

	public static void sendMsgToSockets(Message sendingMsg, int destNode){
		int nid = mySockets.server.findNode(nodeID);
		String sendMsg = sendingMsg.convertMsgToString(sendingMsg, destNode);
		mySockets.server.handle(nid, sendMsg);
	}
	
	//receive REQUEST message
	public static void receiveRequest(Message rcvMsg) {
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		String temp = null;
		temp = "Node " + nodeID + " received a REQUEST message";
		System.out.println(temp);
		myReader.writeLine(temp, logFile, true);
		
		//update Node's seq num if necessary
		seqNum = Math.max(seqNum, rcvMsg.seqNum);
		addMsgToReqQ(rcvMsg);
		
		if(nodeID==lowestQuorumMember){
			if(holder == nodeID && using == false) {
				//send PRIVILEGE message
				if(!requestQ.isEmpty()) {
					Message newSendMsg = requestQ.remove(0);
					if(newSendMsg.senderList.isEmpty()) {
						holder = newSendMsg.originalSender;
						newSendMsg.senderList.add(newSendMsg.originalSender);
					}
					else {
						if(newSendMsg.senderList.get(0) == nodeID){
							int throwaway = newSendMsg.senderList.remove(0);
						}
						if(newSendMsg.senderList.size() == 1){
							holder = newSendMsg.senderList.get(0);
						}
						else{
							holder = newSendMsg.senderList.remove(0);
						}
					}
					newSendMsg.privilege = true;
					temp = "Node " + nodeID + " is sending the PRIVILEGE to Node " + holder; 
					System.out.println(temp);
					myReader.writeLine(temp, logFile, true);
					//TODO send PRIVILEGE to destNode
					sendMsgToSockets(newSendMsg, holder);//TODO replaced holder with destNode
				}
			}
			//if it's the holder and it's using, then do nothing
			//if it's not the holder, it will be soon since it's the lowestQuorumMember, so do nothing
		}
		else{
			//this node is not lowestQuorumMember so forward request to lowestQuorumMember
			Message forwardMsg = new Message(rcvMsg.seqNum, rcvMsg.originalSender, rcvMsg.senderList, rcvMsg.privilege);
			forwardMsg.senderList.add(0, nodeID); //adds itself to front of sender list
			temp = "Node " + nodeID + " is forwarding a REQUEST to Node " + lowestQuorumMember;
			System.out.println(temp);
			myReader.writeLine(temp, logFile, true);
			//TODO send REQUEST forwardMsg to lowestQuorumMember
			sendMsgToSockets(forwardMsg, lowestQuorumMember);
		}
	}
	
	//receive PRIVILEGE message
	public static void receivePrivilege(Message rcvMsg) {
		holder=nodeID;
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		String temp = null;
		temp = "Node " + nodeID + " received a PRIVILEGE message";
		System.out.println(temp);
		myReader.writeLine(temp, logFile, true);
		printReqQ();//TODO test
		if(!requestQ.isEmpty()) {
			//Have a REQUEST is my queue, process it to determine who the PRIVILEGE belongs to
			Message rQueueMsg = requestQ.remove(0);
			if(rQueueMsg.originalSender == nodeID) { 
				//privilege is for this Node
				holder = nodeID; 
				enterCriticalSection();
				//sending of message is handled by enterCriticalSection()
			}
			else {
				int destNode = 0; 
				if(rQueueMsg.senderList.isEmpty()) {
					destNode = rQueueMsg.originalSender;
					rQueueMsg.senderList.add(rQueueMsg.originalSender); //don't send an empty senderList
				}
				else {
					if(rQueueMsg.senderList.get(0) == nodeID){
						int throwaway = rQueueMsg.senderList.remove(0);
					}
					if(rQueueMsg.senderList.size() == 1){
						destNode = rQueueMsg.senderList.get(0);
					}
					else{
						destNode = rQueueMsg.senderList.remove(0);
					}
				}
				rQueueMsg.privilege = true;
				temp = "Node " + nodeID + " is sending the PRIVILEGE to Node " + destNode;
				System.out.println(temp);
				myReader.writeLine(temp, logFile, true);
				//update HOLDER
				if(nodeID==lowestQuorumMember){
					holder=destNode; //set
				}
				else{
					holder=lowestQuorumMember;
				}
				//TODO send rQueueMsg PRIVILEGE to destNode
				sendMsgToSockets(rQueueMsg, destNode);
			}
		}//end if REQUEST_Q is empty
		else{
			if(nodeID!=lowestQuorumMember){
				//Node received PRIVILEGE but there's no messages in REQUEST_Q, so return PRIVILEGE to lowestQuorumMember 
				//Only if node is not the lowestQuorumMember already
				ArrayList<Integer> tempAL = new ArrayList<Integer>();
				tempAL.add(nodeID);
				Message newMsg = new Message(seqNum, nodeID, tempAL, true); 
				temp = "Node " + nodeID + " is returning the PRIVILEGE to Node " + lowestQuorumMember;
				System.out.println(temp);
				myReader.writeLine(temp, logFile, true);
				//TODO send Privilege back to lowestQuorumMember
				sendMsgToSockets(newMsg, lowestQuorumMember);
			}
			//else hang on to privilege and wait for new REQUEST
		}
	}
	
	public static void enterCriticalSection(){
		using = true;
		//enter CRITICAL_SECTION
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		String temp = "Node " + nodeID + " is entering CRITICAL_SECTION now";
		System.out.println(temp);
		myReader.writeLine(temp, mutexFile, true);
		try {
			TimeUnit.SECONDS.sleep(criticalSectionDelay);
		} catch (InterruptedException e) {
			System.out.println("Error in CRITICAL_SECTION, could not sleep");
		}
		//exit CRITICAL_SECTION
		System.out.println("Node " + nodeID + " is exiting CRITICAL_SECTION now");
		using = false;
		temp = "Node " + nodeID + " is exiting CRITICAL_SECTION now";
		myReader.writeLine(temp, mutexFile, true);
		if(nodeID!=lowestQuorumMember){
			//this node is not the lowestQuorumMember;send privilege back to lowestQuorumMember
			holder=lowestQuorumMember;
			ArrayList<Integer> tempAL = new ArrayList<Integer>();
			tempAL.add(nodeID);
			Message newMsg = new Message(seqNum, nodeID, tempAL, true); 
			temp = "Node " + nodeID + " is returning the PRIVILEGE to Node " + lowestQuorumMember;
			System.out.println(temp);
			myReader.writeLine(temp, logFile, true);
			//TODO send Privilege back to lowestQuorumMember
			sendMsgToSockets(newMsg, lowestQuorumMember);
		}
		else{
			//node is lowestQuorumMember, process another REQUEST message to determine who gets PRIVILEGE next
			if(!requestQ.isEmpty()) {
				Message newSendMsg = requestQ.remove(0);
				if(newSendMsg.senderList.isEmpty()) {
					holder = newSendMsg.originalSender;
					newSendMsg.senderList.add(newSendMsg.originalSender);
				}
				else {
					if(newSendMsg.senderList.get(0) == nodeID){
						int throwaway = newSendMsg.senderList.remove(0);
					}
					if(newSendMsg.senderList.size() == 1){
						holder = newSendMsg.senderList.get(0);
					}
					else{
						holder = newSendMsg.senderList.remove(0);
					}
				}
				newSendMsg.privilege = true;
				temp = "Node " + nodeID + " is sending the PRIVILEGE to Node " + holder;
				System.out.println(temp);
				myReader.writeLine(temp, logFile, true);
				//TODO send newSendMsg PRIVILEGE to holder
				sendMsgToSockets(newSendMsg, holder);
			}
			//else, hold on to privilege
		}
	}
	
	public static void printReqQ() {
		System.out.println("RequestQ for Node " +nodeID);
		for(int i=0; i<requestQ.size(); i++){
			System.out.println("Message "+i+": "+requestQ.get(i).seqNum +","
					+requestQ.get(i).originalSender+","+requestQ.get(i).senderList.toString());
		}
	}
	
	public static void addMsgToReqQ(Message rcvMsg) {
		if(requestQ.isEmpty()){
			requestQ.add(rcvMsg);
		}
		else{
			for(int i=0; i<requestQ.size(); i++){
				if(rcvMsg.seqNum<requestQ.get(i).seqNum){
					//rcvMsg has lower sequence number
					requestQ.add(i, rcvMsg);
					return;
				}
				if(rcvMsg.seqNum==requestQ.get(i).seqNum && rcvMsg.originalSender<requestQ.get(i).originalSender){
					//rcvMsg has same sequence number but higher priority node
					requestQ.add(i, rcvMsg);
					return;
				}
			}
			//if reach this point, add to end of queue
			requestQ.add(rcvMsg);
		}
		printReqQ();//TODO remove
	}
	
	//read in Config File
	public void readConfigFile(String configFile) {
		
		ArrayList<String> aLine = new ArrayList<>();
		ReadWriteHelperClass myReader = new ReadWriteHelperClass();
		aLine = myReader.readFile(configFile);
		for(int i=0; i<aLine.size(); i++){
			String sLine = aLine.get(i);
			String[] str_array = sLine.split(",");
            if(Integer.parseInt(str_array[1]) == nodeID) { //if this entry in config file is for this node
            	timestamps.add(Integer.parseInt(str_array[0])); //add timestamp to array
            }
		}
		if(!timestamps.isEmpty()) {
			Collections.sort(timestamps); //sort timestamps, in case config file is out of order
		}
	}	
	//
	//SENDING REQUEST MESSAGES
	//
	//Node wants to send a REQUEST message for *itself*
	public static void sendRequest(int ts) {
		seqNum = ts;
		ArrayList<Integer> tempAL = new ArrayList<Integer>();
		tempAL.add(nodeID);
		Message newMsg = new Message(seqNum, nodeID, tempAL, false); 
		addMsgToReqQ(newMsg);
		//TODO send message newMsg to lowestQuorumMember;
		if(nodeID!=lowestQuorumMember){
			sendMsgToSockets(newMsg, lowestQuorumMember);
		}
	}
	
	//for testing
	public static void printTimestamps() {
		System.out.println("Timestamps for this node: ");
		for(int i=0; i<timestamps.size(); i++){
			System.out.print(timestamps.get(i) + "\t");
		}
		System.out.println();
	}
	
	class SendOutRequests extends TimerTask {
		public SendOutRequests(){	
		}
		public void run() {
			counter++;
			ReadWriteHelperClass writer = new ReadWriteHelperClass();
			String sLine = null; 
			if (!timestamps.isEmpty()){
				if(counter == timestamps.get(0)){
					int ts = timestamps.get(0);
					timestamps.remove(0);
					if(holder == nodeID && lowestQuorumMember == nodeID){
						//this node is holder, don't send request but instead enter critical section
						enterCriticalSection();
						//sending of message is handled by enterCriticalSection()
					}
					else{
						sLine = "Node: " + nodeID + " send REQUEST msg with timestamp: " + Integer.toString(ts); 
						System.out.println(sLine);
						writer.writeLine(sLine, logFile, true);
						sendRequest(ts);
					}
				}
			}
			else {
				sLine = "All Request Messages have been sent!"; 
				System.out.println(sLine);
				writer.writeLine(sLine, logFile, true);
				timer.cancel();
			}
		}
		//start timer only after start synchronization
		public void startTimer() {
			timer = new Timer();
			timer.schedule(new SendOutRequests(), 0, // initial delay
					10 * 1000); // subsequent rate, every 10 seconds
		}
	}
	
	class ReceiveMessages extends TimerTask {
		public ReceiveMessages(){	
		}
		public void run() {
			if(!mySockets.server.rcvMessage.isEmpty()){
				System.out.println("rcvMessage is " + mySockets.server.rcvMessage);
				Message newMsg = Message.convertStringToMsg(mySockets.server.rcvMessage.remove(0));
				processMessage(newMsg);
			}
		}
		//start timer only after start synchronization
		public void startTimer() {
			rcvTimer = new Timer();
			rcvTimer.schedule(new ReceiveMessages(), 0, // initial delay
					1 * 1); // subsequent rate (milliseconds), so every 1 ms
		}
		//
		public void stopTimer() {
			ReadWriteHelperClass writer = new ReadWriteHelperClass();
			String sLine = "All Messages have been received!"; 
			System.out.println(sLine);
			writer.writeLine(sLine, logFile, true);
			rcvTimer.cancel();
		}
		public void processMessage(Message newMsg){
			if(newMsg.privilege == true){
				receivePrivilege(newMsg);
				return;
			}
			if(newMsg.privilege == false){
				receiveRequest(newMsg);
			}
		}
	}
	
}

