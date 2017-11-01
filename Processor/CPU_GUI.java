/**
 *Text genereted by Simple GUI Extension for BlueJ
 */

import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.border.Border;
import javax.swing.*;
import java.io.*;
/**
 * Creates the GUI for the processor
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class CPU_GUI extends JFrame {
    private JMenuBar menuBar;
    private JButton bttn_reset;
    private JButton bttn_exit;
    private JButton bttn_printState;
    private JButton bttn_runDelay;
    private JButton bttn_runStep;
    private JLabel lbl_registers;
    private JTextField tf_delayTime;
    private JTextArea txt_instructions;
    private JTextArea txt_registers;
    private JScrollPane registerScrollPane;
    private JScrollPane codeScrollPane;
    private Timer timer;

    private boolean programDone = false;
    /**
     * Constructor for objects of class CPU_GUI
     */
    public CPU_GUI(){

        this.setTitle("CPU GUI");
        this.setSize(990,521);
        //menu generate method
        generateMenu();
        this.setJMenuBar(menuBar);

        //pane with null layout
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(990,521));
        contentPane.setBackground(new Color(0,0,255));

        bttn_reset = new JButton();
        bttn_reset.setBounds(445,87,533,35);
        bttn_reset.setBackground(new Color(214,217,223));
        bttn_reset.setForeground(new Color(0,0,0));
        bttn_reset.setEnabled(true);
        bttn_reset.setFont(new Font("sansserif",0,14));
        bttn_reset.setText("Reset");
        bttn_reset.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        bttn_reset.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    resetCPU(evt);
                }
            });

        bttn_runDelay = new JButton();
        bttn_runDelay.setBounds(445,47,273,35);
        bttn_runDelay.setBackground(new Color(214,217,223));
        bttn_runDelay.setForeground(new Color(0,0,0));
        bttn_runDelay.setEnabled(true);
        bttn_runDelay.setFont(new Font("sansserif",0,14));
        bttn_runDelay.setText("Run With Delay");
        bttn_runDelay.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        bttn_runDelay.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    runDelay(evt);
                }
            });

        bttn_runStep = new JButton();
        bttn_runStep.setBounds(445,7,533,35);
        bttn_runStep.setBackground(new Color(214,217,223));
        bttn_runStep.setForeground(new Color(0,0,0));
        bttn_runStep.setEnabled(true);
        bttn_runStep.setFont(new Font("sansserif",0,14));
        bttn_runStep.setText("Run Step");
        bttn_runStep.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        bttn_runStep.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    runStep(evt);
                }
            });

        bttn_printState = new JButton();
        bttn_printState.setBounds(445,127,533,35);
        bttn_printState.setBackground(new Color(214,217,223));
        bttn_printState.setForeground(new Color(0,0,0));
        bttn_printState.setEnabled(true);
        bttn_printState.setFont(new Font("sansserif",0,14));
        bttn_printState.setText("Print State");
        bttn_printState.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        bttn_printState.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    printState(evt);
                }
            });

        bttn_exit = new JButton();
        bttn_exit.setBounds(445,167,533,35);
        bttn_exit.setBackground(new Color(214,217,223));
        bttn_exit.setForeground(new Color(0,0,0));
        bttn_exit.setEnabled(true);
        bttn_exit.setFont(new Font("sansserif",0,14));
        bttn_exit.setText("Exit");
        bttn_exit.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        bttn_exit.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    exitProgram(evt);
                }
            });

        lbl_registers = new JLabel();
        lbl_registers.setBounds(653,198,117,31);
        lbl_registers.setBackground(new Color(214,217,223));
        lbl_registers.setForeground(new Color(255,255,255));
        lbl_registers.setEnabled(true);
        lbl_registers.setFont(new Font("SansSerif",0,18));
        lbl_registers.setText("Registers");
        lbl_registers.setVisible(true);

        tf_delayTime = new JTextField();
        tf_delayTime.setBounds(725,47,251,35);
        tf_delayTime.setBackground(new Color(255,255,255));
        tf_delayTime.setForeground(new Color(0,0,0));
        tf_delayTime.setEnabled(true);
        tf_delayTime.setFont(new Font("sansserif",0,14));
        tf_delayTime.setText("Delay in Seconds");
        tf_delayTime.setVisible(true);

        txt_instructions = new JTextArea();
        //txt_instructions.setBounds(5,5,433,511);
        txt_instructions.setBackground(new Color(255,255,255));
        txt_instructions.setForeground(new Color(0,0,0));
        txt_instructions.setEnabled(true);
        txt_instructions.setFont(new Font("sansserif",0,17));
        txt_instructions.setText("");
        txt_instructions.setBorder(BorderFactory.createBevelBorder(1));
        txt_instructions.setVisible(true);
        txt_instructions.setEditable(false);

        txt_registers = new JTextArea();
        //txt_registers.setBounds(444,225,537,291);
        txt_registers.setBackground(new Color(255,255,255));
        txt_registers.setForeground(new Color(0,0,0));
        txt_registers.setEnabled(true);
        txt_registers.setFont(new Font("sansserif",0,17));
        txt_registers.setText("");
        txt_registers.setBorder(BorderFactory.createBevelBorder(1));
        txt_registers.setVisible(true);
        txt_registers.setEditable(false);

        registerScrollPane = new JScrollPane(txt_registers);
        registerScrollPane.setBounds(444,225,537,291);

        codeScrollPane = new JScrollPane(txt_instructions);
        codeScrollPane.setBounds(5,5,433,511);

        //adding components to contentPane panel
        contentPane.add(bttn_reset);
        contentPane.add(bttn_runDelay);
        contentPane.add(bttn_runStep);
        contentPane.add(lbl_registers);
        contentPane.add(tf_delayTime);
        //contentPane.add(txt_instructions);
        //contentPane.add(txt_registers);
        contentPane.add(registerScrollPane);
        contentPane.add(codeScrollPane);
        contentPane.add(bttn_printState);
        contentPane.add(bttn_exit);

        //adding panel to JFrame and seting of window position and close operation
        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Populates the register text area with the registers and their values
     * 
     * @param   reg Registers Object that the CPU uses
     */
    public void populateRegisters(Registers reg){
        txt_registers.setText("");
        for(int i = 0; i < reg.getLength(); i++){
            txt_registers.append("X" + i + ": " + reg.getReg(i) + "       " + getTwosCompliment(reg.getReg(i)) + "\n");
        }
    }

    /**
     * Populates the instructiosn text area with the assembly code the program is running
     * 
     * @param   fileName   file that the assembly code is in
     */
    public void populateCode(String fileName){
        txt_instructions.setText("");
        //attempt to load the Assembly file
        FileReader assemblyFile;
        try{
            assemblyFile =  new FileReader(fileName);
        }catch(FileNotFoundException e){
            System.out.println("Unable to open the given file. Please make sure that the given file is in the same directory as the program files.");
            return;
        }

        BufferedReader reader = new BufferedReader(assemblyFile);
        String line = null;
        try{
            while(((line = reader.readLine()) != null)){
                line = line.trim();                                 //trim any extra whitespace in the line
                String string_array_1[] = line.split(";", 2);       //parse out the comment
                String command = string_array_1[0];
                command = command.trim();                           //trim any extra whitespace in the command

                txt_instructions.append(command + "\n");
            }
        }catch(IOException e){
            System.out.println("Error reading the file '" + fileName + "'.");
            return;
        }
    }

    /**
     * Helper method to set whether the program is done running
     * 
     * @param   done   boolean value represents if the program is done yet
     */
    public void setProgramDone(boolean done){
        programDone = done;
    }

    //Method mouseClicked for bttn_reset
    private void printState (MouseEvent evt) {
        CPU.printState();
    }

    //Method mouseClicked for bttn_reset
    private void exitProgram (MouseEvent evt) {
        System.exit(0);
    }

    //Method mouseClicked for bttn_reset
    private void resetCPU (MouseEvent evt) {
        if(timer != null){
            timer.stop();
        }
        txt_registers.setText("");
        CPU.resetProgram();
        programDone = false;
    }

    //Method mouseClicked for bttn_runDelay
    private void runDelay (MouseEvent evt) {

        String delay = tf_delayTime.getText();
        double timeDelay = Double.parseDouble(delay);
        timeDelay *= 1000;

        ActionListener timerListener = new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    if(!programDone){
                        CPU.runStep();
                    }

                }
            };

        timer = new Timer((int)timeDelay, timerListener);
        timer.start();

        
    }
    //Method mouseClicked for bttn_runStep
    private void runStep (MouseEvent evt) {
        if(!programDone){
            CPU.runStep();
        }
    }

    private static int getTwosCompliment(String binaryInt) {
        //Check if the number is negative.
        //We know it's negative if it starts with a 1
        if (binaryInt.charAt(0) == '1') {
            //Call our invert digits method
            String invertedInt = invertDigits(binaryInt);
            //Change this to decimal format.
            int decimalValue = Integer.parseInt(invertedInt, 2);
            //Add 1 to the curernt decimal and multiply it by -1
            //because we know it's a negative number
            decimalValue = (decimalValue + 1) * -1;
            //return the final result
            return decimalValue;
        } else {
            //Else we know it's a positive number, so just convert
            //the number to decimal base.
            return Integer.parseInt(binaryInt, 2);
        }
    }

    private static String invertDigits(String binaryInt) {
        String result = binaryInt;
        result = result.replace("0", " "); //temp replace 0s
        result = result.replace("1", "0"); //replace 1s with 0s
        result = result.replace(" ", "1"); //put the 1s back in
        return result;
    }

    //method for generate menu
    public void generateMenu(){
        menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenu tools = new JMenu("Tools");
        JMenu help = new JMenu("Help");

        JMenuItem open = new JMenuItem("Open   ");
        JMenuItem save = new JMenuItem("Save   ");
        JMenuItem exit = new JMenuItem("Exit   ");
        JMenuItem preferences = new JMenuItem("Preferences   ");
        JMenuItem about = new JMenuItem("About   ");

        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exit);
        tools.add(preferences);
        help.add(about);

        menuBar.add(file);
        menuBar.add(tools);
        menuBar.add(help);
    }

    /**
     * Main method for the CPU_GUI class, unused in the CPU class, really just for checking how the gui looks
     * 
     */
    public static void main(String[] args){
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new CPU_GUI();
                }
            });
    }

}