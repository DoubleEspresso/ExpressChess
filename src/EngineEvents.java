import java.util.EventListener;
import java.util.EventObject;
import javax.swing.event.EventListenerList;

public abstract class EngineEvents {

	// types
	public static int BestMove = 1;
	public static int ThinkLine = 2;
	
	protected EventListenerList listenerList = new EventListenerList();

	public synchronized void addEngineListener(EngineEventListener listener) {
		listenerList.add(EngineEventListener.class, listener);
	}

	public synchronized void removeEngineListener(EngineEventListener listener) {
		listenerList.remove(EngineEventListener.class, listener);
	}
	
	abstract void onEngineEvent(EngineEvent e);
	
	public void fireEngineEvent(EngineEvent e)
	{
		Object[] listeners = listenerList.getListenerList();
		for (int i=0; i<listeners.length; i += 2)
		{
			if (listeners[i] == EngineEventListener.class)
			{
				((EngineEventListener) listeners[i+1]).EngineEventOccurred(e); 
			}
		}
	}
}

// event definitions for standard chess engine
// events sent to the UI
class EventType
{
	int BEST_MOVE = 1;
	int THINK_INFO = 2;
	// has to be others ... ?
	
	int etype = -1;
	public EventType(int t)
	{
		etype = t;
	}
	
	public int type()
	{
		return etype;
	}
}

@SuppressWarnings("serial")
class EngineEvent extends EventObject
{
	private EventType type;
	private String engineData = null;
	
	public EngineEvent(Object source, int t) {
		super(source);
		type = new EventType(t);
	}
	
	public void setData(String s) 
	{
		engineData = s;
	}
	
	public String getData()
	{
		return engineData;
	}
	
	public int get()
	{
		return type.type();
	}

}	

interface EngineEventListener extends EventListener {
	public void EngineEventOccurred(EngineEvent e);
}

