/*
 * Author: Chandra Sekhar Pedamallu
 * Usage: 
 * Details: After performing RepeatMasker, run this script to get all reads that passed the filter.
 */


import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepeatMaskerRead {

	private static String caller= "RepeatMaskerRead ";

	public static void main(String args[]) throws FileNotFoundException {

		long start = System.currentTimeMillis();

		String inputFile1= 	args[0]; //'.masked' file following RepeatMasker run
		String inputFile2=	args[1]; //one-line file of all reads that went into the filter (same number and order)
		String outputFile= 	args[2]; //output one-line file of all reads without low complexity regions
		String complexCO = 	args[3]; //cut-off for tolerable # of 'N's per read

		int coN= Integer.parseInt(complexCO);
		int maskCounter= 0; //counts the number of 'N's in a read

		int readCountForLength= 0;
		int readLength= 0;
		int nCounter= 0;

		int seqCounter= 0;
		ArrayList<Integer> passedReads = new ArrayList<Integer>();

		int fq1Counter= 0;
		int prIndexCount= 0;

		final String REF  = ">";   	   	Pattern refLine= Pattern.compile(REF);

		try{

			//Section 1: Find the read length (assumed to be same for all reads)
			FileReader fr0=new FileReader(inputFile1);
			BufferedReader br0=new BufferedReader(fr0);
					
			String strLine0;

			while ((strLine0 = br0.readLine()) != null && readCountForLength <= 1)   {
				Matcher refLineMatcher = refLine.matcher(strLine0);
				if ( refLineMatcher.find() ) {
					readCountForLength++;
				} else
					readLength= readLength + strLine0.length();
			}
			fr0.close();

			System.out.println("Read length: " + readLength +"bp");

			//Section 2: Make list of all reads that passed the filter
			FileReader fr1=new FileReader(inputFile1);
			BufferedReader br1=new BufferedReader(fr1);
			

			String strLine1;

			while ((strLine1 = br1.readLine()) != null)   {
				Matcher refLineMatcher = refLine.matcher(strLine1);

				if ( refLineMatcher.find() ) {
					seqCounter++;
					nCounter= 0;
					maskCounter= 0;
				} else
					nCounter= nCounter + strLine1.length();

				String[] column= strLine1.split("");
				for ( int i= 0; i < strLine1.length(); i++ )
					if ( column[i].equals("N") )
						maskCounter++;

				if ( (maskCounter <= coN) && (nCounter == readLength) )
					passedReads.add(seqCounter);

			}
			fr1.close();

			System.out.println( passedReads.size()+" reads passed the complexity filter." );

			//Section 3: Output all reads that passed the filter into a new file

			BufferedWriter out0 = new BufferedWriter(new FileWriter(outputFile));
			FileReader fr2=new FileReader(inputFile2);
			BufferedReader br2=new BufferedReader(fr2);

			String strLine2;

			while ((strLine2 = br2.readLine()) != null)   {

				fq1Counter++;

				if ( fq1Counter == passedReads.get(prIndexCount) ) {
					out0.write(strLine2 + "\n");
					if ( prIndexCount < passedReads.size()-1 )
						prIndexCount++;
				}

			}
			fr2.close();

			out0.flush();
			out0.close();

			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(seqCounter+" reads in this dataset.\n" +
					"\nAmount of time: " + elapsedTimeSec);

		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	public static String getCaller() {
		return caller;
	}
}
