import java.util.*;
/**
 * WOOF -- Words Occurring Often in Files Tracks the number of times each word
 * occurs in a text file
 * 
 * @author Tanner Leach
 * 
 */
public class WOOF {
	/** ArrayList to hold all of the words we encounter **/
	private ArrayList<Word> wordList = new ArrayList<Word>();
    private String filename;
	/**
	 * 
	 * @param args
	 *            Command line arguments. args[0] should be the file to process.
	 */
	public static void main(String[] args) {
        //must enter one arguement, preferably a text file.
        if(args.length<1){
            System.err.println("Incorrect number of arguements.");
            System.exit(-1);
        }
        WOOF woof = new WOOF(args[0]);
        //for timing woof.start() \/
        final long startTime = System.currentTimeMillis();
        woof.start();
        System.out.println("Processing time: " 
                       + (double)(System.currentTimeMillis()-startTime)/1000);
        //for timing woof.start() /\
        System.out.println(woof);
	}

    /**
     * Constructor. Given a filename, process the file
     * @param filename to be processed
     */
    public WOOF(String filename){
        this.filename = filename;
    }


	/**
	 * The <code>start</code> function does the following things:
	 * <ul>
	 * <li>Makes a new <code>FileReaderUtility</code> object and call
	 * <code>readFile()</code></li>
	 * <li>Use <code>String</code>'s <code>split()</code> method to break the
	 * line returned from <code>getNextLine()</code> into different words, based
	 * on whitespace.</li>
	 * <li>Send that word to <code>addOrIncrementWord()</code>, and repeat for
	 * each word in the line</li>
	 * <li>Repeat this entire process until <code>readFile()</code> returns
	 * <code>null</code>, meaning there are no more lines.</li>
	 * </ul>
	 */
	public void start() {
        FileReaderUtility fru = new FileReaderUtility();
        boolean loaded = fru.readFile(this.filename);
        if(!loaded){
            System.err.println("Error opening file");
            System.exit(-1);
        }
        String line;
        int i = 0;
        String x;
        while((line = fru.getNextLine()) != null){
            //replaced line.split() for efficiency
            StringTokenizer st = new StringTokenizer(line);
            while(st.hasMoreTokens()){
                x = st.nextToken();
                addOrIncrementWord(x);
            }
            //after 380 lines read in, do a preliminary searched.
            //380 seemed to be the optimal time to sort first
            if(i++ == 380)
                Collections.sort(wordList, new numOccComparator());
        }
        Collections.sort(wordList, new numOccComparator());

    }

    /**
     * Once we get a single word from a line, we need to either create a new
     * <code>Word</code> object, or increment the number of occurrences of an
     * existing <code>Word</code> object, if we have already encountered this
     * word.
     * <p>
     * Basically, we prep the word we encountered using
     * <code>modifyString</code>, then iterate through all of the words we've
     * already seen. If we see the word in our ArrayList, then simply call
     * <code>increment</code> on that <code>Word</code> object and return. If we
     * don't find that word already in our ArrayList, then we need to add a new
     * <code>Word</code> object to the ArrayList.
     * 
     * @param word The word encountered from the file.
     */
    private void addOrIncrementWord(String word) {
        word = modifyString(word);
        //find if word is already in list
        for(Word w : wordList){
            if(word.equals(w.getWord())){
                w.increment();
                return;
            }
        }
        //if not in list, add to end
        wordList.add(new Word(word));
    }

    /**
     * This method makes the word all lowercase, and removes leading and
     * trailing punctuation
     * 
     * @param s
     *            The string to 'fix'
     * @return s in all lowercase, with no leading/trailing punctuation.
     */
    private String modifyString(String s) {
        s = s.toLowerCase().trim();
        //If just one character that is not a letter, return empty string
        if(!(Character.isLetter(s.charAt(0))) && s.length() == 1)
            return "";

        //remove non-letters from front
        while(!(Character.isLetter(s.charAt(0))) 
                && !s.isEmpty()){
            s = s.substring(1);
        }

        //remove non-letters from the end
        while(!(Character.isLetter(s.charAt(s.length()-1))) 
                && !s.isEmpty()){
            s = s.substring(0, s.length()-1);
        }
        return s;
    }

    /**
     * This prints out all the words and the number of occurrences of each word, sorted by
     * the number of occurrences.  If you write the <code>toString()</code> method in
     * <code>Word</code> correctly, this is a piece of cake.
     * 
     * @return String formatted for printing.
     */
    public String toString() {
        String r = "The top 10 words in " 
                    + filename.substring(
                            filename.lastIndexOf("/")+1 , filename.length()
                      ) + ":\n";
        //concat top 10 words
        for(int i = 0; i < 10; i++){
            r = r.concat(
                String.format("%2d: %s\n",i+1,wordList.get(i).toString()));
        }
        return r;
    }

}
