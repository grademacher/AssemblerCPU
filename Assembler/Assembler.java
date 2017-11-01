import java.io.*;
import java.util.*;
/**
 * This class is the main class that takes a given file name and parses through the assembly code in the file and then outputs
 * a file with the extension .o with "little finger" in it. Little finger is my own version of machine code that will be handed off to the CPU to be ran
 * 
 * @author Garrett Rademacher
 * @version 10/17/2017
 */
public class Assembler
{
    private static final String INSTRUCTION_FILE = "../Files/InstructionSet.txt";

    private static final boolean EASY_READ = false;

    // instance variables
    private static String inputName; //name of the input file with assembly code
    private static String outputName; //name of the output file this program writes to
    private static String masterName;

    private static HashTable instructionSet;
    private static Hashtable<String, String> labelSet;
    private static TempFile outputArray;
    private static TempFile tempOutFile;

    private static int firstLineLength = 0;
    private static int maxmem = 0;
    private static String strMaxmem = "";

    private static boolean aligned = false;
    private static int alignSize = 0;

    private static int currentByteCount = 0;
    
    private static long timeStart;
    private static long timeEnd;
    private static int linesParsed = 0;
    
    
    /**
     * Run method of the Assembler program, handles all other methods in this program
     * 
     * @param  file_name   name of the file to process, taken fromt he command line
     * 
     */
    public static void main(String [] args){
        timeStart = System.currentTimeMillis();
        labelSet = new Hashtable<String, String>();
        FileReader assemblyFile = null;
        File outputFile = null;

        if(args.length == 0){//check to make sure a program file name was passed to the program
            System.out.println("Please pass a file name for the program to parse.");
            return;
        }else{ //check that the string passed is a valid file name
            inputName = args[0];
            masterName = args[0];
            String string_array_1[] = inputName.split("\\.");
            inputName = "../Files/" + args[0];
            if(!(inputName.contains(".")) || !(string_array_1[1].equals("as"))){
                System.out.println("The file must have the extension '.as'. Please input a correct file name.");
                return;
            }

        }

        //create the ArrayList to temporarily hold the codes
        tempOutFile = new TempFile();

        //create the hashtable for the instruction set
        try{
            //open the instruction set text file
            FileReader IFile = new FileReader(INSTRUCTION_FILE);

            //create the hashtable
            instructionSet = new HashTable();
            instructionSet.fillSet(IFile);
        }catch(Exception e){
            System.out.println("An error occured while reading the Instruction Set file");
            return;
        }

        //attempt to load the Assembly file
        try{
            assemblyFile =  new FileReader(inputName);
        }catch(FileNotFoundException e){
            System.out.println("Unable to open the given file. Please make sure that the given file is in the same directory as the program files.");
            return;
        }

        //parse the file
        parseFile(assemblyFile);

        //write the codes from the temp holder (ArrayList) to the output file
        try{
            //print the codes from the temp arraylist to the file
            String string_array_2[] = masterName.split("\\.");
            outputName = "../Files/" + string_array_2[0] + ".o";
            //writeFile(outputName);
            PrintWriter pw = new PrintWriter(new FileWriter(outputName));
            tempOutFile.writeFile(pw, firstLineLength, maxmem, EASY_READ);
            pw.close();
        }catch(Exception e){
            System.out.println("Error printing to the output file.");
            return;
        }

        //close the files
        try{
            assemblyFile.close();
        }catch(Exception e){
            System.out.println("Error when closing the output file.");
        }
        
        timeEnd = System.currentTimeMillis();
        long timeTaken = timeEnd - timeStart;
        System.out.println("\n\n\nAssebler processed " + linesParsed + " commands in " + timeTaken + " miliseconds.");
        
    }
    
