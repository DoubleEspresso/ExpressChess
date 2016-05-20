import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;

public abstract class TestWindow extends Composite 
{

    protected int minWidth = 200;
    protected int minHeight = 200;
    private GLCanvas canvas = null;
    private Composite parent = null;
	public TestWindow(Composite parent, int style) 
	{
		super(parent, style);
		
		GLData d = new GLData();
		d.doubleBuffer = true;
		canvas = new GLCanvas(this, style, d);
		this.parent = parent;
		canvas.setBounds(0, 0, parent.getClientArea().width, parent.getClientArea().height);
		canvas.setCurrent();
		initialize();
		
	}

	private void initialize()
	{
        initGL();
        addResizeListener();
       	addMouseWheelListener();
       	addMouseMoveListener();
       	addMouseDownListener();
       	addMouseUpListener();
       	addDoubleClickListener();
       	addPaintListener();
       	addKeyListener();
	}
	
    public void initGL()
    {
    	GL.createCapabilities();
    	
    }
	public void refresh()
	{
		redraw();
	}
	
	
	private void addResizeListener()
	{
		addListener(SWT.Resize, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				resizeWindow();
			}
		});
	}
	
	private void addMouseDownListener()
	{
		canvas.addListener(SWT.MouseDown, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				if (e.button == 1) // left mouse button
				{
					canvas.setCurrent();
					onMouseDown(e);
				}
			}
		});
	}
	
	private void addMouseUpListener()
	{
		canvas.addListener(SWT.MouseUp, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				if (e.button == 1) // left mouse button
				{
					canvas.setCurrent();
					onMouseUp(e);
				}
			}
		});
	}
	
	private void addDoubleClickListener()
	{
		canvas.addListener(SWT.MouseDoubleClick, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				onMouseDoubleClick(e);
			}
		});
	}
	
	private void addMouseMoveListener()
	{		
		canvas.addMouseMoveListener(new MouseMoveListener()
		{			
			public void mouseMove(MouseEvent e)
			{
				canvas.setCurrent();
				onMouseMove(e);
				//refresh();
			}
		});
	}
	
	private void addPaintListener()
	{
		canvas.addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent paintevent)
			{
				renderGLWindow();
			}
		});
	}

	public void renderGLWindow()
	{		
		
		Display.getDefault().asyncExec(new Runnable()
		{			
			public void run()
			{
				if (!isDisposed()) // avoid errors on exit
				{
					
					Rectangle r = windowSize();
					canvas.setCurrent();									
					paint(null, r.width, r.height);					
					canvas.swapBuffers();				
				}
			}			
		});
		
	}
	private void addKeyListener()
	{
		canvas.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				onKeyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				onKeyReleased(e);
			}
		});
	}

	private void resizeWindow()
	{
		Rectangle r = getClientArea();
		if (r.width < minWidth)
		{
			setSize(minWidth, r.height);
			return;
		}
		else if (r.height < minHeight)
		{
			setSize(r.width, minHeight);
			return;
		}
		canvas.setBounds(0, 0, r.width, r.height);
		canvas.setCurrent();
		onResize(r.width, r.height);
		refresh();
	}
	
	public void onResize(int w, int h) 
	{
        glMatrixMode( GL_PROJECTION );
        glClearColor(0f, 0f, 0f, 0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT); // helps to stop flickering on resize        
        glLoadIdentity();               
        glOrtho(0, w, h, 0, 0, 1);        
        glMatrixMode(GL_MODELVIEW);
        glViewport( 0, 0, w, h);        
	}
	
	public Rectangle windowSize()
	{
		return getClientArea();
	}
	
	private void addMouseWheelListener()
	{
		canvas.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseScrolled(MouseEvent e)
			{
				canvas.setCurrent();				
				onMouseScroll(e);
				refresh();				
			}
		});
	}
	
	
	public abstract void onMouseScroll(MouseEvent e);
	public abstract void paint(GL gl, int w, int h);
	public abstract void onMouseMove(MouseEvent e);
	public abstract void onMouseDown(Event e);
	public abstract void onMouseUp(Event e);
	public abstract void onMouseDoubleClick(Event e);
	
	public void onKeyPressed(KeyEvent e) {}
	public void onKeyReleased(KeyEvent e) {}

	
	@Override
	protected void checkSubclass() 
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
