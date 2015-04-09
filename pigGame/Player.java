public class Player{
    
    private String name;
    private int currScore;
    private int totalScore;
    private int numRolls;
    private int numTurns;
    private int numPigOuts;


    public Player(String name){
        this.setName(name);
        this.currScore = 0;
        this.totalScore = 0;
        this.numRolls = 0;
        this.numTurns = 0;
        this.numPigOuts = 0;
    }

    public static void main(String[] args){
        Player p = new Player("Tanner");
        p.addToCurrScore(100);
        p.addToTotalScore();

        System.out.println(p);

    }
    /*
     * Used to set name of the player at start of game. 
     * @param name of player as a <code>String</code> 
     */
    public void setName(String playerName){
        //die if null
        if(playerName.equals(null))
            return;

        this.name = playerName;
    }

    /*
     * @return the players name as a <code>String</code>
     */
    public String getName(){
        return this.name;
    }

    /*
     * @param <code>int</code> value of the current roll. 
     */
    public void addToCurrScore(int num){
        //dies if given a negative number
        if(num < 0)
            return;

        currScore += num;
    }

    
    public void resetCurrScore(){
        this.currScore=0;
    }
    /*
     * @return the score for the current round as an <code>int</code>.
     */
    public int getCurrScore(){
        return this.currScore;
    }

    /*
     * Using the current score form the round, updates the totl
     * score for the current player.
     */
    public void addToTotalScore(){
        this.totalScore += this.getCurrScore();
    }

    /*
     * @return the total score for the current player 
     * as an <code>int</code>.
     */
    public int getTotalScore(){
        return this.totalScore;
    }

    /*
     * Increments <code>numTurns</code> by one
     */
    public void addATurn(){
        this.numTurns++;
    }

    /*
     * @return the number of turns taken
     * this game as an <code>int</code>
     */
    public int getnumTurns(){
        return this.numTurns;
    }

    /*
     * Increments <code>numRolls</code> by one
     */
    public void addARoll(){
        this.numRolls++;
    }

    /*
     * @return the number of rolls taken
     * this game as an <code>int</code>
     */
    public int getnumRolls(){
        return this.numRolls;
    }

    /*
     * Increments <code>numPigOuts</code> by one
     */
    public void addAPigOut(){
        this.currScore = 0;
        this.numPigOuts++;
    }

    /*
     * @return the number of 'Pig Outs'
     * this game as an <code>int</code>
     */
    public int getNumPigOuts(){
        return this.numPigOuts;
    }

    public String toString(){
        return String.format("%-12s%4d",this.getName(), this.getTotalScore());
    }
}
