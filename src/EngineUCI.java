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
	public EngineUCI(String exe, String[] options)
	{
		try {
			p = Runtime.getRuntime().exec(exe, options);
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			//startListening();
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void startListening()
	{
		listeningThread = new Thread( new Runnable()
				{

					@Override
					public void run() {
						listen();			
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
		Scanner scanner = new Scanner(p.getInputStream());
		try {
			
			for (int j=0; j<10; ++j)
			{
				System.out.println("sending cmd " + cmd);
				writer.write(cmd + "\n");
				writer.flush();
				//if ((response = reader.readLine()) != null)
				if (scanner.hasNextLine())
				{
					//System.out.println("..got response " + scanner.nextLine());
					return scanner.nextLine();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("..no response after .5sec");
		return response;
	}
	
	public void listen()
	{
		String line;
		try {
			while ((line = reader.readLine()) != null) {
			  System.out.println(line);
			  Thread.sleep(30);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
