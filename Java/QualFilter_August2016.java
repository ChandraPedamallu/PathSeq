/*
 * Author: Chandra Sekhar Pedamallu
 * Usage:
 * Details: Filters FQ1 file for reads that pass a Q-value cut-off.
 */

import java.io.*;
import java.util.*;

public class QualFilter_August2016 {



	public static void main(String args[]) throws FileNotFoundException {

		String inputFile	= args[0]; //fq1 file

		String cutOff   	= args[1]; //set the cut-off score
		String exptBases	= args[2]; //set the number of bases NOT required to meet the cut-off
		String offset		= args[3]; //offset Q-value, usually 37 or 64, should be positive
		String outputFile	= args[4]; //Good quality Reads in FQ1



		int co= Integer.parseInt(cutOff);
		int eb= Integer.parseInt(exptBases);
		int os= Integer.parseInt(offset);





		//long start = System.currentTimeMillis();

		long seqCounter = 0;
		long qfPassCounter = 0;

		try{
			// Quality Reads

			BufferedWriter outfile = new BufferedWriter(new FileWriter(outputFile));

			FileReader fr=new FileReader(inputFile);
			BufferedReader fstream=new BufferedReader(fr);


			String strLine;

			HashMap uniquereads=new HashMap();
			int sequence_count=1;

			while ((strLine = fstream.readLine()) != null)   {
				seqCounter++;
				int baseQualCounter = 0;
				int badbaseQualCounter=0;
				char Add='Y';

				//String[] column = strLine.split("\t");
				String seqname="", seqreads="", seqstrand="", quality="", compseqreads="";
				StringTokenizer str=new StringTokenizer(strLine, "\t");
				int token_count=0;
				while(str.hasMoreTokens()){
					String tokens=str.nextToken().toString();
					if(token_count==0){
						seqname=tokens;
					}
					else if(token_count==1){
						seqreads=tokens.toUpperCase();
					}
					else if(token_count==2){
						seqstrand=tokens;

					}
					else if(token_count==3){
						quality=tokens;
					}

					token_count++;
				}

				for (int i=0; i < quality.length(); i++) {
					if( (int) quality.charAt(i)-os < co ){ //set the cut-off score
						badbaseQualCounter++;
					}
					if ( badbaseQualCounter > Integer.parseInt(exptBases) ) {
						Add='N';
						break;
					}
				}
				if ( Add=='Y') { //set the number of bases required to meet cut-off
					qfPassCounter++;

					outfile.write(seqname+"_AA"+qfPassCounter+"\t");
					outfile.write(seqreads+"\t");
					outfile.write(seqstrand+"\t");
					outfile.write(quality+"\n");
				}
			}

			fr.close();
			//long elapsedTimeMillis = System.currentTimeMillis()-start;
			//float elapsedTimeSec = elapsedTimeMillis/1000F;
			//System.out.println(	seqCounter + " reads in this dataset.\n" + qfPassCounter + " reads have passed the quality filter.\n" + "Amount of time: " + elapsedTimeSec);
			outfile.close();
			System.out.println(	seqCounter + " reads in this dataset.\n" + qfPassCounter + " reads have passed the quality filter.\n");


		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