    /**
     * Parses the .o file into manageable chunks and calls other methods to process the chunks
     * 
     * @param  input   the file that is being processed
     * 
     */
    private static void parseFile(FileReader input){
        BufferedReader reader = new BufferedReader(input);
        int lineNumber = 1;
        String line = null;
        tempOutFile.add("#hex",0);
        firstLineLength++;
        try{
            while(((line = reader.readLine()) != null)){
                if(line.length() < 1){lineNumber++; continue;}; //skip blank lines
                linesParsed++;
                line = line.trim();                                 //trim any extra whitespace in the line
                String string_array_1[] = line.split(";", 2);       //parse out the comment
                String command = string_array_1[0];
                command = command.trim();                           //trim any extra whitespace in the command

                String string_array_2[] = command.split(" ", 2);    //parse out the instruction name and arguments

                String instruction = string_array_2[0];             //get the instruction and trim any whitespace
                instruction = instruction.trim();
                //System.out.println("Instruction:" + instruction);

                //check for directives
                if(instruction.contains(".")){ //the line contains a directive
                    if(string_array_2.length < 2){
                        System.out.println("Line " + lineNumber + ". Error occured while parsing directive: Insuficient Arguments");
                    }else{
                        String instructionArgs = string_array_2[1];
                        instructionArgs = instructionArgs.trim();
                        String error = parseDirective(instruction, instructionArgs);
                        if(error.length() > 0){
                            System.out.println("Line " + lineNumber + ". Error occured while parsing directive: " + error);
                            return;
                        }
                    }

                }
                else if(instruction.contains(":")){ //the line contains a label
                    if(string_array_2.length > 1){
                        System.out.println("Line " + lineNumber + ". Error occured while parsing label: Too Many Arguments");
                    }else{
                        String error = parseLabel(instruction);
                        if(error.length() > 0){
                            System.out.println("Line " + lineNumber + ". Error occured while parsing label: " + error);
                            return;
                        }
                    }

                }else{
                    //check for instructions
                    String instHandle = instructionSet.getValue(instruction);
                    if(instHandle == null){
                        System.out.print("Line " + lineNumber + ". Error reading assembly file: Instruction Not Recognized.");
                        return;
                    }else{
                        //parse out the opCode and format code
                        String string_array_3[] = instHandle.split(" ");

                        String format = string_array_3[0];                      //get the format code
                        //System.out.println(format);
                        int opCode = Integer.parseInt(string_array_3[1]);       //get the op code

                        String error = "";

                        String instructionArgs = "";
                        if( !(format.equals("F")) ){
                            instructionArgs = string_array_2[1];
                            instructionArgs = instructionArgs.trim();
                        }

                        if(format.equals("A") || format.equals("B") || format.equals("C")){
                            error = convertGeneral(opCode, instructionArgs, format);
                        }else if(format.equals("D")){                           //
                            error = convertD(opCode, instructionArgs);
                        }else if(format.equals("E")){                           //
                            error = convertE(opCode, instructionArgs);
                        }else if(format.equals("F")){                           //
                            error = convertF(opCode);
                        }else if(format.equals("G")){
                            error = convertG(opCode, instructionArgs);
                        }

                        //if an error was generated and returned, print the error and stop the assembler so the user can fix the mistake
                        if(error.length() > 0){
                            System.out.println("Line " + lineNumber + ". Error occured while converting instruction: " + error);

                            //close the files

                            return;
                        }
                    }
                }

                
                lineNumber++;
            }

        }catch(IOException e){
            System.out.println("Error reading the file '" + inputName + "'.");
            return;
        }
    }
    
