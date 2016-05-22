import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;


public class BoardWindow extends GLWindow
{
	private static List<List<Texture>> wPieceTextures = null; // [piece][texture_id]
	private static List<List<Texture>> bPieceTextures = null; // [piece][texture_id]
	private List<Texture> boardSquares = null;
	
	private static String texDir = "/home/mjg/java-workspace-mars/ExpressChess/graphics/pieces/merida/132";
	private Boolean hasSquares = false;
	private Boolean hasPieces = false;
	private Position position = null;
	private Boolean draggingPiece = false;
	private Texture ActivePiece;
	private Vec2 MousePos = new Vec2(0,0);
	private Vec2 BoardDims = new Vec2(0,0);
	private int fromSq = -1;
	private int toSq = -1;
	private int movingPiece = -1;
	private int movingColor = -1;
	private Composite parent = null;
	
	public BoardWindow(Composite parent) 
	{
		super(parent);
		this.parent = parent;
		position = new Position();
		if (!position.Load(Position.StartFen))
		{
			System.out.println("ERROR: failed to load starting position!");
		}
		position.setStartFen();
		
		loadBoardTexture("/home/mjg/java-workspace-mars/ExpressChess/graphics/boards/wooden-light");
		loadPiecesTexture("/home/mjg/java-workspace-mars/ExpressChess/graphics/pieces/merida/132");		
	}

	public Boolean loadBoardTexture(String directory)
	{		
		if (boardSquares == null) boardSquares = new ArrayList<Texture>();
		else boardSquares.clear();	
		
		// squares
		boardSquares.add(new Texture(directory + "/light.gif"));
		boardSquares.add(new Texture(directory + "/dark.gif"));
		
		hasSquares = true;
		
		return true;
	}
	
	public Boolean loadPiecesTexture(String directory) // points to directory containing fnames 
	{		
		if (wPieceTextures == null) wPieceTextures = new ArrayList<List<Texture>>(); //[piece][texture_id]
		wPieceTextures.clear();
		 
		if (bPieceTextures == null) bPieceTextures = new ArrayList<List<Texture>>(); //[piece][texture_id]
		bPieceTextures.clear();
		
		if (wPieceTextures.isEmpty()) for (int i=0; i<6; ++i) wPieceTextures.add(new ArrayList<Texture>()); // pawns, knights, bishops, rooks, queens, kings;
		if (bPieceTextures.isEmpty()) for (int i=0; i<6; ++i) bPieceTextures.add(new ArrayList<Texture>()); // pawns, knights, bishops, rooks, queens, kings;
		
		
		// white pawns
		List<Integer> wpSquares = position.getPieceSquares(position.WHITE, Position.Piece.PAWN.P());
		
		
		List<Integer> bpSquares = position.getPieceSquares(position.BLACK, Position.Piece.PAWN.P());
		if (wpSquares.size() > 0) for (int j=0; j<wpSquares.size(); ++j) wPieceTextures.get(0).add(new Texture(directory + "/wp.png"));
		if (bpSquares.size() > 0) for (int j=0; j<bpSquares.size(); ++j) bPieceTextures.get(0).add(new Texture(directory + "/bp.png"));
	
		// white knights
		List<Integer> wnSquares = position.getPieceSquares(position.WHITE, Position.Piece.KNIGHT.P());
		List<Integer> bnSquares = position.getPieceSquares(position.BLACK, Position.Piece.KNIGHT.P());
		if (wnSquares.size() > 0) for (int j=0; j<wnSquares.size(); ++j) wPieceTextures.get(1).add(new Texture(directory + "/wn.png"));
		if (bnSquares.size() > 0) for (int j=0; j<bnSquares.size(); ++j) bPieceTextures.get(1).add(new Texture(directory + "/bn.png"));
				
		// white bishops
		List<Integer> wbSquares = position.getPieceSquares(position.WHITE, Position.Piece.BISHOP.P());
		List<Integer> bbSquares = position.getPieceSquares(position.BLACK, Position.Piece.BISHOP.P());
		if (wbSquares.size() > 0) for (int j=0; j<wbSquares.size(); ++j) wPieceTextures.get(2).add(new Texture(directory + "/wb.png"));
		if (bbSquares.size() > 0) for (int j=0; j<bbSquares.size(); ++j) bPieceTextures.get(2).add(new Texture(directory + "/bb.png"));
		
		// white rooks
		List<Integer> wrSquares = position.getPieceSquares(position.WHITE, Position.Piece.ROOK.P());
		List<Integer> brSquares = position.getPieceSquares(position.BLACK, Position.Piece.ROOK.P());
		if (wrSquares.size() > 0) for (int j=0; j<wrSquares.size(); ++j) wPieceTextures.get(3).add(new Texture(directory + "/wr.png"));
		if (brSquares.size() > 0) for (int j=0; j<brSquares.size(); ++j) bPieceTextures.get(3).add(new Texture(directory + "/br.png"));
		
		// white queens
		List<Integer> wqSquares = position.getPieceSquares(position.WHITE, Position.Piece.QUEEN.P());
		List<Integer> bqSquares = position.getPieceSquares(position.BLACK, Position.Piece.QUEEN.P());
		if (wqSquares.size() > 0) for (int j=0; j<wqSquares.size(); ++j) wPieceTextures.get(4).add(new Texture(directory + "/wq.png"));
		if (bqSquares.size() > 0) for (int j=0; j<bqSquares.size(); ++j) bPieceTextures.get(4).add(new Texture(directory + "/bq.png"));
		
		// white kings
		List<Integer> wkSquares = position.getPieceSquares(position.WHITE, Position.Piece.KING.P());
		List<Integer> bkSquares = position.getPieceSquares(position.BLACK, Position.Piece.KING.P());
		if (wkSquares.size() > 0) for (int j=0; j<wkSquares.size(); ++j) wPieceTextures.get(5).add(new Texture(directory + "/wk.png"));
		if (bkSquares.size() > 0) for (int j=0; j<bkSquares.size(); ++j) bPieceTextures.get(5).add(new Texture(directory + "/bk.png"));
		
		hasPieces = true;
		
		return true;
	}
		
