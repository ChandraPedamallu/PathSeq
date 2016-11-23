/*
 * Author: Chandra Sekhar Pedamallu
 * Usage:
 * Details: Filters FQ1 file for reads that pass a Q-value cut-off.
 */

import java.io.*;
import java.util.*;

public class QualFilter_RemoveDuplicate_August2016 {
	public static String complement(String readseq){
		String comp="";
		for(int k=0; k<readseq.length(); k++){
			if(readseq.charAt(k)=='A'){
				comp=comp+"T";
			}
			else if(readseq.charAt(k)=='T'){
				comp=comp+"A";
			}
			else if(readseq.charAt(k)=='G'){
				comp=comp+"C";
			}
			else if(readseq.charAt(k)=='C'){
				comp=comp+"G";
			}
			else if(readseq.charAt(k)=='N'){
				comp=comp+"N";
			}
		}

		return comp;

	}


	public static HashMap removeDuplicates(BufferedWriter outfile, HashMap uniquereads, String readseq, int sequence_count){
		try{

				// Tokenize each line to extra each information separartely
				String seqname="", seqreads="", seqstrand="", quality="", compseqreads="";
				StringTokenizer str=new StringTokenizer(readseq, "\t");
				int token_count=0;
				while(str.hasMoreTokens()){
					String tokens=str.nextToken().toString();
					if(token_count==0){
						seqname=tokens;
					}
					else if(token_count==1){
						seqreads=tokens.toUpperCase();
						compseqreads=complement(seqreads);
					}
					else if(token_count==2){
						seqstrand=tokens;

					}
					else if(token_count==3){
						quality=tokens;
					}

					token_count++;
				}

				if(uniquereads.size()==0){
					outfile.write(seqname+"_AA"+sequence_count+"\t");
					outfile.write(seqreads+"\t");
					outfile.write(seqstrand+"\t");
					outfile.write(quality+"\n");

					ArrayList tmp=new ArrayList();
					tmp.add(seqname);
					uniquereads.put(seqreads, tmp);
					sequence_count++;
				}
				else{
					if(uniquereads.containsKey(seqreads)){
						ArrayList tmp1=(ArrayList) uniquereads.get(seqreads);
						tmp1.add(seqname);
						uniquereads.put(seqreads, tmp1);
					}
					else if(uniquereads.containsKey(compseqreads)){
						ArrayList tmp1=(ArrayList) uniquereads.get(compseqreads);
						tmp1.add(seqname+"_COMPLEMENT");
						uniquereads.put(seqreads, tmp1);
					}
					else{
						outfile.write(seqname+"_AA"+sequence_count+"\t");
						outfile.write(seqreads+"\t");
						outfile.write(seqstrand+"\t");
						outfile.write(quality+"\n");

						ArrayList tmp=new ArrayList();
						tmp.add(seqname);
						uniquereads.put(seqreads, tmp);
						sequence_count++;
					}

				}
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return uniquereads;

	}


	public static void main(String args[]) throws FileNotFoundException {

		String inputFile	= args[0]; //fq1 file

		String cutOff   	= args[1]; //set the cut-off score
		String exptBases	= args[2]; //set the number of bases NOT required to meet the cut-off
		String offset		= args[3]; //offset Q-value, usually 37 or 64, should be positive
		String outputFile1	= args[4]; //Unique Reads in FQ1



		int co= Integer.parseInt(cutOff);
		int eb= Integer.parseInt(exptBases);
		int os= Integer.parseInt(offset);





		//long start = System.currentTimeMillis();

		long seqCounter = 0;
		long qfPassCounter = 0;

		try{
			// Quality Reads
			//BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

			// Unique reads
			BufferedWriter outfile = new BufferedWriter(new FileWriter(outputFile1));
			BufferedWriter outfile1 = new BufferedWriter(new FileWriter(outputFile1+".catalogue"));
			BufferedWriter outfile2 = new BufferedWriter(new FileWriter(outputFile1+".badquality"));
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
						compseqreads=complement(seqreads);
					}
					else if(token_count==2){
						seqstrand=tokens;

					}
					else if(token_count==3){
						quality=tokens;
					}

					token_count++;
				}


				if(uniquereads.containsKey(seqreads)){
						outfile1.write(seqname+"\t"+seqreads+"\tExact\n");
				}
				else if (uniquereads.containsKey(compseqreads)){
						outfile1.write(seqname+"\t"+seqreads+"\tComplement\n");
				}
				else{
					for (int i=0; i < seqreads.length(); i++) {
						if( (int) seqreads.charAt(i)-os < co ){ //set the cut-off score
							badbaseQualCounter++;
						}
						if ( badbaseQualCounter > Integer.parseInt(exptBases) ) {
							Add='N';
							break;
						}
					}
					if ( Add=='Y') { //set the number of bases required to meet cut-off
						qfPassCounter++;

						outfile.write(seqname+"_AA"+sequence_count+"\t");
						outfile.write(seqreads+"\t");
						outfile.write(seqstrand+"\t");
						outfile.write(quality+"\n");
						outfile1.write(seqname+"\t"+seqreads+"\tExact\n");

						uniquereads.put(seqreads, "0");

					}
					else{
						outfile2.write(seqname+"\t"+seqreads+"\tExact\n");
					}
				}
			}

			fr.close();
			//long elapsedTimeMillis = System.currentTimeMillis()-start;
			//float elapsedTimeSec = elapsedTimeMillis/1000F;
			//System.out.println(	seqCounter + " reads in this dataset.\n" + qfPassCounter + " reads have passed the quality filter.\n" + "Amount of time: " + elapsedTimeSec);
			outfile.close();
			outfile1.close();
			outfile2.close();


			System.out.println(	seqCounter + " reads in this dataset.\n" + qfPassCounter + " reads have passed the quality filter.\n");


		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
