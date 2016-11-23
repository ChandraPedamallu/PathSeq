/*
 * Author: Chandra Sekhar Pedamallu
 * Usage: works with SplitFiles.java
 * Details: Splits a file into multiple files.
 */


import java.io.FileNotFoundException;


public class FileSplitter {
	
	private static String caller= "FileSplitter ";

	public static void main(String args[]) throws FileNotFoundException, IllegalArgumentException {

		//long start = System.currentTimeMillis();
		
		String inputFile= 			args[0]; //any file
		String linesPerFile= 		args[1]; //number of lines per file
		String outputFileName=		args[2]; //Name of the output file
		String makeNewDirectory=	args[3]; 	//Make new directory for each file? (FOREACH);
												//Make one new directory for all files? (ONE);
												//Make no new directories? (NONE);

		int nlpf= Integer.parseInt(linesPerFile);
		
		int mnd= 0;
		if ( makeNewDirectory.equals("FOREACH") )
			mnd= 1;
		if ( makeNewDirectory.equals("NONE") )
			mnd= 2;
		if ( makeNewDirectory.equals("ONE") )
			mnd= 3;

		try{		
			SplitFiles ob1 = new SplitFiles(inputFile,nlpf,outputFileName,mnd);

			//long elapsedTimeMillis = System.currentTimeMillis()-start;
			//float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(ob1.getNumFiles());

			/*System.out.println(ob1.getNumLines()+" lines in the original file.\n" + 
						ob1.getNumFiles()+" files have been created.\n" +
					"Time spent: "+elapsedTimeSec+" sec.");
			*/
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static String getCaller() {
		return caller;
	}
}

