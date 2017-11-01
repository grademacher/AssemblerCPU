import java.io.*;
import java.util.*;

/**
 * Runs the little finger machine code from the passed file. The code, registers, and memory are all displayed in seperate GUIs.
 * 
 * 
 * @author Garrett Rademacher
 * @version 10/18/2017
 */
public class CPU
{
    private static final String INSTRUCTION_FILE = "../Files/InstructionSet.txt";

    // instance variables
    private static boolean verbose = true;
    private static boolean delayedRun = true;
    private static int pauseTime = 0;                      //in seconds
    private static String inputName; //name of the input file with hex code
    private static String masterName;

    private static String firstLine = "";
    private static int maxmem = 0;
    private static int wordSize = 0;
    private static int registerCount = 0;
    private static String mainLoc = "";
    private static String stackLoc = "";
    private static int commandLocation;

    private static String charType = "";

    private static ArrayList<String> charList;          //stores indiviual hex chars
    private static ArrayList<String> byteList;          //stores individual bytes in hex
    private static ArrayList<String> commandList;       //stores individual commands (singles) in hex
    private static ArrayList<String> doubleList;        //stores individual doubles in hex
    private static FileReader hexFile = null;

    private static ArrayList<String> binaryComList;     //stores commands in binary form

    private static HashTableCPU instructionSet;

    private static Registers reg;

    private static CPUMemFillP memory;

    //flags
    private static boolean zFlag = false;
    private static boolean nFlag = false;
    private static boolean cFlag = false;
    private static boolean vFlag = false;

    private static CPU_GUI cpu_gui;
    private static Mem_GUI mem_gui;
    
    /**
     * PThe main run method of the CPU, handles the setup and calls the methods responsible for processing.
     * 
     * @param  file_name   The file to read from, passed in from the command line
     * @param  verbose_mode   Whether verbose mode is on, passed in from the command line
     */
    public static void main(String [] args){

        if(args.length == 0){//check to make sure a program file name was passed to the program
            System.out.println("Please pass a file name for the program to parse.");
            return;
        }else{ //check that the string passed is a valid file name
            inputName = args[0];
            masterName = args[0];
            String string_array_1[] = inputName.split("\\.");
            inputName = "../Files/" + args[0];
            if(!(inputName.contains(".")) || !(string_array_1[1].equals("o"))){
                System.out.println("The file must have the extension '.o'. Please input a correct file name.");
                return;
            }

            if(args.length == 2){
                if(args[1].equals("true")){
                    verbose = true;
                }else if(args[1].equals("false")){
                    verbose = false;
                }else{
                    System.out.println("Verbose mode argument not recognized, please input 'true' or 'false'.");
                }
            }else{
                System.out.println("Please input an argument for verbose mode.");
                return;
            }

        }
        //create the hashtable for the instruction set
        try{
            //open the instruction set text file
            FileReader IFile = new FileReader(INSTRUCTION_FILE);

            //create the hashtable
            instructionSet = new HashTableCPU();
            instructionSet.fillSet(IFile);
        }catch(Exception e){
            System.out.println("An error occured while reading the Instruction Set file");
            return;
        }

        //attempt to load the hexFile
        try{
            hexFile =  new FileReader(inputName);
        }catch(FileNotFoundException e){
            System.out.println("Unable to open the given file. Please make sure that the given file is in the same directory as the program files.");
            return;
        }

        memory = new CPUMemFillP(inputName);
        //parse the hex file
        parseFile(hexFile);

        //Build GUI
        cpu_gui = new CPU_GUI();
        cpu_gui.populateRegisters(reg);

        mem_gui = new Mem_GUI();
        mem_gui.populateMemory(commandList);

        String string_array_1[] = masterName.split("\\.");
        String assemblyFileName = "../Files/" + string_array_1[0] + ".as";
        cpu_gui.populateCode(assemblyFileName);
        cpu_gui.populateRegisters(reg);
    }

