import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class EngineListener implements Runnable
{
	private Process p = null;
	private BufferedReader reader = null;
	private BufferedWriter writer = null; 
	Thread listeningThread = null;
	Scanner scanner = null;
	public Object hasMove = new Object();
	public String bestMoveString = null;
	private UCIEngineHandler uciEngineHandler = null;
	
	public EngineListener(UCIEngineHandler uciEngineHandler, String exe, String[] options)
	{
		try {	
			p = Runtime.getRuntime().exec(exe, options);
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			scanner = new Scanner(p.getInputStream());
			this.uciEngineHandler = uciEngineHandler;
			if (!startEngine())
			{
				System.out.println("WARNING: UCI engine failed to respond, may not have loaded");
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private Boolean startEngine() {
		try {
			for (int i = 0; i < 10; ++i) {
				if (sendCommand("uci") != null)
					return true;
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String sendCommand(String cmd) {
		String response = "";
		System.out.println("sending cmd " + cmd);
		try {
			writer.write(cmd + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private synchronized void receivedBestMove(String s)
	{
		EngineEvent e = new EngineEvent(new Object(), UCIEvents.BestMove);
		e.setData(s);
		uciEngineHandler.fireEngineEvent(e);
	}
	
	private synchronized void receivedThinkLine(String s)
	{
		EngineEvent e = new EngineEvent(new Object(), UCIEvents.ThinkLine);
		e.setData(s);
		uciEngineHandler.fireEngineEvent(e);
	}
	
	// listening thread responsible for firing all relevant engine events
	public void listen() {
		String line;
		while (scanner.hasNextLine()) {
			
			line = scanner.nextLine();
			
			if (line.contains("bestmove")) {
				receivedBestMove(line);
			}
			else if (line.contains("info"))
			{
				receivedThinkLine(line);
			}
		}
	}

	
	@Override
	public void run() 
	{
		listen();
	}

	public void close()
	{	
		p.destroy();
	}
}