    /**
     * Processes the passed directives from the .o file
     * 
     * @param  directive   the directive to be processed
     * @param  directiveArgs   the arguments associated with the directive
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String parseDirective(String directive, String directiveArgs){
        String dirCode = "";

        directiveArgs = directiveArgs.trim();
        String args_array[] = directiveArgs.split(" ");
        if(args_array.length > 1){
            return "Too Many Arguments.";
        }else if(args_array.length < 1){
            return "Too Few Arguments.";
        }
        String arg1 = args_array[0];
        arg1 = arg1.trim();

        if(directive.equals(".wordsize")){
            firstLineLength++;
            try{
                int ws = Integer.parseInt(arg1);
            }catch(Exception e){
                return "WordSize Must Be An Integer.";
            }
            dirCode = ":WS-" + arg1;
            tempOutFile.add(dirCode,1);
        }else if(directive.equals(".regcnt")){
            firstLineLength++;
            try{
                int ws = Integer.parseInt(arg1);
            }catch(Exception e){
                return "Register Count Must Be An Integer.";
            }
            dirCode = ":RC-" + arg1;
            tempOutFile.add(dirCode,2);
        }else if(directive.equals(".maxmem")){
            firstLineLength++;
            arg1 = arg1.substring(2);
            strMaxmem = arg1;
            try{
                int ws = Integer.parseInt(arg1,16);
            }catch(Exception e){
                return "Max Memory Must Be A Hex Integer.";
            }
            dirCode = ":MM-0x" + arg1;
            tempOutFile.add(dirCode,3);
            maxmem = Integer.parseInt(arg1, 16);
            //System.out.println("MM: " + maxmem);
        }

        else if(directive.equals(".align")){
            arg1 = args_array[0];
            aligned = true;
            alignSize = Integer.parseInt(arg1);
        }

        else if(directive.equals(".double")){
            arg1 = arg1.substring(3);
            //System.out.println(arg1);
            int value = Integer.parseInt(arg1, 16);
            try{
                //int ws = Integer.parseInt(arg1);
            }catch(Exception e){
                return "Double Must Be A Hex Number.";
            }

            if(aligned && (alignSize >= 8)){
                dirCode = String.format(("%0" + (alignSize*2) + "X"), value);
                aligned = false;
                alignSize = 0;
            }else{
                dirCode = String.format("%016X", value);
            }

            currentByteCount += 8;
            tempOutFile.add(dirCode);
        }else if(directive.equals(".single")){
            arg1 = arg1.substring(3);
            //System.out.println(arg1);
            int value = Integer.parseInt(arg1, 16);
            try{
                //int ws = Integer.parseInt(arg1);
            }catch(Exception e){
                return "Single Must Be A Hex Number.";
            }

            if(aligned && (alignSize >= 4)){
                dirCode = String.format(("%0" + (alignSize*2) + "X"), value);
                aligned = false;
                alignSize = 0;
            }else{
                dirCode = String.format("%08X", value);
            }

            currentByteCount += 4;
            tempOutFile.add(dirCode);
        }else if(directive.equals(".half")){
            arg1 = arg1.substring(3);
            //System.out.println(arg1);
            int value = Integer.parseInt(arg1, 16);
            try{
                //int ws = Integer.parseInt(arg1);
            }catch(Exception e){
                return "Half Must Be A Hex Number.";
            }

            //check for allignment
            if(aligned && (alignSize >= 2)){
                dirCode = String.format(("%0" + (alignSize*2) + "X"), value);
                aligned = false;
                alignSize = 0;
            }else{
                dirCode = String.format("%04X", value);
            }

            currentByteCount += 2;
            tempOutFile.add(dirCode);
        }else if(directive.equals(".byte")){
            arg1 = arg1.substring(3);
            //System.out.println(arg1);
            int value = Integer.parseInt(arg1, 16);
            try{
                //int ws = Integer.parseInt(arg1);
            }catch(Exception e){
                return "Byte Must Be A Hex Number.";
            }

            if(aligned && (alignSize >= 1)){
                dirCode = String.format(("%0" + (alignSize*2) + "X"), value);
                aligned = false;
                alignSize = 0;
            }else{
                dirCode = String.format("%02X", value);
            }

            currentByteCount += 1;
            tempOutFile.add(dirCode);
        }

        else if(directive.equals(".pos")){
            arg1 = arg1.substring(3);
            //System.out.println(arg1);
            int value = Integer.parseInt(arg1,16);
            int bytesToFill = value - currentByteCount;

            for(int i = 0; i < bytesToFill; i++){
                tempOutFile.add("00");
            }
        }else{
            return "Not a recognized directive.";
        }

        return "";
    }
    
    /**
     * Processes the passed label from the .o file
     * 
     * @param  label   the label to be processed
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String parseLabel(String label){
        label = stripChars(label, ":");
        label.trim();

        //System.out.println(label + ": " + currentByteCount);

        String byteLocation = String.format("%0" + strMaxmem.length() + "X", currentByteCount);;
        String finalString = ":" + label + "-0x" + byteLocation;

        if(label.equals("main") || label.equals("stack")){
            tempOutFile.add(finalString, firstLineLength);
            firstLineLength++;
        }

        labelSet.put(label, byteLocation);

        return "";
    }
    
    /**
     * Converts arguments of formats A, B, and C to their binary code then hex to be outputed
     * 
     * @param  opCode   the instruction to be processed
     * @param  instructionArgs   the arguments associated with the instruction
     * @param  format   the format code associated with the instruction
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String convertGeneral(int opCode, String instructionArgs, String format){
        //System.out.print("      format: " + format + "      opCode: " + opCode + "      args: " + instructionArgs + "\n\n");
        //check for brackets, if there are some, remove them
        instructionArgs = stripChars(instructionArgs, "X[]#");
        //parse the arguments
        String argsArray[] = instructionArgs.split(",");
        //check for correct number of elements, return if incorrect
        if(argsArray.length < 3){
            return "Insuficient Arguments.";
        }else if(argsArray.length > 3){
            return "Too Many Arguments.";
        }
        //finish parsing
        String tempParse;
        int arg1 = 0;
        int arg2 = 0;
        int arg3 = 0;

        for(int i = 0; i <argsArray.length; i++){
            argsArray[i] = argsArray[i].trim();
        }

        arg1 = Integer.parseInt(argsArray[0]);
        //System.out.println("Arg1: " + arg1);
        arg2 = Integer.parseInt(argsArray[1]);
        //System.out.println("Arg2: " + arg2);
        arg3 = Integer.parseInt(argsArray[2]);
        //System.out.println("Arg3: " + arg3);

        //convert to a complete code
        int code = 0;
        //call seperate code gen functions depending on the format code
        if(format.equals("A")){
            code = codeA(opCode, arg3, arg2, arg1);
        }else if(format.equals("B")){
            code = codeB(opCode, arg3, arg2, arg1);
        }else if(format.equals("C")){
            code = codeC(opCode, arg3, arg2, arg1);
        }

        String finalCode = String.format("%08X", code);
        currentByteCount += 4;
        tempOutFile.add(finalCode);
        //System.out.println(intToString(code,4));

        return "";
    }

    /**
     * Converts arguments of format D to their binary code then hex to be outputed
     * 
     * @param  opCode   the instruction to be processed
     * @param  instructionArgs   the arguments associated with the instruction
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String convertD(int opCode, String instructionArgs){
        //System.out.print("      format: D      opCode: " + opCode + "      args: " + instructionArgs + "\n\n");
        //check for brackets, if there are some, remove them
        instructionArgs = stripChars(instructionArgs, "X[]#");
        //parse the arguments
        String argsArray[] = instructionArgs.split(",");
        //check for correct number of elements, return if incorrect
        if(argsArray.length < 2){
            return "Insuficient Arguments.";
        }else if(argsArray.length > 2){
            return "Too Many Arguments.";
        }
        //finish parsing
        String tempParse;
        int arg1 = 0;
        int arg2 = 0;

        for(int i = 0; i <argsArray.length; i++){
            argsArray[i] = argsArray[i].trim();
        }

        String location = labelSet.get(argsArray[1]); //location in hex
        arg2 = Integer.parseInt(location, 16);     //location in decimal bytes

        arg1 = Integer.parseInt(argsArray[0]);
        //System.out.println("Arg1: " + arg1);
        //System.out.println("Arg2: " + arg2);

        //convert to a complete code
        int code = 0;
        //call seperate code gen functions depending on the format code
        code = codeD(opCode, arg2, arg1);

        String finalCode = String.format("%08X", code);
        currentByteCount += 4;
        tempOutFile.add(finalCode);
        //System.out.println(intToString(code,4));

        return "";
    }

    /**
     * Converts arguments of format E to their binary code then hex to be outputed
     * 
     * @param  opCode   the instruction to be processed
     * @param  instructionArgs   the arguments associated with the instruction
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String convertE(int opCode, String instructionArgs){
        //System.out.print("      format: E      opCode: " + opCode + "      args: " + instructionArgs + "\n\n");
        //check for brackets, if there are some, remove them
        instructionArgs = stripChars(instructionArgs, "X[]#");
        //parse the arguments
        String argsArray[] = instructionArgs.split(",");
        //check for correct number of elements, return if incorrect
        if(argsArray.length < 1){
            return "Insuficient Arguments.";
        }else if(argsArray.length > 1){
            return "Too Many Arguments.";
        }
        //finish parsing
        String tempParse;
        int arg1 = 0;

        for(int i = 0; i <argsArray.length; i++){
            argsArray[i] = argsArray[i].trim();
        }

        String location = labelSet.get(argsArray[0]); //location in hex
        //System.out.println("hex location: " + location);
        arg1 = Integer.parseInt(location, 16);     //location in decimal bytes


        //convert to a complete code
        int code = 0;
        //call seperate code gen functions depending on the format code
        code = codeE(opCode, arg1);

        String finalCode = String.format("%08X", code);
        currentByteCount += 4;
        tempOutFile.add(finalCode);
        //System.out.println(intToString(code,4));

        return "";
    }

    /**
     * Converts arguments of format F to their binary code then hex to be outputed
     * 
     * @param  opCode   the instruction to be processed
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String convertF(int opCode){
        //System.out.print("      format: F      opCode: " + opCode + "\n\n");

        //convert to a complete code
        int code = 0;
        //call seperate code gen functions depending on the format code
        code = codeF(opCode);

        String finalCode = String.format("%08X", code);
        currentByteCount += 4;
        tempOutFile.add(finalCode);
        //System.out.println(intToString(code,4));

        return "";
    }

    /**
     * Converts arguments of format G to their binary code then hex to be outputed
     * 
     * @param  opCode   the instruciton to be processed
     * @param  instructionArgs   the arguments associated with the instruction
     * @return     Returns a string, string only contains something if there was an error processing
     */
    private static String convertG(int opCode, String instructionArgs){
        //System.out.print("      format: E      opCode: " + opCode + "      args: " + instructionArgs + "\n\n");
        //check for brackets, if there are some, remove them
        instructionArgs = stripChars(instructionArgs, "X[]#");
        //parse the arguments
        String argsArray[] = instructionArgs.split(",");
        //check for correct number of elements, return if incorrect
        if(argsArray.length < 1){
            return "Insuficient Arguments.";
        }else if(argsArray.length > 1){
            return "Too Many Arguments.";
        }
        //finish parsing
        String tempParse;
        int arg1 = 0;

        for(int i = 0; i <argsArray.length; i++){
            argsArray[i] = argsArray[i].trim();
        }

        //push and pop
        arg1 = Integer.parseInt(argsArray[0]);
        //System.out.println("Arg1: " + arg1);

        
        //convert to a complete code
        int code = 0;
        //call seperate code gen functions depending on the format code
        code = codeE(opCode, arg1);

        String finalCode = String.format("%08X", code);
        currentByteCount += 4;
        tempOutFile.add(finalCode);
        //System.out.println(intToString(code,4));

        return "";
    }

