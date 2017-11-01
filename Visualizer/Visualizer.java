import java.io.*;
import java.util.*;
/**
 * Write a description of class Visualizer here.
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class Visualizer
{
    // instance variables - replace the example below with your own
    private static String inputName; //name of the input file with hex code
    private static String masterName;
    private static FileReader hexFile = null;
    private static CPUMemFill memory;

    private static ArrayList<String> charList;          //stores indiviual hex chars
    private static ArrayList<String> byteList;          //stores individual bytes in hex
    private static ArrayList<String> commandList;       //stores individual commands (singles) in hex
    private static ArrayList<String> doubleList;        //stores individual doubles in hex
    private static ArrayList<String> binaryComList;     //stores commands in binary form

    private static String firstLine = "";

    private static Visualizer_GUI mem_gui;

    /**
     * Constructor for objects of class Visualizer
     */
    public static void main(String [] args){

        if(args.length == 0){//check to make sure a program file name was passed to the program
            System.out.println("Please pass a file name for the program to parse.");
            return;
        }else{ //check that the string passed is a valid file name
            inputName = args[0];
            masterName = args[0];
            String string_array_1[] = inputName.split("\\.");
            inputName = "../Files/" + inputName;
            if(!(inputName.contains(".")) || !(string_array_1[1].equals("o"))){
                System.out.println("The file must have the extension '.o'. Please input a correct file name.");
                return;
            }

        }

        //attempt to load the hexFile
        try{
            hexFile =  new FileReader(inputName);
        }catch(FileNotFoundException e){
            System.out.println("Unable to open the given file. Please make sure that the given file is in the same directory as the program files.");
            return;
        }

        memory = new CPUMemFill(inputName);
        //parse the hex file
        parseFile(hexFile);

        //Build GUI

        mem_gui = new Visualizer_GUI();
        mem_gui.populateMemory(commandList);
    }

    /**
     * exports the new state of memory to the .o file
     * 
     * @param   memoryUpdated   ArrayList of the updated memory from the Vlisualizer class
     */
    public static void saveFile(ArrayList<String> memoryUpdated){
        String args_array[] = firstLine.split(":");

        String displayType = args_array[0].substring(1,args_array[0].length());
        try{
            PrintWriter pw = new PrintWriter(new FileWriter(inputName));
            pw.println(firstLine);
            for(int i = 0; i < commandList.size(); i++){
                if(displayType.equals("binary")){
                    int decimal = Integer.parseInt(memoryUpdated.get(i),2);
                    String hexStr = Integer.toString(decimal,16);
                    pw.print(hexStr);
                }else{
                    pw.print(memoryUpdated.get(i));
                }
            }
            pw.close();
        }catch(Exception e){
            System.out.println("Error creating export file.");
        }
        //save the first line at the top of the file
        //print out the updated arraylist at the second line in the file
    }

    private static void parseFile(FileReader input)
    {
        BufferedReader reader = new BufferedReader(input);
        String line = null;
        int lineNumber = 1;

        try{
            while(((line = reader.readLine()) != null)){
                if(lineNumber == 1){
                    firstLine = line;
                }else{
                    charList = memory.getChars();
                    byteList = memory.getBytes();
                    commandList = memory.getCommands();
                    doubleList = memory.getDoubles();
                    parseBinary();
                }

                lineNumber++;
            }

        }catch(IOException e){
            System.out.println("Error reading the file '" + inputName + "'.");
            return;
        }
    }

    private static void parseBinary(){
        binaryComList = new ArrayList<String>();
        for(int i = 0; i < commandList.size(); i++){
            String hexCommand = commandList.get(i);
            //System.out.println("hex: "+ hexCommand);
            long decimalCommand = Long.parseLong(hexCommand, 16);
            //System.out.println("decimal: "+ decimalCommand);
            String binaryCommand = String.format("%32s", Long.toBinaryString(decimalCommand)).replace(' ', '0');
            //System.out.println("binary: " + binaryCommand);
            binaryComList.add(binaryCommand);
        }
    }
}
