/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.db.restoreutility.restorepackage;

/**
 *
 * @author elijah
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import javax.swing.*;    
import java.awt.event.*;    
import java.io.*; 
import java.text.SimpleDateFormat;  
import java.util.Date;
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;  
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  
import java.io.File;  
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import java.util.concurrent.TimeUnit;


public class RestoreDB 
    extends JFrame 
        implements ActionListener
{   
    //variables and jcomponents declaration
    private final Container container; 
    private final JLabel title;  
    private final JMenuBar menubar;    
    private final JMenu file;
    private final JLabel fileinfo; 
    private final JPanel panel;
    private final JTextArea output;
    private final JButton restore; 
    private final JButton reset; 
    private final JButton selectFile;
    private String logs="",filename="",filepath="",dbName="",dbUser= "",dbPass ="", utilityPass="", providedPass="";
    
    RestoreDB()
    {
        setTitle("Eagone Business Solutions"); 
        setBounds(200, 90, 1040, 600); 
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setResizable(false); 
        
        container = getContentPane(); 
        container.setLayout(null); 
        
        title = new JLabel("Client Database Restore Utility"); 
        title.setFont(new Font("Arial", Font.PLAIN, 30)); 
        title.setSize(500, 30); 
        title.setLocation(300, 30); 
        container.add(title);
        
        file=new JMenu("Restore Menu");
        file.setFont(new Font("Arial", Font.PLAIN, 15)); 
        
        
        selectFile=new JButton("Select Restore File");  
        selectFile.setFont(new Font("Arial", Font.PLAIN, 15)); 
        selectFile.setSize(200, 25); 
        selectFile.setLocation(100, 105); 
        selectFile.addActionListener(this); 
        container.add(selectFile); 
        
        
        menubar=new JMenuBar();
        menubar.setSize(700, 30); 
        menubar.setLocation(100, 70); 
        menubar.add(file);                           
        container.add(menubar); 
        
        fileinfo = new JLabel("Selected Restore-file Information and Logs: "); 
        fileinfo.setFont(new Font("Arial", Font.PLAIN, 15)); 
        fileinfo.setSize(400, 20); 
        fileinfo.setLocation(100, 150); 
        container.add(fileinfo); 
        
        output = new JTextArea(); 
        output.setFont(new Font("Arial", Font.PLAIN, 15)); 
        output.setSize(700, 100); 
        output.setLocation(100, 180); 
        output.setLineWrap(true); 
        output.setEditable(false); 
        container.add(output); 
        
        panel = new JPanel();
        panel.setSize(700, 100);
        panel.setLocation(100, 180);
        output.setLineWrap(true); 
        output.setEditable(false); 
        panel.setBackground(Color.white);
        container.add(panel);
        
        restore = new JButton("Restore Now"); 
        restore.setFont(new Font("Arial", Font.PLAIN, 15)); 
        restore.setSize(180, 25); 
        restore.setLocation(290, 300); 
        restore.addActionListener(this); 
        container.add(restore); 
  
        reset = new JButton("Clear Selection"); 
        reset.setFont(new Font("Arial", Font.PLAIN, 15)); 
        reset.setSize(180, 25); 
        reset.setLocation(490, 300); 
        reset.addActionListener(this); 
        container.add(reset); 
         
    }  
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {    
        if(e.getSource()==selectFile)
        {    
            JFileChooser selectedFile = new JFileChooser();  
            int i = selectedFile.showOpenDialog(this);    
            if(i==JFileChooser.APPROVE_OPTION){    
                File f = selectedFile.getSelectedFile();    
                filepath = f.getAbsolutePath();
                filename = f.getName();
                logs = " SELECTED RESTORE FILE: " + filename + "\n" + " FILE LOCATION: " + filepath;
                output.setText(logs);
                output.setEditable(false);
            }    
        }  
        else if(e.getSource()==restore)
        {  

            if(filepath == "")
            {    
                logs = " No file has been selected for restore";
                output.setText(logs);
                output.setEditable(false);
            } 
            else
            {
                try   
                {  
                    //creating a constructor of file class and parsing an XML file  
                    File file = new File("config.xml");  
                    //an instance of factory that gives a document builder  
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
                    //an instance of builder to parse the specified xml file  
                    DocumentBuilder db = dbf.newDocumentBuilder();  
                    Document doc = db.parse(file);  
                    doc.getDocumentElement().normalize();  
                    NodeList nodeList = doc.getElementsByTagName("database");   
                    Node node = nodeList.item(0);    
                        
                    if (node.getNodeType() == Node.ELEMENT_NODE)   
                    {  
                        Element eElement = (Element) node;  
                        dbName =  eElement.getElementsByTagName("dbname").item(0).getTextContent();  
                        dbUser =  eElement.getElementsByTagName("dbuser").item(0).getTextContent();  
                        dbPass = eElement.getElementsByTagName("dbpassword").item(0).getTextContent(); 
                        utilityPass = eElement.getElementsByTagName("utilitypassword").item(0).getTextContent(); 
                        
                        /*NOTE: Used to create a cmd command*/
                        String[] executeCmd = new String[]{"mysql", dbName, "-u" + dbUser, "-p" + dbPass, "-e", " source " + filepath};
                       // String executeCmd = "mysql -u " + dbUser + " -p"+dbPass+" "+dbName + " < " + filepath;
                        //System.out.println("Execute command: "+executeCmd);
                       // System.out.println("Restoring database...");
                        
                        /*NOTE: Executing the command here*/
                        int confirm = JOptionPane.showConfirmDialog(null, "Proceed to restore database?");
                        if(confirm==0)
                        {
                            String logs = " Database restore process. \n"+" Please wait. This process may take up to 10 minutes... \n";
                            //System.out.println(logs);
                            output.setText(logs);
                            output.setEditable(false);
                            providedPass = JOptionPane.showInputDialog("Enter restore PrivateKey:");
                         
                            if(providedPass == null ? utilityPass == null : providedPass.equals(utilityPass))
                            {
                                Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                                int processComplete = runtimeProcess.waitFor();

                                /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
                                if (processComplete == 0) 
                                {
                                    //System.out.println("Restore Complete");
                                    logs = " Restore Complete.";

                                    output.setText(logs);
                                    output.setEditable(false);
                                    JOptionPane.showMessageDialog(null, "Restore Complete", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    //System.out.println("Restore Failed");
                                    logs = " Restore Failed";
                                    output.setText(logs);
                                    output.setEditable(false);
                                    JOptionPane.showMessageDialog(null, "Restore Failed", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(null, "Incorrect restore PrivateKey. Try again", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        
                        
                    }  
                }   
                catch (IOException | ParserConfigurationException | DOMException | SAXException ex)   
                {  
                    ex.printStackTrace();  
                } catch (InterruptedException ex) {
                    Logger.getLogger(RestoreDB.class.getName()).log(Level.SEVERE, null, ex);
                }  
            }
            filepath = "";
            filename = "";
        }
        else if(e.getSource()==reset)
        {    
            filename = "";
            filepath = "";
            System.out.println(filename +""+ filepath);
            logs = " Restore-File Information has been reset";
            output.setText(logs);
            output.setEditable(false);  
        } 
}
    
    public static void main(String[] args) 
    {    
        RestoreDB restore=new RestoreDB();    
        restore.setSize(900,550);     
        restore.setVisible(true);    
        restore.setDefaultCloseOperation(EXIT_ON_CLOSE);    
    }    
}  
