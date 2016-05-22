import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class PromotionSelectionWindow extends Composite {

	public PromotionSelectionWindow(Composite parent, int style, String directory) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Canvas canvas = new Canvas(this, SWT.NONE);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
		// display queen
		final Image queen = new Image(Display.getDefault(), directory + "/wq.png" );
		parent.addPaintListener( new PaintListener()
				{
					public void paintControl(PaintEvent e)
					{
						e.gc.drawImage(queen, 0, 0);
					}
				});

		Canvas canvas_1 = new Canvas(this, SWT.NONE);
		canvas_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		final Image rook = new Image(Display.getDefault(), directory + "/wr.png" );
		parent.addPaintListener( new PaintListener()
				{
					public void paintControl(PaintEvent e)
					{
						e.gc.drawImage(rook, 60, 0);
					}
				});
		
		Canvas canvas_2 = new Canvas(this, SWT.NONE);
		canvas_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		final Image bishop = new Image(Display.getDefault(), directory + "/wb.png" );
		parent.addPaintListener( new PaintListener()
				{
					public void paintControl(PaintEvent e)
					{
						e.gc.drawImage(bishop, 0, 60);
					}
				});
		
		
		Canvas canvas_3 = new Canvas(this, SWT.NONE);
		canvas_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		final Image knight = new Image(Display.getDefault(), directory + "/wn.png" );
		parent.addPaintListener( new PaintListener()
				{
					public void paintControl(PaintEvent e)
					{
						e.gc.drawImage(knight, 60, 60);
					}
				});

		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
