package postagger.compute;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import postagger.structures.AnalyzedData;
import postagger.structures.BigramTag;
import postagger.structures.MatrixCell;
import postagger.structures.TaggedToken;

public class ViterbiHMM 
{
	//============================================ 	PRIVATE VARIABLES =============================================================
	
	
	// Holds an instance to this class.
	private volatile static ViterbiHMM instance;
	
	// Represents a temporary store of the built tag/state model from a prior computation phase.
	private AnalyzedData tempModel;
	
	// Represents a stream path to the testing dataset.
	private BufferedReader testData;
	
	// Represents a given sentence in the testing dataset being currently tagged.
	private ArrayList<String> sentence;
	
	// Represents the number of sentences in the test dataset.
	private int testDataSentenceCount = 0;
	
	// Determines if smoothing is enabled.
	private Boolean smoothing = false;
	
	// Determines if the format of the test data is different from the training data.
	private Boolean alternateFormat = false;
	
	// Represents the default value used to initially fill the bigram tag/state model.
	  private int modelDefaultFill = 0;
	
	// Represents the all the POS tags in a given dataset.
	private String[] posTags;
		
	// Represents the previous column in the probability matrix, used by the Viterbi algorithm.
	private String previousColumnLabel = " ";
	
	// Represents the Viterbi algorithm probability matrix.
	private HashMap<String, ArrayList<MatrixCell>> probabilityMatrix = new HashMap<String, ArrayList<MatrixCell>>();
	
	// Represents a list of each sentence token and the POS tag assigned to it by the Viterbi system.
	private ArrayList<TaggedToken> tTokens = new ArrayList<TaggedToken>();
	
