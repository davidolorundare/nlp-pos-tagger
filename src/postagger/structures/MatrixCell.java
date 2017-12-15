package postagger.structures;

/**
 * The class represents a data 
 * structure used to store the 
 * cells of the Viterbi probability
 * matrix.
 * 
 * @author David Olorundare
 *
 */
public class MatrixCell 
{
	//============================================ PRIVATE VARIABLES =============================================================
	
	// Represents the POS tag of the current cell.
	public String rowX = " ";
	
	// Represent the sentence token of the current cell.
	public String ColY = " ";
	
	// Represents the probability of the current cell.
	public Double cellValue = 0.0;
	
	//============================================ CONSTRUCTOR =============================================================
	
	// No Constructor

	//============================================ PUBLIC METHODS =============================================================
		
	// No Public methods
	
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private methods
	
}
