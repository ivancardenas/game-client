
package co.edu.eafit.dis.ui;

import co.edu.eafit.dis.entities.User;
import co.edu.eafit.dis.threads.Users;

import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class GameLoginUI extends JFrame {
    
    private final int width = 460, height = 300;
    
    private String user = "", pass = "";
    
    JLabel labelGameIcon, labelGameName, 
            labelUser, labelPass, labelLogin;
    
    JTextField textUser;
    
    JPasswordField textPass;
    
    JButton butEnter, butExit;
    
    ImageIcon imageGameIcon;
    
    Border emptyBorder = BorderFactory.
            createLineBorder(Color.GRAY);
    
    public GameLoginUI() {
        
        createGameLoginUI();
    }
    
    private void createGameLoginUI() {
        
        this.setTitle("Dots and Boxes");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);
        this.getContentPane().setBackground(Color.WHITE);
        
        imageGameIcon = new ImageIcon(getClass().getResource
            ("/co/edu/eafit/dis/res/dots_and_boxes.png"));
        labelGameIcon = new JLabel(imageGameIcon);
        labelGameIcon.setBounds(30, 30, imageGameIcon.getIconWidth(),
                imageGameIcon.getIconHeight());
        
        labelGameName = new JLabel("D O T S  A N D  B O X E S");
        labelGameName.setBounds(30, 220, imageGameIcon.getIconWidth(), 20);
        labelGameName.setHorizontalAlignment(JLabel.CENTER);
        labelGameName.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        labelUser = new JLabel("U S E R N A M E");
        labelUser.setHorizontalAlignment(JLabel.CENTER);
        labelUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelUser.setBounds(230, 30, 200, 20);
        
        textUser = new JTextField();
        textUser.setHorizontalAlignment(JTextField.CENTER);
        textUser.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textUser.setBorder(emptyBorder);
        textUser.setBounds(230, 60, 200, 30);
        
        labelPass = new JLabel("P A S S W O R D");
        labelPass.setHorizontalAlignment(JLabel.CENTER);
        labelPass.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelPass.setBounds(230,105,200,20);
        
        textPass = new JPasswordField("");
        textPass.setHorizontalAlignment(JTextField.CENTER);
        textPass.setFont(new Font("Serif", Font.PLAIN, 13));
        textPass.setEchoChar('*');
        textPass.setBorder(emptyBorder);
        textPass.setBounds(230, 135, 200, 30);
        
        labelLogin = new JLabel("Create an account");
        labelLogin.setHorizontalAlignment(JLabel.CENTER);
        labelLogin.setFont(new Font("Monospaced", Font.ITALIC, 10));
        labelLogin.setForeground(Color.BLUE);
        labelLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelLogin.setBounds(230, 170, 200, 20);
        
        butEnter = new JButton("E N T E R");
        butEnter.setHorizontalAlignment(JButton.CENTER);
        butEnter.setFont(new Font("Monospaced", Font.BOLD, 11));
        butEnter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butEnter.setFocusPainted(false);
        butEnter.setBounds(230, 205, 100, 35);
        
        butEnter.addActionListener((ActionEvent e) -> {
            
            user = textUser.getText().trim();
            pass = textPass.getText().trim();
            
            if (user.isEmpty() || pass.isEmpty()) {
                
                JOptionPane.showMessageDialog(null, "Incomplete fields!");
                
                if (user.isEmpty()) { textUser.requestFocus(); textUser.setText(""); }
                if (pass.isEmpty()) { textPass.requestFocus(); textPass.setText(""); }
                
            } else if (!User.online(user)) {
                
                if (User.validate(user, pass)) { // It does correctly.
                    
                    if (User.status("users/".concat(user), 1)) {
                        
                        User.setUser(user); // Config. global var.
                        
                        this.dispose(); // Close this window.
                        
                        new GameListUI().setVisible(true);
                    }
                    
                } else {
                    
                    textUser.setText(""); textPass.setText("");
                        
                    JOptionPane.showMessageDialog(null, 
                            "Username or password incorrect!");
                    
                    textUser.requestFocus();
                }
            } else {
                
                textPass.requestFocus(); textPass.setText("");
                textUser.requestFocus(); textUser.setText("");
                
                JOptionPane.showMessageDialog(null, "You are already online!");
            }
        });
        
        butExit = new JButton("E X I T");
        butExit.setHorizontalAlignment(JButton.CENTER);
        butExit.setFont(new Font("Monospaced", Font.BOLD, 11));
        butExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butExit.setFocusPainted(false);
        butExit.setBounds(330, 205, 100, 35);
        
        butExit.addActionListener((ActionEvent e) -> {
            
            System.exit(0); // Leave execution.
        });
        
        labelLogin.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                
                dispose(); // Close while registration.
                new GameRegisterUI().setVisible(true);
            }
        });
        
        this.add(labelUser);
        this.add(labelPass);
        this.add(textUser);
        this.add(textPass);
        this.add(labelLogin);
        this.add(butEnter);
        this.add(butExit);
        this.add(labelGameIcon);
        this.add(labelGameName);
    }
}