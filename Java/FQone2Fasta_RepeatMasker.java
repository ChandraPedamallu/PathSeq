import java.io.*;

public class FQone2Fasta_RepeatMasker {
	
	private static String caller= "compSubtr.bioinformaticsTools.FQone2Fasta_RepeatMasker ";

	public static void main(String args[]) throws FileNotFoundException {

		String inputFile=  args[0]; //FQ1
		String outputFile= args[1]; //FA
		
		long start = System.currentTimeMillis();
		
		int seqCounter = 0;


		try{
			BufferedWriter out=new BufferedWriter(new FileWriter(outputFile));
			FileReader fr=new FileReader(inputFile);
			BufferedReader br=new BufferedReader(fr);
			

			String strLine;
		
			while ((strLine = br.readLine()) != null)   {
				seqCounter++;
				String[] column= strLine.split("\t");
				out.write(">Seq_" + seqCounter + "\n" + 
						        column[1] + "\n" );
			}
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			 float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(seqCounter+" reads in this dataset." + "  Amount of time: "+elapsedTimeSec);
			
			
			fr.close();
			out.flush();
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	public static String getCaller() {
		return caller;
	}
}
