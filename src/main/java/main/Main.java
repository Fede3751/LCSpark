package main;


public class Main
{
	
	public static void main(String[] args)
	{	
		LCSJob job = LCSJob.getInstance();
		
		if(args.length != 5)
		{
			System.out.println("Usage mode: java infile cellSize no_partitions text_size output_log");
			
			//AWS Mode disabled
			//System.out.println("\nor\n");
			//System.out.println("java online bucketkey objectkey cellSize no_partitions text_size output_log");
			
			return;
		}
		
		job.setInputFile(args[0]);
		if(args.length>=2)
		{
			Variables.SERIAL_CELL_SIZE 	= Integer.parseInt(args[1]);
			Variables.NO_SLICES 		= Integer.parseInt(args[2]);
			Variables.CUT_PERCENTAGE	= Float.parseFloat(args[3]);
			Variables.FILE_OUTPUT		= args[4];
		}

		long start = System.currentTimeMillis();
		
		job.start();
		
		long end = System.currentTimeMillis();
		
		long totalTime = (end-start)/1000;
		
		System.out.println("Start time: "+start);
		System.out.println("End Time: "+end);
		System.out.println("\nTotal time (in seconds): "+totalTime);
		
	}
	
}
