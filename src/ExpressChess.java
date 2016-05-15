import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.wb.swt.SWTResourceManager;
//import org.eclipse.wb.swt.SWTResourceManager;

public class ExpressChess extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) 
	{
		try 
		{
			Display display = Display.getDefault();
			ExpressChess shell = new ExpressChess(display);
			shell.open();
			shell.layout();
			
			while (!shell.isDisposed()) 
			{
				if (!display.readAndDispatch()) 
				{
					display.sleep();
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public ExpressChess(final Display display) 
	{
		super(display, SWT.SHELL_TRIM);
		
		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);
		
		MenuItem mntmNewItem = new MenuItem(menu, SWT.NONE);
		mntmNewItem.setText("File");
		
		MenuItem mntmNewItem_1 = new MenuItem(menu, SWT.NONE);
		mntmNewItem_1.setText("Engine");
		
		MenuItem mntmNewItem_2 = new MenuItem(menu, SWT.NONE);
		mntmNewItem_2.setText("Game");
		
		MenuItem mntmNewItem_3 = new MenuItem(menu, SWT.NONE);
		mntmNewItem_3.setText("Database");
		
		TabFolder tabFolder = new TabFolder(this, SWT.BORDER);
		tabFolder.setBounds(377, 10, 303, 405);
		
		TabItem tbtmGame = new TabItem(tabFolder, SWT.NONE);
		tbtmGame.setText("Game");
		
		SashForm sashForm = new SashForm(tabFolder, SWT.VERTICAL);
		sashForm.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//sashForm.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		sashForm.setToolTipText("PGN notation");
		tbtmGame.setControl(sashForm);
		
		TabItem tbtmNewItem_2 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_2.setText("Book");
		
		TabFolder tabFolder_1 = new TabFolder(this, SWT.NONE);
		tabFolder_1.setBounds(10, 421, 669, 87);
		
		TabItem tbtmNewItem_3 = new TabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_3.setText("Engine Output");
		
		TabItem tbtmNewItem_4 = new TabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_4.setText("Engine Log");
		
		/*the chess clocks*/
		
		
		GLData data_1 = new GLData();
		data_1.doubleBuffer = true;
		final GLCanvas canvas_1 = new GLCanvas(getShell(), SWT.BORDER | SWT.NO_REDRAW_RESIZE, data_1);
		FormData fd_canvas_1 = new FormData();
		fd_canvas_1.top = new FormAttachment(0, 38);
		fd_canvas_1.left = new FormAttachment(0, 107);
		fd_canvas_1.bottom = new FormAttachment(0, 244);
		canvas_1.setLayoutData(fd_canvas_1);
		canvas_1.setCurrent();
		GL.createCapabilities();
		canvas_1.setBounds(10, 10, 361, 38);
		createContents();		
		
		/*the chess board*/
		GLData data = new GLData();
		data.doubleBuffer = true;
		final BoardWindow canvas = new BoardWindow(getShell());
				
				//(getShell(), SWT.BORDER | SWT.NO_REDRAW_RESIZE, data);

		canvas.setBounds(10, 51, 361, 361);
		createContents();

		getShell().addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					getShell().close();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
				default:
				    break;
				}
			}
		});

		

	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Express Chess");
		setSize(695, 583);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
