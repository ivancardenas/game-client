
package co.edu.eafit.dis.ui;

import co.edu.eafit.dis.entities.User;

import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class GameRegisterUI extends JFrame {
    
    private final int width = 240, height = 350;
    
    private String name = "", user = "", pass = "";
    
    JLabel labelRegister, labelName, labelUser, labelPass;
    
    JTextField textName, textUser;
    
    JPasswordField textPass;
    
    JButton butEnter, butExit;
    
    Border emptyBorder = BorderFactory.
            createLineBorder(Color.GRAY);
    
    public GameRegisterUI() {
        
        createGameRegisterUI();
    }
    
    private void createGameRegisterUI() {
        
        this.setTitle("Registration");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(2);
        this.getContentPane().setBackground(Color.WHITE);
        
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                new GameLoginUI().setVisible(true);
            }
        });
        
        labelRegister = new JLabel("REGISTRATION");
        labelRegister.setHorizontalAlignment(JLabel.CENTER);
        labelRegister.setFont(new Font("Monospaced", Font.PLAIN, 18));
        labelRegister.setBounds(0, 25, 240, 20);
        
        labelName = new JLabel("Y O U R  N A M E");
        labelName.setHorizontalAlignment(JLabel.CENTER);
        labelName.setFont(new Font("Monospaced", Font.PLAIN, 13));
        labelName.setBounds(0, 60, 240, 20);
        
        textName = new JTextField();
        textName.setHorizontalAlignment(JTextField.CENTER);
        textName.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textName.setBorder(emptyBorder);
        textName.setBounds(30, 90, 180, 30);
         
        labelUser = new JLabel("U S E R N A M E");
        labelUser.setHorizontalAlignment(JLabel.CENTER);
        labelUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelUser.setBounds(0, 130, 240, 20);
        
        textUser = new JTextField();
        textUser.setHorizontalAlignment(JTextField.CENTER);
        textUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textUser.setBorder(emptyBorder);
        textUser.setBounds(30, 160, 180, 30);
        
        labelPass = new JLabel("P A S S W O R D");
        labelPass.setHorizontalAlignment(JLabel.CENTER);
        labelPass.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelPass.setBounds(0, 200, 240, 20);
        
        textPass = new JPasswordField();
        textPass.setHorizontalAlignment(JTextField.CENTER);
        textPass.setFont(new Font("Serif", Font.PLAIN, 12));
        textPass.setEchoChar('*');
        textPass.setBorder(emptyBorder);
        textPass.setBounds(30, 230, 180, 30);
        
        butEnter = new JButton("ENTER");
        butEnter.setHorizontalAlignment(JButton.CENTER);
        butEnter.setFont(new Font("Monospaced", Font.BOLD, 11));
        butEnter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butEnter.setFocusPainted(false);
        butEnter.setBounds(30, 275, 85, 35);
        
        butEnter.addActionListener((ActionEvent e) -> {
            
            name = textName.getText().trim();
            user = textUser.getText().trim();
            pass = textPass.getText().trim();
            
            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                
                JOptionPane.showMessageDialog(null, "Incomplete fields!");
                
                if (pass.isEmpty()) { textPass.requestFocus(); textPass.setText(""); }
                if (user.isEmpty()) { textUser.requestFocus(); textUser.setText(""); }
                if (name.isEmpty()) { textName.requestFocus(); textName.setText(""); }
                
            } else {
                
                if (!User.exist(user)) {
                    
                    if (User.insert(name, user, pass)) {
                        
                        this.dispose();
                        
                        JOptionPane.showMessageDialog(null, "Successful "
                                + "registration!");
                        
                        new GameLoginUI().setVisible(true);
                        
                    } else {
                        
                        JOptionPane.showMessageDialog(null, 
                                "Something went wrong!");
                    }
                } else {
                    
                    JOptionPane.showMessageDialog(null, 
                            "The user is already in use!");
                    
                    textUser.setText(""); textUser.requestFocus();
                }
            }
        });
        
        butExit = new JButton("CANCEL");
        butExit.setHorizontalAlignment(JButton.CENTER);
        butExit.setFont(new Font("Monospaced", Font.BOLD, 11));
        butExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butExit.setFocusPainted(false);
        butExit.setBounds(125, 275, 85, 35);
        
        butExit.addActionListener((ActionEvent e) -> {
            
            this.dispose(); // Close this window.
            new GameLoginUI().setVisible(true);
        });
        
        this.add(labelRegister);
        this.add(labelName);
        this.add(textName);
        this.add(labelUser);
        this.add(textUser);
        this.add(labelPass);
        this.add(textPass);
        this.add(butEnter);
        this.add(butExit);
    }
}