    /**
     * Parse the file and extract characters, bytes, commands, doubles, and convert hex commands to binary commands
     * 
     * @param  input   FileReader var that the metthod will read from
     */
    private static void parseFile(FileReader input)
    {
        BufferedReader reader = new BufferedReader(input);
        String line = null;
        int lineNumber = 1;

        try{
            while(((line = reader.readLine()) != null)){
                if(lineNumber == 1){
                    firstLine = line;
                    parseHeader(line);
                }else{
                    charList = memory.getChars();
                    byteList = memory.getBytes();
                    commandList = memory.getCommands();
                    doubleList = memory.getDoubles();
                    parseBinary();
                }

                lineNumber++;
            }

            int decMainLoc = getDecimal(mainLoc);
            commandLocation = decMainLoc/4;

        }catch(IOException e){
            System.out.println("Error reading the file '" + inputName + "'.");
            return;
        }
    }


    /**
     *Runs one individual instruction
     * 
     */
    public static void runStep(){
        String error = runCommand(commandLocation);
        commandLocation++;

        if(error.equals("HALT")){
            //do all the printing out of status here
            System.out.println("Program hit the halt command");
            reg.printRegisters();
            cpu_gui.setProgramDone(true);
            return;
        }
    }
    
    
    /**
     * Reset the simulation to the beginning state, resets the GUIs, memory, and run position
     * 
     */
    public static void resetProgram(){

        //attempt to load the hexFile
        try{
            hexFile =  new FileReader(inputName);
        }catch(FileNotFoundException e){
            System.out.println("Unable to open the given file. Please make sure that the given file is in the same directory as the program files.");
            return;
        }
        memory = new CPUMemFillP(inputName);
        //parse the hex file
        parseFile(hexFile);
        //reset the starting memory location
        int decMainLoc = getDecimal(mainLoc);
        commandLocation = decMainLoc/4;
    }

    /**
     * Prints the current state of the processor to a file, this includes the memory, registers, and flags
     * 
     */
    public static void printState(){
        String string_array_1[] = masterName.split("\\.");
        String printFileName = "../Files/" + string_array_1[0] + "2.o";
        try{
            PrintWriter pw = new PrintWriter(new FileWriter(printFileName));
            pw.println(firstLine);
            for(int i = 0; i < commandList.size(); i++){
                pw.print(commandList.get(i));
            }
            pw.close();
        }catch(Exception e){
            System.out.println("Error creating export file.");
        }

        String CPUStateFile = "../Files/" + string_array_1[0] + "_CPU_State.txt";
        try{
            PrintWriter pw = new PrintWriter(new FileWriter(CPUStateFile));
            for(int i = 0; i < reg.getLength(); i++){
                pw.println("X" + i + ":   " + reg.getReg(i) + "   " + getTwosCompliment(reg.getReg(i)));
            }
            pw.println("\n\n");
            pw.println("Zero Flag: " + zFlag);
            pw.println("Negative Flag: " + nFlag);
            pw.println("Carry Out Flag: " + cFlag);
            pw.println("Overflow Flag: " + vFlag);
            pw.close();
        }catch(Exception e){
            System.out.println("Error creating export file.");
        }
    }

