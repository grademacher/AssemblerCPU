import java.util.*;
import java.io.*;

/**
 * Creates the hash table for the CPU
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class HashTableCPU
{
    // instance variables
     private Hashtable<Integer, String> instructionSet;

    /**
     * Constructor for objects of class HashTableCPU
     */
    public HashTableCPU()
    {
        // initialise instance variables
        instructionSet = new Hashtable<Integer, String>();
    }

    /**
     * Getter method for register number of registers
     * 
     * @param   file   instruction set file
     */
    public void fillSet(FileReader file){
        BufferedReader reader = new BufferedReader(file);
        int lineNumber = 1;
        String line = null;
        try{
            while((line = reader.readLine()) != null){ //while there are more lines in the instruction set file
                String string_array_1[] = line.split(" ");
                String name = string_array_1[0];     //get the op name
                name = name.trim();
                String format = string_array_1[1];   //get the format code
                format = format.trim();
                String opCode = string_array_1[2];
                opCode = opCode.trim();
                
                int key = Integer.parseInt(opCode);
                String value = name + " " + format;
                
                
                
                //put the keys and values into the hashtable
                instructionSet.put(key, value);
                lineNumber++;
            }
        }catch(Exception e){
            System.out.println("Error occured while filling the Hashtable on line " + lineNumber);
        }
    }
    
    /**
     * getter method that returns a value based on a passed key
     * 
     * @param  key   String value that corresponds to a value in the hashtable
     * @return     corresponding  string value to the key
     */
    public String getValue(int key)
    {
        return instructionSet.get(key);
    }
}
