import java.util.StringTokenizer;
import java.io.*;


public class removeduplicates_new {
	public static void main(String[] args)
	{
		try{
			FileReader fr=new FileReader(args[0]);
			BufferedReader br=new BufferedReader(fr);
			String record=new String();
			BufferedWriter out=new BufferedWriter(new FileWriter(args[1]));
			BufferedWriter cat=new BufferedWriter(new FileWriter("TotalNumberQFReads.txt"));
			String pSeq="NONE";
			int number_reads=0;
			while((record=br.readLine()) != null){
				String rec = record.trim();
				StringTokenizer stt=new StringTokenizer(rec, "\t");
				int no=0;
				String seq="";
				while(stt.hasMoreElements()){
					String token=stt.nextToken().toString();
					if(no==1){
						seq=token;
					}
					no++;
				}

				if(!(pSeq.equals(seq))){
					out.write(rec+"\n");
					pSeq=seq;
				}
				number_reads++;

				//list.add(new Row(name, seq, quality));
			}

			fr.close();
			System.out.println("completed");
			cat.write(number_reads+"\n");	
			out.close();
			cat.close();
		}catch(Exception e){System.out.println(e);}
	}
}
