import java.util.ArrayList;
import java.util.Collections;

public class MessageTracker {
	
	ArrayList<Integer> timestamps = new ArrayList<Integer>();
	
	public MessageTracker() {
	}
	
	public void readConfigFile(String configFile, int nodeID) {
		
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
}