    /**
     * Parse the file and extract characters, bytes, commands, doubles, and convert hex commands to binary commands
     * 
     * @param  index   Runs the command at the given index
     * @return      Returns any error message that occur, otherwise the string is empty
     */
    private static String runCommand(int index){
        String command = binaryComList.get(index);
        int opCode = Integer.parseInt(command.substring(0, 6), 2);

        //System.out.println("" + command + "     " + opCode);
        String instHandle = instructionSet.getValue(opCode);
        //pull out the format code
        String string_array_1[] = instHandle.split(" ");
        String format = string_array_1[1]; //get the format code
        String opName = string_array_1[0]; //get the operation name

        if(verbose){
            System.out.println("\n\nMachine Code: " + command + "   OP Code: " + opCode + "    Operation Name: " + opName);
        }

        if(format.equals("A")){
            String rm, rn, rd;
            rm = command.substring(6,11);
            rn = command.substring(22,27);
            rd = command.substring(27,32);

            executeA(opCode, opName, rm, rn, rd);
        }else if(format.equals("B")){
            String ALU, rn, rd;
            ALU = command.substring(6,22);
            rn = command.substring(22,27);
            rd = command.substring(27,32);

            executeB(opCode, opName, ALU, rn, rd);
        }else if(format.equals("C")){
            String DTA, rn, rt;
            DTA = command.substring(6,22);
            rn = command.substring(22,27);
            rt = command.substring(27,32);

            executeC(opCode, opName, DTA, rn, rt);
        }else if(format.equals("D")){
            String CBA, rt;
            CBA = command.substring(6,27);
            rt = command.substring(27,32);

            executeD(opCode, opName, CBA, rt);
        }else if(format.equals("E")){
            String BRANCH;
            BRANCH = command.substring(6,32);

            executeE(opCode, opName, BRANCH);
        }else if(format.equals("F")){ //halt and nop
            if(opCode == 0){//NOP
                //do nothing and skip to the next command
            }else if(opCode == 63){
                return "HALT";
            }
        }else if(format.equals("G")){ //push and pop
            executeG(opCode);
        }

        //update the GUIs
        cpu_gui.populateRegisters(reg);
        mem_gui.populateMemory(commandList);

        //print out register values
        if(verbose){
            for(int i = 0; i < registerCount; i++){
                System.out.print("X"+ i + ": " + getTwosCompliment(reg.getReg(i)) + "   ");
            }
        }
        //print out flag values
        if(verbose){
            System.out.println("\nZero Flag: " + zFlag + "    Negative Flag: " + nFlag + "    Carry Out Flag: " + cFlag + "   Overflow Flag: " + vFlag);
        }

        return "";
    }

    /**
     * Executes instructions of the A format
     * 
     * @param  opCode   op code of the instruction
     * @param  opName   name of the operation being executed
     * @param  rm   register
     * @param  rn   register
     * @param  rd   register
     */
    private static void executeA(int opCode, String opName, String rm, String rn, String rd){
        //get the values from the registers
        String val1 = reg.getReg(Integer.parseInt(rn, 2));
        String val2 = reg.getReg(Integer.parseInt(rm, 2));
        int rdNum = Integer.parseInt(rd,2);
        int rnNum = Integer.parseInt(rn,2);
        int rmNum = Integer.parseInt(rm,2);

        if(verbose){
            System.out.println("Instruction Arguments: X" + rdNum + ", X" + rnNum + ", X" + rmNum);
        }

        if(opCode == 1){
            //do the operation
            String result = addBinary(val1, val2, opCode);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 2){
            //take the twos compliment of val2
            if(Integer.parseInt(val2,2) != 0){
                val2 = twosCompliment(val2);
            }

            //do the add method
            String result = addBinary(val1, val2, opCode);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 5){
            resetFlags();

            //do the operation
            String result = addBinary(val1, val2, opCode);
            setFlags(result);

            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 6){
            resetFlags();

            //take the twos compliment of val2
            if(Integer.parseInt(val2,2) != 0){
                val2 = twosCompliment(val2);
            }
            //do the add method
            String result = addBinary(val1, val2, opCode);
            setFlags(result);

            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 9){
            //do the operation
            String result = ANDBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 10){
            //do the operation
            String result = ORRBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 11){
            //do the operation
            String result = EORBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }
    }

