package main;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

/**
 * Class used to transform a JavaRDD into a JavaPairRDD. Used for creating the first JavaPairRDD for the LCS problem.
 * @author fede3751
 *
 */
public class LCSMapFunction implements PairFunction<Tuple2<MatrixKey, MatrixValue>, MatrixKey, MatrixValue>
{
	
	private static final long serialVersionUID = 1L;

	@Override
	public Tuple2<MatrixKey, MatrixValue> call(Tuple2<MatrixKey, MatrixValue> value) 
	{		
		return new Tuple2<>(value._1(), value._2());
	}

}
