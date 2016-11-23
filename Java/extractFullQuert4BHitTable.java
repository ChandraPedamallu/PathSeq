/*
	Created: Chandra Sekhar Pedamallu
	Usage: extract unmapped reads
	Input file: Database file in Fasta

	Copyright@DFCI, BroadInst.
*/

import java.util.*;
import java.io.*;


public class extractFullQuert4BHitTable{


	public static void main(String args[]){

		try{
			//Read file
			FileReader fr=new FileReader(args[0]);
			BufferedReader br=new BufferedReader(fr);

			String pervious="NOTHING";
			Hashtable hashtable = new Hashtable();
			String record=new String();
			while((record=br.readLine()) != null){
				String rec = record.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				String seq="";
				String readname="";
				int no_tokens=stt.countTokens();
				while(stt.hasMoreElements()){
					String token=stt.nextToken();
					if(no==0){
						readname=token;
					}
					else if(no==1){
						seq=token;
					}
					no++;
				}
				hashtable.put(readname, seq);
			}


			fr.close();
			System.out.println(hashtable.size());

			//BlastHittable
			FileReader fr1=new FileReader(args[1]);
			BufferedReader br1=new BufferedReader(fr1);
			BufferedWriter out=new BufferedWriter(new FileWriter(args[2]));

			String record1=new String();
			while((record1=br1.readLine()) != null){
				String rec = record1.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				int found=0;
				Vector tmp=new Vector();
				while(stt.hasMoreElements()){
					String token=stt.nextToken().toString();
					// Check whether the reads names are the same.
					if(no==0){
						if( hashtable.containsKey(token)){
							out.write(rec);
							out.write("\t");
							out.write(hashtable.get(token).toString());
							out.write("\n");
						}
					}
					no++;
				}

			}
			fr1.close();
			out.close();
		}catch(Exception e){System.out.println(e);}
	}
}
