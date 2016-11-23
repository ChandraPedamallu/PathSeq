/*
	Created: Chandra Sekhar Pedamallu
	Usage: extract unmapped reads
	Input file: Database file in Fasta

	Copyright@DFCI, BroadInst.
*/

import java.util.*;
import java.io.*;


public class extractUnmapped_newlatest{


	public static void main(String args[]){

		try{
			//Blast Hit Table file
			FileReader fr=new FileReader(args[0]); // Hit table
			BufferedReader br=new BufferedReader(fr);

			double thres_eval=Double.parseDouble(args[1]); // Threshold value
			Hashtable hashtable = new Hashtable();
			String readname="";
			String record=new String();
			while((record=br.readLine()) != null){
				String rec = record.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				int f_bindex=0; int f_beindex=0;
				int no_tokens=stt.countTokens();
				while(stt.hasMoreElements()){
					String token=stt.nextToken();
					if(no==0){
						readname=token;
						f_bindex=0;
						f_beindex=0;
					}
					else if(no==2){
						int no_blast=Integer.parseInt(token.toString());
						if(no_blast==1){
							f_bindex=1;
						}
					}
					else if(no==8){
						double Eval=Double.parseDouble(token.toString());
						if((f_bindex==1) && (Eval <= thres_eval)){
							f_beindex=1;
						}
					}
					else if(no==(no_tokens-1)){
						if(f_beindex==1){
								hashtable.put(readname, "f");
						}
					}
					no++;
				}
			}

			fr.close();
			System.out.println(hashtable.size());

			FileReader fr1=new FileReader(args[2]);
			BufferedReader br1=new BufferedReader(fr1);
			BufferedWriter out=new BufferedWriter(new FileWriter(args[3])); // Unmapped
			BufferedWriter out1=new BufferedWriter(new FileWriter(args[4])); // Mapped

			String record1=new String();
			while((record1=br1.readLine()) != null){
				String rec = record1.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;

				while(stt.hasMoreElements()){
					String token=stt.nextToken().toString();
					// Check whether the reads names are the same.
					if(no==0){
						if( hashtable.containsKey(token)){
							out1.write(rec);
							out1.write("\n");
						}
						else{
							out.write(rec);
							out.write("\n");
						}
					}

					no++;
				}

			}
			fr1.close();
			out.close();
			out1.close();
		}catch(Exception e){System.out.println(e);}
	}
}