	// Represent a list of all the unknown words encountered from the testing dataset.
	HashSet<String> unknownWords = new HashSet<String>();
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
  /**
	* Private Constructor of the ViterbiHMM class.
	*/
	private ViterbiHMM(){	}
	
	
  /**
   * Returns a singleton instance of the ViterbiHmmEngine class,
   * ensuring that only one instance of the ViterbiHmmEngine is active 
   * at any single time.
   * 
   */
	public static ViterbiHMM getInstance() 
	{
      if (instance == null)
      {
          synchronized (ViterbiHMM.class)
          {
              if (instance == null)
              {
                  instance = new ViterbiHMM();
              }
          }
      }
      return instance;
   }


	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Computes the POS tags for the sentences in a given test dataset.
	 * 
	 * @param posModel	the tag/state model used to compute the POS tags.
	 * 
	 * @param testingDataSource	the test dataset source.
	 * 	
	 * @throws IOException	if any error occurs while reading the test dataset source file.
	 * 
	 */
	public AnalyzedData computeTags(AnalyzedData posModel, BufferedReader testingDataSource, Boolean smoothingEnabled) throws IOException
	{
		tempModel = posModel;
		testData = testingDataSource;
		String lineRead = " ";
		smoothing = smoothingEnabled;
		
		// Create a set of POS tags as the state rows in the probability matrix. 
		createTagRows(posModel.getTagWord().keySet()); 
		
		// Tag the tokens in each sentence of the dataset, using the tag/state models.
		while ( (lineRead = testData.readLine()) != null )
		{
			sentence = new ArrayList<String>();
			while(true)
			{
				// End of sentence reached, break and go to the next sentence.
				if (lineRead.equals("")) { break; }
		
				// Read the next token.
				sentence.add(lineRead);
				lineRead = testData.readLine();
			}
			// Run the Viterbi algorithm on the sentence.
			testDataSentenceCount++;
			tagSentence(sentence.toArray(new String[sentence.size()]));
		}
		// Store all the tagged words/sentences in a data structure sent to the calling function.
		posModel.setTaggedData(tTokens); 
		// Store the number of sentences in the test dataset.
		posModel.setTestDataSentenceCount(testDataSentenceCount);
		// Store the list containing unknown words encountered in the testing dataset.
		posModel.setUnknownWords(unknownWords);
		return posModel;
	}
	
	
	//============================================ PRIVATE METHODS =============================================================
	
	
	/**
	 * Tags the list of tokens in a given sentence using the Viterbi algorithm.
	 * 
	 * @param tokens	list of sentence tokens.
	 * 
	 */
	private void tagSentence(String[] tokens)
	{
		// Checked the format of the incoming test data before processing.
		if (tokens[0].split(" ").length < 2 ) { alternateFormat = true; }
		
		HashMap<String, ArrayList<MatrixCell>> temp = new HashMap<String, ArrayList<MatrixCell>>(); 
		temp = createMatrix(tokens);
		computeStartingColumn(temp, tokens);
		computeRemainingColumns(temp, tokens);
	}
	
	 
	/**
	 * Stores the POS tags of the training dataset into an array.
	 * 
	 * @param tagRows the Set of POS tags in the training dataset.
	 * 
	 */
	private void createTagRows(Set<String> tagRows)
	{
		posTags = new String[tagRows.size()];
		int index = 0;
		
		for (String tag : tagRows)
		{
			posTags[index] = tag;
			index++;
		}
	}
	
	
	/**
	 * Builds out the Viterbi probability matrix.
	 * 
	 * @param columnTokens	all tokens of a given sentence to be tagged.
	 * 
	 * @return a newly created Viterbi probability matrix.
	 * 
	 */
	private HashMap<String, ArrayList<MatrixCell>> createMatrix(String[] columnTokens)
	{
		ArrayList<MatrixCell> columnCells;
		MatrixCell cell;
		
		for (int i = 0; i < columnTokens.length; i++)
		{
			columnCells  = new ArrayList<MatrixCell>();
			for(String row : posTags)
			{
				cell = new MatrixCell();
				cell.ColY = columnTokens[i].split(" ")[0]; cell.rowX =row;
				columnCells.add(cell);
			}
			
			probabilityMatrix.put(columnTokens[i].split(" ")[0], columnCells);
		}
		return probabilityMatrix;
	}
	
	
	/**
	 * Computes the values of the first column cells in the Viterbi probability matrix.
	 * 
	 * @param matrix	an empty viterbi probability matrix.
	 * 
	 * @param columnTokens	all tokens of a given sentence to be tagged.
	 * 
	 */
	private void computeStartingColumn(HashMap<String, ArrayList<MatrixCell>>  matrix, String[] columnTokens)
	{
		// The name of the current probability matrix column (i.e. the sentence token) and store it.
		ArrayList<MatrixCell> cells = matrix.get(columnTokens[0].split(" ")[0]);
		previousColumnLabel = columnTokens[0].split(" ")[0];
		
		// Variables to calculate backPointer - - the most likely POS tag to which the token of that column is associated with.
		Double maxCell = 0.0;
		MatrixCell mostProbableCell = new MatrixCell();
		
		for(int i = 0; i < cells.size(); i++)
		{
			cells.get(i).cellValue = getTransitionProbability(cells.get(i).rowX, "<START>")[0] * getEmissionProbability(cells.get(i).rowX, cells.get(i).ColY)[0];
			
			// Perform backPointer determination.
			if (cells.get(i).cellValue > maxCell) { maxCell = cells.get(i).cellValue; mostProbableCell = cells.get(i); }
		}
		// Set this Viterbi probability matrix column(sentence-token) to it POS tags cells in the matrix.
		matrix.put(columnTokens[0].split(" ")[0], cells);
		
		// Assign this column sentence-token to the 'mostProbableCell' - to know the POS tag to which this current token is most likely associated with.
		TaggedToken tagged = new TaggedToken();
		tagged.token = columnTokens[0]; tagged.matrixData = mostProbableCell;
		tTokens.add(tagged);
	}
	
	
	/**
	 * Compute the values of the remaining column cells in the Viterbi matrix.
	 * 
	 * @param matrix the Viterbi probability matrix.
	 * 
	 * @param columnTokens	the remaining tokens in the sentence currently being tagged.
	 * 
	 */
	private void computeRemainingColumns(HashMap<String, ArrayList<MatrixCell>>  matrix, String[] columnTokens)
	{
		// Variable needed to compute the probabilities of the remaining columns in the matrix.
		ArrayList<MatrixCell> cells = new ArrayList<MatrixCell>();
		
		// Mapping of the cumulative maximum probability of the previous column-tokens and its probability.
		HashMap<String, Double> tempCellTransitionVal;
		
		//Variables for backPointer tracking.
		MatrixCell mostProbableCell;
		Double maxCumulativeMatrixProbability = 0.0;
		
		// For every column sentence-token and For each probability matrix cell(POS tag) for that sentence-token,
		// compute the probability based on the cumulative maximum probabilities of the previous column-sentence tokens.
		for(int i = 1; i < columnTokens.length; i++ )
		{
			cells = matrix.get(columnTokens[i].split(" ")[0]);
			Double maxCell = 0.0;
			mostProbableCell = new MatrixCell();
			
			for (int j = 0; j < cells.size(); j++)
			{
				tempCellTransitionVal = computeTransitionMax(previousColumnLabel, cells.get(j).rowX);
				for(String posTag : tempCellTransitionVal.keySet()) { maxCumulativeMatrixProbability = tempCellTransitionVal.get(posTag); }
				cells.get(j).cellValue = maxCumulativeMatrixProbability * getEmissionProbability(cells.get(j).rowX, columnTokens[i].split(" ")[0])[0];
				
				//Determine the backPointer - - the most likely POS tag to which the current token of this column is associated with.
				if (cells.get(j).cellValue > maxCell) { maxCell = cells.get(j).cellValue; mostProbableCell = cells.get(j); }
			}
			
			// Assign this column sentence-token to the 'mostProbableCell' - to know the POS tag to which this current token is most likely associated with.
			TaggedToken tagged = new TaggedToken();
			tagged.token = columnTokens[i]; tagged.matrixData = mostProbableCell;
			tTokens.add(tagged);
			
			// Set the current column as the old-column for the next iteration.
			previousColumnLabel = columnTokens[i].split(" ")[0];
			// Set this Viterbi probability matrix column(sentence-token) to it POS tags cells in the matrix.
			matrix.put(columnTokens[i].split(" ")[0], cells);
		}
	}
	
	
	/**
	 * Computes the intermediary maximum transition probability of 
	 * the previous column cells in the probability matrix for a given 
	 * current cell's probability being computed. 
	 * 
	 * @param previousColumnName	the previous column in the probability matrix.
	 * 
	 * @param currentTransitionRow	the POS tag of the current cell whose transition probability is being computed.
	 * 
	 * @return the previous cell in the probability matrix with the highest probability.
	 * 
	 */
	private HashMap<String, Double> computeTransitionMax(String previousColumnName, String currentTransitionRow)
	{
		// Variables needed for copying the probability matrix to
		// a temporary matrix to prevent operations on the current
		// column in the matrix from affecting the previous columns.
		ArrayList<MatrixCell> matrix = new ArrayList<MatrixCell>();
		ArrayList<MatrixCell> tempMatrix = new ArrayList<MatrixCell>();
		HashMap<String, Double> maxCell = new HashMap<String, Double>();

		// Make a copy of the probability matrix to work with.
		copyMatrix(probabilityMatrix.get(previousColumnName), tempMatrix);
		copyMatrix(tempMatrix, matrix);
		tempMatrix.clear();
		
		// Store a probability matrix cell's probability and the maximum probability computed so far. 
		Double maxCellValue = 0.0; 
		Double columnCellProbability = 0.0;

		// Cumulative compute all the probabilities in this sentence-token's probability matrix cells.
		for(int i = 0; i < matrix.size(); i++)
		{
			
			Double columnCell = matrix.get(i).cellValue;
			columnCellProbability = columnCell * getTransitionProbability(currentTransitionRow, matrix.get(i).rowX)[0];
			// Determine which matrix cell has the maximum probability.
			if (columnCellProbability > maxCellValue){ maxCellValue = columnCellProbability; maxCell.put(matrix.get(i).rowX, maxCellValue); }
		}
	
		return maxCell;
	}
	
	 
	/**
	 * Computes the transition probability of a POS tag given a preceding tag.
	 * Adds Laplace smoothing if enabled.
	 * 
	 * @param followTag	the second POS tag in a bigram tag.
	 * 
	 * @param previousTag	the first POS tag in a bigram tag
	 * 
	 * @return	the normal and log probabilities of the second POS tag occurring immediately after the second.
	 * 
	 */
	private Double[] getTransitionProbability(String followTag, String previousTag)
	{
		// Handle Laplace (add-one) smoothing.
		// 'V' is the number of unique POS tags.
		int tagTypes = 0;
		if (smoothing) { tagTypes = (tempModel.getTagWord().keySet().size() - 1); }
		
		int previousTagCount = 0;
		int transistionTagCount = 0;
		Double[] probability = new Double[2];
		
		if (tempModel.getTagNumber().containsKey(previousTag))
		{	previousTagCount = tempModel.getTagNumber().get(previousTag); }
		
		BigramTag bTag = new BigramTag();
		bTag.setFirstTag(previousTag); bTag.setSecondTag(followTag);
		
		if (tempModel.getBigramTags().containsKey(bTag) )
		{
			transistionTagCount = tempModel.getBigramTags().get(bTag);
		}
		
		probability[0] = computeProbability(transistionTagCount, previousTagCount+tagTypes);
		probability[1] = computeLogProbability(transistionTagCount, previousTagCount+tagTypes);
		return probability;
	}
	
	
	/**
	 * Computes the emission probability that a given word is associated with a given POS tag.
	 * 
	 * @param tag	a given POS tag.
	 * 
	 * @param givenWord	a given word that is associated with the POS tag.
	 * 
	 * @return	the normal and log probabilities of the given word being associated with the given POS tag.
	 * 
	 */
	private Double[] getEmissionProbability(String tag, String givenWord)
	{
		Double[] probability = new Double[2];
		int tagCount = 0;
		int givenWordCount = 0;
		if (smoothing) { modelDefaultFill = 1;}
		
		
		// Handle unknown words that are not in the training dataset,
		// by putting them in their own POS tag class <UNK> and assigning
		// their frequency to be 1.
		if (!PosModeler.getInstance().getUniqueTokens().contains(givenWord))
		{
			unknownWords.add(givenWord);
			tempModel.getTagWord().put("<UNK>", convertToList(unknownWords));
			
			probability[0] = computeProbability((1 + modelDefaultFill), tempModel.getTagNumber().get(tag));
			probability[1] = computeLogProbability((1 + modelDefaultFill), tempModel.getTagNumber().get(tag));
			return probability;
		}
		
		if (tempModel.getTagWord().containsKey(tag) && tempModel.getTagNumber().containsKey(tag)  )
		{	tagCount = tempModel.getTagNumber().get(tag); 
			givenWordCount = getTagWordOccurrence(tag, givenWord);
		}
		
		probability[0] = computeProbability(givenWordCount, tagCount);
		probability[1] = computeLogProbability(givenWordCount, tagCount);
		return probability;
	}
	
	
	/**
	 * Computes the rate of occurrence of a given word with a specific tag.
	 * 
	 * @param tag	 a given POS tag.
	 * 
	 * @param givenWord	 a given word associated with the given POS tag.
	 * 
	 * @return	the rate of occurrence at which the given word is associated with the given POS tag.
	 * 
	 */
	private int getTagWordOccurrence(String tag, String givenWord)
	{	
		ArrayList<String> wordList = tempModel.getTagWord().get(tag);
		int givenWordCount = 0;
		
		if ( wordList.contains(givenWord))
		{	for (String word : wordList)
			{
				if (word.equals(givenWord)) { givenWordCount++; } 
			}
		}
		return givenWordCount;
	}
	
	
	/**
	 * Computes the normal probability of an event occurring.
	 * 
	 * @param event	a given event.
	 * 
	 * @param totalOccurrence	the total number of occurrences.
	 * 
	 * @return	the normal probability that a given event occurs.
	 * 
	 */
	private Double computeProbability(int event, int totalOccurrence)
	{
		Double probability = 0.0;
		probability = ((new Integer(event).doubleValue() )/(new Integer(totalOccurrence).doubleValue()));
		//if (probability == 0) { return NEGATIVE_PROBABILITY; }
		//else { return probability; }	
		
		return probability;
	}
	
	
	/**
	 * Computes the log probability of an event.
	 * 
	 * @param event	a given event.
	 * 
	 * @param totalOccurrence	the total number of occurrences.
	 * 
	 * @return	the log probability that a given event occurs.
	 * 
	 */
	private Double computeLogProbability(int event, int totalOccurrence)
	{
		Double logProbability = 0.0;
		Double probability = ((new Integer(event).doubleValue() )/(new Integer(totalOccurrence).doubleValue()));
		if (probability == 0.0 || probability == 1.0) { return 0.0; }
		else { logProbability = Math.log(probability); }
		
		return logProbability;
	}
	
	
	/**
	 * Helper method that copies the value of the cells in the old
	 * matrix into a new matrix.
	 * 
	 * @param oldCells	the old matrix to copy values from.
	 * 
	 * @return	a new matrix with the copied old values.
	 * 
	 */
	private void copyMatrix(ArrayList<MatrixCell> oldCells, ArrayList<MatrixCell> newCells)
	{
		for ( MatrixCell thing : oldCells)
		{
			newCells.add(thing);
		}	
	}
	
	
	/**
	 * Helper method that converts the words in a HashSet
	 * to an ArrayList.
	 * 
	 * @param words	set of words to convert into a list.
	 * 
	 * @return	list containing the converted words.
	 */
	private ArrayList<String> convertToList(HashSet<String> words)
	{
		ArrayList<String> convertedWords = new ArrayList<String>();
		
		for (String word : words)
		{
			convertedWords.add(word);
		}
		
		return convertedWords;
	}
	
	
}
