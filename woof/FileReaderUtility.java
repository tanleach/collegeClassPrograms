import java.io.*;
import java.util.*;

/**
 * This class will be a utility for the WOOF project, making it easy to read the
 * contents of a file
 * 
 * @author Marty Gilbert
 * 
 */
public class FileReaderUtility {
	private ArrayList<String> lines = new ArrayList<String>();
	private ListIterator<String> li;

	/**
	 * This will return the next line of the text file.
	 * 
	 * @return String that holds the next line in the text file,
	 *         <code>null</code> if there are no more lines in the file.
	 */
	public String getNextLine() {
		if (li == null)
			li = lines.listIterator();
		if (li.hasNext())
			return li.next();
		return null;
	}

	/**
	 * This function will read the entire contents of a file, and store the
	 * different lines so that they can be retrieved by a call to
	 * <code>getNextLine()</code>.
	 * 
	 * @param filename
	 *            The name of the file to be read
	 * @return True if the file can be read, false if the file does not exist or
	 *         cannot be read for other reasons.
	 */
	public boolean readFile(String filename) {
		li = null; // reset the list iterator?
		File f = new File(filename);
		if (!f.exists()) {
			System.err.println("File: " + filename
					+ " does not exist. Exiting.");
			return false;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
		}
		return true;
	}
}