    /**
     * Executes instructions of the B format
     * 
     * @param  opCode   op code of the instruction
     * @param  opName   name of the operation being executed
     * @param  ALU   ALU immediate
     * @param  rn   register
     * @param  rd   register
     */
    private static void executeB(int opCode, String opName, String ALU, String rn, String rd){
        //get the values from the registers
        String val1 = reg.getReg(Integer.parseInt(rn, 2));
        String val2 = ALU;
        int rdNum = Integer.parseInt(rd,2);
        int rnNum = Integer.parseInt(rn,2);
        int ALUNum = getTwosCompliment(ALU);

        if(verbose){
            System.out.println("Instruction Arguments: X" + rdNum + ", X" + rnNum + ", #" + ALUNum);
        }

        if(opCode == 3){
            //do the operation
            String result = addBinary(val1, val2, opCode);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 4){
            //take the twos compliment of val2
            if(Integer.parseInt(val2,2) != 0){
                val2 = twosCompliment(val2);
            }
            //do the add method
            String result = addBinary(val1, val2, opCode);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 7){
            //reset the flags
            resetFlags();

            //do the operation
            String result = addBinary(val1, val2, opCode);
            setFlags(result);

            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 8){
            //reset the flags
            resetFlags();

            //take the twos compliment of val2
            if(Integer.parseInt(val2,2) != 0){
                val2 = twosCompliment(val2);
            }
            //do the add method
            String result = addBinary(val1, val2, opCode);
            setFlags(result);

            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 12){
            //do the operation
            String result = ANDBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 13){
            //do the operation
            String result = ORRBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 14){
            //do the operation
            String result = EORBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 15){
            //do the operation
            String result = LSLBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }else if(opCode == 16){
            //do the operation
            String result = LSRBinary(val1, val2);
            //save the result to the result register
            reg.setReg(Integer.parseInt(rd,2), result);
        }
    }

    /**
     * Executes instructions of the C format
     * 
     * @param  opCode   op code of the instruction
     * @param  opName   name of the operation being executed
     * @param  DTA   DT Address
     * @param  rn   register
     * @param  rt   register
     */
    private static void executeC(int opCode, String opName, String DTA, String rn, String rt){
        int rtNum = Integer.parseInt(rt,2);
        int rnNum = Integer.parseInt(rn,2);
        int DTANum = getTwosCompliment(DTA);

        if(verbose){
            System.out.println("Instruction Arguments: X" + rtNum + ", X" + rnNum + ", #" + DTANum);
        }

        if(opCode == 17){//LDUR
            //offset is in number of bits, need to convert this to an index in command storage and get the value from command list
            //rt is the destination register
            int rtIndex = Integer.parseInt(rt,2);
            //DTA is the offset in singles (ie size of command list)
            int offset = Integer.parseInt(DTA,2);
            //System.out.println(offset);
            //rn is the register that stores the location of the starting point
            int rnIndex = Integer.parseInt(rn,2);
            String storedHexVal = commandList.get(Integer.parseInt(reg.getReg(rnIndex),2) + offset);
            //System.out.println(storedHexVal);
            int tempDecimal = Integer.parseInt(storedHexVal, 16);
            //System.out.println(""+tempDecimal);
            String storedVal = Integer.toBinaryString(tempDecimal);
            //System.out.println(storedVal);

            String tempA = storedVal;
            tempA = String.format(("%" + wordSize + "s"), tempA);
            tempA = tempA.replace(" ", "0");
            tempA = tempA.substring(tempA.length()-wordSize, tempA.length());
            storedVal = tempA;
            reg.setReg(rtIndex, storedVal);
            //reg.printRegisters();
        }else if(opCode == 18){//STUR
            //rt is the value that you are placing into memory
            int rtIndex = Integer.parseInt(rt,2);
            String rtVal = reg.getReg(rtIndex);
            int decimal = Integer.parseInt(rtVal,2);
            String hexStr = Integer.toString(decimal,16);
            //System.out.println("value to be placed into memory: "+rtVal);

            //rn is the index of the starting point value
            int rnIndex = Integer.parseInt(rn,2);

            int offset = Integer.parseInt(DTA,2);
            int memIndex = Integer.parseInt(reg.getReg(rnIndex),2) + offset;

            commandList.set((memIndex), hexStr);
            //reg.printRegisters();
        }else if(opCode == 19){//LDURSW
            //nope
        }else if(opCode == 20){//STURW
            //nope
        }else if(opCode == 21){//LDURH
            //nope
        }else if(opCode == 22){//STURH
            //nope
        }else if(opCode == 23){//LDURB
            //nope
        }else if(opCode == 24){//STURB
            //nope
        }
    }

