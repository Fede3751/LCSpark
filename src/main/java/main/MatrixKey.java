package main;

import scala.Serializable;

/**
 * Class which defines a key in a (key, value) pair for the LCS Problem.
 * @author fede3751
 *
 */
public class MatrixKey implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	
	/**
	 * Simple constructor used to set the x and y value of the MatrixKey
	 * @param x The X value to set
	 * @param y The Y value to set
	 */
	public MatrixKey(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Simple GetMethod
	 * @return The x value of the MatrixKey
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Simple GetMethod
	 * @return The y value of the MatrixKey
	 */
	public int getY()
	{
		return y;
	}
	
	
	@Override
	public boolean equals(Object o)
	{
		if (o.getClass() != getClass())
			return false;
		else 
		{
			MatrixKey mk = (MatrixKey)o;
			return (mk.getY() == getY()) && (mk.getX() == getX());
		}
	}
	
	@Override
	public int hashCode()
	{
		//HashCode is simplified to a simple return y to let the partitioner divide 
		//every SubMatrix correctly without touching the partitioner itself
		return y;
	}
}
