package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

public class LCSJob
{
	private static LCSJob instance = null;
	private String infile;
	
	private static Broadcast<String> broadW1;
	private static Broadcast<String> broadW2;

	private LCSJob()
	{
	}
	
	public static LCSJob getInstance()
	{
		if(instance == null)
			instance = new LCSJob();
		return instance;
	}
	
	public void setInputFile(String infile)
	{
		this.infile=infile;
	}
	
	public Broadcast<String> getW1()
	{
		return broadW1;
	}
	
	public Broadcast<String> getW2()
	{
		return broadW2;
	}
	
	
	/**
	 * Method used to start a LCS problem on the given file
	 */
	public void start()
	{
		//First iteration, not parallelized, read word from input file	
		
		String w1 = "";
		String w2 = "";
		
		FileReader fr;
		BufferedReader br;
		
		try
		{
			fr = new FileReader(infile);
			br = new BufferedReader(fr);
			
			String originW1 = br.readLine();
			String originW2 = br.readLine();
			
			w1 = "";
			w2 = "";
			

			int iterations = (int)Math.floor(Variables.CUT_PERCENTAGE);
			
			for(int i=0; i<iterations; i++)
			{
				w1 = w1 + originW1;
				w2 = w2 + originW2;
			}
				
			float lastAmount = Variables.CUT_PERCENTAGE-(int)Math.floor(Variables.CUT_PERCENTAGE);
			
			w1 = w1 + originW1.substring(0, (int)(originW1.length()*lastAmount));
			w2 = w2 + originW2.substring(0, (int)(originW2.length()*lastAmount));
			
			fr.close();
			br.close();
		}
		catch (Exception e)
		{
			System.out.println("ERROR while trying to read the file: "+e.getMessage());
			System.out.println("Expected file syntax:\n\nword1\nword2");
		}
		

		if(Variables.SERIAL_CELL_SIZE > w1.length() || Variables.SERIAL_CELL_SIZE > w2.length())
		{
			System.out.println("ERROR: Cell size is too big for the given strings, please input a smaller one");
			System.exit(1);
		}
		
		long start = System.currentTimeMillis();
		
		int size = Variables.SERIAL_CELL_SIZE;
		
		String s1 = w1.substring(0, size);
		String s2 = w2.substring(0, size);
		
		LCSSerial ss = new LCSSerial(s1, s2);
		
		//Solve first sub-matrix in local
		ss.solve();
		
		
		
		//Now start real parallelized task with Spark
		
		//Set configuration parameters
		SparkConf spark = new SparkConf().setAppName("LCS");
		spark.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		
		//Create tuples to add to the RDD for the first iteration
		Tuple2<MatrixKey, MatrixValue> firstRow = new Tuple2<>
		(
				new MatrixKey(0, size),
				new MatrixValue(0, size, ValueType.ROW, ss.getBottomCorner())
		);
		Tuple2<MatrixKey, MatrixValue> firstCol = new Tuple2<>
		(
				new MatrixKey(size, 0),
				new MatrixValue(size, 0, ValueType.COLUMN, ss.getRightCorner())
		);
		Tuple2<MatrixKey, MatrixValue> zeroCol = new Tuple2<>
		(
				new MatrixKey(0, size),
				new MatrixValue(0, size, ValueType.COLUMN, new int[size+1])
		);
		Tuple2<MatrixKey, MatrixValue> zeroRow = new Tuple2<>
		(
				new MatrixKey(size, 0),
				new MatrixValue(size, 0, ValueType.ROW, new int[size+1])
		);
		
		List<Tuple2<MatrixKey, MatrixValue>> rawList = new ArrayList<>();
		
		rawList.add(firstRow);
		rawList.add(zeroCol);
		rawList.add(firstCol);
		rawList.add(zeroRow);

		JavaSparkContext context = new JavaSparkContext(spark);
		
		//Checkpoint (neither used nor tested):
		//context.setCheckpointDir("/your/directory/for/checkpoint");
		
		//Save input words in global context (so that every computer in the cluster can read them)
		broadW1 = context.broadcast(w1);
		broadW2 = context.broadcast(w2);
		
		//Calculate number of iterations
		int iterations 		= ((int)(Math.ceil((float)w1.length()/size))+(int)(Math.ceil((float)w2.length()/size)))-2;

		//Create RDD from the starting collection
		JavaPairRDD<MatrixKey, MatrixValue> temp = context.parallelize(rawList).mapToPair(new LCSMapFunction());


		//Apply Spark's operations on the RDD
		for(int i=0; i<iterations; i++)
		{
			temp = temp.reduceByKey(new LCSReduceFunction(broadW1, broadW2, Variables.SERIAL_CELL_SIZE), Variables.NO_SLICES);
			temp = temp.flatMapToPair(new LCSPairFlatMapFunction(Variables.SERIAL_CELL_SIZE));
			
			//Use collect to cache the RDD at a predefined interval
			if((i+1)%Variables.CHECKPOINT_INTERVAL == 0)
				temp.collect();
		}

		//Collect the final result
		List<Tuple2<MatrixKey, MatrixValue>> collectResult = temp.collect();

		
		long end = System.currentTimeMillis();
		float totalTime = ((float)(end-start))/1000;
		
		//Iterate on the collection and get the only item in it
		for(Tuple2<MatrixKey, MatrixValue> tuple : collectResult)
		{
	
				int[] values = tuple._2().getValues();
				
				String output = Variables.CUT_PERCENTAGE+"\t\t\t"+totalTime+"\t\t\t"+values[values.length-1]+"\n";
				
				if(Variables.FILE_OUTPUT == null)
					System.out.println(output);
				else
				{
					try
					{
						Files.write(Paths.get(Variables.FILE_OUTPUT), output.getBytes(), StandardOpenOption.APPEND);
					}
					catch(IOException e)
					{
						System.out.println("Couldn't write to the given file, to not waste this run, this is the output:\n");
						System.out.println(output);
					}
				}
		}
		
		context.close();
	}
}
