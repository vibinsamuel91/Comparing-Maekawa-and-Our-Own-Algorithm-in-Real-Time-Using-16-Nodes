import java.util.ArrayList;

public class Message {
	
	private int seqNum = 0;
	private int nodeID = 0;
	private Boolean privilege = false;
	int originalSender = 0; 
    ArrayList<Integer> senderList = new ArrayList<Integer>();

	public Message() {
		
	}
	
	public Message(int sn, int nid, Boolean p) {
		seqNum = sn;
		nodeID = nid;
		privilege = p;
	}
	
	public Message(int sn, int nid) {
		seqNum = sn;
		nodeID = nid;
	}
	
	public int getSeqNum() {
		return seqNum;
	}
	
	public int getNodeID() {
		return nodeID;
	}
	
	public Boolean getPrivilege() {
		return privilege;
	}
	
	public Boolean isMsgEmpty() {
		if(seqNum == 0 && nodeID == 0 && privilege ==false) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//Msg String format:
		// destinationNode;seqNum,originalSender,senderList(colon-delimited),privilege
		public String convertMsgToString(Message msg, int dest){
			String ret = Integer.toString(dest) +";"+ Integer.toString(msg.seqNum) +","+ Integer.toString(msg.originalSender) +",";
			String list = "";
			for(int i = 0; i<msg.senderList.size(); i++){
				list += Integer.toString(msg.senderList.get(i));
				list += ":";
			}
			ret += list +",";
			ret += Boolean.toString(msg.privilege);
			return ret;
		}
		
		//Msg String format:
		// destinationNode;seqNum,originalSender,senderList(colon-delimited),privilege
		//returns null if error
		public Message convertStringToMsg(String sString){
			Message newMsg = new Message();
			String[] firstSplit = sString.split(";");
			//firstSplit[1] = seqNum,originalSender,senderList(colon-delimited),privilege
			if(firstSplit[1].isEmpty()) {
				return null;
			}
			String[] getMsgPeices = firstSplit[1].split(",");
			newMsg.seqNum = Integer.parseInt(getMsgPeices[0]);
			newMsg.originalSender = Integer.parseInt(getMsgPeices[1]);
			newMsg.privilege = Boolean.parseBoolean(getMsgPeices[3]);
			
			//get senderList
			String[] getSenderList = getMsgPeices[2].split(":");
			for(int i = 0; i<getSenderList.length; i++){
				if(!getSenderList[i].isEmpty() || getSenderList[i] != null){
					newMsg.senderList.add(Integer.parseInt(getSenderList[i]));
				}
			}
			return newMsg;
		}
}
