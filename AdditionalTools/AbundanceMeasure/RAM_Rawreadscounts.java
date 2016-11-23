/*
 * Author: Chandra Sekhar Pedamallu
 *
 * Date: October, 12, 2016
 * Usage:
 * Raw read counts in RNASEQ with no normalization
 * Metric = (Number of reads mapped to a microbe 1 / Number of Human reads or library size) * 10^6
 */



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.*;
import java.util.regex.*;

public class RAM_Rawreadscounts {

	private static Map<Integer,Integer> id2parent=new HashMap<Integer,Integer>();
	/** get all ancestor of a given taxon id*/
    private List<Integer> lineage(int id){
        if(!id2parent.containsKey(id)) throw new IllegalArgumentException();
        LinkedList<Integer> L=new LinkedList<Integer>();
        for(;;)
            {
            L.addFirst(id);
            Integer parent=id2parent.get(id);
            if(parent==null || parent.equals(id)) break;
            id=parent;
            }
        return L;
     }
    // Hashmap sorted by Value
    public static List sortByValue(final Map m) {
    	List keys = new ArrayList();
	    keys.addAll(m.keySet());
	    Collections.sort(keys, new Comparator() {
        public int compare(Object o1, Object o2) {
            Object v1 = m.get(o1);
            Object v2 = m.get(o2);
            if (v1 == null) {
                return (v2 == null) ? 0 : 1;
            }
            else if (v1 instanceof Comparable) {
                return ((Comparable) v1).compareTo(v2);
            }
            else {
                return 0;
            }
        }
    });
	    Collections.reverse(keys);
	    return keys;
	}

	public static String findkingdom(String namesubject, HashMap txnames){
		// Tax Names
		ArrayList<String> tnames=(ArrayList) txnames.get(namesubject);

		// Tax ID
		String idtax=tnames.get(0);
		//System.out.println(me.getKey().toString()+"\t"+samplereads+"\t"+idtax);

		Integer id=new Integer(idtax); // Taxid

		// Acesstary of the unique tax id
		RAM_Rawreadscounts tmpobj=new RAM_Rawreadscounts();
		List<Integer> L1= tmpobj.lineage(id);
		String king="UNKNOWN";
		for(int s=0; s<L1.size(); s++){ // Run through the List to find the kingdom information
			String taxnum=(L1.get(s).toString());
			if(taxnum.equals("2157")){ //Archaea
				king="ARCHAEA";
			}
			else if(taxnum.equals("2")){ // Bacteria
				king="BACTERIA";
			}
			else if(taxnum.equals("4751")){ // Fungi
				king="FUNGI";
			}
			else if(taxnum.equals("33208")){ // Metazoa
				king="METAZOA";
			}
			else if(taxnum.equals("33090")){ // Viridiplantae
				king="PLANT";
			}
			else if(taxnum.equals("10239")){ // viruses
				king="VIRUSES";
			}
		}
		return king;
	}

	// Find the subject name
	public static String findsubjectname(String tokens1, HashMap txnames){
		String tokens2=tokens1.replace("&APOS;","'");
		String tokens3=tokens2.replace("&apos;","'");
		String tokens=tokens3.replace("\"","");
		//System.out.println(tokens);
		String subject="NULL";
		int subjectfound=-1;
		StringTokenizer tokensHitname=new StringTokenizer(tokens, " ");
		int name_Hit=0;
		String nameHit1="";
		String shortname1="";
		int number_parts=0; // Number of subparts from the genome name.. Take 4 parts of the sequences
		int found=0;

		// Extract the subject name from the Hit table
		while(tokensHitname.hasMoreTokens()){
			String nameHittmp=tokensHitname.nextToken().toUpperCase().trim();
			if(name_Hit==0){
				if(!(nameHittmp.contains("|"))){
					nameHit1=nameHittmp;
					number_parts++;
				}
			}
			else{
				nameHit1=nameHit1+" "+nameHittmp;
			}
			name_Hit++;
		}

		String nameHit2=nameHit1.trim().toUpperCase();


		// Remove all "," in the subject
		tokensHitname=new StringTokenizer(nameHit2, ",");
		name_Hit=0;
		while(tokensHitname.hasMoreTokens()){
			String nameHittmp=tokensHitname.nextToken().toUpperCase().trim();
			if(name_Hit==0){
				nameHit1=nameHittmp;
			}
			name_Hit++;
		}
		String nameHit=nameHit1.trim().toUpperCase();

		//Generate all possible subjects
		StringTokenizer hitCompare=new StringTokenizer(nameHit, " ");
		int cc=0;
		ArrayList<String> possiblesubjects=new ArrayList<String>();
		String qtmp="";
		int check=0;

		// Look for all possible queies in the hit
		while(hitCompare.hasMoreTokens()){
			String tokens11=hitCompare.nextToken();
			if(cc==0)
				qtmp=tokens11;
			else
				qtmp=qtmp+"$"+tokens11;

			possiblesubjects.add(qtmp);
			cc++;
		}

		//Look for the longest subject found in taxnames
		String subjectname="";
		for(int ss=0; ss<possiblesubjects.size(); ss++){
			String subject_name=possiblesubjects.get(ss).toString();
			if(txnames.containsKey(subject_name)){
				subjectfound=ss;
				subjectname=subject_name;
			}
		}
		subject=subjectname;

		if(subjectfound==-1){
			//System.out.println(tokens);
			return "NULL";
		}
		else{
			return subject;
		}
	}


	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		String outputFile=   args[0];  //output file

