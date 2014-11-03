/**
 * This class will hold a single 'word' found in a file, and also the number of times the
 * word has been seen.  We define 'word' as being delimited by whitespace, no leading or
 * trailing punctuation.
 *
 * @author Marty Gilbert
 *
 */
public class Word{
	/** int To hold the number of times this word has been seen **/
	private int numOccurrences;
	
	/** String The word that was seen in the file **/
	private String word;
	
	/*
     * Only constructor for the Word class. It's required that you give it the 'word' you
     * encountered before you can create the Word object.
     * 
     * By providing a constructor that takes a parameter and NOT implementing
     * the default constructor, this is the <b>only</b> way to make a <code>Word</code>
     * object.
	 */
	public Word(String word){
        this.word = word;
        this.numOccurrences = 1;
	}
	
	/**
	 * Call this method when you encounter another word that matches this one. 
	 * It will increment numOccurrences by one
	 */
	public void increment(){
        this.numOccurrences++;
	}
	
	/**
	 * @return The 'word' that this file represents
	 */
	public String getWord(){ return this.word; }
	
	/**
	 * @return an integer representing the number of times this word has been seen.
	 */
	public int getNumOccurrences(){ return this.numOccurrences; }
	
	/**
	 * String representation of this object
	 */
	public String toString(){
        return String.format("%8s -> %5d", this.getWord(), this.getNumOccurrences());
	}
    //TEST PURPOSES ONLY
    public static void main(String args[]) {
        Word w = new Word("the");
        for(int i = 0; i < 10001; i++){
                w.increment();
        }
        System.out.println(w);
    }
}
