/*
	Created: Chandra Sekhar Pedamallu
	Usage: HTML_Report
	Copyright@DFCI, BroadInst.
*/


import java.io.*;
import java.util.*;


public class HTML_Report{

	public static void main(String args[]){

		try{

//Config File
			FileReader fr0=new FileReader(args[0]);
//Input File
			String inputfile=args[1];
			String outputfile=args[2];

			BufferedReader br0=new BufferedReader(fr0);
			String record0=new String();
			BufferedWriter out=new BufferedWriter(new FileWriter(outputfile));
			out.write("<HTML>"+"\n");
			out.write("<BODY bgcolor=lightblue>"+"\n");
			out.write("<TABLE border=1>"+"\n");


			int numberScatter=0;
			int numberGather=0;
			int steps=1;
			String firsttoken="";
			while((record0=br0.readLine()) != null){
				String rec = record0.trim();
				StringTokenizer sttmp=new StringTokenizer(rec,":");
				ArrayList tmp=new ArrayList();
				int token_first=0;
				while(sttmp.hasMoreTokens()){
					String token=sttmp.nextToken().toString();
					if(token_first==0){
						firsttoken=token;
					}
					//System.out.println(token);
					if(token.equalsIgnoreCase("SCATTER")){

						steps=1;
						numberScatter++;
					}
					else if(token.equalsIgnoreCase("GATHER") || token.equalsIgnoreCase("GATHERASSEMBLER") || token.equalsIgnoreCase("FINISH")){
						numberGather++;
					}
					else{
						tmp.add(token);
					}
					token_first++;
					//System.out.println(token);
				}
				if(firsttoken.equalsIgnoreCase("SCATTER") || firsttoken.equalsIgnoreCase("GATHER") || firsttoken.equalsIgnoreCase("CLEAN")||firsttoken.equalsIgnoreCase("GATHERASSEMBLER") || firsttoken.equalsIgnoreCase("FINISH") || firsttoken.equalsIgnoreCase("FINISH_CLEAN") ){
				}
				else{
					out.write("<TR>"+"\n");
					out.write("<TD bgcolor=lightgreen><b>Step_"+numberScatter+"_"+steps+" : </b>");
					int index=0;
					for(int i=0; i<tmp.size(); i++){
						if(i==0){out.write(" Tool : "+tmp.get(i).toString()+"<br>");}
						else if(i	==1){out.write("Database location : "+tmp.get(i).toString()+"<br> More info : ");}
						else{out.write(tmp.get(i).toString()+"</TD>\n");}
					}

					int velvet_contig=0;
					if(tmp.get(0).equals("BWA")){
						out.write("</TR>"+"\n");

						BufferedReader reader = new BufferedReader(new FileReader("BWA.unmappedbwa.fq1."+numberScatter+"_"+(steps+1)));
						int lines = 0;
						while (reader.readLine() != null) lines++;
						reader.close();

						out.write("<TR><TD>Number of unmapped reads after the step : "+lines+"</TD></TR>\n");
						out.write("<TR><TD>Aligned file location (in Sam format) : <a href=BWAalignedsamfile."+numberScatter+"_"+(steps+1)+".sam>Aligned Sam file</a></TD></TR>\n");
						out.write("<TR><TD>Unmapped reads after Step (in FQ1) : <a href=BWA.unmappedbwa.fq1."+numberScatter+"_"+(steps+1)+">Unmapped Reads</a></TD></TR>\n");

					}
					else if(tmp.get(0).equals("PREMEGABLAST")){
						out.write("</TR>"+"\n");
						BufferedReader reader = new BufferedReader(new FileReader("Premegablast.unmappedpremega.fq1."+numberScatter+"_"+(steps+1)));
						int lines = 0;
						while (reader.readLine() != null) lines++;
						reader.close();

						out.write("<TR><TD>Number of unmapped reads after the step : "+lines+"</TD></TR>\n");
						out.write("<TR><TD>Hittable : <a href=Premegablast.premega.annotate.hittable."+numberScatter+"_"+(steps+1)+">Hittable</a></TD></TR>\n");
						out.write("<TR><TD>Unmapped reads after Step (in FQ1) : <a href=Premegablast.unmappedpremega.fq1."+numberScatter+"_"+(steps+1)+">Unmapped Reads</a></TD></TR>\n");
						out.write("<TR><TD>Mapped reads after Step (in FQ1) : <a href=Premegablast.mappedpremega.fq1."+numberScatter+"_"+(steps+1)+">Mapped Reads</a></TD></TR>\n");

					}
					else if(tmp.get(0).equals("MEGABLAST")){
						out.write("</TR>"+"\n");
						BufferedReader reader = new BufferedReader(new FileReader("Megablast.unmappedmega.fq1."+numberScatter+"_"+(steps+1)));
						int lines = 0;
						while (reader.readLine() != null) lines++;
						reader.close();

						out.write("<TR><TD>Number of unmapped reads after the step : "+lines+"</TD></TR>\n");
						out.write("<TR><TD>Hittable : <a href=Megablast.mega.annotate.hittable."+numberScatter+"_"+(steps+1)+">Hittable</a></TD></TR>\n");
						out.write("<TR><TD>Unmapped reads after Step (in FQ1) : <a href=Megablast.unmappedmega.fq1."+numberScatter+"_"+(steps+1)+">Unmapped Reads</a></TD></TR>\n");
						out.write("<TR><TD>Mapped reads after Step (in FQ1) : <a href=Megablast.mappedmega.fq1."+numberScatter+"_"+(steps+1)+">Mapped Reads</a></TD></TR>\n");

					}
					else if(tmp.get(0).equals("BLASTX")){
						out.write("</TR>"+"\n");
						BufferedReader reader = new BufferedReader(new FileReader("Blastx.unmappedblastx.fq1."+numberScatter+"_"+(steps+1)));
						int lines = 0;
						while (reader.readLine() != null) lines++;
						reader.close();

						out.write("<TR><TD>Number of unmapped reads after the step : "+lines+"</TD></TR>\n");
						out.write("<TR><TD>Hittable : <a href=Blastx.blastx.annotate.hittable."+numberScatter+"_"+(steps+1)+">Hittable</a></TD></TR>\n");
						out.write("<TR><TD>Unmapped reads after Step (in FQ1) : <a href=Blastx.unmappedblastx.fq1."+numberScatter+"_"+(steps+1)+">Unmapped Reads</a></TD></TR>\n");
						out.write("<TR><TD>Mapped reads after Step (in FQ1) : <a href=Blastx.mappedblastx.fq1."+numberScatter+"_"+(steps+1)+">Mapped Reads</a></TD></TR>\n");

					}
					else if(tmp.get(0).equals("VELVET")){
						String inputfile_name=inputfile;
						for(int kk=0; kk<numberScatter-1; kk++){
							inputfile_name=inputfile_name+".unmappedfinal.fq1";
						}
						inputfile_name=inputfile_name+".contigs.fq1";
						out.write("<TR><TD>Contigs (in FQ1) : <a href="+inputfile_name+">Contigs in FQ1</a></TD></TR>\n");
						velvet_contig++;
					}
					else if(tmp.get(0).equals("REPEATMASKER")){
						out.write("</TR>"+"\n");
						BufferedReader reader = new BufferedReader(new FileReader("RepeatMasker.afterrep.fq1"));
						int lines = 0;
						while (reader.readLine() != null) lines++;
						reader.close();

						out.write("<TR><TD>Number of unmapped reads after the step : "+lines+"</TD></TR>\n");
						out.write("<TR><TD>Unmapped reads after Step (in FQ1) : <a href=RepeatMasker.afterrep.fq1>Unmapped Reads</a></TD></TR>\n");

					}
					System.out.println(numberScatter+"\t"+steps+"\t"+tmp);
					steps++;
				}

			}
			fr0.close();
			out.write("</TABLE>"+"\n");
			out.write("</BODY>"+"\n");
			out.write("</HTML>"+"\n");
			out.close();


			//System.out.println("HELLO");
		}catch(Exception e){System.out.println(e);}
	}
}
