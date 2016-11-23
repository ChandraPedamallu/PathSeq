/*
 * Details: Filters FQ1 file for reads that pass a Q-value cut-off.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class QualFilter {


	public static void main(String args[]) throws FileNotFoundException {

		String inputFile	= args[0]; //fq1 file
		String outputFile	= args[1];
		String cutOff   	= args[2]; //set the cut-off score
		String exptBases	= args[3]; //set the number of bases NOT required to meet the cut-off
		String filterType	= args[4]; //"region" OR "fullLength"
		String begRegionS	= args[5]; //start of region
		String endRegionS	= args[6]; //end of region
		String offset		= args[7]; //offset Q-value, usually 37 or 64, should be positive

		int co= Integer.parseInt(cutOff);
		int eb= Integer.parseInt(exptBases);
		int bqr= Integer.parseInt(begRegionS);
		int eqr= Integer.parseInt(endRegionS);
		int os= Integer.parseInt(offset);

		int readCountForLength= 0;
		int readLength= 0;
		int qualLength= 0;

		int nb=0;

		PrintWriter outstream = new PrintWriter(outputFile);
		BufferedWriter out = new BufferedWriter(outstream);


		long start = System.currentTimeMillis();

		long seqCounter = 0;
		long qfPassCounter = 0;

		try{
			//Section 1: Find the read length (assumed to be same for all reads)
			FileInputStream fstream0 = new FileInputStream(inputFile);
			DataInputStream in0 = new DataInputStream(fstream0);
			BufferedReader br0 = new BufferedReader(new InputStreamReader(in0));
			String strLine0;

			while ((strLine0 = br0.readLine()) != null && readCountForLength <= 1)   {
				readCountForLength++;

				String[] column = strLine0.split("\t");
				readLength= column[1].length();
				qualLength= column[3].length();
				if ( !(readLength == qualLength) ) {
					throw new IllegalArgumentException("PathSeq Error QF1: The sequence length does not equal the Q-value string length!");
				}
			}
			in0.close();

			int begQualRegion= 0;
			int endQualRegion= 0;
			if ( filterType.equals("region") ) {
				begQualRegion= bqr-1;
				endQualRegion= eqr;
				nb= endQualRegion - begQualRegion - eb;
			}
			else if ( filterType.equals("fullLength") ) {
				begQualRegion= 0;
				endQualRegion= qualLength;
				nb= readLength - eb;
			}
			else
				throw new IllegalArgumentException("\n\nMust specifiy a filter type.\n\n");

			System.out.println("Read length: " + readLength +"bp\n");


			FileInputStream fstream = new FileInputStream(inputFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null)   {
				seqCounter++;
				int baseQualCounter = 0;
				String[] column = strLine.split("\t");
				if( column[3].length() == qualLength ) {
					for (int i=begQualRegion; i < endQualRegion; i++) {
						if( (int) column[3].charAt(i)-os >= co ) //set the cut-off score
							baseQualCounter++;
					}
					if ( baseQualCounter >= nb ) { //set the number of bases required to meet cut-off
						qfPassCounter++;
						out.write(strLine + "\n");
					}
				} //else throw new IllegalArgumentException("Not all Q-value strings in the file are a uniform length.");
				else System.out.println("Format error for read record # " + seqCounter + ": Q-vlaue string length does not equal sequence length!  This read record has been discarded.");

			}

			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(	seqCounter + " reads in this dataset.\n" +
					qfPassCounter + " reads have passed the quality filter.\n" +
					"Amount of time: " + elapsedTimeSec);

			in.close();
			out.flush();
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
