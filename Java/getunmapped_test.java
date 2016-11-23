/*
	Created: Chandra Sekhar Pedamallu
	Usage: extract pairs
	Input file: Database file in BAM file
	bsub -q hour -R "rusage[mem=5]" -o log.txt java -Xmx4048m -Xms4048m -classpath /xchip/pasteur/chandra/tools/pairend_contigs:sam-1.35.jar extractPairs_latest_BSAM /xchip/pasteur/chandra/MM-B030/stats/UnmappedAfterCompSub.fq1 /xchip/pasteur/chandra/MM-B030/MM-B030_pairend.bam test.fq1
	Copyright@DFCI, BroadInst.
*/

import java.util.*;
import java.io.*;


import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMFileReader.ValidationStringency;

public class getunmapped_test{

	

	public static void main(String args[]){

		try{
			
	
			
			//Enum SAMFileReader.ValidationStringency=LENIENT ;

			//BAM file
			final File inputSamOrBamFile=new File(args[0]);
			final SAMFileReader inputSam = new SAMFileReader(inputSamOrBamFile);
			inputSam.setValidationStringency(ValidationStringency.LENIENT);


			BufferedWriter out=new BufferedWriter(new FileWriter(args[1]));
			BufferedWriter cat1=new BufferedWriter(new FileWriter("Total_ReadsinBAMfile.txt"));
			BufferedWriter cat=new BufferedWriter(new FileWriter("Total_Unmappedreads.txt"));
			String record=new String();
			String readname="";

			
			
			System.out.println("\nIterating through SAM/BAM file...");
			int n_pairs=0;
			int SAMreadCounter=0;
			int number_unmappedreads=0;

			for(final SAMRecord samRecord : inputSam){
				//System.out.println(SAMreadCounter);
				SAMreadCounter++;
				String ismp=samRecord.getReadName().trim();
				
				String Seq=samRecord.getReadString().trim();
				String SeqB=samRecord.getBaseQualityString().trim();
				if(samRecord.getReadUnmappedFlag()){
					String sreadstmp=ismp+"\t"+Seq+"\t+\t"+SeqB;
					out.write("@"+ismp);
					out.write("\t");
					out.write(Seq);
					out.write("\t");
					out.write("+");
					out.write("\t");
					out.write(SeqB);
					
					//out.write(sreadstmp);
					out.write("\n");
					number_unmappedreads++;
				}
				

			}
			

			out.close();
			inputSam.close();
			cat.write(number_unmappedreads+"\n");
			cat.close();
			cat1.write(SAMreadCounter+"\n");
			cat1.close();

			
			//System.out.println("HELLO");
		}catch(Exception e){System.out.println(e);}
	}
}