    /**
     * Formats op codes and instructions arguments to binary format A
     * 
     * @param  op   opCode of the instrucction
     * @param  rm   register
     * @param  rn   register
     * @param  rd   register
     * @return     returns the decimal form of the binary code
     */
    private static int codeA(int op, int rm, int rn, int rd){
        int tempCode = 0;

        op = op<<26;

        //pad, trim, and shift
        String rmBinary = Integer.toBinaryString(rm);
        rmBinary = String.format("%5s", rmBinary);
        rmBinary = rmBinary.replace(" ", "0");
        rmBinary = rmBinary.substring(rmBinary.length()-5, rmBinary.length());
        rm = Integer.parseInt(rmBinary, 2);
        rm = rm<<21;

        //pad, trim, and shift
        String rnBinary = Integer.toBinaryString(rn);
        rnBinary = String.format("%5s", rnBinary);
        rnBinary = rnBinary.replace(" ", "0");
        rnBinary = rnBinary.substring(rnBinary.length()-5, rnBinary.length());
        rn = Integer.parseInt(rnBinary, 2);
        rn = rn<<5;

        //pad, trim, and shift
        String rdBinary = Integer.toBinaryString(rd);
        rdBinary = String.format("%5s", rdBinary);
        rdBinary = rdBinary.replace(" ", "0");
        rdBinary = rdBinary.substring(rdBinary.length()-5, rdBinary.length());
        rd = Integer.parseInt(rdBinary, 2);

        tempCode = tempCode|op;
        tempCode = tempCode|rm;
        tempCode = tempCode|rn;
        tempCode = tempCode|rd;

        return tempCode;
    }

