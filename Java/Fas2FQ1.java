/*
 * Created: Chandra Sekhar Pedamallu
 * Fas to Fq1
 */


import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fas2FQ1 {

	public static void main(String[] args) throws FileNotFoundException {
		String inputFile =   args[0]; //Fasta file
		String outputFile =   args[1]; //FQ1 file

		final String REFLINE     = ">";
		Pattern refLine= Pattern.compile(REFLINE);
		ArrayList<String> rwnh = new ArrayList<String>(); //reads with no hits
		int rwnhCounter= 0;
		int foundPairCounter= 0;

		try{

			BufferedWriter out=new BufferedWriter(new FileWriter(outputFile));
			FileReader fr=new FileReader(inputFile);
			BufferedReader br=new BufferedReader(fr);

			String strLine;
			String strSeq="";
			String seqName="";

			while ((strLine = br.readLine()) != null)   {
				Matcher refLineMatcher = refLine.matcher(strLine);

				if ( refLineMatcher.find() ) {
					//System.out.println(strLine);
					if(rwnhCounter>0){
						String strQual="";
						for(int sp=0; sp<strSeq.length();sp++){
							strQual=strQual+"@";
						}
						out.write(seqName+"\t"+strSeq+"\t+\t"+strQual+"\n");
					}
					int numb=strLine.length();
					seqName=strLine.substring(1, numb);
					strSeq="";
					rwnhCounter++;
				}
				else{
					strSeq=strSeq+strLine;
				}
			}
			String strQual="";
			for(int sp=0; sp<strSeq.length();sp++){
				strQual=strQual+"@";
			}
			out.write(seqName+"\t"+strSeq+"\t+\t"+strQual+"\n");
			//out.write(seqName+"\t"+strSeq+"\n");
			out.close();
			fr.close();

			System.out.println(	rwnhCounter + " reads need to be found.\n" );

		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
