import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

//This class sends out all of the request messages
// It runs in a Timer thread
// Timer ticks every 5 seconds
// Each time the Timer ticks, the counter ticks
// When the timestamp == counter, send RequestMessage
public class SendRequestMsg {
	
	ArrayList<Integer> timestamps = new ArrayList<Integer>();
	Timer timer;
	int counter = 0;
	int nodeID;
	
	public SendRequestMsg(int nid) {
		nodeID = nid;
	}
	
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
	
	//for testing
	public void printTimestamps() {
		System.out.println("Timestamps for this node: ");
		for(int i=0; i<timestamps.size(); i++){
			System.out.print(timestamps.get(i) + "\t");
		}
		System.out.println();
	}
	
	//start timer only after start synchronization
	public void startTimer() {
		timer = new Timer();
		timer.schedule(new SendOutRequests(), 0, // initial delay
				5 * 1000); // subsequent rate
	}
	
	class SendOutRequests extends TimerTask {
			 
		public void run() {
			counter++;
			if (!timestamps.isEmpty()){
				if(counter == timestamps.get(0)){
					int ts = timestamps.get(0);
					timestamps.remove(0);
					//TODO send message
					ReadWriteHelperClass writer = new ReadWriteHelperClass();
					String sLine = "Node: " + nodeID + " send REQUEST msg with timestamp: " + Integer.toString(ts); 
					writer.writeLine(sLine, "H:/workspace/AOS/log.txt", true);
				}
			}
			else {
				System.out.println("All Request Messages have been sent!");
				ReadWriteHelperClass writer = new ReadWriteHelperClass();
				String sLine = "All Request Messages have been sent!"; 
				writer.writeLine(sLine, "H:/workspace/AOS/log.txt", true);
				
				timer.cancel();
			}
		}
	}
}