    /**
     * Executes instructions of the D format
     * 
     * @param  opCode   op code of the instruction
     * @param  opName   name of the operation being executed
     * @param  CBA   conditional branching address
     * @param  rt   register
     */
    private static void executeD(int opCode, String opName, String CBA, String rt){
        int rtNum = Integer.parseInt(rt,2);
        int CBANum = Integer.parseInt(CBA,2);
        String CBAHex = String.format("%05X",CBANum);

        if(verbose){
            System.out.println("Instruction Arguments: X" + rtNum + ", 0x" + CBAHex);
        }
        if(opCode == 25){//CBZ
            int rtIndex = Integer.parseInt(rt,2);
            String rtVal = reg.getReg(rtIndex);
            int rtValDecimal = getTwosCompliment(rtVal);
            if(rtValDecimal == 0){
                int decimalBytes = Integer.parseInt(CBA,2);
                decimalBytes = decimalBytes/4;
                commandLocation = decimalBytes-1;
            }
        }else if(opCode == 26){//CBNZ
            int rtIndex = Integer.parseInt(rt,2);
            String rtVal = reg.getReg(rtIndex);
            int rtValDecimal = getTwosCompliment(rtVal);
            if(rtValDecimal != 0){
                int decimalBytes = Integer.parseInt(CBA,2);
                decimalBytes = decimalBytes/4;
                commandLocation = decimalBytes-1;
            }
        }else if(opCode == 27){//BCOND

        }
    }

    /**
     * Executes instructions of the E format
     * 
     * @param  opCode   op code of the instruction
     * @param  opName   name of the operation being executed
     * @param  BRANCH   Branching address
     */
    private static void executeE(int opCode, String opName, String BRANCH){
        int BRANCHNum = Integer.parseInt(BRANCH,2);
        String BRANCHHex = String.format("%05X",BRANCHNum);

        if(verbose){
            System.out.println("Instruction Arguments: 0x" + BRANCHHex);
        }
        if(opCode == 28){//B
            //BRANCH is the memory address is decimal bytes
            int decimalBytes = Integer.parseInt(BRANCH,2);
            decimalBytes = decimalBytes/4;
            commandLocation = decimalBytes-1;
        }else if(opCode == 29){//BR
            //nope
        }else if(opCode == 30){//BL
            //nope
        }
    }

    /**
     * Executes instructions of the G format
     * 
     * @param  opCode   op code of the instruction
     */
    private static void executeG(int opCode){
        if(opCode == 31){//PUSH

        }else if(opCode == 32){//POP

        }
    }

    /**
     * Parses characters out of a string and stores them in an arraylist
     * 
     * @param  line   string to parse chars out of
     */
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

    /**
     * Parses bytes out of a string and stores them in an arraylist
     * 
     * @param  line   string to parse bytes out of
     */
    private static void parseBytes(String line){
        byteList = new ArrayList<String>();

        while(line.length() >1){
            String byteString = line.substring(0,2);
            line = line.substring(2,line.length());
            byteList.add(byteString);
        }
    }

    /**
     * Parses singles(command size) out of a string and stores them in an arraylist
     * 
     * @param  line   string to parse singles out of
     */
    private static void parseCommands(String line){
        commandList = new ArrayList<String>();

        while(line.length() >7){
            String commandString = line.substring(0,8);
            line = line.substring(8,line.length());
            commandList.add(commandString);
            System.out.println(commandString);
        }
    }

    /**
     * Parses doubles out of a string and stores them in an arraylist
     * 
     * @param  line   string to parse doubles out of
     */
    private static void parseDoubles(String line){
        doubleList = new ArrayList<String>();

        while(line.length() >15){
            String doubleString = line.substring(0,16);
            line = line.substring(16,line.length());
            doubleList.add(doubleString);
        }
    }

