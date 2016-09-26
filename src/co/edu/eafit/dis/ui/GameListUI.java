
package co.edu.eafit.dis.ui;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.threads.Games;
import co.edu.eafit.dis.entities.User;
import co.edu.eafit.dis.threads.Users;
import co.edu.eafit.dis.threads.Invitations;

import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class GameListUI extends JFrame {

    private final int width = 600, height = 397;
    
    JLabel labelOnline, labelInvite, labelInvit, 
            labelSent, labelReceiv;
    
    public static JLabel labelCurrent;
    
    static JScrollPane scrollUsers, scrollSentInvit, 
            scrollReceivInvit;
    public static JPanel panelUsers, panelSentInvit, 
            panelReceivInvit;
    
    public static JTextField textInvitation;
    
    JSeparator separator, divide;
    
    public static JButton butInvitation, butExit;
    
    Border emptyBorder = BorderFactory.
            createLineBorder(Color.GRAY);
    
    Color color = new Color(250, 250, 250);
    
    private static Timer timer;

    public GameListUI() {

        createGameListUI();
    }

    private void createGameListUI() {
        
        new Users().start(); // Thread for list users.
        new Invitations("user").start(); // List sent invit.
        new Invitations("player").start(); // List receiv. invit.
        new Games("user").start(); // Background task.
        new Games("player").start(); // Backgrounds task.

        this.setTitle("Dots and Boxes - Start Game");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(2);
        this.getContentPane().setBackground(Color.WHITE);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                User.status("users/".concat(User.getUser()), 0);
                dispose(); // Close this window.
                new GameLoginUI().setVisible(true);
            }
        });

        labelOnline = new JLabel("O N L I N E  U S E R S");
        labelOnline.setHorizontalAlignment(JLabel.CENTER);
        labelOnline.setFont(new Font("Monospaced", Font.PLAIN, 18));
        labelOnline.setBounds(0, 20, 300, 20);
        
        panelUsers = new JPanel();
        panelUsers.setBackground(color);
        panelUsers.setLayout(new BoxLayout(panelUsers, BoxLayout.Y_AXIS));
        
        listOnlineUsers(); // List the first online users.

        scrollUsers = new JScrollPane(panelUsers);
        scrollUsers.setViewportView(panelUsers);
        scrollUsers.setBounds(20, 60, 260, 180);
        scrollUsers.setBorder(null);
        
        labelInvite = new JLabel("S E N D  I N V I T A T I O N");
        labelInvite.setHorizontalAlignment(JLabel.CENTER);
        labelInvite.setFont(new Font("Monospaced", Font.PLAIN, 14));
        labelInvite.setBounds(0, 250, 300, 25);         
        
        textInvitation = new JTextField();
        textInvitation.setBounds(30, 280, 240, 30);
        textInvitation.setHorizontalAlignment(JTextField.CENTER);
        textInvitation.setBorder(emptyBorder);
        textInvitation.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textInvitation.setToolTipText("Username of your opponent!");
                
        butInvitation = new JButton("S E N D");
        butInvitation.setHorizontalAlignment(JButton.CENTER);
        butInvitation.setFont(new Font("Monospaced", Font.BOLD, 11));
        butInvitation.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butInvitation.setFocusPainted(false);
        butInvitation.setBounds(30, 324, 115, 35);
        
        butInvitation.addActionListener((ActionEvent e) -> {
            
            if (!textInvitation.getText().trim().isEmpty()) {
                
                if (User.exist(textInvitation.getText().trim())) {
                    
                    if (User.online(textInvitation.getText().trim())) {
                        
                        String player = textInvitation.getText().trim();
                        
                        String json = "{ \"game\": { \"user_id\": \"" 
                                + User.getUser() + "\", \"status\": 4,"
                                + " \"player\": \"" + player + "\" } }";
                                
                        String response = API.doPOST("games", json, 0);
                        
                        String gameID[] = JSON.getParameter(response, "game_id");
                        
                        textInvitation.setText("");
                        textInvitation.requestFocus();
                        
                        JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "Waiting for answer!");
                        
                        timer = new Timer(1000, (ActionEvent a) -> {
                            
                            String get = API.doGET("games/"+gameID[0]);
                            String status[] = JSON.getParameter(get, "status");
                            
                            if (status[0].equals("1")) {
                                
                                JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "'"+
                                        player + "' has accepted your game!");
                                
                                // Start new game (interface - UI).
                                new GameUI(Integer.parseInt(gameID[0]), 
                                        player).setVisible(true);
                                
                                butInvitation.setEnabled(false);
                                panelReceivInvit.removeAll();
                                panelSentInvit.removeAll();
                                
                                timer.stop(); // Stop retrieving information.
                                
                            } else if (status[0].equals("0")) {
                                
                                JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "'"+
                                        player + "' has rejected your game!");
                                
                                timer.stop(); // Stop retrieving information.
                            }
                        });
                        
                        timer.start();
                        
                    } else {
                        
                        String player = textInvitation.getText().trim();
                        
                        String json = "{ \"game\": { \"user_id\": \"" 
                                + User.getUser() + "\", \"status\": 2,"
                                + " \"player\": \"" + player + "\" } }";
                        
                        API.doPOST("games", json);
                        
                        textInvitation.setText("");
                        textInvitation.requestFocus();
                            
                        // The user is not online.
                        JOptionPane.showMessageDialog(this, 
                                "You invited '" + player + "' to a new game!");
                        
                        listSentInvitations();
                    }
                
                } else {
                    
                    textInvitation.setText(""); 
                    textInvitation.requestFocus();
                    JOptionPane.showMessageDialog(this, 
                            "The user does not exist!");
                }
                
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Complete the field above!");
                textInvitation.setText("");
                textInvitation.requestFocus();
            }
        });
        
        butExit = new JButton("E X I T");
        butExit.setHorizontalAlignment(JButton.CENTER);
        butExit.setFont(new Font("Monospaced", Font.BOLD, 11));
        butExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butExit.setFocusPainted(false);
        butExit.setBounds(155, 324, 115, 35);
        
        butExit.addActionListener((ActionEvent e) -> {
            
            User.status("users/".concat(User.getUser()), 0);
            dispose(); // Close this window.
            new GameLoginUI().setVisible(true);
        });
        
        separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setBounds(295, 20, 10, 337);
        
        labelInvit = new JLabel("I N V I T A T I O N S");
        labelInvit.setHorizontalAlignment(JLabel.CENTER);
        labelInvit.setFont(new Font("Monospaced", Font.PLAIN, 18));
        labelInvit.setBounds(300, 20, 300, 20);
        
        labelSent = new JLabel("S E N T");
        labelSent.setHorizontalAlignment(JLabel.CENTER);
        labelSent.setFont(new Font("Monospaced", Font.PLAIN, 15));
        labelSent.setBounds(300, 60, 300, 20);
        
        panelSentInvit = new JPanel();
        panelSentInvit.setBackground(color);
        panelSentInvit.setLayout(new BoxLayout(panelSentInvit, BoxLayout.Y_AXIS));
        
        listSentInvitations(); // List the first online users.

        scrollSentInvit = new JScrollPane(panelSentInvit);
        scrollSentInvit.setViewportView(panelSentInvit);
        scrollSentInvit.setBounds(320, 90, 260, 110);
        scrollSentInvit.setBorder(null);
        
        labelReceiv = new JLabel("R E C E I V E D");
        labelReceiv.setHorizontalAlignment(JLabel.CENTER);
        labelReceiv.setFont(new Font("Monospaced", Font.PLAIN, 15));
        labelReceiv.setBounds(300, 216, 300, 20);
        
        panelReceivInvit = new JPanel();
        panelReceivInvit.setBackground(color);
        panelReceivInvit.setLayout(new BoxLayout(panelReceivInvit, BoxLayout.Y_AXIS));
        
        listReceivedInvitations(); // List the first online users.
        
        scrollReceivInvit = new JScrollPane(panelReceivInvit);
        scrollReceivInvit.setViewportView(panelReceivInvit);
        scrollReceivInvit.setBounds(320, 246, 260, 110);
        scrollReceivInvit.setBorder(null);
        
        
        divide = new JSeparator(SwingConstants.HORIZONTAL);
        divide.setBounds(20, 375, 560, 10);
        
        labelCurrent = new JLabel();
        
        this.add(labelOnline);
        this.add(scrollUsers);
        this.add(labelInvite);
        this.add(textInvitation);
        this.add(butInvitation);
        this.add(butExit);
        this.add(separator);
        this.add(labelInvit);
        this.add(labelSent);
        this.add(labelReceiv);
        this.add(scrollSentInvit);
        this.add(scrollReceivInvit);
        this.add(divide);
        this.add(labelCurrent);
    }
    
    public static void listOnlineUsers() {
        
        panelUsers.removeAll(); // Remove old users.
        
        String onlineUser[] = User.onlineUser();
        String onlineName[] = User.onlineName();
        
        JLabel labelUsers[] = new JLabel[onlineUser.length];
        
        for (int i = 0; i < onlineUser.length; i++) {
            
            if (onlineUser[i].equals(User.getUser())) continue;
            
            labelUsers[i] = new JLabel("[".concat(onlineName[i])
                    .concat("]: ".concat(onlineUser[i])));
            
            labelUsers[i].setFont(new Font("Monospaced", Font.PLAIN, 12));
            labelUsers[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            labelUsers[i].setBorder(new EmptyBorder(10, 10, 5, 5));
            
            labelUsers[i].setName(onlineUser[i]); // Add listener.
            
            
            labelUsers[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    textInvitation.setText(e.getComponent().getName());
                }
            });
            
            
            
            panelUsers.add(labelUsers[i]);
        }
        
        panelUsers.revalidate();
        panelUsers.repaint();
    }
    
    public static void listSentInvitations() {
        
        panelSentInvit.removeAll(); // Remove old invitations.
        
        int games[] = User.games("user", 2);
        String players[] = User.games("user", "player", 2);
        
        JLabel labelInvitations[] = new JLabel[games.length];
        
        for (int i = 0; i < games.length; i++) {
            
            labelInvitations[i] = new JLabel("["
                    .concat(Integer.toString(games[i]))
                    .concat("]: ").concat(players[i]));
            
            labelInvitations[i].setFont(new Font("Monospaced", Font.PLAIN, 12));
            labelInvitations[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            labelInvitations[i].setBorder(new EmptyBorder(10, 10, 5, 5));
            
            labelInvitations[i].setName(Integer
                    .toString(games[i]) + "," + players[i]);
            
            labelInvitations[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    String props = e.getComponent().getName();

                    String properties[] = props.split(",");

                    int game = Integer.parseInt(properties[0]);
                    String player = properties[1];

                    if (startNewGame(game, "user")) {

                        API.doPUT("games/"+game, " { \"game\": { \"status\": 4 } } ");

                        JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "Waiting for answer!");

                        timer = new Timer(1000, (ActionEvent a) -> {

                            String get = API.doGET("games/"+game);
                            String status[] = JSON.getParameter(get, "status");

                            if (status[0].equals("1")) {

                                JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "'"+
                                        player + "' has accepted your game!");

                                // Start new game (interface - UI).
                                new GameUI(game, player).setVisible(true);

                                butInvitation.setEnabled(false);
                                panelReceivInvit.removeAll();
                                panelSentInvit.removeAll();

                                timer.stop(); // Stop retrieving information.

                            } else if (status[0].equals("0")) {

                                JOptionPane.showMessageDialog(GameListUI.getFrames()[0], "'"+
                                        player + "' has rejected your game!");

                                timer.stop(); // Stop retrieving information.
                            }
                        });

                        timer.start();
                    }
                }
            });
            
            panelSentInvit.add(labelInvitations[i]);
        }
        
        panelSentInvit.revalidate();
        panelSentInvit.repaint();
    }
    
    public static void listReceivedInvitations() {
        
        panelReceivInvit.removeAll(); // Remove old invitations.
        
        int games[] = User.games("player", 2);
        String players[] = User.games("player", "user_id", 2);
        
        JLabel labelInvitations[] = new JLabel[games.length];
        
        for (int i = 0; i < games.length; i++) {
            
            labelInvitations[i] = new JLabel("["
                    .concat(Integer.toString(games[i]))
                    .concat("]: ").concat(players[i]));
            
            labelInvitations[i].setFont(new Font("Monospaced", Font.PLAIN, 12));
            labelInvitations[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            labelInvitations[i].setBorder(new EmptyBorder(10, 10, 5, 5));
            
            labelInvitations[i].setName(Integer
                    .toString(games[i]) + "," + players[i]);
            
            
            labelInvitations[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    String props = e.getComponent().getName();

                    String properties[] = props.split(",");

                    int game = Integer.parseInt(properties[0]);
                    String player = properties[1];

                    if (startNewGame(game, "player")) {

                        API.doPUT("games/"+game, " { \"game\": { \"status\": 3 } } ");

                        JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "Waiting for answer!");

                        timer = new Timer(1000, (ActionEvent a) -> {

                            String get = API.doGET("games/"+game);
                            String status[] = JSON.getParameter(get, "status");

                            if (status[0].equals("1")) {

                                JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "'"+
                                        player + "' has accepted your game!");

                                // Start new game (interface - UI).
                                new GameUI(game, player).setVisible(true);

                                butInvitation.setEnabled(false);
                                panelReceivInvit.removeAll();
                                panelSentInvit.removeAll();

                                timer.stop(); // Stop retrieving information.

                            } else if (status[0].equals("0")) {

                                JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "'"+
                                        player + "' has rejected your game!");

                                timer.stop(); // Stop retrieving information.
                            }
                        });

                        timer.start();
                    }
                }
            });
            
            
            panelReceivInvit.add(labelInvitations[i]);
        }
        
        panelReceivInvit.revalidate();
        panelReceivInvit.repaint();
    }
    
    private static boolean startNewGame(int gameID, String scope) {
        
        String response = API.doGET("games/".concat(String.valueOf(gameID)));
        
        switch(scope) {
            
            case "user":
                
                String user[] = JSON.getParameter(response, "player");
                
                if (User.online(user[0])) return true;
                else JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), 
                        "The user is not online!");
                
                break;
            
            case "player":
                
                String player[] = JSON.getParameter(response, "user_id");
                
                if (User.online(player[0])) return true;
                else JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), 
                        "The user is not online!");
                
                break;
        }
        
        return false;
    }
}