import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

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
		

		/*the chess board*/
		final BoardWindow canvas = new BoardWindow(getShell());
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.verticalSpan = 2;
		canvas.setLayoutData(gridData);
		createContents();

		getShell().addListener(SWT.Traverse, new Listener() 
		{
			public void handleEvent(Event event) 
			{
				switch (event.detail) 
				{
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
	
	protected void createContents() 
	{
		setText("Express Chess");
		setSize(695, 583);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
