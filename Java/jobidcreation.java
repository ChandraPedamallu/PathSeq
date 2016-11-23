
import java.util.Random;

public class jobidcreation {

	private static final String posschar= "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";

	public static void main(String args[]){
		int len=Integer.parseInt(args[0]);
		Random rand = new Random();
		String sb = "";
		for (int i=0; i<len; i++) {
			int pos= rand.nextInt(posschar.length());
			sb=sb+""+posschar.charAt(pos);
		}
		System.out.println(sb);

	}

}
