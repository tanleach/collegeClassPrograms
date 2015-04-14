import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.util.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;

public class SinglePlayerPig {
    //Global to keep track of the current roll
    private int rollVall;
    //Dice objec tto be rolled
    private Dice stopDropAnd;

    //Static messages
    public static final String CT = "Current Turn: ";

    //Holds the current player (neccessary for multiplayer)
    private Player currentPlayer;

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
        /* Used only for testing 
           playerList.add(new Player());
         */

        //Loads current player getting his/her name.
        currentPlayer = new Player(getPlayerName());
        playerList.put(currentPlayer.getName(), currentPlayer);

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
    public void gameOver(){
        bankButton.setEnabled(false);
        rollButton.setEnabled(false);

        String winnerMessage = "Congratulations " + currentPlayer.getName() 
            + "! You are the winner!";
        JOptionPane.showMessageDialog(null, winnerMessage,"GAME OVER!", 
                JOptionPane.INFORMATION_MESSAGE);

    }

    /*
     * Adds the current score to players total score
     */
    public void bankEm(){
        currentPlayer.addToTotalScore();
        scoreField.setText(Integer.toString(currentPlayer.getTotalScore()));

        if(currentPlayer.getTotalScore() > 29){
            gameOver();
            return;
        }

        String bankMessage = currentPlayer.getName() + ", you just added " +
            currentPlayer.getCurrScore() + " to your total." + 
            "You now have " + currentPlayer.getTotalScore() + 
            " points.\n";

        currentPlayer.resetCurrScore();

        JOptionPane.showMessageDialog(
                null, bankMessage,"Well Done!", 
                JOptionPane.INFORMATION_MESSAGE);

        gameFrame.setTitle( CT + currentPlayer.getName());

        setLabel();
    }

    /*
     * Runs when you roll a one, displays a message notifying you.
     */
    public void youPiggedOut(){
        narratorText.append("\n" 
                + currentPlayer.getName() 
                + " has pigged out...");

        currentPlayer.addAPigOut();

        String pigMessage = "Sorry " + currentPlayer.getName() + 
            ", you have 'Pigged out'!\n";

        JOptionPane.showMessageDialog(
                null, pigMessage,"Oops, Too Greedy!", 
                JOptionPane.INFORMATION_MESSAGE);

        gameFrame.setTitle( CT + currentPlayer.getName());

        setLabel();
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

        if(rollVal == 1){
            rollButton.setIcon(new ImageIcon(diceImages.get(0)));            
            bankButton.setEnabled(false);
            youPiggedOut();
        }
        else{
            currentPlayer.addToCurrScore(rollVal);
        }

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
        //protection if statsFrame is not showing
        if(!statsFrame.isVisible()) return;

        statsFrame.getContentPane().add(BorderLayout.CENTER, getStatsPanel());

        //statsFrame.repaint();
       // gameFrame.repaint();
    }

    public JPanel getStatsPanel(){
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();

        cons.gridx = 0;
        cons.gridy = 0;

        int n = 0;
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
            
            System.err.println(player.getName());
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

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    /*                  ActionListeners                       */
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    class RollButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            currentPlayer.addARoll();
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
        }
    }

    class EndGameButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.exit(1);
        }
    }

    class ShowStatsButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //protection if statsFrame is not created
            if(statsFrame == null){ 
                statsFrame = new JFrame("Statistics");
                statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                statsFrame.setSize(250,250);
                statsFrame.setVisible(true);
            }

            updateStats();
            statsFrame.revalidate();
        }
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    /*                  ButtonListeners                       */
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    // For when we recieve a message indicating a player has roll
    class HandleRemotePlayerRoll implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_ROLL);
        }

        public void handle(String[] message){
            //update the score board.
        }
    }

    //Handler for sending a message to the server that I want to play
    class HandleJoinGame implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.JOIN_GAME);
        }

        public void handle(String[] message){
        }
    }

    //Handler for the message we receive when the game begins
    class HandleAllPlayersInitial implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.ALL_PLAYERS_INITIAL);
        }

        public void handle(String[] message){
        }
    }

    //Handler for the message sent by the server saying its my turn
    class HandleYourTurn implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.YOUR_TURN);
        }

        public void handle(String[] message){
        }
    }

    //Handler to send the server a message about my roll
    class HandleMyRoll implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.MY_ROLL);
        }

        public void handle(String[] message){
        }
    }

    //Handler to process message from teh server about someone elses roll
    class HandlePlayerRoll implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_ROLL);
        }

        public void handle(String[] message){
        }
    }

    //Handler for message recieved from the server about new player
    class HandleAddnewPlayer implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.ADD_NEW_PLAYER);
        }

        public void handle(String[] message){
        }
    }

    //Handler for letting the serve know im done playing
    class HandleQuitGame implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.QUIT_GAME);
        }

        public void handle(String[] message){
        }
    }

    //Handler for message from server letting me know someones is a quitter
    class HandleRemovePlayer implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.REMOVE_PLAYER);
        }

        public void handle(String[] message){
        }
    }

    //Handler for message from server annoucng a players turn ended
    class HandleTurnOver implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_TURN_OVER);
        }

        public void handle(String[] message){
        }
    }

    //Handler for message fromt he server with info to update stats.
    class HandleTurnScore implements MessageHandler{

        public boolean canHandle(String messageType){
            return messageType.equals(Constants.PLAYER_TURN_SCORE);
        }

        public void handle(String[] message){
        }
    }
}
