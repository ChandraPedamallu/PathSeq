import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.util.*;

public class BWAunmapped_June2016 {

	public static void main(String args[]) throws FileNotFoundException {

		String inputFile1=args[0]; //Input Sequence
		String inputFile2=args[1]; // Sam file
		String outputFastqFile=args[2];

		long start = System.currentTimeMillis();

		long totalReadCounter 		= 0;
		long unmappedReadCounter 	= 0;
		long mappedReadCounter		= 0;

		try{

			FileReader fstream1=new FileReader(inputFile1);
			BufferedReader br1=new BufferedReader(fstream1);
			HashMap inputreads=new HashMap();

			String strLine1;
			while ((strLine1 = br1.readLine()) != null)   {
				String read="", readseq="", quality="", strand="";
				StringTokenizer str2=new StringTokenizer(strLine1, "\t");
				int s_count=0;
				while(str2.hasMoreTokens()){
					String tokens=str2.nextToken().toString();
					if(s_count==0){
						String firstLetterRead=Character.toString(tokens.charAt(0));
						if(firstLetterRead.equals("@")){
							read=tokens.substring(1);
							//System.out.println(tokens+"DDDDDDDDDDDDDDD");
							//System.out.println(read);
						}
						else{
							read=tokens;
						}
					}
					else if(s_count==1){
						readseq=tokens;
					}
					else if(s_count==2){
						strand=tokens;
					}
					else if(s_count==3){
						quality=tokens;
					}
					s_count++;
				}
				String tmp=readseq+"\t"+strand+"\t"+quality;
				inputreads.put(read, tmp);
			}
			fstream1.close();

			BufferedWriter out = new BufferedWriter(new FileWriter(outputFastqFile));

			FileReader fstream=new FileReader(inputFile2);
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
						String readinfo=inputreads.get(readme).toString();
						out.write("@"+readme+"\t"+readinfo+"\n");
						//out.write("@"+readme+"\t"+seq+"\t+\t"+qscore+"\n");

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

