/*
	Created: Chandra Sekhar Pedamallu
	Usage: Results
	Copyright@DFCI, BroadInst.
*/


import java.io.*;


public class results{

	public static void main(String args[]){

		try{

			//Total number of unmapped reads
			System.out.println(args[0]);
			FileReader fr0=new FileReader(args[0]);
			BufferedReader br0=new BufferedReader(fr0);
			String record0=new String();


			int totalUnmapped=0;
			while((record0=br0.readLine()) != null){
				String rec = record0.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped=totalUnmapped+no_reads;
			}
			fr0.close();
			System.out.println("totalUnmapped "+totalUnmapped);


			//Total number of unmapped reads after duplicate remover
			System.out.println(args[1]);
			FileReader fr1=new FileReader(args[1]);
			BufferedReader br1=new BufferedReader(fr1);
			String record1=new String();

			int totalUnmapped_dup=0;
			while((record1=br1.readLine()) != null){
				String rec = record1.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped_dup=totalUnmapped_dup+no_reads;
			}
			fr1.close();
			System.out.println(totalUnmapped_dup);

			//Total number of unmapped reads after adapter remover
			System.out.println(args[2]);
			FileReader fr2=new FileReader(args[2]);
			BufferedReader br2=new BufferedReader(fr2);
			String record2=new String();

			int totalUnmapped_adap=0;
			while((record2=br2.readLine()) != null){
				String rec = record2.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped_adap=totalUnmapped_adap+no_reads;
			}
			fr2.close();
			System.out.println("totalUnmapped_adap"+totalUnmapped_adap);


			//Total number of unmapped reads after ribosomal remover
			System.out.println(args[3]);
			FileReader fr3=new FileReader(args[3]);
			BufferedReader br3=new BufferedReader(fr3);
			String record3=new String();

			int totalUnmapped_ribo=0;
			while((record3=br3.readLine()) != null){
				String rec = record3.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped_ribo=totalUnmapped_ribo+no_reads;
			}
			fr3.close();
			System.out.println(totalUnmapped_ribo);



			//Total number of unmapped reads after megablast_bacterial remover
			FileReader fr4=new FileReader(args[4]);
			BufferedReader br4=new BufferedReader(fr4);
			String record4=new String();

			int totalUnmapped_bact=0;
			while((record4=br4.readLine()) != null){
				String rec = record4.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped_bact=totalUnmapped_bact+no_reads;
			}
			fr4.close();
			System.out.println("totalUnmapped_bact "+totalUnmapped_bact);
System.out.println(args[5]);

			//Total number of unmapped reads after blastn_HS+allpathogen remover
			FileReader fr5=new FileReader(args[5]);
			BufferedReader br5=new BufferedReader(fr5);
			String record5=new String();
			System.out.println(args[5]);

			int totalUnmapped_hsallpath=0;
			while((record5=br5.readLine()) != null){
				String rec = record5.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped_hsallpath=totalUnmapped_hsallpath+no_reads;
			}
			fr5.close();
			System.out.println(totalUnmapped_hsallpath);


			// Type of Assembler
			String assembler=args[6];


			// Number of contigs
			String numb_contigs=args[7];

			// Number of unused reads (only enabled for Velvet)
			String numb_unused=args[8];


			// Reads after duplicate remover
			String readsAduplicate=args[9];

			// Reads mapped to Adapter regions
			String readsMadapter=args[10];

			// Reads unmapped to Adapter regions
			String readsUMadapter=args[11];



			// Reads mapped to Ribosomal database
			String readsMribo=args[12];

			// Reads unmapped to Ribosomal database
			String readsUMribo=args[13];


			// Reads mapped to bacterial database
			String readsMbact=args[14];

			// Reads unmapped to bacterial database
			String readsUMbact=args[15];

			// Reads mapped to HS+allPathogen database
			String readsMhsallpath=args[16];

			// Reads unmapped to HS+allPathogen database
			String stillunmapped=args[17];

			//Reads.stat NumberReadsAfterDuplicate.stat NumbReadsAfterAdapter.stat NumbReadsAfterRibosomal.stat NumbReadsAfterMegablast2bac.stat NumbReadsAfterBlastn2HumanplusPathogen.stat VELVET <no_contigs>
			//<no_unused> Reads_after_duplicate.fq1 ReadsMappedAdapters_megablast.fq1 ReadsUnmappedAdapters_megablast.fq1 ReadsMappedRibo_Megablast.fq1 ReadsUnmappedRibo_Megablast.fq1
			//ReadsMappedBact_Megablast.fq1 ReadsUnmappedBact_Megablast.fq1 ReadsMappedHumanplusPathogen_Blastn.fq1 stillunmapped.fq1 stillunmapped.fq1_f.fasta stillunmapped.fq1_r.fasta
			//All_pairs Contigs4Stillunmapped.fa Unusedreads.fa RAM.results Megblast2Bacterial.HITTABLE Blastn2Humanpluspathogen.HITTABLE Contigs.blastn.HITTABLE Contigs.blastx.HITTABLE
			//results.html

			// Input_forwardreads
			String stillunmapped_f=args[18];

			// Input_reversereads
			String stillunmapped_r=args[19];

			// Type of pairs (All pairs / Good pairs / Single end)
			String pairs=args[20];

			// Contigs from assemebler
			String contigs=args[21];

			// Unused reads by the assemebler
			String unused=args[22];

			// RAM results
			String ramresults=args[23];

			// Hittables - Bacterial hittable
			String hittableBac=args[24];

			// Hittables - HS+allpathogen hittable
			String hittableHs=args[25];

			// Hittables - Contigs blatN hittable (HS+allPathogen)
			String hittableContigblastN=args[26];

			// Hittables - Contigs blastX hittable (NR)
			String hittableContigblastX=args[27];

			/*
			 * Total number of unmapped reads |
			 * Number of reads after Megablast to bacterial genomes |
			 * Number of still unmapped reads after Blastn to Human+Allpathogens |
			 * --------------------------------------------------------------------------
			 *
			 * --------------------------------------------------------------------------
			 * Hit tables on Post-substraction (unmapped reads)
			 * -----------------------------------------------------------------------
			 * Mapped to Bacterial genomes | Megablast hittable |Reads mapped (<1E-10)|
			 * Mapped to Human+AllPathogens| Blastn hittable 	|Reads mapped (<1E-10)|
			 * ------------------------------------------------------------------------
			 *
			 * Hit tables on Post-substraction (Contigs)
			 * -----------------------------------------------------------------------
			 * Mapped to Bacterial genomes | Megablast hittable |Reads mapped (<1E-10)|
			 * Mapped to Human+AllPathogens| Blastn hittable 	|Reads mapped (<1E-10)|
			 * ------------------------------------------------------------------------
			 *
			 * RAM results
			 * Contigs (in fasta)
			 * Still unmapped reads (in fq1)
			 *
			 */
			//Total number of unmapped reads after phage remover
			System.out.println(args[29]);
			FileReader fr100=new FileReader(args[29]);
			BufferedReader br100=new BufferedReader(fr100);
			String record100=new String();

			int totalUnmapped_phage=0;
			while((record100=br100.readLine()) != null){
				String rec = record100.trim();
				int no_reads=Integer.parseInt(rec);
				totalUnmapped_phage=totalUnmapped_phage+no_reads;
			}
			fr100.close();
			System.out.println(totalUnmapped_phage);

			// Hittables - Phage hittable
			String hittablePhage=args[30];

			// Reads mapped to Phage database
			String readsMphage=args[31];

			// Reads unmapped to Phage database
			String readsUMphage=args[32];



			BufferedWriter out=new BufferedWriter(new FileWriter(args[28]));
			out.write("<HTML>"+"\n");
			out.write("<BODY bgcolor=lightblue>"+"\n");
			out.write("<b>Total number of unmapped reads: "+totalUnmapped+"</b><br>");

			out.write("<table border=1>");
			out.write("<tr><td>Steps in the pipeline</td><td># of reads removed</td><td># of reads left after the Step</td></tr>");
			out.write("<tr><td>Duplicate removal</td><td>"+(totalUnmapped-totalUnmapped_dup)+"</td><td><a href="+readsAduplicate+">"+totalUnmapped_dup+"</a></td></tr>\n");
			out.write("<tr><td>Adapter removal</td><td><a href="+readsMadapter+">"+(totalUnmapped_dup-totalUnmapped_adap)+"</a></td><td><a href="+readsUMadapter+">"+totalUnmapped_adap+"</a></td></tr>\n");
			out.write("<tr><td>Ribosomal reads removal</td><td><a href="+readsMribo+">"+(totalUnmapped_adap-totalUnmapped_ribo)+"</a></td><td><a href="+readsUMribo+">"+totalUnmapped_ribo+"</a></td></tr>\n");
			out.write("<tr><td>Phage reads removal</td><td><a href="+readsMphage+">"+(totalUnmapped_ribo-totalUnmapped_phage)+"</a></td><td><a href="+readsUMphage+">"+totalUnmapped_phage+"</a></td></tr>\n");

			out.write("<tr><td>Mapped to Bacterial genomes (megablast)</td><td><a href="+readsMbact+">"+(totalUnmapped_ribo-totalUnmapped_bact)+"</a></td><td><a href="+readsUMbact+">"+totalUnmapped_bact+"</a></td></tr>\n");
			out.write("<tr><td>Mapped to HS+all pathogens (blastn)</td><td><a href="+readsMhsallpath+">"+(totalUnmapped_bact-totalUnmapped_hsallpath)+"</a></td><td><a href="+stillunmapped+">"+totalUnmapped_hsallpath+"</a></td></tr>\n");
			out.write("</table>");
			out.write("<br>");
			out.write("<br>");
			out.write("RAM results: <a href="+ramresults+">Ram results</a><br><br>\n");
			out.write("Reads that are still unmapped: <a href="+stillunmapped+">Reads</a><br><br>\n");
			out.write("<hr><br>");
			out.write("<b>Contigs and its related information</b><br>");
			out.write("<hr><br>");
			out.write("Assembler used :"+assembler+"<br><br>\n");
			out.write("Type of assembly :"+pairs+"<br><br>\n");

			if(pairs.equals("AllPairs") || pairs.equals("GoodPairs")){
				out.write("Input reads for assembly : <a href="+stillunmapped_f+">Forward reads</a> --- <a href="+stillunmapped_r+">Reverse reads</a><br><br>\n");
			}
			else{
				out.write("Input reads for assembly : <a href="+stillunmapped_f+">Reads</a><br><br>\n");
			}
			out.write("Number of Contigs (> read length)<a href="+contigs+">"+numb_contigs+"</a><br>\n");
			out.write("Number of reads unused in Contigs <a href="+unused+">"+numb_unused+"</a><br>\n");
			out.write("<hr><br>");
			out.write("Hit table<br>");
			out.write("<hr><br>");
			out.write("Unmapped reads -- hit table to bacterial databases (megablast) : <a href="+hittableBac+"> Hittable</a><br>\n");
			out.write("Unmapped reads -- hit table to Human+Pathogens databases (blastn) :<a href="+hittableHs+"> Hittable</a><br>\n");
			out.write("Contigs -- hit table to Human+Pathogens (blastn) : <a href="+hittableContigblastN+"> Hittable</a><br>\n");
			out.write("Contigs -- hit table to NR databases (blastx) :<a href="+hittableContigblastX+"> Hittable</a><br>\n");

			out.write("<hr><br>");
			out.write("<b>Notes:<br></b>\n");
			out.write("Mapping E-value=1E-10<br>");
			out.write("</BODY>"+"\n");
			out.write("</HTML>"+"\n");
			out.close();



			//System.out.println("HELLO");
		}catch(Exception e){System.out.println(e);}
	}
}