		String taxnames=   args[1];  //taxnames file
		String taxnodes=   args[2];  //taxnodes file
		String filename=   args[3];  //taxnodes file
		//String taxnames=   "/xchip/pandora/taxonomy/names.dmp";  //taxnames file
		//String taxnodes=   "/xchip/pandora/taxonomy/nodes.dmp";  //taxnodes file

		double identityRatioCutOff= 0.9;
		double queryCoverageRatioCutOff= 0.9;


		long start = System.currentTimeMillis();
		int lineCount= 0;

		try{
			// Read taxnodes
			Pattern pipe=Pattern.compile("[\\|]");
	    	BufferedReader in=new BufferedReader(new FileReader(taxnodes));
	    	String line;
	    	HashMap<String, String> id2taxnam=new HashMap<String, String>();
	    	while((line=in.readLine())!=null)
	        {
	    		String tokens[]=pipe.split(line,4);
	    		Integer tax_id=Integer.parseInt(tokens[0].trim());
	    		Integer parent_id=Integer.parseInt(tokens[1].trim());
	    		String tax_info=tokens[2].trim();
	    		//System.out.println(tax_info);
	    		id2parent.put(tax_id,parent_id);

	    		id2taxnam.put(tokens[0].trim(),tax_info);
	        }
	    	in.close();


			HashMap txnames=new HashMap(); // Taxonomy names ---- <Key: Tax name> <Value ID>
			HashMap scnames=new HashMap(); // Scientific names----<Key: Tax id> <Taxname>

			// Tax names
			FileReader fr1=new FileReader(taxnames);
			BufferedReader br1=new BufferedReader(fr1);
			String strLine1;

			// The Hashmap contains <taxname> <taxid>
			while ((strLine1 = br1.readLine()) != null)   {
				StringTokenizer strtmp=new StringTokenizer(strLine1, "|");
				int ctokens=0;
				String taxid="";	String taxname="";		String taxname1="";		String cspecies_sci="";
				String taxnamenew="";	String taxname1new="";		String cspecies_scinew="";

				while(strtmp.hasMoreTokens()){
					String strtoken=strtmp.nextToken().toString().trim();
					if(ctokens==0){
						taxid=(strtoken);
					}
					else if (ctokens==1){
						taxname=strtoken;
						taxnamenew=taxname.replace(" ", "$");
					}
					else if (ctokens==2){
						taxname1=strtoken;
						taxname1new=taxname1.replace(" ", "$");
					}
					else if (ctokens==3){
						if(strtoken.equalsIgnoreCase("scientific name")){
							cspecies_sci=taxname;
							cspecies_scinew=cspecies_sci.replace(" ", "$");
							ArrayList tx_name=new ArrayList();
							tx_name.add(cspecies_scinew.toUpperCase());
							tx_name.add("0");
							//System.out.println(taxid+" "+tx_name);
							scnames.put(taxid, tx_name);
						}
					}
					ctokens++;
				}
				ArrayList<String> tmp=new ArrayList<String>();
				tmp.add(taxid);
				tmp.add("0");

				if(taxname1.length()>0){
					txnames.put(taxname1new.toUpperCase(),tmp);
				}
				else{
					txnames.put(taxnamenew.toUpperCase(),tmp);
				}
				if((cspecies_sci.length()>0) && (taxname1.length()>0)){
					txnames.put(cspecies_scinew.toUpperCase(),tmp);
				}
			}
			fr1.close();

/*****************************************/
			// Reading Hittables
			HashMap<String, ArrayList> taxhittable=new HashMap<String, ArrayList>();
			HashMap Unknownspec=new HashMap();
			HashMap<String, ArrayList> kingdomlevel=new HashMap<String, ArrayList>();


			// Create framework of Kingdom
			ArrayList kingdomframe=new ArrayList();
			kingdomframe.add("ARCHAEA");
			kingdomframe.add("BACTERIA");
			kingdomframe.add("FUNGI");
			kingdomframe.add("METAZOA");
			kingdomframe.add("PLANT");
			kingdomframe.add("VIRUSES");
			kingdomframe.add("UNKNOWN");


			// Create framework of samples and initialize the list with zero's
			HashMap samplenames=new HashMap();
			ArrayList samplenames_list=new ArrayList();

			HashMap sampleid_humanreads=new HashMap();

			FileReader fr3=new FileReader(filename);
			BufferedReader br3=new BufferedReader(fr3);
			String strLine3="";
			int filecounter=0;
			//for(int i=1; i<args.length; i++){
			while ((strLine3 = br3.readLine()) != null)   {

				StringTokenizer strtoken2=new StringTokenizer(strLine3, "\t");

				int counts=0;
				String inputfileloc="", samplename="";
				int no_humanreads=0;
				while(strtoken2.hasMoreTokens()){
					String tokens=strtoken2.nextToken().toString();
					if(counts==0){
						samplename=tokens;
						samplenames_list.add(tokens);
						samplenames.put(samplename,filecounter+"");
					}
					else if(counts==1){
						inputfileloc=tokens;
					}
					else if(counts==2){
						no_humanreads=Integer.parseInt(tokens);
						sampleid_humanreads.put(filecounter+"",tokens);
					}
					counts++;
				}
				//System.out.println(filecounter);
				System.out.println(samplenames);
				//System.out.println(sampleid_humanreads);
				filecounter++;

			}
			fr3.close();

			// Tax names
			FileReader fr2=new FileReader(filename);
			BufferedReader br2=new BufferedReader(fr2);
			String strLine2="";

			//for(int i=1; i<args.length; i++){
			while ((strLine2 = br2.readLine()) != null)   {

				StringTokenizer strtoken2=new StringTokenizer(strLine2, "\t");

				int counts=0;
				String inputfileloc="", samplename="";
				int no_humanreads=0;
				while(strtoken2.hasMoreTokens()){
					String tokens=strtoken2.nextToken().toString();
					if(counts==0){
						samplename=tokens;
					}
					else if(counts==1){
						inputfileloc=tokens;
					}
					else if(counts==2){
						no_humanreads=Integer.parseInt(tokens);
					}
					counts++;
				}
				//System.out.println(i+" "+args[i]);
				double count_archaea=0.0, count_bacteria=0.0, count_eukaryota=0.0, count_fungi=0.0, count_metazoa=0.0, count_viridiplantae=0.0, count_virus=0.0, count_unknown=0.0;

				int no_allreads=0, no_uniqreads=0;

				// Hittables
				FileReader fr=new FileReader(inputfileloc);
				BufferedReader br=new BufferedReader(fr);
				String strLine="";

				ArrayList<String> tmpsubject=new ArrayList<String>();

				String readname="";
				int hitnumber=-9999;
				double evalue=0.0;
				double identity=0.0;
				double qcoverage=0.0;
				String subject="";


				String preadname="NULL";
				double pevalue=0.0;
				double pidentity=0.0;
				double pqcoverage=0.0;
				int phitnumber=0;

				int addrecord2RAM=0; //Don't add to RAM
				int line_first=0;

				//Read Hittable
				while ((strLine = br.readLine()) != null){
					if(line_first > 0){
						//System.out.println(strLine);
						StringTokenizer strtoken=new StringTokenizer(strLine, "\t");
						int no_token=0;
						while(strtoken.hasMoreTokens()){
							String token=strtoken.nextToken().toString();
							if(no_token ==0){ // Name of read
								readname=token;
							}
							else if(no_token ==2){ // hit numbers
								hitnumber=Integer.parseInt(token);
							}
							else if(no_token ==4){ // subject
								subject=token;
							}
							else if(no_token ==8){ // E-value
								evalue=Double.parseDouble(token);
							}
							else if(no_token ==13){ // Percent identity
								identity=Double.parseDouble(token);
							}
							else if(no_token ==14){ // Query coverage
								qcoverage=Double.parseDouble(token);
							}
							no_token++;
						}
						//System.out.println(preadname+" "+readname+" "+identity+" "+qcoverage+" "+pidentity+" "+pqcoverage);

						if(identity >= identityRatioCutOff && qcoverage >= queryCoverageRatioCutOff){ // Select the record that pass this cutoff
							//System.out.println(strLine);
							if(preadname.equalsIgnoreCase("NULL")){
								//System.out.println(strLine+" "+identity+" "+qcoverage+" "+subject);
								String subject_name1=findsubjectname(subject, txnames);

								if(!(subject_name1.equals("NULL"))){
									//System.out.print(subject+"\t"+subject_name1+"\t");

									// Tax Names
									ArrayList<String> tnames=(ArrayList) txnames.get(subject_name1);

									// Tax ID
									String idtax=tnames.get(0);

									// Scientific names
									String subject_name="";
									if(scnames.containsKey(idtax)){
										ArrayList<String> tmp2=(ArrayList) scnames.get(idtax);
										subject_name=tmp2.get(0).toString();
									}
									//System.out.println(idtax+" "+subject_name);

									//System.out.println(strLine+" "+identity+" "+qcoverage+" "+subject_name);
									preadname=readname; pevalue=evalue; pidentity=identity; pqcoverage=qcoverage;
									phitnumber=hitnumber;

									if(subject_name.equalsIgnoreCase("NULL")){
										//System.out.println(strLine+" "+subject);
										Unknownspec.put(subject,"0");
									}
									else{
										tmpsubject.add(subject_name);
									}
								}
								else{
									System.out.println(subject+" : NOT FOUND");
								}

							}
							else{
								if(preadname.equalsIgnoreCase(readname)){
									if(identity >= pidentity && qcoverage >=pqcoverage){
										String subject_name1=findsubjectname(subject, txnames);
										if(!(subject_name1.equals("NULL"))){
											//System.out.print(subject+"\t"+subject_name1+"\t");

											// Tax Names
											ArrayList<String> tnames=(ArrayList) txnames.get(subject_name1);

											// Tax ID
											String idtax=tnames.get(0);

											// Scientific names
											String subject_name="";
											if(scnames.containsKey(idtax)){
												ArrayList<String> tmp2=(ArrayList) scnames.get(idtax);
												subject_name=tmp2.get(0).toString();
											}
											//System.out.println(idtax+" "+subject_name);
											//System.out.println(strLine+" "+identity+" "+qcoverage+" "+subject_name);

											preadname=readname; pevalue=evalue; pidentity=identity; pqcoverage=qcoverage;
											phitnumber=hitnumber;
											if(subject_name.equalsIgnoreCase("NULL")){
												//System.out.println(strLine+" "+subject);
												Unknownspec.put(subject,"0");
											}
											else{
												tmpsubject.add(subject_name);
											}
										}
										else{
											System.out.println(subject+" : NOT FOUND");
										}

									}
								}
								else{
									//addrecord2RAM=1;
									if(tmpsubject.size() > 0){

										if(tmpsubject.size()==1){
											no_uniqreads++;
											no_allreads++;
										}
										else{
											no_allreads++;
										}

										int scount4read=tmpsubject.size();
										double correctedreadcount=(1/(scount4read+0.0));

										for(int ks=0; ks<tmpsubject.size(); ks++){
											String name_s=tmpsubject.get(ks); // subject name
											String kingdom_name=findkingdom(name_s, txnames);
											//System.out.println(name_s+"-------"+kingdom_name);
											if(kingdom_name.equals("ARCHAEA")){
												count_archaea=count_archaea + correctedreadcount;
											}
											else if(kingdom_name.equals("BACTERIA")){
												count_bacteria=count_bacteria + correctedreadcount;
											}
											else if(kingdom_name.equals("FUNGI")){
												count_fungi=count_fungi + correctedreadcount;
											}
											else if(kingdom_name.equals("METAZOA")){
												count_metazoa=count_metazoa + correctedreadcount;
											}
											else if(kingdom_name.equals("PLANT")){
												count_viridiplantae=count_viridiplantae + correctedreadcount;
											}
											else if(kingdom_name.equals("VIRUSES")){
												count_virus=count_virus + correctedreadcount;
											}
											else if(kingdom_name.equals("UNKNOWN")){
												count_unknown=count_unknown + correctedreadcount;
											}
											//System.out.println(name_s+"\t"+correctedreadcount);
											if(taxhittable.containsKey(name_s)){
												ArrayList<Double> listsamples = (ArrayList<Double>)taxhittable.get(name_s);

												if(samplenames.containsKey(samplename)){
													int sampleindex=Integer.parseInt(samplenames.get(samplename).toString());
													double oldcount=Double.parseDouble(listsamples.get(sampleindex).toString());
													double newcount=oldcount+correctedreadcount;

													listsamples.set(sampleindex, newcount);
													taxhittable.put(name_s, listsamples);
												}
												else{
													System.out.println("SAMPLE NAME IS MISSING : "+name_s);
												}
											}
											else{
												ArrayList<Double> listsamples = new ArrayList<Double>(Collections.nCopies(samplenames.size(), 0.0)); // intialize

												if(samplenames.containsKey(samplename)){
													int sampleindex=Integer.parseInt(samplenames.get(samplename).toString());
													listsamples.set(sampleindex, correctedreadcount);
													taxhittable.put(name_s, listsamples);
												}
												else{
													System.out.println("SAMPLE NAME IS MISSING : "+name_s);
												}
											}
										}
									}
									addrecord2RAM=0;
									tmpsubject.clear();

									String subject_name1=findsubjectname(subject, txnames);
									//System.out.print(subject+"\t"+subject_name1+"\t");
									if(!(subject_name1.equals("NULL"))){
										// Tax Names
										ArrayList<String> tnames=(ArrayList) txnames.get(subject_name1);

										// Tax ID
										String idtax=tnames.get(0);

										// Scientific names
										String subject_name="";
										if(scnames.containsKey(idtax)){
											ArrayList<String> tmp2=(ArrayList) scnames.get(idtax);
											subject_name=tmp2.get(0).toString();
										}
										//System.out.println(idtax+" "+subject_name);
										//System.out.println(strLine+" "+identity+" "+qcoverage+" "+subject_name);
										//System.out.println(strLine+" "+identity+" "+qcoverage+" "+subject_name);
										preadname=readname; pevalue=evalue; pidentity=identity; pqcoverage=qcoverage;
										phitnumber=hitnumber;
										if(subject_name.equalsIgnoreCase("NULL")){
											Unknownspec.put(subject,"0");
										}
										else{
											tmpsubject.add(subject_name);
										}
									}
									else{
										System.out.println(subject+" : NOT FOUND");
									}
								}
							}
						}
					}
					line_first++;
				}


				//Add a record to the RAM results

				if(tmpsubject.size() > 0){
					int scount4read=tmpsubject.size();
					double correctedreadcount=(1/(scount4read+0.0));

					for(int ks=0; ks<tmpsubject.size(); ks++){
						String name_s=tmpsubject.get(ks); // subject name
						String kingdom_name=findkingdom(name_s, txnames);
						//System.out.print(name_s+" "+kingdom_name);
						if(kingdom_name.equals("ARCHAEA")){
							count_archaea=count_archaea + correctedreadcount;
						}
						else if(kingdom_name.equals("BACTERIA")){
							count_bacteria=count_bacteria + correctedreadcount;
						}
						else if(kingdom_name.equals("FUNGI")){
							count_fungi=count_fungi + correctedreadcount;
						}
						else if(kingdom_name.equals("METAZOA")){
							count_metazoa=count_metazoa + correctedreadcount;
						}
						else if(kingdom_name.equals("PLANT")){
							count_viridiplantae=count_viridiplantae + correctedreadcount;
						}
						else if(kingdom_name.equals("VIRUSES")){
							count_virus=count_virus + correctedreadcount;
						}
						else if(kingdom_name.equals("UNKNOWN")){
							count_unknown=count_unknown + correctedreadcount;
						}
						if(taxhittable.containsKey(name_s)){
							ArrayList<Double> listsamples = (ArrayList<Double>)taxhittable.get(name_s);

							if(samplenames.containsKey(samplename)){
								int sampleindex=Integer.parseInt(samplenames.get(samplename).toString());
								double oldcount=Double.parseDouble(listsamples.get(sampleindex).toString());
								double newcount=oldcount+correctedreadcount;

								listsamples.set(sampleindex, newcount);
								taxhittable.put(name_s, listsamples);
							}
							else{
								System.out.println("SAMPLE NAME IS MISSING : "+name_s);
							}
						}
						else{
							ArrayList<Double> listsamples = new ArrayList<Double>(Collections.nCopies(samplenames.size(), 0.0)); // intialize

							if(samplenames.containsKey(samplename)){
								int sampleindex=Integer.parseInt(samplenames.get(samplename).toString());
								listsamples.set(sampleindex, correctedreadcount);
								taxhittable.put(name_s, listsamples);
							}
							else{
								System.out.println("SAMPLE NAME IS MISSING : "+name_s);
							}
						}
					}
				}
				tmpsubject.clear();

				// Kingdome level -- for all samples
				for(int kking=0; kking<kingdomframe.size();kking++){

					if(kingdomlevel.containsKey(kingdomframe.get(kking).toString())){
						ArrayList<Double> listsamples = (ArrayList<Double>)kingdomlevel.get(kingdomframe.get(kking).toString());

						if(samplenames.containsKey(samplename)){
							int sampleindex=Integer.parseInt(samplenames.get(samplename).toString());
							double oldcount=Double.parseDouble(listsamples.get(sampleindex).toString());
							double newcount=0.0;


							if(kking==0){
								newcount=oldcount+count_archaea;
							}
							else if(kking==1){
								newcount=oldcount+count_bacteria;
							}
							else if(kking==2){
								newcount=oldcount+count_fungi;
							}
							else if(kking==3){
								newcount=oldcount+count_metazoa;
							}
							else if(kking==4){
								newcount=oldcount+count_viridiplantae;
							}
							else if(kking==5){
								newcount=oldcount+count_virus;
							}
							else if(kking==6){
								newcount=oldcount+count_unknown;
							}
							listsamples.set(sampleindex, newcount);
							//System.out.println(kingdomframe.get(kking).toString()+" "+listsamples);

							kingdomlevel.put(kingdomframe.get(kking).toString(), listsamples);
						}
						else{
							System.out.println("SAMPLE NAME IS MISSING : ");
						}
					}
					else{
						ArrayList<Double> listsamples = new ArrayList<Double>(Collections.nCopies(samplenames.size(), 0.0)); // intialize

						if(samplenames.containsKey(samplename)){

							int sampleindex=Integer.parseInt(samplenames.get(samplename).toString());

							if(kking==0){
								listsamples.set(sampleindex, count_archaea);
							}
							else if(kking==1){
								listsamples.set(sampleindex, count_bacteria);
							}
							else if(kking==2){
								listsamples.set(sampleindex, count_fungi);
							}
							else if(kking==3){
								listsamples.set(sampleindex, count_metazoa);
							}
							else if(kking==4){
								listsamples.set(sampleindex, count_viridiplantae);
							}
							else if(kking==5){
								listsamples.set(sampleindex, count_virus);
							}
							else if(kking==6){
								listsamples.set(sampleindex, count_unknown);
							}
							//System.out.println(kingdomframe.get(kking).toString()+" "+listsamples);
							kingdomlevel.put(kingdomframe.get(kking).toString(), listsamples);
						}
						else{
							System.out.println("SAMPLE NAME IS MISSING : ");
						}
					}
				}



				fr.close();
			}
			System.out.println(taxhittable.size());
			fr2.close();

		    BufferedWriter kingdomelevelinfo = new BufferedWriter(new FileWriter(outputFile+".kingdom"));
		    kingdomelevelinfo.write("Kingdom\t");
			for(int i=0; i<samplenames_list.size(); i++){
				kingdomelevelinfo.write(samplenames_list.get(i)+"\t");
			}
			kingdomelevelinfo.write("\n");


			Set set33 = kingdomlevel.entrySet();
		    Iterator kk33 = set33.iterator();

		   // Kingdome level readcount --- Bacteria, Fungi, Archaeae, ...
		    while(kk33.hasNext()){
		    	Map.Entry me = (Map.Entry)kk33.next();
		    	//System.out.println(me.getKey().toString()+" "+me.getValue());

		    	kingdomelevelinfo.write(me.getKey().toString()+"\t");
		    	ArrayList kingdomelevelreads=(ArrayList) me.getValue();

		    	for(int i=0; i<kingdomelevelreads.size(); i++){
					int count_humans=Integer.parseInt(sampleid_humanreads.get((i)+"").toString());

					double klevelreads=Double.parseDouble(kingdomelevelreads.get(i).toString());
					double tmp_value1=(klevelreads/count_humans+0.0)*1000000;
					kingdomelevelinfo.write(tmp_value1+"\t");
				}
				kingdomelevelinfo.write("\n");
		   	}
			kingdomelevelinfo.close();

			//System.out.println("Species levels");
		// <Key: Speices name / Leaf name, Value: ArrayList of reads from samples> --->> <Key: taxid, Value: ArrayList of <Scientific name, # reads from samples>>
			Set set3 = taxhittable.entrySet();
		    Iterator kk3 = set3.iterator();
		    HashMap newtaxonomy=new HashMap(); //  <Key: taxid, Value: ArrayList of <Scientific name, # reads from samples>>

		    while(kk3.hasNext()){
		    	Map.Entry me = (Map.Entry)kk3.next();

		    	ArrayList samplereads=(ArrayList) me.getValue();

		    	String kingdom_name=findkingdom(me.getKey().toString(), txnames);
		    	//System.out.println(me.getKey().toString()+"\t"+kingdom_name);

		    	if(kingdom_name.equals("BACTERIA")){ // REMOVE THIS AFTER FIXING THE GENOMELENGTH PROBLEM WITH VIRUSES ------ IMPORTNAT
		    	//if(kingdom_name.equals("FUNGI")){ // FUNGI  IMPORTNAT
					//System.out.println(me.getKey().toString());

					// Tax Names
					ArrayList<String> tnames=(ArrayList) txnames.get(me.getKey().toString());

					// Tax ID
					String idtax=tnames.get(0);

					// Scientific names
					String sc_name="";
					if(scnames.containsKey(idtax)){
						ArrayList<String> tmp2=(ArrayList) scnames.get(idtax);
						sc_name=tmp2.get(0).toString();
					}

					if(newtaxonomy.containsKey(idtax)){
						//System.out.println("Hello $$$$$$$$$$$$$$$$$$$$$$$$$ "+me.getKey().toString()+"******"+idtax);
						ArrayList tmp1=(ArrayList) newtaxonomy.get(idtax);
						ArrayList tmp2=new ArrayList();
						for(int ns=0; ns<tmp1.size(); ns++){
							if(ns==0){
								tmp2.add(tmp1.get(ns).toString());
							}
							else{
								double no_reads=Double.parseDouble(tmp1.get(ns).toString());
								//System.out.println(no_reads);
								ArrayList kingdomelevelreads=(ArrayList)kingdomlevel.get(kingdom_name);
								//System.out.println(kingdomelevelreads+"kind");
								double total_mappedreads=Double.parseDouble(kingdomelevelreads.get(ns).toString()); // Total mapped to that kingdom
								//System.out.println(total_mappedreads+"kind");
								double no_reads1=Double.parseDouble(samplereads.get(ns-1).toString()); // Current number of reads
								//System.out.println(no_reads1+"kind11");
								double metric=no_reads1;


								//double final_reads=no_reads+no_reads1; //No normalization
								double final_reads=metric+no_reads1; //normalization

								tmp2.add(final_reads+"");
							}
						}
						newtaxonomy.put(idtax, tmp2);
					}
					else{
						ArrayList tmp1=new ArrayList();
						tmp1.add(sc_name);
						for(int i=0; i<samplereads.size(); i++){
							double no_reads1=Double.parseDouble(samplereads.get(i).toString()); // Current number of reads // Adbunance --- #reads --- but no. of reads mapped to bacteria is not listed here
							//System.out.print(no_reads1+" no_reads1");

							ArrayList kingdomelevelreads=(ArrayList)kingdomlevel.get(kingdom_name);
							//System.out.print(kingdomelevelreads+" kingdomlevel reads");

							double total_mappedreads=Double.parseDouble(kingdomelevelreads.get(i).toString()); // Total mapped to that kingdom

							double metric=no_reads1;
							//System.out.print(metric+" metric");
							//tmp1.add(samplereads.get(i).toString()); // No normalization
							tmp1.add(metric); // normalizaed
						}
						newtaxonomy.put(idtax, tmp1);
					}
				}
				else{
					//System.out.println(kingdom_name);
				}
			}

			Set set4 = newtaxonomy.entrySet();
		    Iterator kk4 = set4.iterator();

		    HashMap othereu=new HashMap(); // other eukaryotes
		    HashMap taxonomy=new HashMap();

		    while(kk4.hasNext()){
		    	Map.Entry me = (Map.Entry)kk4.next();
		    	//System.out.println(me.getKey()+"	"+me.getValue());

		    	Integer id=new Integer(me.getKey().toString()); // Taxid

		    	// Acesstary of the unique tax id
		    	RAM_Rawreadscounts tmpobj=new RAM_Rawreadscounts();
		    	List<Integer> L1= tmpobj.lineage(id);

		    	// Value <Taxname> <# reads from multiple samples>
		    	ArrayList value=(ArrayList)me.getValue();

		    	String taxonomy_string="";

		    	// Attach the Kingdom attached
		    	String kingdom="";
		    	String skingdom="";
		    	String skingdomid="";
		    	String oeukingdomid="";
		    	int found=0;


		       	for(int s=0; s<L1.size(); s++){ // Run through the List to find the kingdom information
		    		String taxnum=(L1.get(s).toString());

		    		ArrayList<String> tmp2=(ArrayList) scnames.get(L1.get(s).toString()); // TO find kingdom
		    		String taxscientificname=tmp2.get(0).toString(); // To find kingdom

		    		// Phylum, Speices, class....
					String typetaxid=id2taxnam.get(L1.get(s).toString()).toString();

					// Assign kingdom
					if(taxnum.equals("2")){ // Bacteria
						kingdom=taxscientificname;
					}
					else if(taxnum.equals("2157")){//Archaea
						kingdom=taxscientificname;
					}
					else if(taxnum.equals("12884")){//Viroids
						kingdom=taxscientificname;
					}
					else if(taxnum.equals("10239")){//Viruses
						kingdom=taxscientificname;
					}
					else if(taxnum.equals("2759")){ // Eukaryota-Superkingdom
						skingdomid=taxnum;
						skingdom=taxscientificname;
					}
					else if(taxnum.equals("12908")){ // Unclassified sequences
						kingdom=taxscientificname;
					}
					else if(taxnum.equals("28384")){ // Other sequences
						kingdom=taxscientificname;
					}
					else{
						if(skingdomid.equals("2759") && taxnum.equals("33090")){
							kingdom=skingdom+"|"+taxscientificname;
							found=1;
						}
						else if(skingdomid.equals("2759") && taxnum.equals("33208")){
							kingdom=skingdom+"|"+taxscientificname;
							found=1;
						}
						else if(skingdomid.equals("2759") && taxnum.equals("4751")){
							kingdom=skingdom+"|"+taxscientificname;
							found=1;
						}
						else{
							if(skingdomid.equals("2759") && found==0){
								kingdom=skingdom+"|OTHER";
								oeukingdomid=taxnum;
							}
						}
					}
		    	}
		    	//System.out.println(kingdom);

		    	if(kingdom.equalsIgnoreCase("EUKARYOTA|OTHER")){
		    		//System.out.println(kingdom+" "+oeukingdomid);
		    		othereu.put(oeukingdomid,"0");
		    	}

		    	// Generate the taxonomy for each entry in blast hittable
		    	for(int s=1; s<L1.size(); s++){

		    		if(taxonomy.size() ==0){ // Empty taxonomy
		    			// Look into the scientific names
		    			ArrayList<String> tmp2=(ArrayList) scnames.get(L1.get(s).toString());
		    			String taxscientificname=tmp2.get(0).toString();

		    			// Phylum, Speices, class....
						String typetaxid=id2taxnam.get(L1.get(s).toString()).toString();

						//Taxonomy tree for each species
						if(s>=1){ // Exclude Root and Cellular Organisms
							String taxscientificname_tmp=taxscientificname.replace('$',' ');
							//taxonomy_string=taxscientificname_tmp+"("+typetaxid+")";
							taxonomy_string=taxscientificname_tmp;
						}

						ArrayList tmpf=new ArrayList();
						tmpf.add(kingdom);
						tmpf.add(taxonomy_string);
						tmpf.add(L1.get(s).toString());
						tmpf.add(typetaxid);
						tmpf.add(taxscientificname);
						tmpf.add(value);
						taxonomy.put(L1.get(s).toString(), tmpf);
		    		}
		    		else{
		    			if(taxonomy.containsKey(L1.get(s).toString())){
		    				ArrayList tmp2=(ArrayList) taxonomy.get(L1.get(s).toString());
		    				ArrayList taxvalue=(ArrayList)(tmp2.get(5));
		    				String taxscientificname=tmp2.get(4).toString();
		    				String typetaxid=tmp2.get(3).toString();

		    				String taxscientificname_tmp=taxscientificname.replace('$',' ');
							if(s>=1){ // Exclude Root and Cellular Organisms
								if(taxonomy_string.length()>0){
									//taxonomy_string=taxonomy_string+"|"+taxscientificname_tmp+"("+typetaxid+")";
									taxonomy_string=taxonomy_string+"|"+taxscientificname_tmp;
								}
								else{
									//taxonomy_string=taxscientificname_tmp+"("+typetaxid+")";
									taxonomy_string=taxscientificname_tmp;
								}
							}

							ArrayList newtaxvalue=new ArrayList();
			    			for(int m=0; m<value.size(); m++){ // New value set + old value set
			    				if(m==0){newtaxvalue.add(value.get(m));}
			    				else{
			    					double cscore=Double.parseDouble(value.get(m).toString())+Double.parseDouble(taxvalue.get(m).toString());
			    					newtaxvalue.add(cscore+"");
			    				}
			    			}

							ArrayList tmpf=new ArrayList();
							tmpf.add(kingdom);
							tmpf.add(taxonomy_string);
							tmpf.add(L1.get(s).toString());
							tmpf.add(typetaxid);
							tmpf.add(taxscientificname);
							tmpf.add(newtaxvalue);
							taxonomy.put(L1.get(s).toString(), tmpf);

		    			}
		    			else{
			    			// Look into the scientific names
			    			ArrayList<String> tmp2=(ArrayList) scnames.get(L1.get(s).toString());
			    			String taxscientificname=tmp2.get(0).toString();
			    			// Phylum, Speices, class....
							String typetaxid=id2taxnam.get(L1.get(s).toString()).toString();

							String taxscientificname_tmp=taxscientificname.replace('$',' ');
							//Taxonomy tree for each species
							if(s>=1){ // Exclude Root and Cellular Organisms
								if(taxonomy_string.length()>0){
									//taxonomy_string=taxonomy_string+"|"+taxscientificname_tmp+"("+typetaxid+")";
									taxonomy_string=taxonomy_string+"|"+taxscientificname_tmp;
								}
								else{
									//taxonomy_string=taxscientificname_tmp+"("+typetaxid+")";
									taxonomy_string=taxscientificname_tmp;
								}
							}
							ArrayList tmpf=new ArrayList();
							tmpf.add(kingdom);
							tmpf.add(taxonomy_string);
							tmpf.add(L1.get(s).toString());
							tmpf.add(typetaxid);
							tmpf.add(taxscientificname);
							tmpf.add(value);
							taxonomy.put(L1.get(s).toString(), tmpf);
		    			}
		    		}
		    	}
		    }

			String kingdom_name="";  String tax_idval="";  String tax_level="";  String tax_name="";  String taxonomystr="";

		    BufferedWriter species = new BufferedWriter(new FileWriter(outputFile));
		    species.write("Kingdom\tTaxonomyString\tTax id\tTaxonomy level\tScientific name\t");
		    System.out.println(samplenames.size());
			for(int i=0; i<samplenames_list.size(); i++){
				species.write(samplenames_list.get(i)+"\t");
			}
			species.write("\n");


		    Set set5 = taxonomy.entrySet();
		    Iterator kk5 = set5.iterator();
		    while(kk5.hasNext()){
		    	Map.Entry me = (Map.Entry)kk5.next();
		    	//System.out.println(me.getKey()+"	"+me.getValue());
		    	ArrayList tmp=(ArrayList)me.getValue();

		    	for(int k=0; k<tmp.size(); k++){
		    		//System.out.println(tmp.get(k).toString());
		    		if(k==0){ // Kingdom
	    				kingdom_name=tmp.get(k).toString();
	    				//out.write(tmp.get(k)+"\t");
		    		}
		    		else if(k==1){ // Taxonomy string
	    				taxonomystr=tmp.get(k).toString();
	    				//out.write(tmp.get(k)+"\t");
		    		}
		    		else if(k==2){ // taxid
		    				tax_idval=tmp.get(k).toString();
		    				//out.write(tmp.get(k)+"\t");
		    		}
		    		else if(k==3){ // Taxonomy levels
		    				tax_level=tmp.get(k).toString();
		    				//out.write(tmp.get(k)+"\t");
		    		}
		    		else if(k==4){ // Name of Taxonomy

			    			String replacetest=tmp.get(k).toString().replace('$',' ');
			    			tax_name=replacetest;
			    			//out.write(replacetest+"\t");
		    		}
		    		else if(k==5){ // Number of reads mapped

		    			//all speices level
		    			species.write(kingdom_name+"\t");
		    			species.write(taxonomystr+"\t");
		    			species.write(tax_idval+"\t");
		    			species.write(tax_level+"\t");
		    			species.write(tax_name+"\t");
		    			ArrayList scores1=(ArrayList) tmp.get(k);
						//System.out.println(scores1);
		    			String scoretmp1="";
		    			for(int ss1=1; ss1<scores1.size(); ss1++){
							double tmp_value=Double.parseDouble(scores1.get(ss1).toString());
							//System.out.println(sampleid_humanreads);
							int count_humans=Integer.parseInt(sampleid_humanreads.get((ss1-1)+"").toString());
							double tmp_value1=(tmp_value/count_humans+0.0)*1000000;

							if(ss1==1){
	    						scoretmp1=tmp_value1+"";
							}
	    					else{
	    						//scoretmp1=scoretmp1+"\t"+scores1.get(ss1).toString();
								scoretmp1=scoretmp1+"\t"+tmp_value1+"";
							}
	    				}
		    			species.write(scoretmp1+"\n");
		    		}
		    	}
		    }
			species.close();




		   // Unknown set because of typos problem

		   if(Unknownspec.size() > 0){
			   System.out.println("Please inform the following list to Chandra");
			   Set tmp123 = Unknownspec.entrySet();
			   Iterator tmp2 = tmp123.iterator();
		    	while(tmp2.hasNext()){
					Map.Entry me = (Map.Entry)tmp2.next();
			    	System.out.println(me.getKey());
				}
		   }

		   System.out.println("Completed runs");

		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