	public static void addTexture(int promotedPiece, int s, int color)
	{
		if (color == 0) //white 
		{
			switch(promotedPiece)
			{
				case 1: wPieceTextures.get(1).add(new Texture(texDir + "/wn.png"));
				case 2: wPieceTextures.get(2).add(new Texture(texDir + "/wb.png"));
				case 3: wPieceTextures.get(3).add(new Texture(texDir + "/wr.png"));
				case 4: wPieceTextures.get(4).add(new Texture(texDir + "/wq.png"));
			}	
		}
		else
		{
			switch(promotedPiece)
			{
				case 1: bPieceTextures.get(1).add(new Texture(texDir + "/bn.png"));
				case 2: bPieceTextures.get(2).add(new Texture(texDir + "/bb.png"));
				case 3: bPieceTextures.get(3).add(new Texture(texDir + "/br.png"));
				case 4: bPieceTextures.get(4).add(new Texture(texDir + "/bq.png"));
			}
		}
	}
	
	public void renderSquares(GL gl, int w, int h)
	{
		float dX = (float) w / 8f;
		float dY = (float) h / 8f;
		float oX = (getClientArea().width - w)/2f;
		float oY = (getClientArea().height - h)/2f;
		Texture t;		
		
		glEnable(GL_TEXTURE_2D);
		// loop backward -- java composite has 0,0 defined in upper left corner
		// and y increases going downward 
		for (int r = 0; r < 8; ++r)
		{
			for (int c = 0; c < 8 ; ++c) // starts with A1 square
			{		
				if (r%2==0)
				{
					t = (c%2 == 0 ? boardSquares.get(1) : boardSquares.get(0));									
				}
				else t = (c%2 == 0 ? boardSquares.get(0) : boardSquares.get(1));	
				
				t.Bind();				
				r = 7 - r; // start from "white" row

				glBegin(GL_QUADS);
	        	glTexCoord2f(0, 0); glVertex2d(oX + c*dX, oY+r*dY);
	        	glTexCoord2f(1, 0); glVertex2d(oX + (c+1)*dX, oY + r*dY);
	        	glTexCoord2f(1, 1); glVertex2d(oX + (c+1)*dX, oY + (r+1)*dY);
	        	glTexCoord2f(0, 1); glVertex2d(oX + c*dX, oY + (r+1)*dY);
	        	glEnd();	        		        	
			}		
		}		
		// render pieces after all squares are rendered .. else dragging pieces sometimes renders squares over the dragging piece
		for (int r =0; r <8; ++r)
		{
			for (int c =0; c<8; ++c)
			{
				r = 7-r;
				if (draggedPiece(r, c)) continue;
				renderPiece(dX, dY, r, c);
			}
		}
		renderDraggingPiece(dX, dY); // render dragging piece last, so it render *over* all other textures.
	}
	
