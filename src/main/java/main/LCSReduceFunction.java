package main;

import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;

/**
 * Class used to apply the LCS algorithm to a sub-matrix.
 */
public class LCSReduceFunction implements Function2<MatrixValue, MatrixValue, MatrixValue> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Broadcast<String> w1;
	private Broadcast<String> w2;
	private int size;
	
	
	public LCSReduceFunction()
	{
	}
	
	public LCSReduceFunction(Broadcast<String> w1, Broadcast<String> w2, int size)
	{
		this.w1 = w1;
		this.w2 = w2;
		this.size = size;
	}

	@Override
	public MatrixValue call(MatrixValue mv1, MatrixValue mv2)
	{
		
		//Store current size and save x and y position of the value we are processing
		//(processing local variables is slightly faster)
		String fullW1 = w1.value();
		String fullW2 = w2.value();
		

		//Get the current X and Y position of the sub-matrix
		int x = mv1.getX();
		int y = mv1.getY();


		//Set the width and height of the sub-matrix
		//Normally they both should be Constants.SERIAL_CELL_SIZE, but on the edges they may be smaller
		int sizeX = Math.min(fullW1.length() - x, size)+1;
		int sizeY = Math.min(fullW2.length() - y, size)+1;
		
		//Initialize value to return
		MatrixValue temp = null;
		
		//Initialize both the edges
		int[] row;
		int[] col;

		//Check what mv1 and mv2 refer to, and assign them properly
		if(mv1.isRow())
		{
			row = mv1.getValues();
			col = mv2.getValues();
		}
		else
		{
			row = mv2.getValues();
			col = mv1.getValues();
		}
		
		//Take the substrings to check
		String w1 = fullW1.substring(x, x+sizeX-1);
		String w2 = fullW2.substring(y, y+sizeY-1);
		
		//Resolve the LCS problem of the sub-matrix using the serial approach
		LCSSerial serial = new LCSSerial(w1, w2, row, col);
		serial.solve();
		
		//Check if the edges have been reached and return the correct type of result
		if(x+size >= fullW1.length())
			temp = new MatrixValue(x, y, ValueType.ROW, serial.getBottomCorner());
		else if(y+size >= fullW2.length())
			temp = new MatrixValue(x, y, ValueType.COLUMN, serial.getRightCorner());
		else
			temp = new MatrixValue(x, y	, serial.getBottomCorner(), serial.getRightCorner());
		return temp;
	}

}