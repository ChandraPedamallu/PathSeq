/*
 * Author: Chandra Sekhar Pedamallu
 *
 * Date: July 12, 2011
 * Usage:
 * Details: This annotates the hittable with kingdom / superkingdom.
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

public class annotate_hittable {

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

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		String taxnames=args[0]; //taxnames file
		String taxnodes=args[1]; // taxnodes file
		String inputFile =   args[2]; //BLAST parsed XML results
		String outputFile=   args[3];  //output file
		//String inputFile =   args[0]; //BLAST parsed XML results
		//String outputFile=   args[1];  //output file
		//String taxnames=   "names.dmp";  //taxnames file
		//String taxnodes=   "nodes.dmp";  //taxnodes file
		long start = System.currentTimeMillis();
		int lineCount= 0;

		try{

		    BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

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

	    	HashMap txnames=new HashMap(); // Taxonomy names
			HashMap scnames=new HashMap(); // Scientific names

			// Taxonomy names
			FileReader fr1=new FileReader(taxnames);
			BufferedReader br1=new BufferedReader(fr1);
			String indexNo="EMPTY";
			String species_sci="";
			String strLine1;

			// The Hashmap contains <taxname> <taxid>
			while ((strLine1 = br1.readLine()) != null)   {
				StringTokenizer strtmp=new StringTokenizer(strLine1, "|");
				int ctokens=0;
				String taxid="";	String taxname="";		String taxname1="";		String cspecies_sci="";
				String taxnamenew="";	String taxname1new="";		String cspecies_scinew="";

				while(strtmp.hasMoreTokens()){
					String strtoken=strtmp.nextToken().toString().trim();

					if(ctokens==0){	taxid=(strtoken);	}
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

			// Hittable
			FileReader fr=new FileReader(inputFile);
			BufferedReader br=new BufferedReader(fr);
			String strLine;

			HashMap<String, Integer> hsall=new HashMap<String, Integer>(); // All best 5 hits counted
			HashMap<String, Integer> hsbest=new HashMap<String, Integer>(); // Only best 5 hits counted
			ArrayList<String> Unknownspec=new ArrayList<String>();

			//out.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n");

			// Read the hit table and add the counts to them
			while ((strLine = br.readLine()) != null)   {
				lineCount++;

				StringTokenizer strtoken=new StringTokenizer(strLine, "\t");
				int no_token=0;
				int hit_numb=1;
				//System.out.println(strLine);


				while(strtoken.hasMoreTokens()){
					String tokens=strtoken.nextToken().toString().trim();
					if(no_token==2){
						hit_numb=Integer.parseInt(tokens);
					}
					else if(no_token==4){

//This is extract subject name of the hit from gi|number| <Name of speicies> -START
						StringTokenizer tokensHitname=new StringTokenizer(tokens, " ");
						int name_Hit=0;
						String nameHit1="";
						String shortname1="";
						int number_parts=0; // Number of subparts from the genome name.. Take 4 parts of the sequences
						String pervious="EMPTY";
						int found=0;
						// Name of the Hit in the Hit table
						while(tokensHitname.hasMoreTokens()){
							String nameHittmp=tokensHitname.nextToken().toUpperCase().trim();
							if(name_Hit==0){
								if(!(nameHittmp.contains("|"))){
									nameHit1=nameHittmp;
									shortname1=nameHittmp;
									number_parts++;
								}
							}
							else{
								nameHit1=nameHit1+" "+nameHittmp;
							}

							name_Hit++;
						}
						String nameHit2=nameHit1.trim().toUpperCase();
						//System.out.println("NAMEHITS	"+nameHit2);

						// Remove all "," from the nameHits
						tokensHitname=new StringTokenizer(nameHit2, ",");
						name_Hit=0;

						int no_tokens=tokensHitname.countTokens();
						while(tokensHitname.hasMoreTokens()){
							String nameHittmp=tokensHitname.nextToken().toUpperCase().trim();
							if(name_Hit==0){
								nameHit1=nameHittmp;
							}
							name_Hit++;
						}
						String nameHit=nameHit1.trim().toUpperCase();
						//System.out.println("---------------------------"+nameHit);
//This is extract subject name of the hit from gi|number| <Name of speicies> -END

//Chop the subject name into smaller piecies and compare to the taxnames
						StringTokenizer hitCompare=new StringTokenizer(nameHit, " ");
						int cc=0;
						ArrayList<String> query=new ArrayList<String>();
						String qtmp="";
						int check=0;

						// Look for all possible queies in the hit
						while(hitCompare.hasMoreTokens()){
							String tokens11=hitCompare.nextToken();
							if(cc==0)
								qtmp=tokens11;
							else
								qtmp=qtmp+"$"+tokens11;

							query.add(qtmp);
							cc++;
						}
						//Look for the longest query found in taxnames
						int qfound=-1; String qname="";
						for(int ss=0; ss<query.size(); ss++){
							String query_name=query.get(ss).toString();
							if(txnames.containsKey(query_name)){
								qfound=ss;
								qname=query_name;
							}
						}
						// Add counter to the longest query found in taxnames
						if(qfound >= 0){
							// Hashmap ------ <species, <taxid, count>>
							//System.out.println(qname);
							ArrayList<String> tmp=(ArrayList) txnames.get(qname);
							String idtax=tmp.get(0);

							// Pull the ancestary for the species... Phylum, Speices, class....
							annotate_hittable tmpobj=new annotate_hittable();
							Integer intobj=new Integer(idtax);
							List<Integer> L1= tmpobj.lineage(intobj);
							String annotate_kingdom="";
							int found_superkingdom=0;
							for(int s=0; s<L1.size(); s++){
								String typetaxid=id2taxnam.get(L1.get(s).toString()).toString();


								ArrayList<String> tmp2=(ArrayList) scnames.get(L1.get(s).toString());
				    			String taxscientificname=tmp2.get(0).toString();

				    			//System.out.println(typetaxid+" "+taxscientificname);
				    			if(typetaxid.equals("superkingdom")){
				    				annotate_kingdom=taxscientificname;
				    				found_superkingdom=1;
				    			}

				    			if((typetaxid.equals("kingdom"))){
				    				annotate_kingdom=taxscientificname;
				    			}

							}
							out.write(strLine+"\t"+annotate_kingdom+"\t"+nameHit+"\n");
						}
						else{
							out.write(strLine+"\tUNKNOWN\t"+nameHit+"\n");
							Unknownspec.add(nameHit);
							//System.out.println("Unknown"+nameHit);
						}


					}
					no_token++;
				}
			}
			fr.close();

			   // Unknown set because of typos problem
			if(Unknownspec.size() > 0){
				System.out.println("Please inform the following list to Chandra (chandra@broadinstitute.org)");
				for(int k=0; k<Unknownspec.size(); k++)
				   	System.out.println(Unknownspec.get(k));
			}


			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(	"Process complete.\n\n" +
					"Total reads: " + hsbest.size() + "\n\n" +
					"CPU time: "+elapsedTimeSec);

			out.flush();
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
