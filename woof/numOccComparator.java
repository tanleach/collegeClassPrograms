public class numOccComparator implements java.util.Comparator<Word>{
        public int compare(Word wOne, Word wTwo){
            return Double.compare(
                        wTwo.getNumOccurrences(),wOne.getNumOccurrences()
                   );
    }
}