    /**
     * Formats op codes and instructions arguments to binary format B
     * 
     * @param  op   opCode of the instrucction
     * @param  ALU   ALU Immediate
     * @param  rn   register
     * @param  rd   register
     * @return     returns the decimal form of the binary code
     */
    private static int codeB(int op, int ALU, int rn, int rd){
        int tempCode = 0;

        //shift the numbers to the right spot
        op = op<<26;

        //pad, trim, and shift
        String ALUBinary = Integer.toBinaryString(ALU);
        ALUBinary = String.format("%16s", ALUBinary);
        ALUBinary = ALUBinary.replace(" ", "0");
        ALUBinary = ALUBinary.substring(ALUBinary.length()-16, ALUBinary.length());
        ALU = Integer.parseInt(ALUBinary, 2);
        ALU = ALU<<10;

        //pad, trim, and shift
        String rnBinary = Integer.toBinaryString(rn);
        rnBinary = String.format("%5s", rnBinary);
        rnBinary = rnBinary.replace(" ", "0");
        rnBinary = rnBinary.substring(rnBinary.length()-5, rnBinary.length());
        rn = Integer.parseInt(rnBinary, 2);
        rn = rn<<5;

        //pad, trim, and shift
        String rdBinary = Integer.toBinaryString(rd);
        rdBinary = String.format("%5s", rdBinary);
        rdBinary = rdBinary.replace(" ", "0");
        rdBinary = rdBinary.substring(rdBinary.length()-5, rdBinary.length());
        rd = Integer.parseInt(rdBinary, 2);

        //ORR the numbers together into one number
        tempCode = tempCode|op;
        tempCode = tempCode|ALU;
        tempCode = tempCode|rn;
        tempCode = tempCode|rd;

        return tempCode;
    }

