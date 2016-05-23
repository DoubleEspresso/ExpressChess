import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class EngineUCI 
{
	private Process p = null;
	private BufferedReader reader = null;
	private BufferedWriter writer = null; 
	Thread listeningThread = null;
	private BoardWindow bw = null;
	Scanner scanner = null;
	public EngineUCI(String exe, String[] options, BoardWindow bw)
	{
		try {
			this.bw = bw;
			p = Runtime.getRuntime().exec(exe, options);
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			scanner = new Scanner(p.getInputStream());
			//startListening();
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void startListening(final String s)
	{
		listeningThread = new Thread( new Runnable()
				{

					@Override
					public void run() {
						listen(s);			
					}				
				});
		listeningThread.start();
	}
	
	public Boolean isReady()
	{		
		try {
			for (int i=0; i<10; ++i)
			{
				if (UCI_CMD("uci") != null) return true;
				Thread.sleep(500);
			}			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String UCI_CMD(String cmd)
	{
		String response = "";
		//Scanner scanner = new Scanner(p.getInputStream());
		System.out.println("sending cmd " + cmd);
		try {
			writer.write(cmd + "\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//scanner.close();
		return response;
	}
	
	public void listen(final String token)
	{
		String line;
		//Scanner scanner = new Scanner(p.getInputStream());
		System.out.println("..starting listen thread.");
		
		while (scanner.hasNextLine()) {
		  line = scanner.nextLine();
		  System.out.println(line);
		  if (line.contains(token)) 
			  { 
			  	if (token == "bestmove") 
			  	{
			  		bw.moveFromEngine(line);
			  		break;
			  	}
			  }
		  //Thread.sleep(30);
		}
		//scanner.close();
	}
	
	public void close()
	{
		//try {
			//listeningThread.join();
			p.destroy();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		};
		
	}
}