	private Boolean draggedPiece(int r, int c)
	{
		if (!draggingPiece) return false;
		int s = 8*(7-r) + c;
		Boolean renderDrag = false;
		if (position.hasPiece(s))
		{			
			Texture t = null; 
			if (position.pieceColorAt(s) == position.WHITE )
			{						
				List<Integer> wsquares = position.getPieceSquares(position.WHITE, position.getPiece(s));
				for (int j=0; j<wsquares.size(); ++j) if( s == wsquares.get(j)) t = wPieceTextures.get(position.getPiece(s)).get(j);
				renderDrag = (t != null && ActivePiece != null && t == ActivePiece && draggingPiece );
			}
			else if (position.pieceColorAt(s) == position.BLACK )
			{
				List<Integer> bsquares = position.getPieceSquares(position.BLACK, position.getPiece(s));
				for (int j=0; j<bsquares.size(); ++j) if( s == bsquares.get(j)) t = bPieceTextures.get(position.getPiece(s)).get(j);
				renderDrag = (t != null && ActivePiece != null && t == ActivePiece && draggingPiece );
			}
		}
		return renderDrag;
	}
	
	private void renderDraggingPiece(double x, double y)
	{
		if (!draggingPiece || ActivePiece == null) return;
		ActivePiece.Bind();
		glEnable(GL_BLEND); // blend to remove ugly piece background
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);		
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2d(MousePos.x-0.5*x, MousePos.y-0.5*y);
		glTexCoord2f(1, 0); glVertex2d(MousePos.x + 0.5*x, MousePos.y - 0.5*y);
		glTexCoord2f(1, 1); glVertex2d(MousePos.x + 0.5*x, MousePos.y + 0.5*y);
		glTexCoord2f(0, 1); glVertex2d(MousePos.x-0.5*x, MousePos.y + 0.5*y);
		glDisable(GL_BLEND);
		glEnd();
	}
	
	private void renderPiece(double x, double y, int r, int c)
	{
		int s = 8*(7-r) + c;
		float oX = (float) ((getClientArea().width - 8*x)/2f);
		float oY = (float) ((getClientArea().height - 8*y)/2f);
		
		if (position.hasPiece(s))
		{
			
			Texture t = null; 
			if (position.pieceColorAt(s) == position.WHITE )
			{			
				
				List<Integer> wsquares = position.getPieceSquares(position.WHITE, position.getPiece(s));
				for (int j=0; j<wsquares.size(); ++j) if( s == wsquares.get(j)) t = wPieceTextures.get(position.getPiece(s)).get(j);
				if (t != null) t.Bind();

			}
			else if (position.pieceColorAt(s) == position.BLACK )
			{
				List<Integer> bsquares = position.getPieceSquares(position.BLACK, position.getPiece(s));
				for (int j=0; j<bsquares.size(); ++j) if( s == bsquares.get(j)) t = bPieceTextures.get(position.getPiece(s)).get(j);
				if (t != null) t.Bind();
			}

			glEnable(GL_BLEND); // blend to remove ugly piece background
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);		
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex2d(c*x+oX, r*y+oY);
			glTexCoord2f(1, 0); glVertex2d((c+1)*x+oX, r*y+oY);
			glTexCoord2f(1, 1); glVertex2d((c+1)*x+oX, (r+1)*y+oY);
			glTexCoord2f(0, 1); glVertex2d(c*x+oX, (r+1)*y+oY);
			glDisable(GL_BLEND);
			glEnd();
		}
	}
	
	@Override
	public void onMouseScroll(MouseEvent e) 
	{
		int idx = position.getDisplayedMoveIdx();
		if (e.count < 0)
		{			
			if ( (idx-1) < 0 ) return;
			position.setPositionFromFenStrings(idx-1, 0);
		}
		else
		{			
			if ((idx+1) > position.maxDisplayedMoveIdx()) return;
			position.setPositionFromFenStrings(idx+1, 0);
		}
		refresh();		
	}

	public void paint(GL gl, int w, int h) 
	{
		if (!hasSquares || !hasPieces) return;
		
		glMatrixMode( GL_PROJECTION );
        glLoadIdentity();               
        glOrtho(0, w, h, 0, 0, 1);

        glMatrixMode(GL_MODELVIEW);
        glViewport( 0, 0, w, h);
		
		glClearColor(0f, 0f, 0f, 0f);
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		
		fixAspectRatio(w,h);		
		renderSquares(gl, (int) BoardDims.x, (int) BoardDims.y);

	}

	private void fixAspectRatio(int w, int h)
	{     	
    	float mind = (float)Math.min(w, h);
    	float nw = (w >= mind ? mind : w);
    	float nh = (h >= mind ? mind : h);

    	BoardDims.x = nw; BoardDims.y = nh;
	}
	
	public Vec2 squareFromMouse(Vec2 v)
	{		
		float oX = (float) ((getClientArea().width - BoardDims.x)/2f);
		float oY = (float) ((getClientArea().height - BoardDims.y)/2f);
		
		//Rectangle r = windowSize();

		float dX = (float) (BoardDims.x / 8f);//(float) r.width / 8f;
		float dY = (float) (BoardDims.y / 8f);//(float) r.height / 8f;
		
		int row = (int) Math.floor( (v.y - oY) / dY);
		int col = (int) Math.floor( (v.x - oX) / dX);
		
		// flip row for display
		row = 7 - row;
		
		//System.out.println("row " + (int)Math.floor(row) + " col " + (int)Math.floor(col));
		return new Vec2(row, col);
	}
	
	@Override
	public void onMouseMove(MouseEvent e) 
	{
		MousePos.x = e.x;
		MousePos.y = e.y;
		if (draggingPiece && ActivePiece != null) refresh();
	}

	@Override
	public void onMouseDown(Event e) 
	{
		Vec2 v = new Vec2(e.x, e.y);
		Vec2 p = squareFromMouse(v);
		int r = (int) p.x; int c = (int) p.y;
		int s = 8*r + c;
		if (position.hasPiece(s))
		{
			fromSq = s;
			if (position.pieceColorAt(s) == position.WHITE )
			{
				movingColor = position.WHITE;
				draggingPiece = true;
				List<Integer> wsquares = position.getPieceSquares(position.WHITE, position.getPiece(s));
				for (int j=0; j<wsquares.size(); ++j) if( s == wsquares.get(j)) { ActivePiece = wPieceTextures.get(position.getPiece(s)).get(j); movingPiece = position.getPiece(s); }
				
			}
			else if (position.pieceColorAt(s) == position.BLACK )
			{
				movingColor = position.BLACK;
				draggingPiece = true;
				List<Integer> wbsquares = position.getPieceSquares(position.BLACK, position.getPiece(s));
				for (int j=0; j<wbsquares.size(); ++j) if( s == wbsquares.get(j)) { ActivePiece = bPieceTextures.get(position.getPiece(s)).get(j); movingPiece = position.getPiece(s); }
				//ActivePiece = whitePieces.get(position.getPiece(s));
			}
				
		}
		else ActivePiece = null;
		
	}

	@Override
	public void onMouseUp(Event e) 
	{
		// need to set from/to, drag piece color, and drag piece typ
		if (draggingPiece)
		{
			Vec2 v = new Vec2(e.x, e.y);
			Vec2 p = squareFromMouse(v);
			int r = (int) p.x; int c = (int) p.y;
			toSq = 8*r + c;
			
			if (position.isLegal(fromSq, toSq, movingPiece, movingColor, true))
			{
				// drop piece..handle all special move types
				position.doMove(fromSq, toSq, movingPiece, movingColor);
				// pawn is now sitting at "to" square
				if (position.isPromotion())
				{										
					popupPromotionWindow(fromSq, toSq, movingPiece, movingColor);
					fromSq = -1;
					toSq = -1;	
					movingColor = -1;
					movingPiece = -1;
					draggingPiece = false;
					ActivePiece = null;
					redraw();
					return;

				}
				// check mate/stalemate
				if (position.isMate(fromSq, toSq, movingPiece, movingColor))
				{
					System.out.println("..game over, mate");					
				}
				
				else if (position.isStaleMate())
				{
					System.out.println("..game over, stalemate");
				}
				
				else if (position.isRepetitionDraw())
				{
					System.out.println("..game over, 3-fold repetition");
				}
				
			}
			
			position.clearMoveData();
			
			fromSq = -1;
			toSq = -1;	
			movingColor = -1;
			movingPiece = -1;
		}
		draggingPiece = false;
		ActivePiece = null;
		redraw();
	}

	private void popupPromotionWindow(final int fromSq, final int toSq, final int movingPiece, final int c)
	{
		String dir = "/home/mjg/java-workspace-mars/ExpressChess/graphics/pieces/merida/132";
			
		final PromotionWindow shell = new PromotionWindow(Display.getDefault(), dir, c); // composite = new Composite(parent, SWT.NONE);	
		shell.getPane().setMoveData(position, fromSq, toSq, movingPiece, c);
		shell.open();
		shell.layout();
		
		parent.setEnabled(false);

		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  event.doit = false;
		    	  if (!shell.getPane().madeSelection())
		    	  {
		    		  position.undoMove(toSq, fromSq, movingPiece, c);
		    	  }

	              parent.setEnabled(true);
	              shell.dispose();
	              redraw();
	            }

		    });

	}
	
	@Override
	public void onMouseDoubleClick(Event e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
