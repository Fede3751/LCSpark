package main;

import scala.Serializable;

/**
 * Class which defines a value in a (key, value) pair for the LCS Problem.
 * @author fede3751
 *
 */
public class MatrixValue implements Serializable
{
	private static final long serialVersionUID = 1L;
	ValueType type;
	int[] rowValues;
	int[] colValues;
	int x, y;
	
	
	/**
	 * Constructor used to create a MatrixValue which represents only a row or a column
	 * @param x The x position of the values (the x position of the top-left cell in the matrix)
	 * @param y The y position of the values (the y position of the top-left cell in the matrix)
	 * @param type ValueType.ROW for rows and ValueType.COLUMN for columns
	 * @param values values to set
	 */
	public MatrixValue(int x, int y, ValueType type, int[] values)
	{
		this.x=x;
		this.y=y;
		this.type = type;
		if(type==ValueType.ROW)
			this.rowValues = values;
		if(type==ValueType.COLUMN)
			this.colValues = values;
	}
	
	/**
	 * Constructor used to create a MatrixValue in a raw state. Containing both row and column values.
	 * @param x The x position of the values (the most-left x coordinate of the values)
	 * @param y The y position of the values (the most-top y coordinate of the values)
	 * @param rowValues The values of the cells in the row
	 * @param colValues The values of the cells in the column
	 */
	public MatrixValue(int x, int y, int[] rowValues, int[] colValues)
	{
		this.type=ValueType.MISC;
		this.x=x;
		this.y=y;
		this.rowValues=rowValues;
		this.colValues=colValues;
	}
	
	
	/**
	 * Simple GetMethod
	 * @return The ValueType of the MatrixValue 
	 */
	public ValueType getType()
	{
		return type;
	}
	
	/**
	 * Simple boolean method used to check if the MatrixValue refers to row values.
	 * @return True if the type of the the MatrixValue is ValueType.ROW
	 */
	public boolean isRow()
	{
		return type==ValueType.ROW;
	}

	/**
	 * Simple boolean method used to check if the MatrixValue refers to column values.
	 * @return True if the type of the the MatrixValue is ValueType.COLUMN
	 */
	public boolean isColumn()
	{
		return type==ValueType.COLUMN;
	}
	

	/**
	 * Simple boolean method used to check if the MatrixValue refers to raw values.
	 * @return True if the type of the the MatrixValue is ValueType.MISC
	 */
	public boolean isRaw()
	{
		return type==ValueType.MISC;
	}
	

	/**
	 * Simple GetMethod
	 * @return The x position of the values 
	 */
	public int getX()
	{
		return x;
	}
	

	/**
	 * Simple GetMethod
	 * @return The y position of the values
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * Method used to retrieve the value cells which the MatrixValue refers to.
	 * To be used ONLY if the MatrixValue is not in a raw state
	 * @return The values in the matrix
	 */
	public int[] getValues()
	{
		if(type==ValueType.ROW)
			return rowValues;
		else
			return colValues;
	}
	

	/**
	 * Method used to retrieve the row values of the MatrixValue
	 * @return The values in the matrix
	 */
	public int[] getRowValues()
	{
		return rowValues;
	}
	
	/**
	 * Method used to retrieve the column values of the MatrixValue
	 * @return The values in the matrix
	 */
	public int[] getColumnValues()
	{
		return colValues;
	}
	
	@Override
	public String toString()
	{
		String temp = "";
		
		for (int i : rowValues)
		{
				temp = temp+" "+i;
		}
		
		return temp;
	}
	
	/**
	 * Transforms the values in the row in a formatted string. Method used only for debugging purposes
	 * @param values The values to print
	 * @return The formatted string
	 */
	public static String stringify(int[] values)
	{
		String temp = "";
		
		for (int i : values)
		{
				temp = temp+" "+i;
		}
		
		return temp;
	}
	
}