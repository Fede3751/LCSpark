package main;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;


/**
 * When the LCSFlatter is called, the state of the values in the JavaRDD are still in a raw state,
 * A value received in input contains the information of both a row and a column because a reducer can
 * only produce one value.
 * Every value we are processing is going to be split in two different pairs of value.
 * With this class we split the unique value in two separate ones ready to be used in the next iteration 
 * of the MapReduce procedure.
 * @author fede3751
 *
 */
public class LCSPairFlatMapFunction implements PairFlatMapFunction<Tuple2<MatrixKey, MatrixValue>, MatrixKey, MatrixValue>
{
	private static final long serialVersionUID = 1L;
	private int size;
	
	public LCSPairFlatMapFunction() {}
	
	public LCSPairFlatMapFunction(int size)
	{
		this.size=size;
	}
	@Override
	public Iterator<Tuple2<MatrixKey, MatrixValue>> call(Tuple2<MatrixKey, MatrixValue> tuple)
	{

		//Store current size and save x and y position of the value we are processing
		//(processing local variables is slightly faster)
		int originX = tuple._1().getX();
		int originY = tuple._1().getY();
		
		MatrixValue mv = tuple._2();
		
		ArrayList<Tuple2<MatrixKey, MatrixValue>> temp = new ArrayList<>(2);
		if(originX == 0 && mv.isRaw())
		{
			//generate also a column with all zeros
			temp.add
			(new Tuple2<MatrixKey, MatrixValue>
					(
							new MatrixKey(originX, originY+size),
							new MatrixValue(originX, originY+size, ValueType.COLUMN, new int[size+1])
					)
			);
		}
		else if(originY == 0 && mv.isRaw())
		{
			//generate also a row with all zeros
			temp.add
			(new Tuple2<MatrixKey, MatrixValue>
					(
							new MatrixKey(originX+size, originY),
							new MatrixValue(originX+size, originY, ValueType.ROW, new int[size+1])
					)
			);
		}
		
		if(mv.isRow())
		{
			MatrixKey key 		= new MatrixKey(originX, originY+size);
			MatrixValue value 	= new MatrixValue(originX, originY+size, ValueType.ROW, mv.getRowValues());
			temp.add(new Tuple2<>(key, value));
			return temp.iterator();
		}		
		else if(mv.isColumn())
		{
			MatrixKey key 		= new MatrixKey(originX+size, originY);
			MatrixValue value 	= new MatrixValue(originX+size, originY, ValueType.COLUMN, mv.getColumnValues());
			temp.add(new Tuple2<>(key, value));
			return temp.iterator();
		}
		
		
		
		//Save the values in the proper objects
		MatrixKey key1 		= new MatrixKey(originX, originY+size);
		MatrixValue value1 	= new MatrixValue(originX, originY+size, ValueType.ROW, mv.getRowValues());
		
		MatrixKey key2 		= new MatrixKey(originX+size, originY);
		MatrixValue value2 	= new MatrixValue(originX+size, originY, ValueType.COLUMN, mv.getColumnValues());
		
		//and add them to the value to return
		temp.add(new Tuple2<MatrixKey, MatrixValue>(key1, value1));
		temp.add(new Tuple2<MatrixKey, MatrixValue>(key2, value2));
		
		return temp.iterator();
	}



}