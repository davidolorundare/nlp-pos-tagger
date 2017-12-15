package postagger.structures;


/**
 * This class represents a data structure
 * for storing bigram POS tags.
 * 
 * @author David Olorundare
 *
 */
public class BigramTag 
{

	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents the first tag.
	public String firstTag = " ";
		
	// Represents the second tag.
	public String secondTag = " ";
		
	// Represents the occurrence count of the bigram tag (firstTag, secondTag - count)
	public int count = 0;
	
	// Represents the probability of the bigram tag (firstTag, secondTag - probability)
	public float probability = 0;
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	// No Constructor
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that sets the probability 
	 * of the bigram POS tags
	 * 
	 * @param value the probability to set.
	 */
	public void setProbability(float value) { probability = value; }
	
	
	/**
	 * Helper method that sets the number
	 * of times the bigram tag occurs in the
	 * training dataset.
	 * 
	 * @param value the count/occurrence rate to set.
	 */
	public void setCount(int value) { count = value; }
	
	
	/**
	 * Helper method that sets the second
	 * POS tag in the bigram.
	 * 
	 * @param value the second POS Tag to set.
	 */
	public void setSecondTag(String value) { secondTag = value; }
	
	
	/**
	 * Helper method that sets the first
	 * POS tag in the bigram.
	 * 
	 * @param value the first POS Tag to set
	 */
	public void setFirstTag(String value) { firstTag = value; }

	
	/**
	 * Helper method that returns the second
	 * POS tag in the bigram.
	 * 
	 * @return the second POS Tag.
	 */
	public String getSecondTag() { return secondTag; }

	
	/**
	 * Helper method that returns the first
	 * POS tag in the bigram.
	 * 
	 * @return the first POS Tag.
	 */
	public String getFirstTag() { return firstTag; }
	
	

	/**
	 * Helper method that returns the count
	 * of bigram tag in the training dataset.
	 * 
	 * @return the count of the bigram tag.
	 */
	public int getCount() { return count; }

	

	/**
	 * Helper method that returns the probability
	 * of this bigram tag.
	 * 
	 * @return the probability of the bigram tag.
	 */
	public float getProbability() { return probability; }

	//======================= SPECIAL METHODS ============================================================
	// Define new hashcode() and equals() methods for use in Viterbi HMM POS Tagging program.
	// The hashcode(), generated from a bigram tag's first and second tags, 
	// is used in conjunction with the equals() method within Hash-based data 
	// structures (e.g. HashMap, HashSet), for testing membership.
	
	
	@Override
	public boolean equals(Object obj)
	{
		// Not usable given that comparing objects would give
		// misleading values.
		/*if (this == obj) return true;
		if (obj == null || this.getClass() != obj.getClass())
		{
			return false;
		}*/
		
		BigramTag other = (BigramTag) obj;
        if((this.firstTag.equals(other.firstTag)) && (this.secondTag.equals(other.secondTag)))
        {
        	return true;	
        }
		else { return false; }
	}
	
	
	@Override
	public int hashCode()
	{
		int hash = 17;
        hash += 31 * hash + this.firstTag.hashCode() + this.secondTag.hashCode();
		return hash;
	}
	
	//====================================================================================================
	
	
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private Methods
	
}
