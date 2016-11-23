import java.util.StringTokenizer;
import java.io.*;


public class catalogueduplicatereads {
	public static void main(String[] args)
	{
		try{
			FileReader fr=new FileReader(args[0]);
			BufferedReader br=new BufferedReader(fr);
			String record=new String();
			BufferedWriter out=new BufferedWriter(new FileWriter(args[1]));
			BufferedWriter cat=new BufferedWriter(new FileWriter(args[1]+".catalog"));
			String pSeq="NONE";
			int n_seq=0;
			while((record=br.readLine()) != null){
				String rec = record.trim();
				if(rec.length() > 0) {
					StringTokenizer stt=new StringTokenizer(rec, "\t");
					int no=0;
					String seq="";
					while(stt.hasMoreElements()){
						String token=stt.nextToken().toString();
						if(no==1){
							//System.out.println(seq);
							seq=token;
						}
						no++;
					}
	
					if(!(pSeq.equals(seq))){
						out.write(rec+"\n");
						if(pSeq.equals("NONE")){
							cat.write("##<Sequence>"+"\t"+"<No. of reads>");
							cat.write("\n");
						}
						else{
							cat.write(pSeq+" "+n_seq);
							cat.write("\n");
						}
						pSeq=seq;
						n_seq=1;
					}
					else{
						n_seq++;
					}

					//list.add(new Row(name, seq, quality));
				}	
			}
			cat.write(pSeq+" "+n_seq);
			cat.write("\n");

			fr.close();
			cat.close();
			System.out.println("completed");

			out.close();
		}catch(Exception e){System.out.println(e);}
	}
}
