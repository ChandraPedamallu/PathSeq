import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;

public class BWAunmapped {

	public static void main(String args[]) throws FileNotFoundException {

		String inputFile=args[0];
		String outputFastqFile=args[1];

		long start = System.currentTimeMillis();

		long totalReadCounter 		= 0;
		long unmappedReadCounter 	= 0;
		long mappedReadCounter		= 0;

		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFastqFile));

			FileReader fstream=new FileReader(inputFile);
			BufferedReader br=new BufferedReader(fstream);

			String strLine;
			while ((strLine = br.readLine()) != null)   {

				String firstLetterS= Character.toString(strLine.charAt(0));
				if (!firstLetterS.equals("@") ) {
					totalReadCounter++;
					StringTokenizer strtmp=new StringTokenizer(strLine, "\t");

					int n_count=0;
					String readme="", ref="",seq="",qscore="";
					String mapped="";
					while(strtmp.hasMoreTokens()){
						String token=strtmp.nextToken().toString().trim();
						if(n_count==0){
							readme=token;
						}
						else if(n_count==1){
							mapped=token;

						}
						else if(n_count==2){
							ref=token;
						}
						else if(n_count==9){
							seq=token;
						}
						else if(n_count==10){
							qscore=token;
						}

						n_count++;
					}

					if((Integer.parseInt(mapped) & 4) == 4 ){
						unmappedReadCounter++;
						out.write("@"+readme+"\t"+seq+"\t+\t"+qscore+"\n");

					}
					else{
						mappedReadCounter++;
					}

				}
			}
			fstream.close();
			out.flush();
			out.close();

			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.println(	"Process complete.\n\n" +
					"Total reads: " + totalReadCounter + "\tMapped reads: " + mappedReadCounter + "\tUnmapped reads: " + unmappedReadCounter + "\n"
					+"CPU time: "+elapsedTimeSec);

		}catch (Exception e){//Catch exception if any
			System.err.println( "Error: " + e.getMessage());
		}
	}
}

