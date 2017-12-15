package postagger.compute;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import postagger.structures.AnalyzedData;
import postagger.structures.BigramTag;


/**
 * This class performs various language model
 * operations on a given training and testing
 * dataset.
 * The class has methods for creating models,
 * Calculating the log probability and perplexity
 * of a sentence (with or without smoothing), as 
 * well as random-generation of new sentences. 
 * 
 * @author David Olorundare
 *
 */
public final class PosModeler 
{
	
	//============================================ 	PRIVATE VARIABLES =============================================================
	
	
	//==================== INITIALIZATION VARIABLES ============================
	
	// Represents an instance to this class.
	private volatile static PosModeler instance;

	// Represents a stream of the training dataset.
	private BufferedReader trainingData;	
		
	//==================================== POS MODELER VARIABLES ============================
 	
 	// Represents the number of tokens in the training dataset.
 	private int tokenCount = 0;
 	
 	// Represents a list of the unique word-tokens in the training dataset.
 	private ArrayList<String> uniqueTokens = new ArrayList<String>();
 	
 	// Represents the number of sentences in the training dataset.
  	private int sentenceCount = 0; 
 	
 	// Represents a mapping between each tag to the words associated with it.
 	private HashMap<String, ArrayList<String>> tagWord;
 	
 	// Represent a mapping between each tag and its occurrence rate in the training dataset.
 	private HashMap<String, Integer> tagNumber;
 	
 	// Represents a mapping between each bigram-tag and its occurrence rate in the training dataset.
  	private HashMap<BigramTag, Integer> bigramTags;
 	
 	// Represents a list of the unique tag-bigrams in the training dataset.
 	private ArrayList<BigramTag> uniqueBigrams;
 	
 	// Represent a temporary list of all words associated with a given tag
 	private ArrayList<String> tagWordList;
 	
 	// Represents a temporary list of all word-tag-tokens in a given sentence.
 	private ArrayList<String> tempTrack;
 	
 	// Represents a variable that determines if smoothing is enabled on the bigram tag/state model.
 	private Boolean smoothing = false;
 	
 	// Represents the default value used to initially fill the bigram tag/state model.
  	private int modelDefaultFill = 0;
 	
 		//========================== OUTPUT VARIABLES =======================
 	
