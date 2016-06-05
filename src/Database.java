import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Database
{
	private static Process p = null;
	private static String exe = "/home/mjg/java-workspace-mars/ExpressChess/lib/hedwig-polyglot.exe"; 
	static Thread probeThread = null;
	public Object hasResult = new Object();
	public String result = null;
	
	public Database()
	{

	}
	
	public static void probe(final String fen)
	{
		probeThread = new Thread( new Runnable()
		{			
			@Override
			public void run() {
				{
					search(fen);
				}
			}				
		});
		probeThread.start();

	}
	
	public static void search(String fen)
	{
		try {
			p = Runtime.getRuntime().exec(new String[] {exe, "-find", fen});
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = reader.readLine()) != null) {
			    System.out.println(s);
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}


	public void close()
	{	
		p.destroy();
	}
}


