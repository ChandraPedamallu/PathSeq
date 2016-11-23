/*
 * Author: Chandra Sekhar Pedamallu
 * Usage:
 * Details: Filters FQ1 file for reads that pass a Q-value cut-off.
 */

import java.io.*;
import java.util.*;

public class QualFilter_RemoveDuplicate_July2016 {
	public static String complement(String readseq){
		String comp="";
		for(int k=0; k<readseq.length(); k++){
			if(readseq.charAt(k) == 'a' || readseq.charAt(k)=='A'){
				comp=comp+"T";
			}
			else if(readseq.charAt(k) == 't' || readseq.charAt(k)=='T'){
				comp=comp+"A";
			}
			else if(readseq.charAt(k) == 'g' || readseq.charAt(k)=='G'){
				comp=comp+"C";
			}
			else if(readseq.charAt(k) == 'c' || readseq.charAt(k)=='C'){
				comp=comp+"G";
			}
			else if(readseq.charAt(k) == 'n' || readseq.charAt(k)=='N'){
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
		String outputFile	= args[1];
		String cutOff   	= args[2]; //set the cut-off score
		String exptBases	= args[3]; //set the number of bases NOT required to meet the cut-off
		String offset		= args[4]; //offset Q-value, usually 37 or 64, should be positive
		String outputFile1	= args[5]; //Unique Reads in FQ1



		int co= Integer.parseInt(cutOff);
		int eb= Integer.parseInt(exptBases);
		int os= Integer.parseInt(offset);





		long start = System.currentTimeMillis();

		long seqCounter = 0;
		long qfPassCounter = 0;

		try{
			// Quality Reads
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

			// Unique reads
			BufferedWriter outfile = new BufferedWriter(new FileWriter(outputFile1));
			BufferedReader fstream=new BufferedReader(new FileReader(inputFile));


			String strLine;

			HashMap uniquereads=new HashMap();
			int sequence_count=1;

			while ((strLine = fstream.readLine()) != null)   {
				seqCounter++;
				int baseQualCounter = 0;
				int badbaseQualCounter=0;
				String[] column = strLine.split("\t");
				for (int i=0; i < column[1].length(); i++) {
					if( (int) column[3].charAt(i)-os >= co ){ //set the cut-off score
						baseQualCounter++;
					}
					else{
						badbaseQualCounter++;
					}
				}
				if ( badbaseQualCounter <= Integer.parseInt(exptBases) ) { //set the number of bases required to meet cut-off
					qfPassCounter++;
					HashMap uniquereads1=(HashMap)removeDuplicates(outfile, uniquereads, strLine, sequence_count);
					uniquereads=uniquereads1;
					out.write(strLine + "\n");
				}
			}

			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(	seqCounter + " reads in this dataset.\n" + qfPassCounter + " reads have passed the quality filter.\n" + "Amount of time: " + elapsedTimeSec);


			BufferedWriter outfile1 = new BufferedWriter(new FileWriter(outputFile1+".catalogue"));

			// Printing the Combined Table
			Iterator it = uniquereads.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				ArrayList info=(ArrayList) pair.getValue();
				outfile1.write(pair.getKey().toString());
				for(int i=0; i<info.size(); i++){
					//System.out.println(pair.getKey() + " = " + pair.getValue());
					outfile1.write("\t"+info.get(i));
				}
				outfile1.write("\n");
				it.remove(); // avoids a ConcurrentModificationException
			}
			outfile1.close();



			fstream.close();
			outfile.close();
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
