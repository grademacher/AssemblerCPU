import java.util.*;
import java.io.*;

/**
 * The class has an ArrayList that temporarily stores the output code when it is being generated
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class TempFile
{
    // instance variables
    private static ArrayList<String> tempFile;

    /**
     * Constructor for objects of class TempFile
     */
    public TempFile()
    {
        tempFile = new ArrayList<String>();
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  code   code to add to the temp out list
     * 
     */
    public void add(String code)
    {
        tempFile.add(code);
    }
    
    /**
     * Adds code to the outpute list in a specified index
     * 
     * @param  code   code to add to the list
     * @param  index   index to add the code at
     */
    public void add(String code, int index){
        tempFile.add(index, code);
    }
    
    /**
     * Writes the temporary output list to the output file
     * 
     * @param  pw   printWriter to use when writing
     * @param  firstLineLength   how many arguments are being passed in the first line
     * @param  maxmem   how many bytes are allowed in the file
     * @param  easyRead   whether to output in an easy read format (not used)
     * 
     */
    public static void writeFile(PrintWriter pw, int firstLineLength, int maxmem, boolean easyRead){
        
        //go through the arrayList and write all of the elements to given file
        //int i = 0; i < tempFile.size(); i++
        int currentByteCount = 0;
        int i = 0;
        while(currentByteCount < maxmem){
            if(i == firstLineLength){pw.println(); currentByteCount = 0;}
            
            if(i >= tempFile.size()){
                if(easyRead && (i > firstLineLength) && (currentByteCount % 8 == 0)){
                    String hexLoc = String.format("%08X", currentByteCount);
                    pw.print("\n0x" + hexLoc + "    00000000    00000000");
                    currentByteCount += 8;
                }else{
                    pw.print("00");
                    currentByteCount++;
                }
                
            }else{
                if(easyRead && (i > firstLineLength) && (currentByteCount % 8 == 0)){
                    String hexLoc = String.format("%08X", currentByteCount);
                    pw.print("\n0x" + hexLoc + "    " + tempFile.get(i) + "    ");
                }else if(easyRead && (i > firstLineLength)){
                    pw.print(tempFile.get(i) + "    ");
                }else{
                    pw.print(tempFile.get(i));
                }
                currentByteCount += tempFile.get(i).length()/2;
            }
            
            
            
            i++;
        }
    }
}
