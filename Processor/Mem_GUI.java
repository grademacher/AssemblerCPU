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
 * The memory GUI displays the current state of the internal CPU memory
 * 
 * @author Garrett Rademacher
 * @version 10/22/2017
 */
public class Mem_GUI extends JFrame {

    private JMenuBar menuBar;
    private JTextArea txt_memoryDisplay;
    private JScrollPane memoryScrollPane;

    /**
     * Constructor for objects of class Mem_GUI
     */
    public Mem_GUI(){

        this.setTitle("Memory GUI");
        this.setSize(403,639);
        //menu generate method
        generateMenu();
        this.setJMenuBar(menuBar);

        //pane with null layout
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(403,639));
        contentPane.setBackground(new Color(0,0,255));


        txt_memoryDisplay = new JTextArea();
        //txt_memoryDisplay.setBounds(5,5,392,629);
        txt_memoryDisplay.setBackground(new Color(255,255,255));
        txt_memoryDisplay.setForeground(new Color(0,0,0));
        txt_memoryDisplay.setEnabled(true);
        txt_memoryDisplay.setFont(new Font("SansSerif",0,17));
        txt_memoryDisplay.setText("");
        txt_memoryDisplay.setBorder(BorderFactory.createBevelBorder(1));
        txt_memoryDisplay.setVisible(true);
        
        memoryScrollPane = new JScrollPane(txt_memoryDisplay);
        memoryScrollPane.setBounds(5,5,392,629);

        //adding components to contentPane panel
        contentPane.add(memoryScrollPane);

        //adding panel to JFrame and seting of window position and close operation
        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
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
     * populates the memory text area with the memory and location
     */
    public void populateMemory(ArrayList<String> memory){
        txt_memoryDisplay.setText("");
        int memoryCounter = 0;
        for(int i = 0; i < memory.size(); i++){
            String location = "0x" + String.format("%08X",memoryCounter);
            txt_memoryDisplay.append( location + "      " + memory.get(i).trim() + "\n");
            memoryCounter += 4;
        }
    }



    /**
     * main method for the memory GUI, un-used
     */
     public static void main(String[] args){
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Mem_GUI();
            }
        });
    }

}