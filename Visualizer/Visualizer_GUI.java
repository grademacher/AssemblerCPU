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
import java.util.*;

/**
 * Write a description of class Visualizer here.
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class Visualizer_GUI extends JFrame {

    private JMenuBar menuBar;
    private JButton btnn_save;
    private JButton bttn_exit;
    private JTextArea txt_memory;
    private JScrollPane memoryScrollPane;

    /**
     * Constructor for objects of class Visualizer_GUI
     */
    public Visualizer_GUI(){

        this.setTitle("GUI_project");
        this.setSize(323,658);
        //menu generate method
        generateMenu();
        this.setJMenuBar(menuBar);

        //pane with null layout
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(323,658));
        contentPane.setBackground(new Color(0,0,255));


        btnn_save = new JButton();
        btnn_save.setBounds(5,616,136,38);
        btnn_save.setBackground(new Color(214,217,223));
        btnn_save.setForeground(new Color(0,0,0));
        btnn_save.setEnabled(true);
        btnn_save.setFont(new Font("SansSerif",0,17));
        btnn_save.setText("Save");
        btnn_save.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        btnn_save.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                fileSave(evt);
            }
        });


        bttn_exit = new JButton();
        bttn_exit.setBounds(168,617,147,36);
        bttn_exit.setBackground(new Color(214,217,223));
        bttn_exit.setForeground(new Color(0,0,0));
        bttn_exit.setEnabled(true);
        bttn_exit.setFont(new Font("SansSerif",0,17));
        bttn_exit.setText("Exit");
        bttn_exit.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        bttn_exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                exitProgram(evt);
            }
        });


        txt_memory = new JTextArea();
        txt_memory.setBackground(new Color(255,255,255));
        txt_memory.setForeground(new Color(0,0,0));
        txt_memory.setEnabled(true);
        txt_memory.setFont(new Font("SansSerif",0,15));
        txt_memory.setText("");
        txt_memory.setBorder(BorderFactory.createBevelBorder(1));
        txt_memory.setVisible(true);
        
        memoryScrollPane = new JScrollPane(txt_memory);
        memoryScrollPane.setBounds(5,5,311,603);

        //adding components to contentPane panel
        contentPane.add(btnn_save);
        contentPane.add(bttn_exit);
        contentPane.add(memoryScrollPane);

        //adding panel to JFrame and seting of window position and close operation
        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    
    /**
     * displays the memory in the memory texta area for the GUI
     * 
     * @param   memory  ArrayList of memory that the method will display
     */
    public void populateMemory(ArrayList<String> memory){
        txt_memory.setText("");
        int memoryCounter = 0;
        for(int i = 0; i < memory.size(); i++){
            String location = "0x" + String.format("%08X",memoryCounter);
            txt_memory.append( location + "      " + memory.get(i).trim() + "\n");
            memoryCounter += 4;
        }
    }

    //Method mouseClicked for btnn_save
    private void fileSave (MouseEvent evt) {
        //refil the memory arraylist from the text area
        String memoryText = txt_memory.getText();
        //System.out.println(memoryText);
        String memory_array[] = memoryText.split("\n");
        ArrayList<String> newMemory = new ArrayList<String>();
        for(int i = 0; i < memory_array.length; i++){
            String memory_split[] = memory_array[i].split(" ",2);
            String hexChunk = memory_split[1];
            hexChunk = hexChunk.trim();
            newMemory.add(hexChunk);
            //System.out.println(hexChunk);
        }
        //pass back the array to a methodd in the visualizer class
        Visualizer.saveFile(newMemory);
    }

    //Method mouseClicked for bttn_exit
    private void exitProgram (MouseEvent evt) {
        System.exit(0);
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



     public static void main(String[] args){
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Visualizer_GUI();
            }
        });
    }

}