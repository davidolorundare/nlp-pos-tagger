package postagger.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import postagger.structures.AnalyzedData;
import postagger.structures.TaggedToken;


/**
 * Printing Utility class that outputs information
 * to the console about the HMM Viterbi models and tagged data 
 * analysis and also stores it in a given output-file.
 * 
 * @author David Olorundare
 *
 */
public final class OutputPrinter 
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Holds an instance to this class
	private volatile static OutputPrinter instance;
	
	// Represents the output analysis information 
	// to be displayed and stored in an external file.
	StringBuilder output;
	
	// Determines if the format of the test data is different from the training data.
	private Boolean alternateFormat = false;
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Private Constructor of the OutputPrinter class.
	 * 
	 */
	private OutputPrinter(){	}
	
	
	/**
	  * Returns a singleton instance of the OutputPrinter class,
	  * ensuring that only one instance of the class is active 
	  * at any single time.
	  * 
	  */
	public static OutputPrinter getInstance() 
	{
		if (instance == null)
	      {
	          synchronized (OutputPrinter.class)
	          {
	              if (instance == null)
	              {
	                  instance = new OutputPrinter();
	              }
	          }
	      }
	      return instance;
	}
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that prints data analysis
	 * of the POS tagging evaluation information.
	 * 
	 * @param data	the results of the POS tagger evaluation to print out.
	 * 
	 */
	public void printAnalysisToScreen(AnalyzedData data) throws IOException
	{
		output = new StringBuilder();
		//========================== STATISTICS OF COMPUTATION ================================
		
		//Retrieve tag data, and determine performance: measure accuracy between reported tag and actual tag of a given token.
		int goodTags = 0;
		
		ArrayList<TaggedToken> tagged = new ArrayList<TaggedToken>();
		tagged = data.getTaggedData();
		
		// Checked the format of the test dataset lines to determine what should be outputted.
		if (tagged.get(0).token.split(" ").length < 2 ) { alternateFormat = true; }
		
		if (!alternateFormat)
		{	for (TaggedToken taggedData : tagged)
			{
				if (taggedData.token.split(" ")[1].equals(taggedData.matrixData.rowX)){ goodTags++; }
			}
		}
		// For debugging - output statistics about the training data tag/state models.
		appendTrainingStatistics(data);
		
		// Works only for the WSJ-test.txt dataset, but 
		// Not used for the POS-test.txt dataset since 
		// it has no labelling to compare if the Viterbi tags are correct.
		// Hence we check 'alternativeFormat' boolean to ensure the test data 
		// is not the POS-test.txt.
		if (!alternateFormat) { appendPerformanceStatistics(data, goodTags); }
		
		
		appendTaggedData(data);
		
		//=============================================================================
		
		// Print the analysis results to the console.
		System.out.println(output.toString());
	
		// Save the analysis results to an external file.
		printAnalysisToFile(output.toString());
	}


	//============================================ PRIVATE METHODS =============================================================
	
	
	/**
	 * Helper method that outputs the dataset
	 * tagged by the Viterbi system.
	 * 
	 * @param data	structure containing the tagged dataset.
	 * 
	 */
	private void appendTaggedData(AnalyzedData data) 
	{
		// Output the tokens and the tags the Viterbi algorithm assigned to each of them.
		ArrayList<TaggedToken> tagged = new ArrayList<TaggedToken>();
		tagged = data.getTaggedData();
		
		// Determine if the test dataset line format is the same as the training dataset before outputting. 
		if (!alternateFormat)
		{	for (TaggedToken taggedData : tagged)
			{
				output.append(taggedData.token.split(" ")[0] + " " + taggedData.token.split(" ")[1] +" " + taggedData.matrixData.rowX + "\n");
			}
		}
		else
		{
			for (TaggedToken taggedData : tagged)
			{
				output.append(taggedData.token.split(" ")[0] + "  " + taggedData.matrixData.rowX + "\n");
			}
		}
		
	}

	
	/**
	 * Helper method that output statistics
	 * on the performance of the Viterbi system
	 * in tagging a given test dataset.
	 * 
	 * @param data	structure containing statistics on the training dataset.
	 * 
	 * @param goodTags	counter that tracks correctly identified POS tags.
	 * 
	 */
	private void appendPerformanceStatistics(AnalyzedData data, int goodTags) 
	{
		// Determine percentage tagging accuracy of the Viterbi system.
		DecimalFormat precision = new DecimalFormat("0.00");
		
		// The total number of unknown words in the testing data
		int totalUnknownWords = data.getUnknowWords().size();
		
		int totalTags = 0;
		ArrayList<TaggedToken> tagged = new ArrayList<TaggedToken>(); tagged = data.getTaggedData();
		for (TaggedToken taggedData : tagged)
		{
			totalTags++;
		}
		// Compute the Confusion Matrix values.
		int totalKnownWords = totalTags - totalUnknownWords;
		int unknownWordsTagged = identifiedUnknownWords(data); 
		int identifiedKnownWords = goodTags - unknownWordsTagged;
				
		// Determine the accuracy rates.
		Double accuracy = new Integer(goodTags).doubleValue()/new Integer(totalTags).doubleValue(); accuracy *= 100;
		Double knownWordAccuracy = new Integer(identifiedKnownWords).doubleValue()/new Integer(totalKnownWords).doubleValue(); knownWordAccuracy *= 100;
		Double unknownWordAccuracy = new Integer(unknownWordsTagged).doubleValue()/new Integer(totalUnknownWords).doubleValue(); unknownWordAccuracy *= 100;
		
		output.append("\nAccuracy:\t" + goodTags +"/" + totalTags + " = " + precision.format(accuracy) + "\n" );
		output.append(" - Known:\t" + identifiedKnownWords + "/" + totalKnownWords + " = " + precision.format(knownWordAccuracy) + "\n");
		output.append(" - UnKnown:\t" + unknownWordsTagged + "/" + totalUnknownWords + " = " + precision.format(unknownWordAccuracy)+ "\n\n");
	}


	/**
	 * Helper method that outputs statistics
	 * on the training dataset.
	 * 
	 * @param data	structure containing statistics on the training dataset.
	 * 
	 */
	private void appendTrainingStatistics(AnalyzedData data) 
	{
		// Subtract the additional word class <UNK> from the list of POS tags.
		output.append("\n* POS tags: \n- # of all POS tags (excluding STOP): " + (data.getTagWord().keySet().size()-1) + "\n");
		
		output.append("\n* Training data:\n- # of sentences: " + data.getSentenceCount() + "\n");
		output.append("- # of unique words: " + data.getUniqueTokenCount() + "\n");
		output.append("- # of word tokens: " + data.getWordTokens() + "\n");
		
		// Tag number = tags + STOP_Tag_number-of-sentences.
		output.append("- # of POS tags: " + data.getTagCount() + "\n" );
				
		// Number of bigram tags.
		output.append("- # of Bigrams: " + data.getUniqueBigramsCount() + "\n" );
		
		int totalTags = 0;
		ArrayList<TaggedToken> tagged = new ArrayList<TaggedToken>();
		tagged = data.getTaggedData();
		for (TaggedToken taggedData : tagged)
		{
			totalTags++;
		}
		
		// Number of word tokens in the test dataset inclusive of the <STOP> tags (which are just the number sentences).
		output.append("\n* Test data:\n- # of word tokens (including </s>'s): " + (totalTags + data.getTestSentenceCount()) + "\n\n");
	}


	//============================================ PRIVATE METHODS =============================================================
	

	/**
	 * Helper method that prints some given
	 * HMM Viterbi system evaluation analysis data to an external file.
	 * 
	 * @param data	the HMM Viterbi evaluation analysis information to store in an external file.
	 * 
	 * @throws IOException	if an error occurs while reading the external file.
	 */
	private void printAnalysisToFile(String data) throws IOException
	{
		FileHandler saveToFile = FileHandler.getInstance();
		
		saveToFile.writeToFile(data);
		
	}

	
	
	/**
	 * Helper method that determines the unknown words
	 * in the testing dataset that were correctly identified.
	 * 
	 * @param data	statistics data on the Viterbi algorithm execution.
	 * 
	 * @return number of unknown words correctly identified.
	 */
	private int identifiedUnknownWords(AnalyzedData data)
	{
		HashSet<String> unknownWords = data.getUnknowWords();
		
		int correctlyIdentified = 0;
		
		ArrayList<TaggedToken> tagged = new ArrayList<TaggedToken>();
		
		tagged = data.getTaggedData();
		
					
		for(String word : unknownWords)
		{
			for (TaggedToken taggedData : tagged)
			{
				if(taggedData.token.split(" ")[0].equals(word) && (taggedData.token.split(" ")[1].equals(taggedData.matrixData.rowX))  )
				{
					correctlyIdentified++;
				}
			}
		}
		
		return correctlyIdentified;
	}
	
	
}
