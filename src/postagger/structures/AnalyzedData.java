package postagger.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * This class represents a data structure
 * that stores statistics from the tag/state
 * models created, the tagged data from the 
 * HMM Viterbi code, and any other useful
 * performance evaluation information during
 * a tagging session. 
 * 
 * 
 * @author David Olorundare
 *
 */
public class AnalyzedData
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Represents the number of sentences in the training dataset.
	private int sentenceCount = 0;
	
	// Represents the number of unique words (vocabulary) in the training dataset.
	private int uniqueTokenCount = 0;
	
	// Represents the number of word tokens in the training dataset.
	private int wordTokens = 0;
	
	// Represents the number of (unique) bigram-tags in the training dataset.
	private int uniqueBigramsCount = 0;
	
	// Represents the number of POS tags (including STOP's) in the training dataset.
	private int tagCount = 0;
	
	// Represents a mapping between each tag and its occurrence rate in the training dataset.
	private HashMap<String, Integer> tagNumber;
	
	// Represents a mapping between each tag to the words associated with it.
	private HashMap<String, ArrayList<String>> tagWord;
	
	// Represents mapping between each bigram-tag and its occurrence rate in the training dataset.
	private HashMap<BigramTag, Integer> bigramTags;
	
	// Represents the list of analyzed and tagged dataset.
	private ArrayList<TaggedToken> taggedData;
	
	// Represents the number of sentences in the test dataset.
	private int testDataSentenceCount = 0;
	
	// Represent the list of all unknown words encountered in the test dataset
	private HashSet<String> unknownWords;
		
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Constructor of the class.
	 * 
	 */
	public AnalyzedData(){	}
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that sets the list
	 * of all unknown words encountered
	 * 
	 * @param value	list of unknown words encountered during Viterbi computation.
	 */
	public void setUnknownWords(HashSet<String> value) { unknownWords = value; }
	

	/**
	 * Helper method that sets the data
	 * tagged by the HMM Viterbi system.
	 * 
	 * @param value	the words/sentences tagged by the HMM Viterbi algorithm.
	 */
	public void setTaggedData(ArrayList<TaggedToken> value) { taggedData = value; }
	
	
	/**
	 * Helper method that sets the number of
	 * sentences in the testing dataset.
	 * 
	 * @param value	number of sentences in testing dataset.
	 */
	public void setTestDataSentenceCount(int value) { testDataSentenceCount = value; }
	
	
	/**
	 * Helper method that sets the number of
	 * sentences in the training dataset.
	 * 
	 * @param value	number of sentences in training dataset.
	 */
	public void setSentenceCount(int value) { sentenceCount = value; }

	
	/**
	 * Helper method that sets the number
	 * of unique words (vocabulary) in the training
	 * dataset.
	 * 
	 * @param value number of unique words in training dataset.
	 */
	public void setUniqueTokenCount(int value) { uniqueTokenCount = value; }
	
	
	/**
	 * Helper method that sets the number of
	 * word tokens in the training dataset.
	 * 
	 * @param value	number of word tokens in training dataset.
	 */
	public void setWordTokens(int value) { wordTokens = value; }

	
	/**
	 * Helper method that sets the number
	 * of (unique) bigram-tags in the training dataset.
	 * 
	 * @param value	number of unique bigram-tags in training dataset.
	 */
	public void setUniqueBigramsCount(int value) { uniqueBigramsCount = value; }

	
	/**
	 * Helper method that sets the number of 
	 * POS tags (including STOP's) in the training
	 * dataset.
	 * 
	 * @param value	number of POS tags in training dataset.
	 */
	public void setTagCount(int value) { tagCount = value; }
	
	
	/**
	 * Helper method that sets a mapping 
	 * between each tag and its occurrence 
	 * rate in the training dataset.
	 * 
	 * @param value mapping between tag and its occurrence rate.
	 */
	public void setTagNumber(HashMap<String, Integer> value) { tagNumber = value; }
	
	
	/**
	 * Helper method that sets a mapping 
	 * between each tag to the words associated 
	 * with it.
	 * 
	 * @param value mapping between tag and associated words.
	 */
	public void setTagWord(HashMap<String, ArrayList<String>> value) { tagWord = value; }
	
	
	/**
	 * Helper method that sets a mapping 
	 * between each bigram-tag and its 
	 * occurrence rate in the training dataset.
	 * 
	 * @param value mapping between bigram-tag and occurrence rate.
	 */
	public void setBigramTags(HashMap<BigramTag, Integer> value) { bigramTags = value; }
	
	
	/**
	 * Helper method that returns the data
	 * tagged by the HMM Viterbi system.
	 * 
	 * @return the words/sentences tagged by the HMM Viterbi algorithm.
	 */
	public ArrayList<TaggedToken> getTaggedData() { return taggedData; }
	
	
	/**
	 * Helper method that returns the list of
	 * unknown words encountered during computation
	 * 
	 * @return list of unknown words.
	 */
	public HashSet<String> getUnknowWords() { return unknownWords; }
	
	
	/**
	 * Helper method that returns the number of
	 * sentences in the training dataset.
	 * 
	 * @return number of sentences in the training set.
	 */
	public int getSentenceCount() { return sentenceCount; }

	
	/**
	 * Helper method that returns the number of
	 * sentences in the testing dataset.
	 * 
	 * @return number of sentences in the testing set.
	 */
	public int getTestSentenceCount() { return testDataSentenceCount; }
	
	
	/**
	 * Helper method that returns the number
	 * of unique words (vocabulary) in the training
	 * dataset.
	 * 
	 * @return number of unique words (vocabulary) in training set.
	 */
	public int getUniqueTokenCount() { return uniqueTokenCount; }

	
	/**
	 * Helper method that returns the number of
	 * word tokens in the training dataset.
	 * 
	 * @return number of word tokens in training set.
	 */
	public int getWordTokens() { return wordTokens; }


	/**
	 * Helper method that returns the number
	 * of (unique) bigram-tags in the training dataset.
	 * 
	 * @return number of (unique) bigram-tags in the training set.
	 */
	public int getUniqueBigramsCount() { return uniqueBigramsCount; }


	/**
	 * Helper method that returns the number of 
	 * POS tags (including STOP's) in the training
	 * dataset.
	 *  
	 * @return number of POS tags in training dataset.
	 */
	public int getTagCount() { return tagCount; }


	/**
	 * Helper method that returns a mapping 
	 * between each tag and its occurrence 
	 * rate in the training dataset.
	 * 
	 * @return mapping between each tag and its occurrence rate in the training dataset.
	 */
	public HashMap<String, Integer> getTagNumber() { return tagNumber; }


	/**
	 * Helper method that returns a mapping 
	 * between each tag to the words associated 
	 * with it.
	 * 
	 * @return mapping between tags and words associated with them.
	 */
	public HashMap<String, ArrayList<String>> getTagWord() { return tagWord; }


	/**
	 * Helper method that returns a mapping 
	 * between each bigram-tag and its 
	 * occurrence rate in the training dataset.
	 * 
	 * @return mapping between bigram-tags and their occurrence-rate.
	 */
	public HashMap<BigramTag, Integer> getBigramTags() { return bigramTags; }

	
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private Methods.
	
}
