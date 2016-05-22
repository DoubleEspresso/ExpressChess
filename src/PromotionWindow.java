import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.opengl.GL;

public class PromotionWindow extends Shell 
{
	private Composite composite = null;
	private ImagePane canvas = null;

	
	public PromotionWindow(final Display display, String dir, int c) 
	{
		super();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout(gridLayout);
				
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		composite.setLayoutData(gd_composite);
		composite.setSize(260, 245);
		
		setResizeListener(composite);

		canvas = new ImagePane(composite, dir, c, this);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createContents();
		
		canvas.refresh();
	}
	
	public ImagePane getPane()
	{
		return canvas;
	}
	

	
	protected void setResizeListener(final Composite c)
	{
		addListener(SWT.Resize, new Listener()
		{
			@Override 
			public void handleEvent(Event e)
			{
				c.setSize(getBounds().width, getBounds().height);
			}
		});
	}
	
	protected void createContents() 
	{
		setText("promotion..");
		setSize(260, 245);
	}
	
	@Override
	protected void checkSubclass() {
	    // Disable the check that prevents subclassing of SWT components
	}

}

class ImagePane extends GLWindow
{

	private Position position = null;
	private int color = -1;
	private int movingPiece = -1;
	private int from = -1;
	private int to = -1;
	
	private String[] wTextures = { "/wq.png", "/wr.png", "/wb.png", "/wn.png" };
	private String[] bTextures = { "/bq.png", "/br.png", "/bb.png", "/bn.png" };
	private List<Texture> Textures = null;
	
	Composite parent = null;
	Vec2 Dims = new Vec2(0,0);
	private int piece = -1;
	PromotionWindow pw = null;
	
	public ImagePane(Composite parent, String dir, int c, PromotionWindow pw) 
	{
		super(parent);	
		this.parent = parent;
		this.pw = pw;
		
		LoadTextures(dir, c);
	}
	
	public ImagePane(Composite parent) 
	{
		super(parent);
		this.parent = parent;
	}

	public void setMoveData(Position p, int from, int to, int movingpiece, int color)
	{
		this.position = p; this.from = from; this.to = to; this.movingPiece = movingpiece; this.color = color;
	}
	
	public Boolean madeSelection()
	{
		return piece != -1;
	}
	
	public void makePromotionSelection(int piece)
	{
		position.doPromotionMove(from, to, 4-piece, color); // already checked the move is legal in BoardWindow
		pw.close();
	}
	
	@Override
	public void onMouseScroll(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	private void LoadTextures(String dir, int c)
	{
		String [] pieces = (c == 0 ? wTextures : bTextures);
		if (Textures == null) Textures = new ArrayList<Texture>(); 
		Textures.clear();
		for (int j=0; j<pieces.length; ++j) 
		{
			Textures.add(new Texture(dir + pieces[j])); //q, r, b, n
			//System.out.println("load promotion texture: " + dir + pieces[j]);
		}
	}
	
	private void fixAspectRatio(int w, int h)
	{     	
    	float mind = (float)Math.min(w, h);
    	float nw = (w >= mind ? mind : w);
    	float nh = (h >= mind ? mind : h);

    	Dims.x = nw; Dims.y = nh;
	}
	
	@Override
	public void paint(GL gl, int w, int h) {
		glMatrixMode( GL_PROJECTION );
        glLoadIdentity();               
        glOrtho(0, w, h, 0, 0, 1);

        glMatrixMode(GL_MODELVIEW);
        glViewport( 0, 0, w, h);
		
		glClearColor(0.92f, 0.92f, 0.92f, 0f);
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		
		fixAspectRatio(w, h);
		RenderPieces(Dims.x, Dims.y);		
	}
	
	private void RenderPieces(double w, double h)
	{
		// render textures
		glEnable(GL_TEXTURE_2D);
		for (int j=0; j<Textures.size(); ++j)
		{
			Textures.get(j).Bind();
			
			// compute offset for proper placement
			double pad = 6;
			double pX = -1; double pY = -1;
			double oX = (float) ((parent.getClientArea().width - w)/2f);
			double oY = (float) ((parent.getClientArea().height - h)/2f)-pad;
			double dX = w/2f; double dY = h/2f;
			switch (j)
			{
				case 0:  pX = oX; pY = oY; break; // queen
				case 1:  pX = oX + dX; pY = oY; break; // rook
				case 2:  pX = oX; pY = oY+dY; break; // bishop
				case 3:  pX = oX + dX; pY = oY+dY; break; // knight
			}

			glEnable(GL_BLEND); // blend to remove ugly piece background
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);		
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex2d(pX, pY);
			glTexCoord2f(1, 0); glVertex2d(pX+dX, pY);
			glTexCoord2f(1, 1); glVertex2d(pX+dX, pY+dY);
			glTexCoord2f(0, 1); glVertex2d(pX, pY+dY);
			glDisable(GL_BLEND);
			glEnd();
		}
	}

	public Vec2 selectionFromMouse(Vec2 v)
	{		
		float oX = (float) ((parent.getClientArea().width - Dims.x)/2f);
		float oY = (float) ((parent.getClientArea().height - Dims.y)/2f);
		
		//Rectangle r = windowSize();

		float dX = (float) (Dims.x / 2f);
		float dY = (float) (Dims.y / 2f);
		
		int row = (int) Math.floor( (v.y - oY) / dY);
		int col = (int) Math.floor( (v.x - oX) / dX);

		return new Vec2(row, col);
	}
	
	private Boolean selectionValid(int piece)
	{
		return (piece >= 0 && piece <= 3);
	}
	
	@Override
	public void onMouseMove(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDown(Event e) 
	{
		Vec2 p = selectionFromMouse(new Vec2(e.x, e.y));
		piece = (int) (p.x*2+p.y);		
	}

	@Override
	public void onMouseUp(Event e) 
	{
		if (selectionValid(piece))
		{
			makePromotionSelection(piece);
		}
		else piece = -1;
	}

	@Override
	public void onMouseDoubleClick(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyPressed(KeyEvent e) 
	{
		if (e.keyCode == SWT.ESC)
		{
			piece = -1;
			pw.close();
		}
		else if (e.character == 'q')
		{
			piece = 0;
			makePromotionSelection(piece);
		}
		else if (e.character == 'r')
		{
			piece = 1;
			makePromotionSelection(piece);
			
		}
		else if (e.character == 'b')
		{
			piece = 2;
			makePromotionSelection(piece);
		}
		else if (e.character == 'n')
		{
			piece = 3;
			makePromotionSelection(piece);
		}
		piece = -1;
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