    /**
     * Parses the commands that are in hex into binary and into their own String ArrayList
     * 
     */
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

    /**
     * Parses arguments out of the first line and stores them/sets the processor up
     * 
     * @param  line   header string to parse
     */
    private static void parseHeader(String line){
        String header_args[] = line.split(":");

        charType = header_args[0];
        charType = charType.substring(1);
        System.out.println("\n\n\n\n\n\n\n");

        for(int i = 1; i < header_args.length; i++){
            if(header_args[i].contains("WS")){
                wordSize = Integer.parseInt(header_args[i].substring(3));
                if(verbose){System.out.println("Word Size: " + wordSize);}
            }else if(header_args[i].contains("RC")){
                registerCount = Integer.parseInt(header_args[i].substring(3));
                if(verbose){System.out.println("Register Count: " + registerCount);}

                //initialise all of the registers
                reg = new Registers(registerCount, wordSize);
            }else if(header_args[i].contains("MM")){
                maxmem = Integer.parseInt(header_args[i].substring(5), 16);
                if(verbose){System.out.println("Max Memory: " + maxmem);}
            }else{ //item in the header is a label
                if(header_args[i].contains("main")){
                    mainLoc = header_args[i].substring(7);
                    if(verbose){System.out.println("Main Location: 0x" + mainLoc);}
                }else if(header_args[i].contains("stack")){
                    stackLoc = header_args[i].substring(8);
                    if(verbose){System.out.println("Stack Location: 0x" + stackLoc);}
                }
            }
        }

        //print out the registers
        if(verbose){
            System.out.println();
            reg.printRegisters();
            System.out.println();
            System.out.println();
        }
    }

    /**
     * Gets the decimal value froma  hex string
     * 
     * @param  hex   hex string to parse
     */
    private static int getDecimal(String hex){
        return Integer.parseInt(hex, 16);
    }

    /**
     * ANDs two binary strings together
     * 
     * @param  a   first binary string
     * @param  b   second binary string
     * @return    result string
     */
    private static String ANDBinary(String a, String b){
        //format both strings to the wordsize taking negative numbers into account
        a = formatWordSize(a);
        b = formatWordSize(b);

        //AND the two string together
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < a.length(); i++){
            if(a.charAt(i) == '1' && b.charAt(i) == '1'){
                sb.append('1');
            }else{
                sb.append('0');
            }
        }

