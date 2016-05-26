
public abstract class UCIEvents extends EngineEvents 
{
	BoardWindow bw = null;
	
	public UCIEvents(BoardWindow bw)
	{
		this.bw = bw;
		init();
	}
	
	private void init()
	{
		addBestMoveListener();
		addThinkLineListener();
		// others?
	}
	
	private void addBestMoveListener()
	{
		addEngineListener(new EngineEventListener()
		{			
			@Override
			public void EngineEventOccurred(EngineEvent e) {
				if (e.get() == BestMove) onBestMoveEvent(e.getData());
			}
		});
			
	}
		
	private void addThinkLineListener()
	{
		addEngineListener( new EngineEventListener()
		{			
			@Override
			public void EngineEventOccurred(EngineEvent e) {
				if (e.get() == ThinkLine) onThinkLineEvent(e.getData());
			}
		});
			
	}

	public abstract void onBestMoveEvent(String bestMove);
	public abstract void onThinkLineEvent(String bestMove);

	
	@Override
	void onEngineEvent(EngineEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
