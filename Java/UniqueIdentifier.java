import java.io.*;


public class UniqueIdentifier {

	private static String caller= "compSubtr.bioinformaticsTools.UniqueIdentifier ";

	public static void main(String[] args) throws FileNotFoundException {

		long start = System.currentTimeMillis();

		String inputFile 	= 	args[0]; //FQ1 file
		String outputFile	= 	args[1]; //FQ1 file of all reads that have been assigned a unique identifier
		String unique_reads =  "Uniquereads.count.txt";


		long seqCounter= 0;

		try{
			BufferedWriter out0 = new BufferedWriter(new FileWriter(outputFile));
			BufferedWriter out1 = new BufferedWriter(new FileWriter(unique_reads));
			FileReader fr0=new FileReader(inputFile);
			BufferedReader br0=new BufferedReader(fr0);


			String strLine0;

			while ((strLine0 = br0.readLine()) != null )   {
				seqCounter++;
				//System.out.println(strLine0);
				String[] column = strLine0.split("\t");
				String readName = column[0];
				readName= readName + "_" + seqCounter;
				out0.write(readName + "\t" + column[1] + "\t" + column[2] + "\t" + column[3] + "\n");
			}
			fr0.close();
			out0.flush();
			out0.close();

			out1.write(seqCounter+"\n");
			out1.close();

			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(	seqCounter+" reads in this dataset.\n" +
								"\nAmount of time: " + elapsedTimeSec);

		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			System.out.println(e);
		}
	}

}