        return sb.toString();
    }

    /**
     * ORs two binary strings together
     * 
     * @param  a   first binary string
     * @param  b   second binary string
     * @return    result string
     */
    private static String ORRBinary(String a, String b){
        //format both strings to the wordsize taking negative numbers into account
        a = formatWordSize(a);
        b = formatWordSize(b);

        //ORR the two string together
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < a.length(); i++){
            if(a.charAt(i) == '1' || b.charAt(i) == '1'){
                sb.append('1');
            }else{
                sb.append('0');
            }
        }

        return sb.toString();
    }

    /**
     * EORs two binary strings together
     * 
     * @param  a   first binary string
     * @param  b   second binary string
     * @return    result string
     */
    private static String EORBinary(String a, String b){
        //format both strings to the wordsize taking negative numbers into account
        a = formatWordSize(a);
        b = formatWordSize(b);

        //EOR the two string together
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < a.length(); i++){
            if( (a.charAt(i) == '1' || b.charAt(i) == '1') && ( a.charAt(i) != b.charAt(i) ) ){
                sb.append('1');
            }else{
                sb.append('0');
            }
        }

        return sb.toString();
    }

    /**
     * Locigal/Arithemitic right shift the first string by the second strings value
     * 
     * @param  a   string to shift
     * @param  b   shift amount
     * @return    result string
     */
    private static String LSRBinary(String a, String b){
        //format both strings to the wordsize taking negative numbers into account
        a = formatWordSize(a);
        b = formatWordSize(b);

        Long shiftAmount = Long.parseLong(b,2);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < shiftAmount; i++){
            sb.append(a.substring(0,1));
        }
        sb.append(a);

        String tempA = sb.toString();
        tempA = tempA.substring(0, tempA.length()-(shiftAmount.intValue()));

        return tempA;
    }

    /**
     * Logical left shift a binary string
     * 
     * @param  a   first binary string
     * @param  b   second binary string
     * @return    result string
     */
    private static String LSLBinary(String a, String b){
        //format both strings to the wordsize taking negative numbers into account
        a = formatWordSize(a);
        b = formatWordSize(b);

        Long shiftAmount = Long.parseLong(b,2);

        StringBuilder sb = new StringBuilder();
        sb.append(a);
        for(int i = 0; i < shiftAmount; i++){
            sb.append('0');
        }

        return formatWordSize(sb.toString());
    }

    /**
     * ADD two binary strings together
     * 
     * @param  a   first binary string
     * @param  b   second binary string
     * @return    result string
     */
    private static String addBinary(String a, String b, int opCode) {
        //format both strings to the wordsize taking negative numbers into account
        a = formatWordSize(a);
        b = formatWordSize(b);

        //add the numbers
        StringBuilder sb = new StringBuilder();
        int i=a.length()-1;
        int j=b.length()-1;
        int carry = 0;

        while(i>=0 || j>=0){
            int sum=0;

            if(a.charAt(i)=='1'){sum++;}
            if(b.charAt(j)=='1'){sum++;}

            sum += carry;

            if(sum>=2){carry=1;}
            else{carry=0;}

            sb.insert(0,  (char) ((sum%2) + '0'));

            i--;
            j--;
        }

        //if(carry==1)sb.insert(0, '1');

        if(opCode == 5 || opCode == 6 || opCode == 7 || opCode == 8){
            if(carry==1)vFlag = true; cFlag = true;
        }

        return sb.toString();
    }

    /**
     * Reset the operation flags
     * 
     */
    private static void resetFlags(){
        zFlag = false;
        nFlag = false;
        cFlag = false;
        vFlag = false;
    }

    /**
     * Set the flags based on the passed string
     * 
     * @param  result   string to get flags from
     */
    private static void setFlags(String result){
        int numResult = getTwosCompliment(result);

        if(numResult == 0){
            zFlag = true;
        }else if(numResult < 0){
            nFlag = true;
        }
    }

    /**
     * Get the two's compliment from the passed string
     * 
     * @param  bin   binary string
     * @return    result string (twos compliment)
     */
    private static String twosCompliment(String bin) {
        String twos = "", ones = "";

        for (int i = 0; i < bin.length(); i++) {
            ones += flip(bin.charAt(i));
        }
        Long number0 = Long.parseLong(ones, 2);
        StringBuilder builder = new StringBuilder(ones);
        boolean b = false;
        for (int i = ones.length() - 1; i > 0; i--) {
            if (ones.charAt(i) == '1') {
                builder.setCharAt(i, '0');
            } else {
                builder.setCharAt(i, '1');
                b = true;
                break;
            }
        }
        if (!b)
            builder.append("1", 0, 7);

        twos = builder.toString();

        return twos;
    }

    private static char flip(char c) {
        return (c == '0') ? '1' : '0';
    }

    private static String formatWordSize(String a){
        String tempA = a;

        tempA = String.format(("%" + wordSize + "s"), tempA);
        if(a.substring(0,1).equals("1")){
            tempA = tempA.replace(" ", "1");
        }else{
            tempA = tempA.replace(" ", "0");
        }
        tempA = tempA.substring(tempA.length()-wordSize, tempA.length());

        return tempA;
    }

    private static int getTwosCompliment(String binaryInt) {
        if (binaryInt.charAt(0) == '1') {//string is negative and is a special case
            String invertedInt = invertDigits(binaryInt);
            int decimalValue = Integer.parseInt(invertedInt, 2);
            decimalValue = (decimalValue + 1) * -1;
            return decimalValue;
        } else { //string is positive and can be done normally
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
