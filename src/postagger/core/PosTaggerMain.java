package postagger.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import postagger.compute.PosModeler;
import postagger.structures.AnalyzedData;
import postagger.utils.FileHandler;
import postagger.utils.OutputPrinter;


/**
 * 					CSC 594 ASSIGNMENT 3: PARTS-OF-SPEECH TAGGER
 * 
 * This is the entry main class for the HMM POS Tagger program.
 *
 * :EXAMPLE USAGE:
 * 
 * Compile the .java files (i.e. javac *.java) then,
 * From the command line run:
 * 
 * >> java PosTaggerMain <input_file_containing_training-dataset> <input_file_containing_test-dataset> <output_file_to_store_tagged_data> <optional smoothing-switch: -S>
 * 
 * 
 * 		:PROGRAM OVERVIEW:
 * 
 * The program implements a Hidden Markov Model Parts of Speech (POS) Tagger built
 * using an input training dataset file, and evaluated using the dataset in the test file 
 * It also returns, in the output file, a statistics of the tagging accuracy of the program. 
 *  * if the '-S' switch is included; Laplace (add-one) smoothing is used when building the bigram tag/state model, 
 *   
 * 
 * 
 * 		:PROGRAM OPERATION/STRUCTURE:
 * 
 * The implementation uses a pipeline-architecture, and code is split into four main components: 
 * 
 * INPUT FILE HANDLING --> TAG_MODEL BUILDING (with or without smoothing) -> HMM VITERBI COMPUTATION AND ANALYSIS --> OUTPUT (Tagged data with overall performance statistics)
 * 
 * The program variables and component are first initialized,
 * then the input tagged training dataset is taken from the 
 * file provided at the command line.
 * The bigram tag models (with transition and emission probabilities) 
 * are built using this data, the HMM Viterbi algorithm is then used
 * to implement the POS tagging system; which is evaluated for its 
 * tagging-accuracy using the test dataset provided. 
 * The results of the computation - the Viterbi tagged data and
 * other performance statistics, confusion matrix, etcc - are outputted 
 * to the console, as well as stored in the specified external output text file.
 *  
 * 
 * @author David Olorundare
 *
 */
public class PosTaggerMain 
{
	public static void main(String[] args) throws IOException
	{
		
		//==================== INITIALIZATION OF PROGRAM COMPONENTS ========================================
		Boolean smoothing = false;
		FileHandler textData = FileHandler.getInstance();
		PosModeler textComputation = PosModeler.getInstance();
		OutputPrinter output = OutputPrinter.getInstance();
		AnalyzedData tagAnalysis = new AnalyzedData();
			
		//============================ INPUT FILE HANDLING, TAG MODELING AND VITERBI COMPUTATION =========================================
		
		// Take input file from the command line, operate on it, and store results in the output file.
		if (args.length >= 2)
		{
			// Set the training dataset, test dataset, and output-results file locations.
			textData.setTrainDataFilePath(args[0]);
			textData.setTestDataFilePath(args[1]);
			textData.setOutputResultFilePath(args[2]);
			
            //	Extra Functionality (compute perplexity, add smoothing, generate-random-sentences)
			if (args.length > 3)
			{
				for (int index = 3; index < args.length; index++ )
				{
					// Include Laplace smoothing.
					if (args[index].equals("-S")){ smoothing = true; }	
				}
			}
			// Begin measure execution time.
			//Long time1 = System.currentTimeMillis();
			try 
			{
				// Load the input text, build the tag models, run the HMM Viterbi system, and return the tagged data and statistics results.
				tagAnalysis = textData.loadAndCompute(textComputation, smoothing); 
			
		   //============================== RESULTS-PRINTING  ======================================================
				
				// Print out tagging data and performance statistics of given input text and stores it in an external file.
				output.printAnalysisToScreen(tagAnalysis);
		        
		   //====================================================================================================
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			// End measure of execution time.
			//Long time2 = System.currentTimeMillis();
			//System.out.println("\nTime taken to execute: " + (time2 - time1) + " ms");
		}
		else 
		{
			// Show the user some Usage-info.
			System.out.println(":Usage: ./java java PosTaggerMain <input_file_containing_training-dataset>"
					+ " <input_file_containing_test-dataset> <output_file_to_store_tagged_data> "
					+ "<optional smoothing-switch: -S>");
			return;
		}
	}
}
