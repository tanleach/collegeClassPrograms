public class Player{
    
    private String name;
    private int currScore;
    private int totalScore;
    private int numRolls;
    private int numTurns;
    private int numPigOuts;
    private int numGamesPlayed;
    private int numWins;


    public Player(String name){
        this.setName(name);
        this.currScore      = 0;
        this.totalScore     = 0;
        this.numRolls       = 0;
        this.numTurns       = 0;
        this.numPigOuts     = 0;
        this.numGamesPlayed = 0;
        this.numWins        = 0;
    }

    public Player(String name, int wins, int gamesPlayed, int pigOuts, int numRolls, int current, int total) {

        this.setName(name);
        this.setWins(wins);
        this.setGamesPlayed(gamesPlayed);
        this.setPigOuts(pigOuts);
        this.setNumRolls(numRolls);
        this.setCurrScore(current);
        this.setTotalScore(total);
    }

    //For testing purposes only.
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

        this.currScore += num;
    }

    public void setCurrScore(int num){
        //dies if given a negative number
        if(num < 0)
            this.currScore = 0;

        this.currScore = num;
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

    public void setTotalScore(int num){
        //dies if given a negative number
        if(num < 0)
            this.totalScore = 0;

        this.totalScore = num;
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

    public void setNumRolls(int rolls){
        if(rolls < 0)
            this.numRolls = 0;
        else
            this.numRolls = rolls;
    }

    /*
     * Increments <code>numPigOuts</code> by one
     */
    public void addAPigOut(){
        this.currScore = 0;
        this.numPigOuts++;
    }

    public void setPigOuts(int pigOuts){
        if(pigOuts < 0)
            this.numPigOuts = 0;
        else
            this.numPigOuts = pigOuts;
    }

    /*
     * @return the number of 'Pig Outs'
     * this game as an <code>int</code>
     */
    public int getNumPigOuts(){
        return this.numPigOuts;
    }


    public void addGamesPlayed(){
        this.numGamesPlayed++;
    }

    public void setGamesPlayed(int games){
        if(games < 0)
            this.numGamesPlayed = 0;
        else
            this.numGamesPlayed = games;
    }

    public int getGamesPlayed(){
        return this.numGamesPlayed;
    }

    public void addWin(){
        this.numWins++;
    }

    public void setWins(int wins){
        if(wins < 0)
            this.numWins = 0;
        else
            this.numWins = wins;
    }

    public int getWins(){
        return this.numWins;
    }


    public String toString(){
        return String.format("%-12s%4d",this.getName(), this.getTotalScore());
    }
}
