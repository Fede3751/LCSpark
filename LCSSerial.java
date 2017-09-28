package main;

/**
 * Class used to solve a given dynamic problem (LCS in this current version) in iteratively.
 * This function should be used to solve smaller problems of a bigger problem in a parallel paradigm
 * @author fede3751
 *
 */
public class LCSSerial
{

	private String 		s1;
	private String 		s2;
	
	private int[][] 	matrix;
	private int[] 		cornerRight;
	private int[] 		cornerBottom;
	private int[]		corners;
	
	
	/**
	 * Minimal constructor, top and left borders' cells are set to 0
	 * @param s1 : First string
	 * @param s2 : Second string
	 */
	public LCSSerial(String s1, String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
		
		int height 	= 	s2.length()+1;
		int width 	= 	s1.length()+1;
		
		
		matrix = new int[height][width];
		
		cornerBottom 	= new int[width];
		cornerRight 	= new int[height];
		corners 		= new int[width+height];
	}

	/**
	 * Creates a SerialSubstring object and sets it's left and top corners to the given one
	 * @param s1 : First string
	 * @param s2 : Second string
	 * @param leftCorner : Left corner of cells to set
	 * @param topCorner : Top corner of cells to set
	 */
	public LCSSerial(String s1, String s2, int[] topCorner, int[] leftCorner)
	{
		this(s1, s2);
		
		for(int i=0; i<s2.length()+1; i++)
			matrix[i][0] = leftCorner[i];
		for(int i=0; i<s1.length()+1; i++)
			matrix[0][i] = topCorner[i];
	}
	
	/**
	 * GetMethod, only top and left corners have the right value if solve() is not called
	 * @return The calculated matrix
	 */
	public int[][] getMatrix()
	{
		return matrix;
	}
	
	/**
	 * GetMethod, quicker call, used for the MapReduce problem
	 * @return The bottom corner of cells in the calculated matrix
	 */
	public int[] getBottomCorner()
	{
		return cornerBottom;
	}
	
	/**
	 * GetMethod, quicker call, used for the MapReduce problem
	 * @return The right corner of cells in the calculated matrix
	 */
	public int[] getRightCorner()
	{
		return cornerRight;
	}
	
	public int[] getCorners()
	{
		return corners;
	}
	
	/**
	 * Solves the given Substring problem iteratively 
	 * @return The object itself
	 */
	public LCSSerial solve()
	{
		
		int width = matrix[0].length;
		int height = matrix.length;

		
		for(int i=1; i<height; i++)
			for(int j=1; j<width; j++)
				matrix[i][j] = applyRule(i, j);
		
		for(int i=0; i<width; i++)
		{
			cornerBottom[i] = matrix[height-1][i];
			corners[i] = matrix[height-1][i];
		}
		
		for(int i=0; i<height; i++)
		{
			cornerRight[i] = matrix[i][width-1];
			corners[i+width] = matrix[i][width-1];
		}
		
		
		
		return this;
	}
	
	
 
	/**
	 * Private method which defines how cells are filled.
	 * Modify this method for a wider range of problems to be solved
	 * @param i : The y coordinate of the cell to apply the rule
	 * @param j : The x coordinate of the cell to apply the rule
	 * @return : the value to write in the cell
	 */
	private int applyRule(int i, int j)
	{
		/* Current condition: just use LCS problem */
		if(s1.charAt(j-1) == s2.charAt(i-1))
			return matrix[i-1][j-1] + 1;
		else
			return Math.max(matrix[i-1][j], matrix[i][j-1]);
		
	}
}
