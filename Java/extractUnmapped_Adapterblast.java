/*
	Created: Chandra Sekhar Pedamallu
	Usage: extract unmapped reads
	Extract the reads that are unmapped from Adapter blast

	Copyright@DFCI, BroadInst.
*/

import java.util.*;
import java.io.*;


public class extractUnmapped_Adapterblast{


	public static void main(String args[]){

		try{
			//Blast Hit Table file
			FileReader fr=new FileReader(args[0]);
			BufferedReader br=new BufferedReader(fr);

			double thres_identity=Double.parseDouble(args[1]); // identity
			double thres_qcover=Double.parseDouble(args[2]); // identity


			Hashtable hashtable = new Hashtable();
			String readname="";

			String record=new String();
			while((record=br.readLine()) != null){
				String rec = record.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				int f_bindex=0; int f_beindex=0;

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
					else if(no==13){
						double identity=Double.parseDouble(token.toString());
						//System.out.print(identity+"\t");
						if((f_bindex==1) && (identity >= thres_identity)){
							f_beindex=1;
							//System.out.println(readname+" "+f_bindex+" "+Eval);
						}
					}
					else if(no==14){
						double qcover=Double.parseDouble(token.toString());
						//System.out.print(qcover+"\n");
						if((f_beindex==1) && (qcover >= thres_qcover)){
								hashtable.put(readname, "f");
						}
					}
					no++;
				}
			}


			fr.close();
			System.out.println(hashtable.size());

			FileReader fr1=new FileReader(args[3]);
			BufferedReader br1=new BufferedReader(fr1);
			BufferedWriter out=new BufferedWriter(new FileWriter(args[4])); // Unmapped reads
			BufferedWriter out1=new BufferedWriter(new FileWriter(args[5])); // Mapped reads

			String record1=new String();
			while((record1=br1.readLine()) != null){
				String rec = record1.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;

				//Vector tmp=new Vector();
				while(stt.hasMoreElements()){
					String token=stt.nextToken().toString();
					// Check whether the reads names are the same.
					if(no==0){
						if( hashtable.containsKey(token)){
							out1.write(rec+"\n");
							//tmp=(Vector)hashtable.get(token);
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
