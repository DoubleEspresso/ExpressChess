import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Position 
{
	private List<List<Integer>> wPieceSquares = null; //[piece][square]
	private List<List<Integer>> bPieceSquares = null;
	private int[] colorOn = null;
	private int[] pieceOn = null;
	private int[] kingSqs = null;
	private int[] pieceDiffs = null;
	
    public static enum Squares
    {
    	A1(0), B1(1), C1(2), D1(3), E1(4), F1(5), G1(6), H1(7),
    	A2(8), B2(9), C2(10), D2(11), E2(12), F2(13), G2(14), H2(15),
    	A3(16), B3(17), C3(18), D3(19), E3(20), F3(21), G3(22), H3(23),
    	A4(24), B4(25), C4(26), D4(27), E4(28), F4(29), G4(30), H4(31),
    	A5(32), B5(33), C5(34), D5(35), E5(36), F5(37), G5(38), H5(39),
    	A6(40), B6(41), C6(42), D6(43), E6(44), F6(45), G6(46), H6(47),
    	A7(48), B7(49), C7(50), D7(51), E7(52), F7(53), G7(54), H7(55),
    	A8(56), B8(57), C8(58), D8(59), E8(60), F8(61), G8(62), H8(63), SQ_NONE(64);
    	
        private int sq;

        private Squares(int a) 
        {
        	sq = a;
        }
        public int S() 
        {
          return sq;
        }
    }
    
    public static enum Pieces
    {
    	W_PAWN(0), W_KNIGHT(1), W_BISHOP(2), W_ROOK(3), W_QUEEN(4), W_KING(5),
    	B_PAWN(6), B_KNIGHT(7), B_BISHOP(8), B_ROOK(9), B_QUEEN(10), B_KING(11);
    	
        private int p;

        private Pieces(int pi) 
        {
        	p = pi;
        }
        public int P() 
        {
          return p;
        }
    }
    
    public static enum Piece
    {
    	PAWN(0), KNIGHT(1), BISHOP(2), ROOK(3), QUEEN(4), KING(5), PIECE_NONE(6);
    	
        private int p;

        private Piece(int pi) 
        {
        	p = pi;
        }
        public int P() 
        {
          return p;
        }
    }
    
    public final String SanPiece = "PNBRQKpnbrqk";
    
    public final int W_KS = 1;
    public final int W_QS = 2;
    public final int B_KS = 4;
    public final int B_QS = 8;
    
    public final int WHITE = 0;
    public final int BLACK = 1;
    public final int COLOR_NONE = 2;
    public int stm = WHITE;
    public int crights = 0;
    
    public int EP_SQ = 0;
    public int Move50 = 0;
    public int HalfMvs = 0;
    
    
    
    public static String StartFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public Position()
    {
    	clear();
    }
    
    public Position(String fen)
    {
    	clear();
    	
    	if (!Load(fen))
    	{
    		System.out.println("..Warning failed to load position " + fen);
    	}
    }

    public Boolean Load(String fen)
    {
    	int s = Squares.A8.S();
    	String[] split_fen = fen.split("\\s+");
    	if (split_fen.length <= 0) return false;
    	
    	for (int i = 0 ; i < split_fen[0].length(); ++i)
    	{
    		char c = fen.charAt(i);
    		Boolean isDigit = (c >= '0' && c <= '9');
    		if (isDigit) s += (c - '0'); 
    		else 
    		{
    			switch (c)
    			{
    			case '/':
    				s -= 16;
    				break;
    			default:
    				set_piece(c, s);
    				++s;
    				break;
    			}
    		}
    	}    		
    	// side to move
    	if (split_fen.length <= 1) return true;
    	else
    	{
    		stm = (split_fen[1].charAt(0) == 'w' ? WHITE : BLACK);    			
    	}    		
    	// castle rights
    	if (split_fen.length <= 2) return true;
    	else
    	{
    	   	for (int i = 0 ; i < split_fen[2].length(); ++i)
    	    {
    	    	char c = split_fen[2].charAt(i);
    	    	switch (c)
    	    	{
    	    	case 'K':
    	    		crights |= W_KS;
    	    		break;
    	    	case 'Q':
    	    		crights |= W_QS;
    	    		break;
    	    	case 'k':
    	    		crights |= B_KS;
    	    		break;
    	    	case 'q':
    	    		crights |= B_QS;
    	    		break;
    	    	}
    	    }
    	}
    	// en-passant square
    	if (split_fen.length <= 3) return true;
    	else
    	{
    		int col = 0; int row = 0;
    		char c = split_fen[3].charAt(0);
    		if (c != '-')
    		{
    			if (c >= 'a' && c <= 'h') col = (int)(c - 'a');
    			if (c == '3' || c == '6') row = (int)(c-  '1');
    			EP_SQ = 8*row + col;
    		}
    		else EP_SQ = 0;
    	}    	
    	// the half-moves since last pawn move/capture
    	if (split_fen.length <= 4) return true;
    	else
    	{
    		Move50 = (int) Integer.parseInt(split_fen[4]);
    	}
    	// the move counter
    	if (split_fen.length <= 5) return true;
    	else
    	{
    		HalfMvs = (int) Integer.parseInt(split_fen[5]);
    	}
    	
    	return true;
    }
    
    public void clear()
    {
    	stm = WHITE; crights = 0;
    	EP_SQ = 0; Move50 = 0; HalfMvs = 0;
    	
    	if (wPieceSquares == null)
    	{
    		wPieceSquares = new ArrayList<List<Integer>>();
    	}
    	wPieceSquares.clear();
    	for (int i=0; i<6; ++i) wPieceSquares.add(new ArrayList<Integer>()); // pawn, knight, bishop, rook queen, king
    	
    	if (bPieceSquares == null)
    	{
    		bPieceSquares = new ArrayList<List<Integer>>();
    	}
    	bPieceSquares.clear();
     	for (int i=0; i<6; ++i) bPieceSquares.add(new ArrayList<Integer>()); // pawn, knight, bishop, rook queen, king

     	if (colorOn == null) colorOn = new int[64];
     	for (int i=0; i<64; ++i) colorOn[i] = COLOR_NONE;
     	
     	if (pieceOn == null) pieceOn = new int[64];
     	for (int i=0; i<64; ++i) pieceOn[i] = Piece.PIECE_NONE.P();
     	
     	if (kingSqs == null) kingSqs = new int[2];
     	for (int i=0; i<2; ++i) kingSqs[i] = Squares.SQ_NONE.S();
     	
     	if (pieceDiffs == null ) pieceDiffs = new int [6];
     	for (int i=0; i<6; ++i) pieceDiffs[i] = 0;
    }
    
    public Boolean hasPiece(int s)
    {
    	return pieceOn[s] != Piece.PIECE_NONE.P();
    }
    
    public List<Integer> getPieceSquares(int c, int p)
    {
    	//System.out.println("getPIeceSquares c = " + c + " p = " + p + " size = " + wPieceSquares.get(p).size());
    	return (c == WHITE ? wPieceSquares.get(p) : bPieceSquares.get(p));
    }
    
    public int getPiece(int s)
    {
    	return pieceOn[s];
    }
    
    public int pieceColorAt(int s)
    {
    	if (!onBoard(s)) return Piece.PIECE_NONE.P();
    	return colorOn[s];
    }
    
    private void set_piece(char c, int s)
    {
    	for (int p = Pieces.W_PAWN.P(); p <= Pieces.B_KING.P(); ++p)
    	{
    		if (c == SanPiece.charAt(p))
    		{
    			int color = (p < 6 ? WHITE : BLACK);
    			
    			// adjust colored piece type to bare piece type (pawn, knight etc.)
    			int piece = (p > Pieces.W_KING.P() ? p - 6 : p);
    			
    			if (color == WHITE)
    			{
    				
    				wPieceSquares.get(piece).add(s);
    				//System.out.println("add w " + c + " at sq " + s + " sz " + wPieceSquares.get(piece).size());
    			}
    			else
    			{
    				//System.out.println("add b " + c + " at sq " + s);
    				bPieceSquares.get(piece).add(s);
    			}
    			
    			colorOn[s] = color;
    			pieceOn[s] = piece;
    			
    			
    			if (p == Pieces.W_KING.P() || p == Pieces.B_KING.P() )
    			{
    				kingSqs[color] = s;
    			}
    			else
    			{
    				if (color == WHITE) pieceDiffs[piece]++;
    				else pieceDiffs[piece]--;
    			}
    			
    		}
    	}
    }

    public Boolean isLegal(int from, int to, int piece, int color)
    {
    	if (!isPseudoLegal(from, to, piece, color)) return false;
    	
    	// check pins/checks etc.
    	return true;
    }
    
    public Boolean isPseudoLegal(int from, int to, int piece, int color)
    {
    	if (pieceColorAt(to) == color) return false;
    	switch (piece)
    	{
    		case 0: // pawn
    			return pseudoLegalPawnMove(from, to, color);
    		case 1: // knight
    			return pseudoLegalKnightMove(from, to, color);  
    		case 2: // bishop
    			return pseudoLegalBishopMove(from, to, color);
    	}
    	return false;
    }

    int rowOf(int from)
    {
    	return (from >> 3);
    }
    
    int colOf(int from)
    {	
    	return (from & 7);
    }
    
    Boolean onBoard(int to)
    {
    	return (to >= 0 && to <= 63);
    }
    
    Boolean isEmpty(int s)
    {
    	return pieceOn[s] == Piece.PIECE_NONE.P();
    }
    
    Boolean enemyOn(int s, int color)
    {
    	return (colorOn[s] == color && pieceOn[s] != Piece.PIECE_NONE.P());
    }
    
    int colDiff(int s1, int s2)
    {
    	return Math.abs(colOf(s1)-colOf(s2));
    }
    
    int rowDiff(int s1, int s2)
    {
    	return Math.abs(rowOf(s1) - rowOf(s2));
    }
    
    Boolean onCol(int s1, int s2)
    {
    	return colDiff(s1, s2) == 0;
    }
    
    Boolean onRow(int s1, int s2)
    {
    	return rowDiff(s1, s2) == 0;
    }
    
    Boolean onDiag(int s1, int s2)
    {
    	return colDiff(s1, s2) == rowDiff(s1, s2);
    }
    
    private Boolean pseudoLegalPawnMove(int from, int to, int color)
    {
    	int enemy = (color == WHITE ? BLACK : WHITE);
    	int forward1 = (color == WHITE ? 8 : -8);
    	int forward2 = (color == WHITE ? 16 : -16);
    	int capRight = (color == WHITE ? 7 : -7);
    	int capLeft = (color == WHITE ? 9 : -9);
    	
    	Boolean on7 = (color == WHITE ? (rowOf(from) == 7) : (rowOf(from) == 1));
    	Boolean on2 = (color == WHITE ? (rowOf(from) == 1) : (rowOf(from) == 7));
    	if (on2)
    	{
    		if ( (from + forward1) == to && isEmpty(to)) return true; 
    		else if ( (from + forward2) == to && isEmpty(to)) return true;
    		else if ( (from + capRight) == to && enemyOn(to, enemy) && colDiff(from,to)==1 && onBoard(to)) return true;
    		else if ( (from + capLeft) == to && enemyOn(to, enemy) && colDiff(from,to)==1 && onBoard(to)) return true;
    	}
    	else 
    	{
    		if ( (from + forward1) == to && isEmpty(to)) return true;
    		else if ( (from + capRight) == to && enemyOn(to, enemy) && colDiff(from,to)==1 && onBoard(to)) return true;
    		else if ( (from + capLeft) == to && enemyOn(to, enemy) && colDiff(from,to)==1 && onBoard(to)) return true;
    	}

    	// TODO: handle ep and promotions
    	return false;
    }

    private Boolean pseudoLegalKnightMove(int from, int to, int color)
    {
    	int[] deltas = { 16-1, 16+1, 2+8, 2-8, -16+1, -16-1, -2-8, -2+8 };
    	int enemy = (color == WHITE ? BLACK : WHITE);
    	
    	for (int j=0; j<deltas.length; ++j)
    	{
    		int t = deltas[j] + from;
    		if (t != to) continue;
    		else if ( !onBoard(t) || (!isEmpty(to) && !enemyOn(to, enemy)) ) return false;
    		else if (onBoard(t) && isEmpty(to) && !enemyOn(to, enemy)) return true;
    		else if (onBoard(t) && !isEmpty(to) && enemyOn(to, enemy)) return true;
    	}
    	return false;
    }

    private Boolean pseudoLegalBishopMove(int from, int to, int color)
    {
    	
    	int[] deltas = {7, 9, -7, -9};
    	int enemy = (color == WHITE ? BLACK : WHITE);
    	for(int j=0; j<deltas.length; ++j)
    	{
    		int d = deltas[j];
    		int c = 1;
    		int t = from + d * c;
    		while (onBoard(t) && onDiag(from, t) && (isEmpty(t) || enemyOn(t, enemy)))
    		{
    			if (!isEmpty(t) && t != to) break;
    			else if ((isEmpty(t) || !isEmpty(to)) && t == to) return true;
    			//else if (!isEmpty(t) && t == to) return true;    			
    			t = from + d * (++c);
    		}
    	}
    	
    	return false;
    }

    private Boolean pseudoLegalRookMove(int from, int to, int color)
    {
    	return false;
    }
 
    private Boolean pseudoLegalQueenMove(int from, int to, int color)
    {
    	return false;
    }
    
    private Boolean pseudoLegalKingMove(int from, int to, int color)
    {
    	return false;
    }
    
    public Boolean doMove(int from, int to, int piece, int color)
    {   	
		if (color == WHITE)
		{
			List<Integer> wsquares = getPieceSquares(WHITE, getPiece(from));
			for (int j=0; j<wsquares.size(); ++j) 
				if( from == wsquares.get(j)) 
				{ 
					wPieceSquares.get(piece).remove(j);
					wPieceSquares.get(piece).add(to);

				}			
		}
		else
		{
			List<Integer> bsquares = getPieceSquares(BLACK, getPiece(from));
			for (int j=0; j<bsquares.size(); ++j) 
				if( from == bsquares.get(j)) 
				{ 
					bPieceSquares.get(piece).remove(j);
					bPieceSquares.get(piece).add(to);
				}
		}
		
    	pieceOn[from] = Piece.PIECE_NONE.P();
    	pieceOn[to] = piece;
    	
    	colorOn[from] = COLOR_NONE;
    	colorOn[to] = color;
    	
    	// handle captures /etc.
    	return true;
    }
}