    /**
     * Formats op codes and instructions arguments to binary format C
     * 
     * @param  op   opCode of the instrucction
     * @param  DTA   DT Address
     * @param  rn   register
     * @param  rt   register
     * @return     returns the decimal form of the binary code
     */
    private static int codeC(int op, int DTA, int rn, int rt){
        int tempCode = 0;

        //shift the numbers to the right spot
        op = op<<26;

        //pad, trim, and shift
        String DTABinary = Integer.toBinaryString(DTA);
        DTABinary = String.format("%16s", DTABinary);
        DTABinary = DTABinary.replace(" ", "0");
        DTABinary = DTABinary.substring(DTABinary.length()-16, DTABinary.length());
        DTA = Integer.parseInt(DTABinary, 2);
        DTA = DTA<<10;

        //pad, trim, and shift
        String rnBinary = Integer.toBinaryString(rn);;
        rnBinary = String.format("%5s", rnBinary);
        rnBinary = rnBinary.replace(" ", "0");
        rnBinary = rnBinary.substring(rnBinary.length()-5, rnBinary.length());
        rn = Integer.parseInt(rnBinary, 2);
        rn = rn<<5;

        //pad, trim, and shift
        String rtBinary = Integer.toBinaryString(rt);
        rtBinary = String.format("%5s", rtBinary);
        rtBinary = rtBinary.replace(" ", "0");
        rtBinary = rtBinary.substring(rtBinary.length()-5, rtBinary.length());
        rt = Integer.parseInt(rtBinary, 2);

        //ORR the numbers together into one number
        tempCode = tempCode|op;
        tempCode = tempCode|DTA;
        tempCode = tempCode|rn;
        tempCode = tempCode|rt;

        return tempCode;
    }

