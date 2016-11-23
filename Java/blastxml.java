import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
public class blastxml {



	public static void main(String[] args){
		try{

			BufferedReader in=new BufferedReader(new FileReader(args[0]));
			String line1="";
			String query="",querylen="";
			BufferedWriter outfile = new BufferedWriter(new FileWriter(args[1]));
			// ArrayList
			ArrayList<String> hitnum=new ArrayList();
			ArrayList<String> hitid=new ArrayList();
			ArrayList<String> hitdef=new ArrayList();
			ArrayList<String> hitacc=new ArrayList();
			ArrayList<String> hitlen=new ArrayList();
			ArrayList<String> bitsc=new ArrayList();
			ArrayList<String> evalue=new ArrayList();
			ArrayList<String> hsphitf=new ArrayList();
			ArrayList<String> hsphitt=new ArrayList();
			ArrayList<String> hspident=new ArrayList();
			ArrayList<String> hspalignlen=new ArrayList();
			ArrayList<String> identity=new ArrayList();
			ArrayList<String> hspqto=new ArrayList();
			ArrayList<String> hspqfr=new ArrayList();
			ArrayList<String> hspqseq=new ArrayList();
			ArrayList<String> qcover=new ArrayList();

			int hspidentity=0, collecths=0;
			int HSPidentity=0;
			while((line1=in.readLine())!=null)
	        {
				String line=line1.trim();
				//System.out.println(line.length()+"*********************************************"+line);
				if(line.length() > 0){
					StringTokenizer str=new StringTokenizer(line, ">");
					int n_counts=0;
					String start="";
					String end="";
					while(str.hasMoreTokens()){
						String token=str.nextToken().toString();
						if(n_counts==0){start=token;}
						else if(n_counts==1){
							StringTokenizer str1=new StringTokenizer(token, "<");
							int n_counts1=0;
							while(str1.hasMoreTokens()){
								String token1=str1.nextToken().toString();
								if(n_counts1==0){
									end=token1;
								}
								n_counts1++;
							}
						}
						n_counts++;
					}
					//System.out.println(start+" "+end);
					// Iterations
					if(start.equalsIgnoreCase("<Iteration")){
						if(hitnum.size() > 0){
							//System.out.println(hitnum.size());
							//System.out.println(hspqfr.size()+" "+hspqto.size());
							for(int i=0; i< hitnum.size(); i++){
								outfile.write(query+"\t"+querylen+"\t");
								outfile.write(hitnum.get(i).toString()+"\t"+hitid.get(i).toString()+"\t");
								outfile.write(hitdef.get(i).toString()+"\t"+hitacc.get(i).toString()+"\t");
								outfile.write(hitlen.get(i).toString()+"\t"+bitsc.get(i).toString()+"\t");
								outfile.write(evalue.get(i).toString()+"\t"+hsphitf.get(i).toString()+"\t");

								outfile.write(hsphitt.get(i).toString()+"\t");
								if(hspident.size()>0){
									outfile.write(hspident.get(i).toString()+"\t");
								}
								else{
									outfile.write("NA"+"\t");
								}
								outfile.write(hspalignlen.get(i).toString()+"\t"+identity.get(i).toString()+"\t");
								outfile.write(qcover.get(i).toString()+"\t");
								outfile.write(hspqfr.get(i).toString()+"\t");
								outfile.write(hspqto.get(i).toString()+"\t"+hspqseq.get(i).toString()+"\n");
							}

						}
						query="";querylen="";
						hitnum.clear();hitid.clear();hitdef.clear();hitacc.clear();hitlen.clear();bitsc.clear();
						evalue.clear();hsphitf.clear();hsphitt.clear();hspident.clear();hspalignlen.clear();identity.clear();
						hspqto.clear();hspqfr.clear();hspqseq.clear();qcover.clear();
						hspidentity=0;
						collecths=0;
					}
					else if(start.equalsIgnoreCase("<Iteration_query-def")){
						
						query=end;
					}
					else if(start.equalsIgnoreCase("<Iteration_query-len")){
						querylen=end;
					}
					else if(start.equalsIgnoreCase("<Hit_num")){
						hitnum.add(end);
						hspidentity=0;
					}
					else if(start.equalsIgnoreCase("<Hit_id")){
						hitid.add(end);
					}
					else if(start.equalsIgnoreCase("<Hit_def")){
						//This is extract subject name of the hit from gi|number| <Name of speicies> -START						
						StringTokenizer tokensHitname=new StringTokenizer(end, " ");
						int name_Hit=0;
						String nameHit1="";

						// Name of the Hit in the Hit table
						while(tokensHitname.hasMoreTokens()){
							String nameHittmp=tokensHitname.nextToken().trim();
							if(name_Hit==0){
								if(!(nameHittmp.contains("|"))){
									nameHit1=nameHittmp;
								}
							}
							else{
								nameHit1=nameHit1+" "+nameHittmp;
							}

							name_Hit++;
						}
						String nameHit2=nameHit1.trim();
						
						hitdef.add(nameHit2);
					}
					else if(start.equalsIgnoreCase("<Hit_accession")){
						hitacc.add(end);
					}
					else if(start.equalsIgnoreCase("<Hit_len")){
						hitlen.add(end);
					}
					else if(start.equalsIgnoreCase("<Hsp_num")){
						if(end.equals("1")){
							collecths=1;
						}
						else{
							collecths=0;
						}
					}

					if(collecths==1){
						if(start.equalsIgnoreCase("<Hsp_bit-score")){
							bitsc.add(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_evalue")){
							evalue.add(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_hit-from")){
							hsphitf.add(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_hit-to")){
							hsphitt.add(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_identity")){ // HSP identity is section that is optional so we should check wheather availble
							hspident.add(end);
							hspidentity=1;
							HSPidentity=Integer.parseInt(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_align-len")){ // HSP identity is section that is optional so we should check wheather availble
							hspalignlen.add(end);
							int Hspalign=Integer.parseInt(end);
							double identcover=HSPidentity/(Hspalign+0.0);
							identity.add(identcover+"");

							int qlength=Integer.parseInt(querylen);

							int qtlength=Integer.parseInt(hspqto.get(hspqto.size()-1));
							int qflength=Integer.parseInt(hspqfr.get(hspqfr.size()-1));

							double lengthq=0.0;
							if(qflength > qtlength){lengthq=qflength-qtlength+1;}
							else{lengthq=qtlength-qflength+1;}

							double qcover1=(lengthq+0.0)/(qlength); //new query coverage
							qcover.add(qcover1+"");

							if(hspidentity==0){
								hspident.add("NA");
							}

						}
						else if(start.equalsIgnoreCase("<Hsp_query-to")){
							hspqto.add(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_query-from")){
							hspqfr.add(end);
						}
						else if(start.equalsIgnoreCase("<Hsp_qseq")){
							hspqseq.add(end);

						}
					}

				}
	        }

			
			if(hitnum.size() > 0){
				for(int i=0; i< hitnum.size(); i++){
					outfile.write(query+"\t"+querylen+"\t");
					outfile.write(hitnum.get(i).toString()+"\t"+hitid.get(i).toString()+"\t");
					outfile.write(hitdef.get(i).toString()+"\t"+hitacc.get(i).toString()+"\t");
					outfile.write(hitlen.get(i).toString()+"\t"+bitsc.get(i).toString()+"\t");
					outfile.write(evalue.get(i).toString()+"\t"+hsphitf.get(i).toString()+"\t");

					outfile.write(hsphitt.get(i).toString()+"\t");
					if(hspident.size()>0){
						outfile.write(hspident.get(i).toString()+"\t");
					}
					else{
						outfile.write("NA"+"\t");
					}
					outfile.write(hspalignlen.get(i).toString()+"\t"+identity.get(i).toString()+"\t");
					outfile.write(qcover.get(i).toString()+"\t");
					outfile.write(hspqfr.get(i).toString()+"\t");
					outfile.write(hspqto.get(i).toString()+"\t"+hspqseq.get(i).toString()+"\n");
				}

			}
			query="";querylen="";
			hitnum.clear();hitid.clear();hitdef.clear();hitacc.clear();hitlen.clear();bitsc.clear();
			evalue.clear();hsphitf.clear();hsphitt.clear();hspident.clear();hspalignlen.clear();identity.clear();
			hspqto.clear();hspqfr.clear();hspqseq.clear();qcover.clear();
			hspidentity=0;
			collecths=0;

			in.close();
			outfile.close();



		}catch (Exception e){//Catch exception if any
			System.out.println(e);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}


}
