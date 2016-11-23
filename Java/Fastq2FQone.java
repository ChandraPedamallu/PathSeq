import java.io.*;

public class Fastq2FQone {

	public static void main(String args[]) throws FileNotFoundException {

		String inputFile=  args[0]; //FASTQ file
		String outputFile= args[1]; //FQ1
		
		long start = System.currentTimeMillis();
		
		int lineCounter = 0;
		int seqCounter = 0;


		try{
			FileReader fr=new FileReader(inputFile);
			BufferedReader br=new BufferedReader(fr);			
			
			BufferedWriter out=new BufferedWriter(new FileWriter(outputFile));
			
			String strLine;		
			while ((strLine = br.readLine()) != null)   {				
				lineCounter++;
				if ( lineCounter % 4 == 0 ) {
					out.write(strLine+"\n");
					seqCounter++;
				} else
					out.write(strLine+"\t");
			}
			fr.close();
			out.flush();
			out.close();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			 float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(seqCounter+" reads in this dataset."+"  Amount of time: "+elapsedTimeSec);
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
