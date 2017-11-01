import java.util.*;
import java.io.*;
/**
 * Class creates and contains a hash table that contains the instruction set
 * 
 * @author Garrett Rademacher 
 * @version 10/17/2017
 */
public class HashTable
{
    // instance variables
    private Hashtable<String, String> instructionSet;   //hashtable to keep the instruction set in

    /**
     * Constructor for objects of class HashTable
     */
    public HashTable()
    {
        // initialise instance variables
        instructionSet = new Hashtable<String, String>();
    }
    
    /**
     * getter method that returns a value based on a passed key
     * 
     * @param  file   file to read the instruction set from
     * 
     */
    public void fillSet(FileReader file){
        BufferedReader reader = new BufferedReader(file);
        int lineNumber = 1;
        String line = null;
        try{
            while((line = reader.readLine()) != null){ //while there are more lines in the instruction set file
                String string_array_1[] = line.split(" ", 2);
                String key = string_array_1[0];     //get the key
                key = key.trim();
                String value = string_array_1[1];   //get the value
                value = value.trim();
                
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
     * @return     returns the string value that corresponds to a passed key
     */
    public String getValue(String key)
    {
        return instructionSet.get(key);
    }
}
