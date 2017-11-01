import java.util.*;
import java.math.*;
/**
 * Holds the registers and their values for the processor
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class Registers
{
    // instance variables
    private static int regCount;
    private static int wordSize;
    private static ArrayList<String> registers;

    /**
     * Constructor for objects of class Registers
     */
    public Registers(int RC, int WS)
    {
        // initialise instance variables
        regCount = RC;
        wordSize = WS;
        registers = new ArrayList<String>();

        for(int i = 0; i < regCount; i++){
            registers.add(String.format(("%0" + wordSize + "d"), 0));
        }
    }

    /**
     * Manipulates registers
     * 
     * @param   regNum  number of the register being maniuplated
     * @param   vlaue   value to put in the specifiedd register
     */
    public void setReg(int regNum, String value){
        if(value.length() > wordSize){
            value = value.substring(0, wordSize);
        }
        registers.set(regNum, value);
    }
    
    /**
     * Getter method for register number of registers
     * 
     * @return   number of registers
     */
    public int getLength(){
        return registers.size();
    }

    /**
     * Getter method for register values
     * 
     * @param   index   index of the register you want to get
     * @return   value in the register
     */
    public String getReg(int index){
        return registers.get(index);
    }

    /**
     * Prints out the values of all of the registers
     * 
     */
    public void printRegisters(){
        for(int i  = 0; i < registers.size(); i++){
            System.out.println("Register " + (i) + ": " + registers.get(i) + "  " + getTwosCompliment(registers.get(i)));
        }
    }

    private static int getTwosCompliment(String binaryInt) {
        if (binaryInt.charAt(0) == '1') {
            String invertedInt = invertDigits(binaryInt);
            int decimalValue = Integer.parseInt(invertedInt, 2);
            decimalValue = (decimalValue + 1) * -1;
            return decimalValue;
        } else {
            return Integer.parseInt(binaryInt, 2);
        }
    }

    private static String invertDigits(String binaryInt) {
        String result = binaryInt;
        result = result.replace("0", " ");
        result = result.replace("1", "0"); 
        result = result.replace(" ", "1");
        return result;
    }
}