 	// Represents results from the language model operations performed in this session.
 	AnalyzedData result = new AnalyzedData();
 	
 	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Private Constructor of the PosModeler class.
	 * 
	 */
	private PosModeler() {	}
	
	
	/**
	  * Returns a singleton instance of the PosModeler class,
	  * ensuring that only one instance is active 
	  * at any single time.
	  * 
	  */
	public static PosModeler getInstance() 
	{
	      if (instance == null)
	      {
	          synchronized (PosModeler.class)
	          {
	              if (instance == null)
	              {
	                  instance = new PosModeler();
	              }
	          }
	      }
	      return instance;
	}
	

	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Performs creation of bigram tags models from
	 * a given training dataset, with transition and
	 * emission probabilities. Returned is a model-format
	 * that is used by the subsequent HMM Viterbi system
	 * to evaluate a given test dataset.
	 * 
	 * @param	 trainingText	the dataset used to build the tag models.
	 * 
	 * @param	 smoothingEnabled	option that determines if smoothing is enabled.
	 * 
	 * @return	structure containing the built bigram tag/state models.
	 * 
	 * @throws	IOException	if an error occurs while reading the dataset
	 * @throws	FileNotFoundException  if data does not exist.
	 * 
	 */
	public AnalyzedData buildModel(BufferedReader trainingText, Boolean smoothingEnabled) throws FileNotFoundException, IOException
	{
		trainingData = trainingText; String startTag = "<START>"; String stopTag = "<STOP>"; String lineRead = " ";
		
		tagWord = new HashMap<String, ArrayList<String>>(); tagNumber = new HashMap<String, Integer>(); 
		bigramTags = new HashMap<BigramTag, Integer>(); uniqueBigrams = new ArrayList<BigramTag>();
		
		 tempTrack = new ArrayList<String>();
		
		// Read each block of sentence in the dataset.
		while ( (lineRead = trainingData.readLine()) != null  )
		{
			// Read tokens that make up the sentence.
			// Beginning of sentence, add the Start-tag.
			tempTrack.add(startTag);
			while(true)
			{
				// End of sentence reached, break and go to the next sentence.
				if (lineRead.equals("")) { break; }
				//
				tempTrack.add(lineRead); tokenCount++;
				// Add unique tokens to a list.
				if ( !uniqueTokens.contains(lineRead.split(" ")[0]) ) { uniqueTokens.add(lineRead.split(" ")[0]); }
				// Read the next token.
				lineRead = trainingData.readLine();
			}
			// Count this as a sentence encountered.
			sentenceCount++;
			// End of sentence, add the Stop-tag.
			tempTrack.add(stopTag);
			// Perform Emission and Transition counts on tokens in this sentence.
			processTokens(tempTrack, smoothingEnabled );
			// Clear the contents in the sentence storage, so as to restart with a new set of sentence tokens.
			tempTrack.clear();
		}
		
		// Calculate the number of POS tags in the training dataset.
		tagNumber.put("<START>", sentenceCount);
		int tagCount = 0;
		for (String tag : tagNumber.keySet())
		{
			tagCount += tagNumber.get(tag);
		}
		tagCount -= sentenceCount;
		
		// Attach the emission and transition count models to a structure, and return it to the calling function for HMM-Viterbi computation.
		// sentenceCount, uniqueTokens.size(), tokenCount, tagCount, tagNumber, tagWord, bigramTags, uniqueBigrams.size()
		result.setSentenceCount(sentenceCount); result.setUniqueTokenCount(uniqueTokens.size()); result.setWordTokens(tokenCount);
		result.setTagCount((tagCount + sentenceCount)); result.setTagWord(tagWord); result.setTagNumber(tagNumber);
		result.setUniqueBigramsCount(uniqueBigrams.size()); result.setBigramTags(bigramTags);
     
		return result;
	}
	
	
	/**
	 * Simultaneously estimates the tag-transition and
	 * tag-emission probability counts of word-tag-tokens
	 * in a given sentence.
	 * 
	 * @param sentences	given sentence with word-tag-tokens.
	 * 
	 * @param smoothing	option that determines if smoothing is enabled.
	 */
	public void processTokens(ArrayList<String> sentences, Boolean smoothing)
	{
		int tokens = sentences.size();
		int index = 0;
		String[] sentenceTokens = new String[tokens];
		for(String token : sentences)
		{
			sentenceTokens[index] = token;
			index++;
		}
		tagEmissionProbability(sentenceTokens, smoothing);
		tagTransitionProbability(sentenceTokens, smoothing);
	}
	
	
	/**
	 * Estimates tag-emission probability count of word-tag-tokens
	 * in a given sentence.
	 * 
	 * @param sentences	given sentence with word-tag-tokens.
	 * 
	 * @param smoothing	option that determines if smoothing is enabled.
	 * 
	 */
	public void tagEmissionProbability(String[] sentences, Boolean smoothing)
	{
		// Add Laplace smoothing if it is enabled
		// Which adds +1 to all bigram POS tags.
		if (smoothing) { modelDefaultFill = 1;} 
		
		String tag = " "; String word = " ";
	
		for(String gram: sentences)
		{
			if ( gram.equals("<START>") || gram.equals("<STOP>")){ continue; }
			word = gram.split(" ")[0];
			tag = gram.split(" ")[1];
			
			// Increment the count of the tag.
			if( tagNumber.containsKey(tag) ){ tagNumber.put(tag, (tagNumber.get(tag)+1) ); }
			else { tagNumber.put(tag, (1+modelDefaultFill));  }
			
			// Add to the list of words associated with the tag.
			if (tagWord.containsKey(tag))
			{
				tagWordList = tagWord.get(tag);
				tagWordList.add(word);
				tagWord.put(tag, tagWordList);
			}
			else { tagWordList = new ArrayList<String>(); tagWordList.add(word); tagWord.put(tag, tagWordList); }
		}
	}

	
	/**
	 * Estimates tag-transition probability count of word-tag-tokens	
	 * in a given sentence. Adds Laplace (add-one) smoothing to
	 * the bigram tag count, if enabled.
	 * 
	 * @param sentences	given sentence with word-tag-tokens.
	 * 
	 * @param smoothing	option that determines if smoothing is enabled.
	 * 
	 */
	public void tagTransitionProbability(String[] sentences, Boolean smoothing)
	{
		// Add Laplace smoothing if it is enabled
		// Which adds +1 to all bigram POS tags.
		if (smoothing) { modelDefaultFill = 1;} 
		
		String firstTag = " "; String secondTag = " ";
		BigramTag bTags;
		Boolean stopValue = false;
		
		for( int i = 0; i < sentences.length; i++)
		{
			bTags = new BigramTag();
			// first word is either a startTag or token.
			if(sentences[i].equals("<START>")) { firstTag = sentences[i]; }
			else{ firstTag = sentences[i].split(" ")[1]; }
		
			// second word is either a stopTag or token.
			if(sentences[i+1].equals("<STOP>")) { secondTag = sentences[i+1]; stopValue = true; }
			else{ secondTag = sentences[i+1].split(" ")[1]; }
			bTags.setFirstTag(firstTag); bTags.setSecondTag(secondTag);
			
			// Increment the number of unique bigrams.
			if ( !uniqueBigrams.contains(bTags)){ uniqueBigrams.add(bTags); }
			
			// Increment the number of bigram(secondTag given firstTag) tags.
			if( bigramTags.containsKey(bTags)){ bigramTags.put(bTags, (bigramTags.get(bTags)+1) ); if(stopValue) break; } 
			else { bigramTags.put(bTags, (1+modelDefaultFill)); if(stopValue) break; }
		}
	}
	
	
	/**
	 * Helper method that returns a
	 * list of the unique word tokens
	 * in the training dataset.
	 *  
	 * @return list containing the unique tokens in the training dataset.
	 */
	public ArrayList<String> getUniqueTokens()
	{
		return uniqueTokens;
	}
	
	
}
