/*
 * Author: Chandra Sekhar Pedamallu
 * Usage: 
 * Details: Run after conversion of FASTQ to one-line FASTQ (FQ1).  This converts a FQ1 file back to FASTQ format.
 */


import java.io.*;


public class FQone2Fastq {

	private static String caller= "FQone2Fastq ";

	public static void main(String args[]) throws FileNotFoundException {

		String inputFile=  args[0]; 
		String outputFile= args[1];



		long start = System.currentTimeMillis();

		int seqCounter = 0;
		int incompleteCount= 0;

		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			
			
			FileReader fr1=new FileReader(inputFile);
			BufferedReader br=new BufferedReader(fr1);
			String strLine;

			while ((strLine = br.readLine()) != null)   {
				seqCounter++;
				String[] column= strLine.split("\t");
				if ( column.length == 4 ) {
					out.write(column[0] + "\n" + 
							column[1] + "\n" +
							column[2] + "\n" + 
							column[3] + "\n");
				}
				else
					incompleteCount++;
			}
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(seqCounter+" reads in this dataset."+ incompleteCount +"  Amount of time: "+elapsedTimeSec);

			fr1.close();
			out.flush();
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public static String getCaller() {
		return caller;
	}
}
