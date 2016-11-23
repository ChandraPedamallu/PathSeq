/*
	Created: Chandra Sekhar Pedamallu
	Usage: extract pairs
	Input file: Database file in BAM file
	bsub -q hour -R "rusage[mem=5]" -o log.txt java -Xmx4048m -Xms4048m -classpath /xchip/pasteur/chandra/tools/pairend_contigs:sam-1.35.jar extractPairs_latest_BSAM /xchip/pasteur/chandra/MM-B030/stats/UnmappedAfterCompSub.fq1 /xchip/pasteur/chandra/MM-B030/MM-B030_pairend.bam test.fq1
	August 1, 2012Fixed the bug: which was not impacted our runs because of readnames

	Copyright@DFCI, BroadInst.
*/

import java.util.*;
import java.io.*;


import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class extractPairs_BAM{

	public static boolean isInteger( String input ){
		try{
			Integer.parseInt( input );
			return true;
		}
		catch( Exception e)
		{
			return false;
		}
	}

	public static void main(String args[]){

		try{

			//Input file READS
			FileReader fr1=new FileReader(args[0]);
			BufferedReader br1=new BufferedReader(fr1);
			String record1=new String();

			HashMap rnames = new HashMap();
			
			String start="";

			String firstline="Yes";
			int yes=0;

			while((record1=br1.readLine()) != null){
				String rec = record1.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				String readname="";
				Vector vc=new Vector();
				while(stt.hasMoreElements()){
					String token=stt.nextToken();
					if(no==0){

						StringTokenizer smp=new StringTokenizer(token, "_");
						String lasttoken="";
						while(smp.hasMoreElements()){
							lasttoken=smp.nextToken().toString();
						}
						if(isInteger(lasttoken)){ // After _ it is number
							readname = token; // Fixed this on August 1st 2012
							//readname = token.substring(0, token.lastIndexOf("_"));
							//System.out.println("first "+readname);
						}
						else{
							readname = token;
						}
						if(firstline.equals("Yes")){
							char[] read_tmp=readname.toCharArray();
							for(int ss=0; ss<readname.length(); ss++){
								if(read_tmp[ss]=='@'){
									start=start+"@";
								}
								else{
									break;
								}
							}
							firstline="No";
						}
					}
					no++;
				}
				rnames.put(readname, vc);
				if(yes==0){
					System.out.println(readname); yes=1;
					}
				//System.out.println(readname);
			}
			fr1.close();
			System.out.println(rnames.size());
			//System.out.println("Hello"+start);

		
			BufferedWriter out=new BufferedWriter(new FileWriter(args[2]));
			String record=new String();
			String readname="";

			//BAM file
			final File inputSamOrBamFile=new File(args[1]);
			final SAMFileReader inputSam = new SAMFileReader(inputSamOrBamFile);

			System.out.println("\nIterating through SAM/BAM file...");
			int n_pairs=0;
			int SAMreadCounter=0;
			yes=0;

			for(final SAMRecord samRecord : inputSam){
				//System.out.println(SAMreadCounter);
				SAMreadCounter++;
				String ismp=samRecord.getReadName().trim();
				String Seq=samRecord.getReadString().trim();
				String SeqB=samRecord.getBaseQualityString().trim();

				String sreadstmp=ismp+"\t"+Seq+"\t+\t"+SeqB;
				if(yes==0){
					System.out.println(ismp); yes=1;
					}

				if(start.trim().length() > 0){
						readname=start+ismp;
				}
				else{
						readname=ismp;
				}
				
				if(rnames.containsKey(readname)){
					Vector tmp=(Vector)rnames.get(readname);
					tmp.addElement(sreadstmp);
					rnames.put(readname, tmp);
					
					if(tmp.size() == 2){
						for(int sk=0; sk<tmp.size(); sk++){
							out.write(tmp.get(sk).toString()+"\n");
						}
						rnames.remove(readname);
					}
				}
				if(rnames.size()==0){
					break;
				}
			}
			
			if(rnames.size()>0){
				System.out.println("Something wrong with your BAM file...it doesn't contain all pairs");
			}


			out.close();
			inputSam.close();
			rnames.clear();
			
			//System.out.println("HELLO");
		}catch(Exception e){System.out.println(e);}
	}
}
