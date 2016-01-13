import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadWriteHelperClass {
	
	public ReadWriteHelperClass(){
		
	}
    
	//zero out file
	public void zeroOutFile(String sFileName) {
		
        try {
        	File objFile = new File(sFileName);
            if(!objFile.exists()){
                objFile.createNewFile();
            }
            writeLine("", sFileName, false);
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + sFileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + sFileName + "'");
            // ex.printStackTrace();
        }
	}
	
	//Read one Line from a file
    public String readLine(String sFileName){
        
        String sLine = null;
        try {
            FileReader objFileReader = new FileReader(sFileName);
            BufferedReader objBufferedReader = new BufferedReader(objFileReader);
            
            sLine = objBufferedReader.readLine();
            objBufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + sFileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + sFileName + "'");
            // ex.printStackTrace();
        }
        return sLine;
    }
    
    //Print one line
    public void writeLine(String sLine, String sFileName, Boolean bAppend){

        try {
        	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String temp = dateFormat.format(date) + " " + sLine;
        	FileWriter objFileWriter = new FileWriter(sFileName, bAppend);
            BufferedWriter objBufferedWriter = new BufferedWriter(objFileWriter);
            objBufferedWriter.write(temp);
            objBufferedWriter.newLine();
            objBufferedWriter.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + sFileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + sFileName + "'");
        }
        return;
    }
    
    //Read from a file
    public ArrayList<String> readFile(String sFileName){
        ArrayList<String> aLines = new ArrayList<>();
        String sLine = null;
        
        try {
            File objFile = new File(sFileName);
            if(!objFile.exists()){
                objFile.createNewFile();
            }
            FileReader objFileReader = new FileReader(sFileName);
            BufferedReader objBufferedReader = new BufferedReader(objFileReader);
            
            while((sLine = objBufferedReader.readLine()) != null) {
                //System.out.println(line);
                aLines.add(sLine);
            }

            objBufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + sFileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + sFileName + "'");
            // ex.printStackTrace();
        }
        return aLines;
    }
    
    //Write to a file
    public void writeFile(String sFileName, List<String> aLines, boolean bAppend){
        try {
            FileWriter objFileWriter = new FileWriter(sFileName, bAppend);
            BufferedWriter objBufferedWriter = new BufferedWriter(objFileWriter);
            for(String line : aLines){
                objBufferedWriter.write(line);
                objBufferedWriter.newLine();
            }
            objBufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '" + sFileName + "'");
            // ex.printStackTrace();
        }
    }
}
