import java.util.*;
import java.io.*;
/**
 * Write a description of class CPUmemFill here.
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class CPUMemFillP
{
    // instance variables
    private static ArrayList<String> charList;          //stores indiviual hex chars
    private static ArrayList<String> byteList;          //stores individual bytes in hex
    private static ArrayList<String> commandList;       //stores individual commands in hex
    private static ArrayList<String> doubleList;        //stores individual doubles in hex

    /**
     * Constructor for objects of class CPUmemFill
     */
    public CPUMemFillP(String fileName)
    {
        FileReader hexFile = null;
        String line = null;
        int lineNumber = 1;

        //attempt to load the hexFile
        try{
            hexFile =  new FileReader(fileName);
        }catch(FileNotFoundException e){
            System.out.println("Unable to open the given file. Please make sure that the given file is in the same directory as the program files.");
            return;
        }

        BufferedReader reader = new BufferedReader(hexFile);

        try{
            while(((line = reader.readLine()) != null)){
                if(lineNumber == 1){
                    //parseHeader(line);
                }else{
                    //System.out.println(line);
                    parseChars(line);
                    parseBytes(line);
                    parseCommands(line);
                    parseDoubles(line);
                    //parseBinary();
                }

                lineNumber++;
            }

        }catch(IOException e){
            System.out.println("Error reading the file '" + fileName + "'.");
            return;
        }
    }
    
    /**
     * Helper method returns the arraylist of chars
     */
    public static ArrayList<String> getChars(){
        return charList;
    }
    
    /**
     * Helper method returns the arraylist of bytes
     */
    public static ArrayList<String> getBytes(){
        return byteList;
    }
    
    /**
     * Helper method returns the arraylist of singles (commands)
     */
    public static ArrayList<String> getCommands(){
        return commandList;
    }
    
    /**
     * Helper method returns the arraylist of doubles
     */
    public static ArrayList<String> getDoubles(){
        return doubleList;
    }

    private static void parseChars(String line){
        charList = new ArrayList<String>();
        Scanner scChar = new Scanner(line);
        scChar.useDelimiter("");
        while(scChar.hasNext()){
            String currentChar = scChar.next();
            //do whatever with the characters
            charList.add(currentChar);
        }

    }

    private static void parseBytes(String line){
        byteList = new ArrayList<String>();

        while(line.length() >1){
            String byteString = line.substring(0,2);
            line = line.substring(2,line.length());
            byteList.add(byteString);
        }
    }

    private static void parseCommands(String line){
        commandList = new ArrayList<String>();

        while(line.length() >7){
            String commandString = line.substring(0,8);
            line = line.substring(8,line.length());
            commandList.add(commandString);
            //System.out.println(commandString);
        }
    }

    private static void parseDoubles(String line){
        doubleList = new ArrayList<String>();

        while(line.length() >15){
            String doubleString = line.substring(0,16);
            line = line.substring(16,line.length());
            doubleList.add(doubleString);
        }
    }
}
