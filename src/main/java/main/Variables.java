package main;


/**
 * Class used to store some commons variables used in the program (most of them are constants)
 * @author fede3751
 *
 */
public final class Variables
{
	public static int 			SERIAL_CELL_SIZE 	= 5000;
	public static final String 	SPLIT_STRING		= "\\s+";
	public static final String	OUTPUT_SEPARATOR 	= "-------------------";
	public static final String 	ACCESS_KEY 			= "";
	public static final String	PRIVATE_KEY			= "";
	public static final boolean AWS_MODE 			= false;
	public static final int 	CHECKPOINT_INTERVAL = 300;
	public static int 			NO_SLICES			= 21;
	public static float			CUT_PERCENTAGE 		= 1;
	public static String 		FILE_OUTPUT			= null;
	
	private Variables(){}
}
