import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class ExpressChess extends Shell 
{

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

	public ExpressChess(final Display display) 
	{
		super(display, SWT.SHELL_TRIM);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout(gridLayout);
		
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
		
		//Canvas canvas = new Canvas(this, SWT.NONE);
		//canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//BoardWindow canvas = new BoardWindow(this);
		//GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		//canvas.setLayoutData(gridData);
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 539;
		composite.setLayoutData(gd_composite);
		
//		Canvas canvas = new Canvas(composite, SWT.NONE);
//		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final BoardWindow canvas = new BoardWindow(composite);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		canvas.refresh();
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		List list = new List(composite_1, SWT.BORDER);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_list.widthHint = 126;
		list.setLayoutData(gd_list);
		
		createContents();

		getShell().addListener(SWT.Traverse, new Listener() 
		{
			public void handleEvent(Event event) 
			{
				switch (event.detail) 
				{
				case SWT.TRAVERSE_ESCAPE:
					canvas.engineHandle().close();
					getShell().close();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
				default:
					break;
				}
			}
		});

		getShell().addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				canvas.release();
				//canvas.alive = false;
				//canvas.engineHandle().signal();
				//canvas.stopListening();
				//canvas.engineHandle().close();
			}
		});
	}
	
	protected void createContents() 
	{
		setText("Express Chess");
		setSize(1075, 883);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
