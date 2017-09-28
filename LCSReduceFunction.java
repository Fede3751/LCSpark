package main;

import org.apache.spark.api.java.function.Function2;


@SuppressWarnings("serial")
public class LCSReduceFunction implements Function2<MatrixValue, MatrixValue, MatrixValue> 
{

	@Override
	public MatrixValue call(MatrixValue mv1, MatrixValue mv2)
	{
		LCSJob job = LCSJob.getInstance();
		
		String fullW1 = job.getW1().value();
		String fullW2 = job.getW2().value();
		

		int x = mv1.getX();
		int y = mv1.getY();

		
		int size = Constants.SERIAL_CELL_SIZE;
		
		int sizeX = Math.min(fullW1.length() - x, size)+1;
		int sizeY = Math.min(fullW2.length() - y, size)+1;
		
		MatrixValue temp = null;
		int[] row;
		int[] col;

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
		
		String w1 = fullW1.substring(x, x+sizeX-1);
		String w2 = fullW2.substring(y, y+sizeY-1);
		
		LCSSerial serial = new LCSSerial(w1, w2, row, col);
		serial.solve();
		
		if(x+size >= fullW1.length())
			temp = new MatrixValue(ValueType.ROW, x, y, serial.getBottomCorner());
		else if(y+size >= fullW2.length())
			temp = new MatrixValue(ValueType.COLUMN, x, y, serial.getRightCorner());
		else
			temp = new MatrixValue(ValueType.MISC, x, y	, serial.getCorners(), sizeX);
		return temp;
	}

}
