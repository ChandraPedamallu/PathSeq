/*
	Created: Chandra Sekhar Pedamallu
	Usage: extract pairs
	Step 1: Get SAM file from UNC BAM using samtools view
	Step 2: RUn the following script
	java -classpath /xchip/pasteur/chandra/tools/tools_July2012/ExtractPairs4UNCdata extractPairs_UNCfile_Oct22012 readnames UNCSAM output
	Copyright@DFCI, BroadInst.
*/

import java.util.*;
import java.io.*;




public class extractPairs_UNCfile_July2014{

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
							readname = token.substring(0, token.lastIndexOf("_"));
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
				//System.out.println(readname);
				rnames.put(readname, vc);
				//System.out.println(readname);
			}
			fr1.close();

			System.out.println(rnames.size());
			//System.out.println("Hello"+start);


			BufferedWriter out=new BufferedWriter(new FileWriter(args[2]));
			String record=new String();
			String readname="";

			//SAM file
			FileReader fr2=new FileReader(args[1]);
			BufferedReader br2=new BufferedReader(fr2);
			String record2=new String();
			HashMap addedreadnames=new HashMap();

			while((record2=br2.readLine()) != null){
				String rec = record2.trim();

				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				while(stt.hasMoreElements()){
					String token=stt.nextToken();
					if(no==0){
						String ismp=token.trim();
						String oReadname=ismp;
						//System.out.println(ismp);
						int index_slash=ismp.lastIndexOf("/") ;

						if(index_slash > 0){
							String read_name=ismp.substring(0, index_slash);
							ismp=read_name;
						}

						//System.out.println(ismp);
						if(start.trim().length() > 0){
								readname=start+ismp;
						}
						else{
								readname=ismp;
						}

						if(rnames.containsKey(readname)){
							//System.out.println(oReadname+"\t"+addedreadnames);
							if(!(addedreadnames.containsKey(oReadname))){
								//System.out.println(rec);
								out.write(rec+"\n");


								addedreadnames.put(oReadname, "0");
							}
						}
					}
					no++;
				}
				//System.out.println((rnames.size()*2)+" "+addedreadnames.size());
				if((rnames.size()*2)==(addedreadnames.size())){
					System.out.println("Found all pairs");
					break;
				}

			}

			if((rnames.size()*2)!=(addedreadnames.size())){
				System.out.println("Something wrong with your BAM file...it doesn't contain all pairs");
			}


			out.close();
			fr2.close();
			rnames.clear();

			//System.out.println("HELLO");
		}catch(Exception e){System.out.println(e);}
	}
}