    /**
     * Formats op codes and instructions arguments to binary format D
     * 
     * @param  op   opCode of the instrucction
     * @param  CBA   branching address
     * @param  rt   register
     * @return     returns the decimal form of the binary code
     */
    private static int codeD(int op, int CBA, int rt){
        int tempCode = 0;

        op = op<<26;

        //pad, trim, and shift
        String CBABinary = Integer.toBinaryString(CBA);
        CBABinary = String.format("%21s", CBABinary);
        CBABinary = CBABinary.replace(" ", "0");
        CBABinary = CBABinary.substring(CBABinary.length()-21, CBABinary.length());
        CBA = Integer.parseInt(CBABinary, 2);
        CBA = CBA<<5;

        //pad, trim, and shift
        String rtBinary = Integer.toBinaryString(rt);
        rtBinary = String.format("%5s", rtBinary);
        rtBinary = rtBinary.replace(" ", "0");
        rtBinary = rtBinary.substring(rtBinary.length()-5, rtBinary.length());
        rt = Integer.parseInt(rtBinary, 2);

        tempCode = tempCode|op;
        tempCode = tempCode|CBA;
        tempCode = tempCode|rt;

        return tempCode;
    }

    /**
     * Formats op codes and instructions arguments to binary format A
     * 
     * @param  op   opCode of the instrucction
     * @param  BRANCH   branching address
     * @return     returns the decimal form of the binary code
     */
    private static int codeE(int op, int BRANCH){
        int tempCode = 0;

        op = op<<26;

        //pad, trim, and shift
        String BRANCHBinary = Integer.toBinaryString(BRANCH);
        BRANCHBinary = String.format("%26s", BRANCHBinary);
        BRANCHBinary = BRANCHBinary.replace(" ", "0");
        BRANCHBinary = BRANCHBinary.substring(BRANCHBinary.length()-26, BRANCHBinary.length());
        BRANCH = Integer.parseInt(BRANCHBinary, 2);

        tempCode = tempCode|op;
        tempCode = tempCode|BRANCH;

        return tempCode;
    }

    /**
     * Formats op codes and instructions arguments to binary format F
     * 
     * @param  op   opCode of the instrucction
     * @return     returns the decimal form of the binary code
     */
    private static int codeF(int op){
        int tempCode = 0;

        op = op<<26;

        tempCode = tempCode|op;

        return tempCode;
    }


    /**
     * Strips given characters from a given string
     * 
     * @param  input   String that characters will be striped from
     * @param  strip   string of characters to strip from the input string
     * @return     String with the given characters stripped out
     */
    private static String stripChars(String input, String strip) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (strip.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
