package postagger.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import postagger.compute.PosModeler;
import postagger.compute.ViterbiHMM;
import postagger.structures.AnalyzedData;


/**
 * This class handles file processing of the  
 * training and test data files and delegates
 * other POS tag operations. 
 * 
 * 
 * @author David Olorundare
 *
 */
public final class FileHandler 
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents the filepath of a file containing the training dataset. 
	private String trainingDataSource;
	
	// Represents the filepath of a file containing the results of the text analysis.
	private String outputDestination; 
	
	// Represents the filepath of a file containing the test-dataset
	private String testingDataSource;
	
	// Represents a File read object for reading-in training data from a file.
	private BufferedReader readTrainingData;
	
	// Represents a File read object for reading-in testing data from a file.
	private BufferedReader readTestingData;
	
	// Holds an instance to this class.
	private volatile static FileHandler instance;
	
	// Represents the text processor used in the analysis of text data from a file.
	private PosModeler textProcessor;
	
	// Represents the POS model with emission and transition probability count data and other statistics
	private AnalyzedData posModel;
	

	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 *  Private Constructor of the FileHandler class.
	 */
	private FileHandler(){	}
	
	
  /**
   * Returns a singleton instance of the FileHandler class,
   * ensuring that only one instance of the Handler is active 
   * at any single time.
   * 
   */
	public static FileHandler getInstance() 
	{
      if (instance == null)
      {
          synchronized (FileHandler.class)
          {
              if (instance == null)
              {
                  instance = new FileHandler();
              }
          }
      }
      return instance;
   }
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that sets the current filepath
	 * of the input training dataset.
	 * 
	 * @param filePath	current filepath of the training dataset.
	 * 
	 */
	public void setTrainDataFilePath(String filePath)
	{
		trainingDataSource = filePath;
	}
	
	
	/**
	 * Helper method that sets the current filepath
	 * of the test-dataset used for evaluating the 
	 * language model.
	 * 
	 * @param filePath	current filepath of the test-dataset.
	 * 
	 */
	public void setTestDataFilePath(String filePath)
	{
		testingDataSource = filePath;
	}
	
	
	/** 
	 * Helper method that sets the current filepath
	 * of the output text-file used for storing the
	 * results of the langugage model operations.
	 * 
	 * @param filePath	current filepath of the output text-file.
	 * 
	 */
	public void setOutputResultFilePath(String filePath)
	{
		outputDestination = filePath;
	}
	
	
	/**
	 * Loads data from a file containing training data and
	 * delegates its tag model creation as well as execution
	 * of the HMM Viterbi algorithm to tag a given test dataset.
	 *  
	 * 
	 * @param	analyzer	an instance of the tag model system used to build the bigram tag/state models.
	 * 
	 * @param	smoothingEnabled	option that determines if smoothing is enabled.
	 * 
	 * @return	a structure containing the HMM Viterbi tagged data and other performance statistics,
	 *
	 * @throws IOException	if an error occurs while reading the input file.
	 * @throws FileNotFoundException	if the input or output text files are empty or cannot be found.
	 * 
	 */
	public AnalyzedData loadAndCompute(PosModeler analyzer, Boolean smoothingEnabled) throws IOException, FileNotFoundException
	{
		textProcessor = analyzer;
	
        // Read-in training and testing data. 
        readTrainingData = new BufferedReader(new FileReader(trainingDataSource)); 
        readTestingData = new BufferedReader(new FileReader(testingDataSource));
        
        // check if stream is ready for reading; analyze the text.
        if (readTrainingData.ready() && readTestingData.ready() )
        { posModel = textProcessor.buildModel(readTrainingData, smoothingEnabled);} 
        else { throw new IOException("Error Reading the Input training dataset"); }
        
        readTrainingData.close();
        
        // Pass the model format and the test dataset to the HMM Engine
        posModel = ViterbiHMM.getInstance().computeTags(posModel,readTestingData, smoothingEnabled);
        readTestingData.close();      
        
        return posModel;
    
	}
	
	
	/**
	 * Helper method that writes some string data
	 * to the given external output file.
	 * 
	 * @param data	the string data to be written to 
	 * 				a given external file.
	 * 
	 * @throws IOException if an error occurs while reading the input file.
	 */
	public void writeToFile(String data) throws IOException
	{
		Writer textFileWriter = new FileWriter(outputDestination);
		
		textFileWriter.write(data);
		textFileWriter.close();
	}
	
	
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private Methods

}
