
package co.edu.eafit.dis.ui;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.entities.User;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;

import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class GameUI extends JFrame {

    private static int gameID; // Game ID for the instance.

    private final String player; // Player for the game.

    private final int width = 400, height = 530;

    public static JPanel gamePanel = new JPanel();

    private int pointX = 0, pointY = 0, moveCount = 0;

    private static int squareCount = 0;

    Point actualPoint = null, finalPoint = null;

    JLabel labelPUser, labelPPlayer, labelUser, labelPlayer;

    static int userq = 0, playerq = 0, userp, playerp;

    private JLabel labelExitGame;

    private HashMap<Integer, List<Integer>> oldPoints = new HashMap<>();
    private HashMap<Integer, List<Integer>> newPoints = new HashMap<>();

    private Timer timer;

    private final boolean gameOwner;

    static int flag = 0, flagn = 0;

    private JLabel dotsArray[][];
    
    public GameUI(int gameID, String player) {

        GameUI.gameID = gameID;
        this.player = player;

        gameOwner = setGameOwner();

        GameListUI.labelCurrent.setText("C U R R E N T  G A M E : "
                + player.toUpperCase());

        createGameUI();
    }

    @Override
    public void paint(Graphics g) {

        gameOver(); // Has the game ended?
        isItMyTurn();
        putScoreResult();

        super.paint(g); // Overlap paint.

        int lines[] = getDrawnLines();

        int dis = (gamePanel.getWidth() - 8) / 5;

        for (int i = 0; i < lines.length / 4; i++) {

            int x0 = lines[i * 4];
            int y0 = lines[i * 4 + 1];
            int x1 = lines[i * 4 + 2];
            int y1 = lines[i * 4 + 3];

            g.drawLine(x0 * dis + 33, y0 * dis + 57,
                    x1 * dis + 33, y1 * dis + 57);
        }
    }

    public boolean isGamePlayerOpen(String player) {

        String response = API.doGET("games/" + gameID);
        String status[] = JSON.getParameter(response, "status");

        return status[0].equals("0");
    }

    private void createGameUI() {

        flag = 0;
        flagn = 0;
        squareCount = 0;

        oldPoints = new HashMap<>();
        fillPoints(oldPoints);

        timer = new Timer(1000, (ActionEvent e) -> {

            newPoints = new HashMap<>();
            fillPoints(newPoints);

            if (newMove(gameID)) {

                oldPoints = new HashMap<>(newPoints);

                repaint();
            }

            fillPoints(oldPoints);

            if (isGamePlayerOpen(player)) {

                dispose();

                JOptionPane.showMessageDialog(GameListUI.labelCurrent.getParent(), "The game has ended!");

                GameListUI.butInvitation.setEnabled(true);
                GameListUI.listReceivedInvitations();
                GameListUI.listSentInvitations();

                timer.stop();
            }

        });
        timer.start();

        this.setTitle("[ " + player.replace("", " ").trim() + " ]");
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(null);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(0);
        this.getContentPane().setBackground(Color.WHITE);

        this.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if (JOptionPane.showConfirmDialog(GameUI.gamePanel,
                        "Do you want to leave the game?", "",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    API.doPUT("games/" + gameID,
                            " { \"game\": { \"status\": 0 } } ");
                    dispose();

                }
            }
        });

        gamePanel.setBounds(30, 30, this.getWidth() - 60,
                this.getWidth() - 60);
        gamePanel.setBackground(Color.WHITE);
        gamePanel.setLayout(null);

        dotsArray = paintDots();

        int rows = 5, cols = 5; // # rows and cols.

        for (int i = 0; i <= rows; i++) {

            for (int j = 0; j <= cols; j++) {

                Point x = dotsArray[i][j].getLocation();
                Point y = dotsArray[i][j].getLocation();

                dotsArray[i][j].addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        
                        int dis = (gamePanel.getWidth() - 8) / (cols);

                        if (actualPoint != null) {

                            setPointX(((int) x.getX()) / dis); // Set final.
                            setPointY(((int) y.getY()) / dis); // Set final.

                            finalPoint = new Point(getPointX(), getPointY());

                            if (validateLine(actualPoint, finalPoint)) {
                                
                                long startTime = System.currentTimeMillis();

                                int x0 = (int) actualPoint.getX();
                                int y0 = (int) actualPoint.getY();
                                int x1 = (int) finalPoint.getX();
                                int y1 = (int) finalPoint.getY();

                                insertPoints(x0, y0, x1, y1);
                                doMove(gameOwner, validateSquare(x0, y0, x1, y1));

                                if (validateSquare(x0, y0, x1, y1)) {

                                    countSquares(x0, y0, x1, y1); // Increase square count.
                                    insertNewSquare(squareCount); // I did one.
                                }

                                flag = 0; flagn = 0; repaint();
                                
                                long elapsedTime = System.currentTimeMillis() - startTime;
                                System.out.println("Gaming response time: " + elapsedTime + "ms");

                            } else {
                                JOptionPane.showMessageDialog(
                                        GameUI.gamePanel, "Invalid movement!");
                            }

                            actualPoint = null;
                            finalPoint = null;

                        } else {

                            setPointX(((int) x.getX()) / dis); // Set initial.
                            setPointY(((int) y.getY()) / dis); // Set initial.

                            actualPoint = new Point(getPointX(), getPointY());
                        }
                        
                        
                    }
                });

            }
        }

        labelUser = new JLabel(User.getUser().toUpperCase().replace("", " ").trim());
        labelUser.setBounds(0, 405, 200, 20);
        labelUser.setHorizontalAlignment(JLabel.CENTER);
        labelUser.setFont(new Font("Monospace", Font.PLAIN, 20));
        labelUser.setForeground(Color.BLUE);

        labelPUser = new JLabel(Integer.toString(userp));
        labelPUser.setBounds(0, 440, 200, 40);
        labelPUser.setHorizontalAlignment(JLabel.CENTER);
        labelPUser.setFont(new Font("Monospace", Font.PLAIN, 50));
        labelPUser.setForeground(Color.BLUE);

        labelPlayer = new JLabel(player.toUpperCase().replace("", " ").trim());
        labelPlayer.setBounds(200, 405, 200, 20);
        labelPlayer.setHorizontalAlignment(JLabel.CENTER);
        labelPlayer.setFont(new Font("Monospace", Font.PLAIN, 20));
        labelPlayer.setForeground(Color.RED);

        labelPPlayer = new JLabel(Integer.toString(playerp));
        labelPPlayer.setBounds(200, 440, 200, 40);
        labelPPlayer.setHorizontalAlignment(JLabel.CENTER);
        labelPPlayer.setFont(new Font("Monospace", Font.PLAIN, 50));
        labelPPlayer.setForeground(Color.RED);

        labelExitGame = new JLabel("E X I T");
        labelExitGame.setBounds(20, 460, 360, 20);
        labelExitGame.setHorizontalAlignment(JLabel.CENTER);
        labelExitGame.setFont(new Font("Monospace", Font.ITALIC, 16));

        labelExitGame.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (JOptionPane.showConfirmDialog(GameUI.gamePanel,
                        "Do you want to leave the game?", "",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    API.doPUT("games/" + gameID,
                            " { \"game\": { \"status\": 0 } } ");
                    dispose();

                }
            }
        });

        this.add(gamePanel);
        this.add(labelUser);
        this.add(labelPlayer);
        this.add(labelPUser);
        this.add(labelPPlayer);
        this.add(labelExitGame);
    }

    private void countSquares(int x0, int y0, int x1, int y1) {

        if (lineExists(x0, y0, x0, y0 + 1)
                || lineExists(x0, y0 + 1, x0, y0)) {
            if (lineExists(x0, y0 + 1, x1, y1 + 1)
                    || lineExists(x1, y1 + 1, x0, y0 + 1)) {
                if (lineExists(x1, y1 + 1, x1, y1)
                        || lineExists(x1, y1, x1, y1 + 1)) {

                    if (x1 > x0) squareCount++;
                    else if (x0 > x1) squareCount++;
                }
            }
        }
        
        if (lineExists(x0, y0, x0, y0 - 1)
                || lineExists(x0, y0 - 1, x0, y0)) {
            if (lineExists(x0, y0 - 1, x1, y1 - 1)
                    || lineExists(x1, y1 - 1, x0, y0 - 1)) {
                if (lineExists(x1, y1 - 1, x1, y1)
                        || lineExists(x1, y1, x1, y1 - 1)) {

                    if (x1 > x0) squareCount++;
                    else if (x0 > x1) squareCount++;
                }
            }
        }
        
        if (lineExists(x0, y0, x0 + 1, y0)
                || lineExists(x0 + 1, y0, x0, y0)) {
            if (lineExists(x0 + 1, y0, x1 + 1, y1)
                    || lineExists(x1 + 1, y1, x0 + 1, y0)) {
                if (lineExists(x1 + 1, y1, x1, y1)
                        || lineExists(x1, y1, x1 + 1, y1)) {

                    if (y1 > y0) squareCount++;
                    else if (y0 > y1) squareCount++;
                }
            }
        }
        
        if (lineExists(x0, y0, x0 - 1, y0)
                || lineExists(x0 - 1, y0, x0, y0)) {
            if (lineExists(x0 - 1, y0, x1 - 1, y1)
                    || lineExists(x1 - 1, y1, x0 - 1, y0)) {
                if (lineExists(x1 - 1, y1, x1, y1)
                        || lineExists(x1, y1, x1 - 1, y1)) {

                    if (y1 > y0) squareCount++;
                    else if (y0 > y1) squareCount++;
                }
            }
        }
    }

    private void insertPoints(int x0, int y0, int x1, int y1) {

        String json = "{ \"point\": { \"game_id\": " + gameID + ", \"x0\": "
                + x0 + ", \"y0\": " + y0 + ", \"x1\": "
                + x1 + ", \"y1\": " + y1 + " } }";

        API.doPOST("points", json); // Insert line points into database.

        // if (API.doPOST("points", json) == 201) System.out.println("OK");
    }

    // # of moves that a player has done. - Update database.
    private void doMove(boolean gameOwner, boolean square) {

        if (!square) {
            moveCount++; // Increment moves count.
        }
        if (gameOwner) {

            String json = "{ \"game\": { \"userp\": " + moveCount + " } }";
            API.doPUT("games/" + gameID, json); // Store request status.

        } else {

            String json = "{ \"game\": { \"playerp\": " + moveCount + " } }";
            API.doPUT("games/" + gameID, json); // Store request status.
        }
    }

    private void insertNewSquare(int squareCount) {

        if (gameOwner) {

            String json = " { \"game\": { \"userq\": " + squareCount + " } } ";
            API.doPUT("games/" + gameID, json); // Store update status.

        } else {

            String json = " { \"game\": { \"playerq\": " + squareCount + " } } ";
            API.doPUT("games/" + gameID, json); // Store update status.
        }
    }

    // Are there a square in the last move? - Checking on the database.
    private boolean validateSquare(int x0, int y0, int x1, int y1) {

        if (lineExists(x0, y0, x0, y0 + 1)
                || lineExists(x0, y0 + 1, x0, y0)) {
            if (lineExists(x0, y0 + 1, x1, y1 + 1)
                    || lineExists(x1, y1 + 1, x0, y0 + 1)) {
                if (lineExists(x1, y1 + 1, x1, y1)
                        || lineExists(x1, y1, x1, y1 + 1)) {

                    if (x1 > x0) return true;
                    else if (x0 > x1) return true;
                }
            }
        }
        
        if (lineExists(x0, y0, x0, y0 - 1)
                || lineExists(x0, y0 - 1, x0, y0)) {
            if (lineExists(x0, y0 - 1, x1, y1 - 1)
                    || lineExists(x1, y1 - 1, x0, y0 - 1)) {
                if (lineExists(x1, y1 - 1, x1, y1)
                        || lineExists(x1, y1, x1, y1 - 1)) {

                    if (x1 > x0) return true;
                    else if (x0 > x1) return true;
                }
            }
        }
        
        if (lineExists(x0, y0, x0 + 1, y0)
                || lineExists(x0 + 1, y0, x0, y0)) {
            if (lineExists(x0 + 1, y0, x1 + 1, y1)
                    || lineExists(x1 + 1, y1, x0 + 1, y0)) {
                if (lineExists(x1 + 1, y1, x1, y1)
                        || lineExists(x1, y1, x1 + 1, y1)) {

                    if (y1 > y0) return true;
                    else if (y0 > y1) return true;
                }
            }
        }
        
        if (lineExists(x0, y0, x0 - 1, y0)
                || lineExists(x0 - 1, y0, x0, y0)) {
            if (lineExists(x0 - 1, y0, x1 - 1, y1)
                    || lineExists(x1 - 1, y1, x0 - 1, y0)) {
                if (lineExists(x1 - 1, y1, x1, y1)
                        || lineExists(x1, y1, x1 - 1, y1)) {

                    if (y1 > y0) return true;
                    else if (y0 > y1) return true;
                }
            }
        }

        return false;
    }

    // Does the line exist into database registers?
    private boolean lineExists(int x0, int y0, int x1, int y1) {

        String response = API.doGET("points?game=" + gameID);

        String intX0[] = JSON.getParameter(response, "x0");
        String intY0[] = JSON.getParameter(response, "y0");
        String intX1[] = JSON.getParameter(response, "x1");
        String intY1[] = JSON.getParameter(response, "y1");

        for (int i = 0; i < intX0.length; i++) {

            int actX0 = Integer.parseInt(intX0[i]);
            int actY0 = Integer.parseInt(intY0[i]);
            int actX1 = Integer.parseInt(intX1[i]);
            int actY1 = Integer.parseInt(intY1[i]);

            if (x0 == actX0 && y0 == actY0) {
                if (x1 == actX1 && y1 == actY1) {
                    return true;
                }
            }
        }

        return false;
    }

    // Validate line. - Local (given an final and initial point).
    private boolean validateLine(Point iP, Point fP) {

        // iP: initial point, fP: final point.
        boolean lineStatus = false;

        if (iP.getX() == fP.getX()) {

            if (iP.getY() > fP.getY()) {

                if (iP.getY() - fP.getY() == 1) lineStatus = true;
                
            } else if (iP.getY() < fP.getY()) {

                if (fP.getY() - iP.getY() == 1) lineStatus = true;
            }

        } else if (iP.getY() == fP.getY()) {

            if (iP.getX() > fP.getX()) {

                if (iP.getX() - fP.getX() == 1) lineStatus = true;
                
            } else if (iP.getX() < fP.getX()) {

                if (fP.getX() - iP.getX() == 1) lineStatus = true;
            }
        }

        return lineStatus;
    }

    public void isItMyTurn() {

        String response = API.doGET("games/" + gameID);
        String getUserP[] = JSON.getParameter(response, "userp");
        String getPlayerP[] = JSON.getParameter(response, "playerp");

        int userTurn = Integer.parseInt(getUserP[0]);
        int playerTurn = Integer.parseInt(getPlayerP[0]);

        if (gameOwner) {

            // I am the user.
            if (userTurn == playerTurn) {

                this.setEnabled(true);
                this.getContentPane().setBackground(Color.WHITE);
                gamePanel.setBackground(Color.WHITE);

                if (flag++ == 0) {
                    JOptionPane
                            .showMessageDialog(this, "It's your turn!");
                }

            } else {

                this.setEnabled(false);
                this.getContentPane().setBackground(Color.GRAY);
                gamePanel.setBackground(Color.GRAY);

                if (flagn++ == 0) {
                    JOptionPane
                            .showMessageDialog(this, player + "'s turn!");
                }
            }

            if (userTurn > playerTurn) {

                this.setEnabled(false);
                this.getContentPane().setBackground(Color.GRAY);
                gamePanel.setBackground(Color.GRAY);

                if (flagn++ == 0) {
                    JOptionPane
                            .showMessageDialog(this, player + "'s turn!");
                }
            }

        } else {

            // I am the player.
            if (userTurn == playerTurn) {

                this.setEnabled(false);
                this.getContentPane().setBackground(Color.GRAY);
                gamePanel.setBackground(Color.GRAY);

                if (flagn++ == 0) {
                    JOptionPane
                            .showMessageDialog(this, player + "'s turn!");
                }

            } else {

                this.setEnabled(true);
                this.getContentPane().setBackground(Color.WHITE);
                gamePanel.setBackground(Color.WHITE);

                if (flag++ == 0) {
                    JOptionPane
                            .showMessageDialog(this, "It's your turn!");
                }
            }

            if (userTurn > playerTurn) {

                this.setEnabled(true);
                this.getContentPane().setBackground(Color.WHITE);
                gamePanel.setBackground(Color.WHITE);

                if (flag++ == 0) {
                    JOptionPane
                            .showMessageDialog(this, "It's your turn!");
                }
            }
        }
    }

    private void putScoreResult() {

        String response = API.doGET("games/" + gameID);

        String resUser[] = JSON.getParameter(response, "userq");
        String resPlayer[] = JSON.getParameter(response, "playerq");

        userq = Integer.parseInt(resUser[0]);
        playerq = Integer.parseInt(resPlayer[0]);

        if (gameOwner) {

            labelPUser.setText(Integer.toString(userq));
            labelPPlayer.setText(Integer.toString(playerq));

        } else {

            labelPUser.setText(Integer.toString(playerq));
            labelPPlayer.setText(Integer.toString(userq));
        }
    }

    private void gameOver() {

        if ((userq + playerq) == 25) {

            if (userq > playerq && gameOwner) {
                JOptionPane.showMessageDialog(this,
                        "YOU WON!!!\nGamer over.");
                // exitGame(gameID); setWinner(user);

            } else if (userq < playerq && gameOwner) {
                JOptionPane.showMessageDialog(this,
                        "YOU LOSE!!!\nGamer over.");
                // exitGame(gameID); setWinner(user);

            } else if (userq < playerq && !gameOwner) {
                JOptionPane.showMessageDialog(this,
                        "YOU WON!!!\nGamer over.");
                // exitGame(gameID); setWinner(player);

            } else if (userq > playerq && !gameOwner) {
                JOptionPane.showMessageDialog(this,
                        "YOU LOSE!!!\nGamer over.");
                // exitGame(gameID); setWinner(player);
            }

            dispose();
        }
    }

    private JLabel[][] paintDots() {

        int rows = 5, cols = 5; // Edit # rows and cols.

        JLabel dots[][] = new JLabel[rows + 1][cols + 1];

        for (int i = 0; i <= rows; i++) {

            for (int j = 0; j <= cols; j++) {

                dots[i][j] = new JLabel("•");
                dots[i][j].setCursor(new Cursor(Cursor.HAND_CURSOR));
                dots[i][j].setSize(10, 10);

                int x = (gamePanel.getWidth() - 8) / (cols) * j;
                int y = (gamePanel.getWidth() - 8) / (rows) * i;

                dots[i][j].setLocation(x, y);

                gamePanel.add(dots[i][j]);
            }
        }

        return dots;
    }

    private boolean setGameOwner() {

        String response = API.doGET("games/" + gameID);

        String owner[] = JSON.getParameter(response, "user_id");

        return owner[0].equals(User.getUser());
    }

    private int[] getDrawnLines() {

        String response = API.doGET("points?game=" + gameID);

        String strX0[] = JSON.getParameter(response, "x0");
        String strY0[] = JSON.getParameter(response, "y0");
        String strX1[] = JSON.getParameter(response, "x1");
        String strY1[] = JSON.getParameter(response, "y1");

        int games[] = new int[strX0.length * 4];

        for (int i = 0; i < strX0.length; i++) {

            games[i * 4] = Integer.parseInt(strX0[i]);
            games[i * 4 + 1] = Integer.parseInt(strY0[i]);
            games[i * 4 + 2] = Integer.parseInt(strX1[i]);
            games[i * 4 + 3] = Integer.parseInt(strY1[i]);
        }

        return games;
    }

    private int getPointX() {
        return pointX;
    }

    private int getPointY() {
        return pointY;
    }

    private void setPointX(int pointX) {
        this.pointX = pointX;
    }

    private void setPointY(int pointY) {
        this.pointY = pointY;
    }

    private void fillPoints(HashMap<Integer, List<Integer>> points) {

        ArrayList<Integer> movesPerGame = new ArrayList<>();

        String moves, countX0[],
                countY0[], countX1[], countY1[];

        moves = API.doGET("points?game=".
                concat(String.valueOf(gameID)));

        countX0 = JSON.getParameter(moves, "x0");
        countY0 = JSON.getParameter(moves, "y0");
        countX1 = JSON.getParameter(moves, "x1");
        countY1 = JSON.getParameter(moves, "y1");

        for (int j = 0; j < countX0.length; j++) {

            movesPerGame.add(Integer.parseInt(countX0[j]));
            movesPerGame.add(Integer.parseInt(countY0[j]));
            movesPerGame.add(Integer.parseInt(countX1[j]));
            movesPerGame.add(Integer.parseInt(countY1[j]));
        }

        points.put(gameID, movesPerGame);

    }

    private boolean newMove(int gameID) {

        Iterator<HashMap.Entry<Integer, List<Integer>>> entries;
        HashMap.Entry<Integer, List<Integer>> entry;

        entries = oldPoints.entrySet().iterator();

        int oldCount = 0, newCount = 0;

        while (entries.hasNext()) {

            entry = entries.next();

            if (entry.getKey().equals(gameID)) {
                oldCount = entry.getValue().size();
            }
        }

        entries = newPoints.entrySet().iterator();

        while (entries.hasNext()) {

            entry = entries.next();

            if (entry.getKey().equals(gameID)) {
                newCount = entry.getValue().size();
            }
        }

        return oldCount < newCount;
    }
}
