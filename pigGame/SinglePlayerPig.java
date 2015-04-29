import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.util.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class SinglePlayerPig {
    //Global to keep track of the current roll
    private int rollVall;
    //Dice objec tto be rolled
    private Dice stopDropAnd;

    //Static messages
    public static final String CT = "Current Turn: ";

    private JFrame gameFrame;
    //seperate window to not clutter up the game frame.
    private JFrame statsFrame;

    private JPanel gamePanel;

    //Game control buttons
    private JButton     rollButton;
    private JButton     bankButton;
    //Text that is continuously updated.
    private JLabel currScoreField;
    private JLabel nameField;
    private JLabel scoreField;
    private JTextArea narratorText;

    private ArrayList<Image> diceImages;
    private Image bankIcon;

    private JLabel playerNameStats;
    private JLabel playerScoreStats;

    private Map<String, Player> playerList;
    private Player currentPlayer;
    private String myID;


    private Socket sock;
    private InputStream inputStream; 
    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    private ArrayList<MessageHandler> handlersArray;

    public static void main(String[] args){

        SinglePlayerPig spp = new SinglePlayerPig();
        spp.loadGame();
    }

    /* 
     * Starts setting up the frame
     * Most of the meat happens here.
     */
    public void loadGame(){
        playerList = new HashMap<String, Player>();

        //Loads current player getting his/her name.
        //currentPlayer = new Player(getPlayerName());
        System.err.println(currentPlayer.getName());

        //Creates a dice objec tto be used all game.
        stopDropAnd = new Dice();

        //Loads the blank, and 1-6 dice images.
        loadDiceImages();

        /* 
         * Creates the game frame which is the main
         * part of the GUI
         */
        gameFrame = new JFrame();
        gameFrame.setJMenuBar(getMenuBar());
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Adding the panels
        gameFrame.getContentPane().add(BorderLayout.CENTER, getPanels());
        gameFrame.getContentPane().add(BorderLayout.SOUTH,  getNarratorPanel());

        bankButton.setEnabled(false);


        System.err.println("Attempting to connect to the Pig Server");

        setupNetworking();

        sendMessage(getJoinGameMessage());
        Thread t = new Thread(new IncomingReader());
        t.start();

        //Make Visible
        gameFrame.setTitle(CT + currentPlayer.getName());
        gameFrame.setVisible(true);
        gameFrame.setSize(424,355);

    }

    /*
     * Loads the name of the player who is starting
     * the game using a JOptionPane
     * @returns <code>String</code> of player's name
     */
    public String getPlayerName(){
        String name = "";
        while(name.length() < 1)
            name = JOptionPane.showInputDialog(
                    null, 
                    "Enter your name, please.");

        if(name == null){
            System.out.println("You have cancelled your entry. Exiting...");
            System.exit(-8);
        }
        return name;
    }

    /*
     * Loads the Dice Images in ./resources
     */
    public void loadDiceImages(){

        diceImages = new ArrayList<Image>();
        try{
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/blank.png")));
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/1.png")));
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/2.png")));
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/3.png")));
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/4.png")));
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/5.png")));
            diceImages.add(
                    (Image) ImageIO.read(getClass().getResource("resources/6.png")));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*
     * Creates a menu bar with option about the game.
     * @return <code>JMenuBar</code>
     */
    public JMenuBar getMenuBar(){
        JMenuBar menu = new JMenuBar();

        /* GAME MENU START */
        JMenu game = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(new NewGameButtonListener());
        newGame.setMnemonic(KeyEvent.VK_N);
        newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                    ActionEvent.ALT_MASK));

        JMenuItem endGame = new JMenuItem("End Game");
        endGame.addActionListener(new EndGameButtonListener());
        endGame.setMnemonic(KeyEvent.VK_E);
        endGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                    ActionEvent.ALT_MASK));
        game.add(newGame);
        game.add(endGame);
        /* GAME MENU END */

        /* STATS MENU START */
        JMenu stats = new JMenu("Stats");

        JMenuItem statWindow = new JMenuItem("Stats Window");
        statWindow.setMnemonic(KeyEvent.VK_W);
        statWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                    ActionEvent.ALT_MASK));
        statWindow.addActionListener(new ShowStatsButtonListener());

        stats.add(statWindow);
        /* STATS MENU END */

        menu.add(game);
        menu.add(stats);

        return menu;
    }

    /*
     * Creates the Game
     * @return <code>JPanel</code>
     */
    public JPanel getPanels(){
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout( new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();

        cons.gridx = 0;
        cons.gridy = 0;

        tempPanel.add(getGamePanel(), cons);

        return tempPanel;
    }

    /*
     * Sets up the top game panel that will hold the bankButton,
     * the dice to be rolled and will tally a running total for the 
     * current turn
     *@return JPanel with dice, holdButton, and current round score.
     */
    public JPanel getGamePanel(){
        JPanel gPanel = new JPanel();
        gPanel.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();

        /* Start of Current Player Info */
        JPanel playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new GridBagLayout());

        cons.gridx      = 0;
        cons.gridy      = 0;
        JLabel nameLabel = new JLabel("Current Player:");

        playerInfoPanel.add(nameLabel, cons);

        cons.gridx = 1;
        cons.gridy = 0;
        cons.ipadx = 10;
        cons.insets = new Insets(0,10,0,0);
        nameField = new JLabel(currentPlayer.getName());
        playerInfoPanel.add(nameField, cons);

        cons.gridx = 0;
        cons.gridy = 1;
        cons.ipadx = 0;
        cons.insets = new Insets(0,0,0,0);
        JLabel scoreLabel = new JLabel("Total Score:");
        playerInfoPanel.add(scoreLabel, cons);

        cons.gridx = 1;
        cons.gridy = 1;
        cons.ipadx = 10;
        cons.insets = new Insets(0,10,0,0);
        scoreField = new JLabel(Integer.toString(currentPlayer.getTotalScore()));
        playerInfoPanel.add(scoreField, cons);

        cons.insets = new Insets(0,0,0,0);

        cons.gridx      = 0;
        cons.gridy      = 0;
        cons.gridwidth  = 2;
        cons.anchor     = GridBagConstraints.CENTER;
        cons.ipadx      = 10;
        cons.ipady      = 10;
        gPanel.add(playerInfoPanel);
        /* End of Current PLayer Info */

        /* Start of Roll Button */
        cons.gridx      = 0;
        cons.gridy      = 1;
        cons.gridwidth  = 2;
        cons.anchor     = GridBagConstraints.CENTER;
        cons.ipadx      = 10;
        cons.ipady      = 10;

        rollButton = new JButton();
        rollButton.setIcon(new ImageIcon(diceImages.get(0)));

        rollButton.addActionListener(new RollButtonListener());
        rollButton.setEnabled(false);

        gPanel.add(rollButton, cons);  
        /* End of Roll Button */

        /* Start of Bank Button */
        //load "bank" icon for ./resources/
        try{
            bankIcon =
                (Image) ImageIO.read(getClass().getResource("resources/bankIcon.jpg"));
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        cons.gridx      = 0;
        cons.gridy      = 2;
        cons.gridwidth  = 1;

        bankButton = new JButton();
        bankButton.setIcon(new ImageIcon(bankIcon));
        bankButton.addActionListener(new BankButtonListener());

        gPanel.add(bankButton, cons);  
        /* End of Bank Button */

        /* Start of Current Score Field */
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new GridBagLayout());

        cons.gridx      = 0;
        cons.gridy      = 0;
        cons.anchor     = GridBagConstraints.CENTER;
        JLabel currScoreLabel = new JLabel("<HTML><U>Current Score</U></HTML>");
        tempPanel.add(currScoreLabel, cons);

        cons.gridy = 1;
        currScoreField = new JLabel("0");

        tempPanel.add(currScoreField, cons);

        cons.gridy = 2;
        cons.gridx = 1;

        gPanel.add(tempPanel, cons);
        /* End of Current Score */
        return gPanel;
    }

    /*
     * A game console that narrates the current
     * rolls and other actions taken by players
     * @return <code>JPanel</code> with narrator text box
     */
    public JPanel getNarratorPanel(){
        narratorText = new JTextArea(5, 30);
        narratorText.setEditable(false);
        narratorText.setLineWrap(true);
        narratorText.setWrapStyleWord(true);

        //Allows for scrolling
        JScrollPane scroller = new JScrollPane(narratorText);
        scroller.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        /* not sure yet if I want this in there.
           scroller.setHorizontalScrollBarPolicy(
           ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         */
        //Makes sure it auto scrolls down when new line is addedd
        DefaultCaret caret = (DefaultCaret)narratorText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JPanel narratorPanel = new JPanel();
        narratorPanel.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();

        cons.gridx = cons.gridy = 0;
        //procide some padding
        cons.insets = new Insets(15,5,0,5);

        narratorPanel.add(scroller, cons);

        return narratorPanel;
    }

    /*
     * Sets the current score field with the players current total score
     */
    public void setLabel(){
        scoreField.setText(Integer.toString(currentPlayer.getTotalScore()));
    }


    /*
     * Displays the winner when desired limit is reached
     * Disables buttons
     */
    public void gameOver(Player p){
        bankButton.setEnabled(false);
        rollButton.setEnabled(false);

        String winnerMessage =  p.getName() 
            + " has won!";

        JOptionPane.showMessageDialog(null, winnerMessage,"GAME OVER!", 
                JOptionPane.INFORMATION_MESSAGE);

    }

    /*
     * Adds the current score to players total score
     */
    public void bankEm(){
        playerList.get(myID).addToTotalScore();
        playerList.get(myID).setCurrScore(0);
        scoreField.setText(Integer.toString(currentPlayer.getTotalScore()));

        String bankMessage = currentPlayer.getName() + ", you just added " +
            currentPlayer.getCurrScore() + " to your total." + 
            "They now have " + currentPlayer.getTotalScore() + 
            " points.\n";

        sendMessage(Constants.PLAYER_TURN_OVER + "\n" + myID + "\n" +
                playerList.get(myID).getTotalScore());

        System.out.println(Constants.PLAYER_TURN_OVER + "\n" + myID + "\n" +
                playerList.get(myID).getTotalScore());
        playerList.get(myID).resetCurrScore();


        setLabel();
        rollButton.setEnabled(false);
        updateStats();
    }

    /*
     * Runs when you roll a one, displays a message notifying you.
     */
    public void youPiggedOut(){
        playerList.get(myID).setCurrScore(0);
        narratorText.append("\n" 
                + currentPlayer.getName() 
                + " has pigged out...");

        playerList.get(myID).addAPigOut();

        sendMessage(Constants.PLAYER_TURN_OVER + "\n" + myID + "\n" +
                playerList.get(myID).getTotalScore());

        /*JOptionPane.showMessageDialog(
                null, pigMessage,"Oops, Too Greedy!", 
                JOptionPane.INFORMATION_MESSAGE);
                */

        //narratorText.append(pigMessage);
        gameFrame.setTitle( CT + currentPlayer.getName());

        setLabel();
        rollButton.setEnabled(false);
    }

    /*
     * Runs when RollButton is clicked.  Creats a new rollVal
     * and sets the corresponding diceImage.
     */
    private void newRoll(){
        int rollVal = stopDropAnd.roll();
        rollButton.setIcon(new ImageIcon(diceImages.get(rollVal)));            
        rollButton.setEnabled(true);

        narratorText.append(
                "\n" + currentPlayer.getName() 
                + " rolled a " + rollVal + ".");

        playerList.get(myID).addToCurrScore(rollVal);

        sendMessage(Constants.MY_ROLL + "\n" + myID + "\n" + rollVal + "\n" + playerList.get(myID).getCurrScore() + "\n");

        if(rollVal == 1){
            rollButton.setIcon(new ImageIcon(diceImages.get(0)));            
            bankButton.setEnabled(false);
            youPiggedOut();
        }
        else{
        //    currentPlayer.addToCurrScore(rollVal);
        }

        updateStats();
        currScoreField.setText(
                String.valueOf(currentPlayer.getCurrScore()));
    }

    /*
     * Animating dice, stolen from Marty Gilbert
     */
    class AnimateDice implements Runnable{
        public void run(){
            for(int i = 0; i < diceImages.size() * 2; i++){
                rollButton.setIcon(
                        new ImageIcon(diceImages.get(i%(diceImages.size() -1))));
                try{
                    Thread.sleep(75);
                }catch(Exception err){}
            }

            newRoll();

        }
    }

    /*
     * Adjusts player stats as well
     * as repaints the stats panel.
     */
    public void updateStats(){
            //protection if statsFrame is not created
            if(statsFrame == null){ 
                statsFrame = new JFrame("Statistics");
                statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                statsFrame.setSize(250,250);
                statsFrame.setLocation(426, 0);
                statsFrame.setVisible(true);
            }

        //protection if statsFrame is not showing
        if(!statsFrame.isVisible()) return;

        statsFrame.getContentPane().removeAll();
        statsFrame.getContentPane().add(BorderLayout.CENTER, getStatsPanel());

        statsFrame.revalidate();
        //statsFrame.repaint();
       //gameFrame.repaint();
    }

    public JPanel getStatsPanel(){
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();

        cons.gridx = 0;
        cons.gridy = 0;
        cons.ipadx = 5;
        tempPanel.add(new JLabel("<HTML><U>NAME</U></HTML>"), cons);
        cons.gridx = 1;
        tempPanel.add(new JLabel("<HTML><U>CURRENT</U></HTML>"), cons);
        cons.gridx = 2;
        tempPanel.add(new JLabel("<HTML><U>TOTAL</U></HTML>"), cons);

        int n = 1;
        for(Player player : playerList.values()){
            cons.gridy = n;
            cons.ipadx = 10;

            cons.gridx = 0;
            tempPanel.add(new JLabel(player.getName()), cons);

            cons.gridx = 1;
            tempPanel.add(new JLabel(Integer.toString(player.getCurrScore())), cons);

            cons.gridx = 2;
            tempPanel.add(new JLabel(Integer.toString(player.getTotalScore())), cons);

            n++;
        }

        return tempPanel;

    }

    /*
     * Used by the MessageHandlers.
     * @param <code>String</code> message recieved from server
     * @return <code>String[]</code> with one line per index of array
     */
    public String[] removeComments(String message){
        String noComms = "";

        Scanner s = new Scanner(message);
        String temp = "";

        while(s.hasNextLine()){
            temp = s.nextLine();
            if(!temp.startsWith("#")){
                noComms += temp + "\n";
            }
        }
        s.close();
        return noComms.split("\n");
    }

    public void sendMessage(String message){
        try{
            writer.writeObject(message);
            writer.flush();
        } catch(IOException e){
            System.err.println("error writing message to server");
            e.printStackTrace();
        }
    }

    private void setupNetworking(){
        try {
            //Class level variables do not forget to define
            //sock = new Socket("", 11111);
            //Uncomment this line for tunneling
            sock = new Socket("localhost", 11111);

            writer = new ObjectOutputStream(sock.getOutputStream());

            inputStream = sock.getInputStream();

            System.err.println("Connection established");

        } catch (IOException ioe){
            JOptionPane.showMessageDialog(null, "Cannot connect to server. Exiting");
            System.exit(1);
        }
    }

    public class IncomingReader implements Runnable{
        public void run(){
            String message;
            setupHandlers();
            try{
                reader = new ObjectInputStream(inputStream);
                while((message = (String)reader.readObject()) != null){
                    if(message == null || message.length() == 0) continue;
                    
                    System.err.println("!!Message Recieved: \n" + message);
                    String[] lines = removeComments(message);
                    boolean handled = false;

                    for(MessageHandler h : handlersArray){
                        if(h.canHandle(lines[0])){
                            h.handle(lines);
                            updateStats();
                            handled = true;
                            break;
                        }
                    }
                    if(!handled)
                        System.err.println("Can't handle message: " + lines[0]);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        public void setupHandlers(){
            handlersArray = new ArrayList<MessageHandler>();
            handlersArray.add(new HandleRemotePlayerRoll());
            handlersArray.add(new HandleAllPlayersInitial());
            handlersArray.add(new HandleYourTurn());
            handlersArray.add(new HandlePlayerRoll());
            handlersArray.add(new HandleAddNewPlayer());
            handlersArray.add(new HandleRemovePlayer());
            handlersArray.add(new HandleGameOver());
            handlersArray.add(new HandleTurnScore());
        }
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    /*                   MessageSenders                       */
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    public String getJoinGameMessage(){
        return Constants.JOIN_GAME + "\n"
               + currentPlayer.getName() + "\n"
               + "100\n"
               + "101\n"
               + "9\n"
               + "109\n"
               + "1000\n";
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    /*                  ActionListeners                       */
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    class RollButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            bankButton.setEnabled(true);
            rollButton.setEnabled(false);

            //Rolls with an animation
            Thread t = new Thread(new AnimateDice());
            t.start();
        }
    }
    /*
     * Finds the product of 4 in a row
     * to either the left or right
     * @param int row representing postion in grid[].
     * @param int col representing postion in grid[row][]
     * @param String op specifying either right (+), or left (-).
     */

    class BankButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            bankEm();
            bankButton.setEnabled(false);
            gameFrame.setTitle( CT + currentPlayer.getName());
        }
    }

    class NewGameButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            narratorText.append("\n\n************************" +
                    "\nStarting new game..."+
                    "\n************************\n");
            rollButton.setEnabled(true);
            currentPlayer = new Player(getPlayerName());
            nameField.setText(currentPlayer.getName());
            setLabel();

            updateStats();
        }
    }

    class EndGameButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            sendMessage(Constants.QUIT_GAME + "\n" + myID);
            System.exit(1);
        }
    }

    class ShowStatsButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            updateStats();
        }
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    /*                  MessageHandlers                       */
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    // For when we recieve a message indicating a player has roll
    class HandleRemotePlayerRoll implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_ROLL);
        }

        public void handle(String[] message){
            narratorText.append("\n" + playerList.get(message[1]).getName() + " rolled a " + message[2]);
            updateStats();
            playerList.get(message[1]).setCurrScore(Integer.parseInt(message[3]));
        }
    }

    //Handler for the message we receive when the game begins
    class HandleAllPlayersInitial implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.ALL_PLAYERS_INITIAL);
        }

        public void handle(String[] message){
            myID = message[1];
            playerList.put(myID, currentPlayer);

            for(int n = 2; (n+8) <= message.length; n+=8){
                playerList.put(message[n+1], new Player(message[n],
                                      Integer.parseInt(message[n+2]),
                                      Integer.parseInt(message[n+3]),
                                      Integer.parseInt(message[n+4]),
                                      Integer.parseInt(message[n+5]),
                                      Integer.parseInt(message[n+6]),
                                      Integer.parseInt(message[n+7])));
            }
        }
    }

    //Handler for the message sent by the server saying its my turn
    class HandleYourTurn implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.YOUR_TURN);
        }

        public void handle(String[] message){
            //bankButton.setEnabled(true);
            if(rollButton == null)
                System.err.println("RollButton Error");
            if(!rollButton.isEnabled())
                rollButton.setEnabled(true);
        }
    }

    //Handler to process message from the server about someone elses roll
    class HandlePlayerRoll implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_ROLL);
        }

        public void handle(String[] message){
            narratorText.append("\n" + playerList.get(message[1]).getName() 
                    + " rolled a " + message[2]); 
            playerList.get(message[1]).setCurrScore(Integer.parseInt(message[3]));
            System.err.println("SHOULD BE UPDATING");
        }
    }

    //Handler for message recieved from the server about new player
    class HandleAddNewPlayer implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.ADD_NEW_PLAYER);
        }

        public void handle(String[] message){
            Player tempPlayer = new Player(message[2]);
            tempPlayer.setWins(Integer.parseInt(message[3]));
            tempPlayer.setGamesPlayed(Integer.parseInt(message[4]));
            tempPlayer.setPigOuts(Integer.parseInt(message[5]));
            tempPlayer.setNumRolls(Integer.parseInt(message[6]));
            playerList.put(message[1],  tempPlayer);

        }
    }

    //Handler for letting the serve know im done playing
    class HandleQuitGame implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.QUIT_GAME);
        }

        public void handle(String[] message){
            gameOver(playerList.get(message[1]));
        }
    }

    //Handler for message from server letting me know someones is a quitter
    class HandleRemovePlayer implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.REMOVE_PLAYER);
        }

        public void handle(String[] message){
            playerList.remove(message[1]);
        }
    }

    //Handler for message fromt the server with info to update stats.
    class HandleTurnScore implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_TURN_SCORE);
        }

        public void handle(String[] message){
           playerList.get(message[1]).setTotalScore(Integer.parseInt(message[2]));
           playerList.get(message[1]).setCurrScore(0);
        }
    }

    class HandleGameOver implements MessageHandler{
        public boolean canHandle(String messageType){
            return messageType.equals(Constants.GAME_OVER);
        }

        public void handle(String[] message){
            playerList.get(message[1]).addWin();
            narratorText.append(playerList.get(message[1]).getName() + " has won!");


            for(Player p : playerList.values()){
                p.setCurrScore(0);
                p.setTotalScore(0);
            }

        }
    }

}
