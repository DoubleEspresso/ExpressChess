import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.*;
import org.lwjgl.opengl.GL;


public abstract class GLWindow extends GLCanvas 
{
    private static GLData gd1;
    protected Rectangle rec;
    protected int minWidth = 600;
    protected int minHeight = 480;
    protected Composite parent;
    
    static 
    {
        gd1 = new GLData();
        gd1.doubleBuffer = true;
    }

    public GLWindow(Composite parent) 
    {
        super(parent, SWT.NO_BACKGROUND, gd1); 
        this.parent = parent;
		setBounds(0, 0, parent.getClientArea().width, parent.getClientArea().height);
		setCurrent();

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
    	GL.setCapabilities(GL.createCapabilities());    	
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
		addListener(SWT.MouseDown, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				if (e.button == 1) // left mouse button
				{
					setCurrent();
					onMouseDown(e);
				}
			}
		});
	}
	
	private void addMouseUpListener()
	{
		addListener(SWT.MouseUp, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				if (e.button == 1) // left mouse button
				{
					setCurrent();
					onMouseUp(e);
				}
			}
		});
	}
	
	private void addDoubleClickListener()
	{
		addListener(SWT.MouseDoubleClick, new Listener()
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
		addMouseMoveListener(new MouseMoveListener()
		{			
			public void mouseMove(MouseEvent e)
			{
				setCurrent();
				onMouseMove(e);
			}
		});
	}
	
	private void addPaintListener()
	{
		addPaintListener(new PaintListener()
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
					setCurrent();									
					paint(null, r.width, r.height);	
					swapBuffers();
				}
			}			
		});
		
	}
	private void addKeyListener()
	{
		addKeyListener(new KeyListener()
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
		setCurrent();
		onResize(r.width, r.height);
		refresh();
	}
	
	public void onResize(int w, int h) 
	{
//        glMatrixMode( GL_PROJECTION );
//        glClearColor(0f, 0f, 0f, 0f);
//        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT); // helps to stop flickering on resize        
//        glLoadIdentity();               
//        glOrtho(0, w, h, 0, 0, 1);        
//        glMatrixMode(GL_MODELVIEW);
//        glViewport( 0, 0, w, h); 
			refresh();
	}
	
	public Rectangle windowSize()
	{
		return getClientArea();
	}
	
	private void addMouseWheelListener()
	{
		addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseScrolled(MouseEvent e)
			{
				setCurrent();				
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
	public abstract void onKeyPressed(KeyEvent e);
	public abstract void onKeyReleased(KeyEvent e);
}

