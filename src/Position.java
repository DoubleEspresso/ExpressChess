import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Position {
	private List<List<Integer>> wPieceSquares = null; // [piece][square]
	private List<List<Integer>> bPieceSquares = null;
	private List<List<String>> FenPositions = null;
	private int[] colorOn = null;
	private int[] pieceOn = null;
	private int[] kingSqs = null;
	private int[] pieceDiffs = null;

	public static enum Squares {
		A1(0), B1(1), C1(2), D1(3), E1(4), F1(5), G1(6), H1(7), 
		A2(8), B2(9), C2(10), D2(11), E2(12), F2(13), G2(14), H2(15), 
		A3(16), B3(17), C3(18), D3(19), E3(20), F3(21), G3(22), H3(23), 
		A4(24), B4(25), C4(26), D4(27), E4(28), F4(29), G4(30), H4(31), 
		A5(32), B5(33), C5(34), D5(35), E5(36), F5(37), G5(38), H5(39), 
		A6(40), B6(41), C6(42), D6(43), E6(44), F6(45), G6(46), H6(47), 
		A7(48), B7(49), C7(50), D7(51), E7(52), F7(53), G7(54), H7(55), 
		A8(56), B8(57), C8(58), D8(59), E8(60), F8(61), G8(62), H8(63), SQ_NONE(64);

		private int sq;

		private Squares(int a) {
			sq = a;
		}

		public int S() {
			return sq;
		}
	}

	public static enum Pieces {
		W_PAWN(0), W_KNIGHT(1), W_BISHOP(2), W_ROOK(3), W_QUEEN(4), W_KING(5), B_PAWN(6), B_KNIGHT(7), B_BISHOP(
				8), B_ROOK(9), B_QUEEN(10), B_KING(11);

		private int p;

		private Pieces(int pi) {
			p = pi;
		}

		public int P() {
			return p;
		}
	}

	public static enum Piece {
		PAWN(0), KNIGHT(1), BISHOP(2), ROOK(3), QUEEN(4), KING(5), PIECE_NONE(6);

		private int p;

		private Piece(int pi) {
			p = pi;
		}

		public int P() {
			return p;
		}
	}

	public final String SanPiece = "PNBRQKpnbrqk";
	public final String SanCols = "abcdefgh";

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

	private Boolean gameFinished = false; // for mate/draw global flag
	private int capturedPiece = -1;
	private int promotedPiece = -1;
	private Boolean moveIsEP = false;
	private Boolean moveIsCapture = false;
	private Boolean moveIsPromotion = false;
	private Boolean moveIsPromotionCapture = false;
	private Boolean moveIsCastle = false;
	private int displayedMove = 0;
	
	public static String StartFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	public Position() {
		clear();
	}

	public Position(String fen) {
		if (!Load(fen)) {
			System.out.println("..Warning failed to load position " + fen);
			
		}

	}

	public void setStartFen()
	{
		FenPositions.add(new ArrayList<String>());		
		FenPositions.get(displayedMove).add(toFen());
	}
	
	public String toFen() {
		String fen = "";
		
		for (int r = 7; r>=0; --r)
		{
			int empties = 0;
			for (int c = 0; c < 8; ++c)
			{
				int s = r * 8 + c;
				if (isEmpty(s)) { ++empties; continue; }
				
				if (empties > 0) 
				{
					fen += empties; empties = 0;
				}
				fen += SanPiece.charAt((colorOn[s] == BLACK ? pieceOn[s]+6 : pieceOn[s]));	
			}
			if (empties > 0) 
			{
				fen += empties; 
			}
			if (r > 0) fen += "/";
		}
		
		fen += (stm == WHITE ? " w" : " b");

		// castle rights
		String castleRights = "";
		if ( (crights & W_KS) == W_KS) castleRights += "K";
		if ( (crights & W_QS) == W_QS) castleRights += "Q";
		if ( (crights & B_KS) == B_KS) castleRights += "k";
		if ( (crights & B_QS) == B_QS) castleRights += "q";
		fen += (castleRights == "" ? " -" : " " + castleRights);
		
		// ep-square
		String epSq = "";
		if (EP_SQ != 0)
		{
			epSq += SanCols.charAt(colOf(EP_SQ)) + Integer.toString(rowOf(EP_SQ)+1);
		}
		fen += (epSq == "" ? " -" : " " + epSq);
		
		
		// move50
		
		// half-mvs
		return fen;
	}

	public Boolean Load(String fen) {
		System.out.println("loading fen " + fen);
		clear();
		int s = Squares.A8.S();
		String[] split_fen = fen.split("\\s+");
		if (split_fen.length <= 0)
			return false;

		for (int i = 0; i < split_fen[0].length(); ++i) {
			char c = fen.charAt(i);
			Boolean isDigit = (c >= '0' && c <= '9');
			if (isDigit)
				s += (c - '0');
			else {
				switch (c) {
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
		if (split_fen.length <= 1)
			return true;
		else {
			stm = (split_fen[1].charAt(0) == 'w' ? WHITE : BLACK);
		}
		// castle rights
		if (split_fen.length <= 2)
			return true;
		else {
			for (int i = 0; i < split_fen[2].length(); ++i) {
				char c = split_fen[2].charAt(i);
				switch (c) {
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
		if (split_fen.length <= 3)
			return true;
		else {
			int col = 0;
			int row = 0;
			char c = split_fen[3].charAt(0);
			if (c != '-') {
				for(int j=0; j<SanCols.length(); ++j)
				{
					if (SanCols.charAt(j) == c) col = j;
				}
				c = (split_fen[3].charAt(1));
				row = (c - '0')-1;
				EP_SQ = 8 * row + col;
			} else
				EP_SQ = 0;
			//System.out.println("EP_SQ = " + EP_SQ);
		}
		// the half-moves since last pawn move/capture
		if (split_fen.length <= 4)
			return true;
		else {
			Move50 = (int) Integer.parseInt(split_fen[4]);
		}
		// the move counter
		if (split_fen.length <= 5)
			return true;
		else {
			HalfMvs = (int) Integer.parseInt(split_fen[5]);
		}

		return true;
	}

	public void clear() {
		stm = WHITE;
		crights = 0;
		EP_SQ = 0;
		Move50 = 0;
		HalfMvs = 0;
		displayedMove = 0; // needed?
		
		gameFinished = false; // for mate/draw global flag
		capturedPiece = -1;
		promotedPiece = -1;
		moveIsEP = false;
		moveIsCapture = false;
		moveIsPromotion = false;
		moveIsPromotionCapture = false;
		moveIsCastle = false;		
		
		if (wPieceSquares == null) {
			wPieceSquares = new ArrayList<List<Integer>>();
		}
		wPieceSquares.clear();
		for (int i = 0; i < 6; ++i)
			wPieceSquares.add(new ArrayList<Integer>()); // pawn, knight,
															// bishop, rook
															// queen, king

		if (bPieceSquares == null) {
			bPieceSquares = new ArrayList<List<Integer>>();
		}
		bPieceSquares.clear();
		for (int i = 0; i < 6; ++i)
			bPieceSquares.add(new ArrayList<Integer>()); // pawn, knight,
															// bishop, rook
															// queen, king

		if (colorOn == null)
			colorOn = new int[64];
		for (int i = 0; i < 64; ++i)
			colorOn[i] = COLOR_NONE;

		if (pieceOn == null)
			pieceOn = new int[64];
		for (int i = 0; i < 64; ++i)
			pieceOn[i] = Piece.PIECE_NONE.P();

		if (kingSqs == null)
			kingSqs = new int[2];
		for (int i = 0; i < 2; ++i)
			kingSqs[i] = Squares.SQ_NONE.S();

		if (pieceDiffs == null)
			pieceDiffs = new int[6];
		for (int i = 0; i < 6; ++i)
			pieceDiffs[i] = 0;
		
		if (FenPositions == null) 
		{
			FenPositions = new ArrayList<List<String>>();
			FenPositions.clear();
		}
	}

	public Boolean hasPiece(int s) {
		return pieceOn[s] != Piece.PIECE_NONE.P();
	}

	public List<Integer> getPieceSquares(int c, int p) {
		// System.out.println("getPIeceSquares c = " + c + " p = " + p + " size
		// = " + wPieceSquares.get(p).size());
		return (c == WHITE ? wPieceSquares.get(p) : bPieceSquares.get(p));
	}

	public int getPiece(int s) {
		return pieceOn[s];
	}

	public int pieceColorAt(int s) {
		if (!onBoard(s))
			return Piece.PIECE_NONE.P();
		return colorOn[s];
	}

	private void set_piece(char c, int s) {
		for (int p = Pieces.W_PAWN.P(); p <= Pieces.B_KING.P(); ++p) {
			if (c == SanPiece.charAt(p)) {
				int color = (p < 6 ? WHITE : BLACK);

				// adjust colored piece type to bare piece type (pawn, knight
				// etc.)
				int piece = (p > Pieces.W_KING.P() ? p - 6 : p);

				if (color == WHITE) {

					wPieceSquares.get(piece).add(s);
					// System.out.println("add w " + c + " at sq " + s + " sz "
					// + wPieceSquares.get(piece).size());
				} else {
					// System.out.println("add b " + c + " at sq " + s);
					bPieceSquares.get(piece).add(s);
				}

				colorOn[s] = color;
				pieceOn[s] = piece;

				if (p == Pieces.W_KING.P() || p == Pieces.B_KING.P()) {
					kingSqs[color] = s;
				} else {
					if (color == WHITE)
						pieceDiffs[piece]++;
					else
						pieceDiffs[piece]--;
				}

			}
		}
	}

	public Boolean isLegal(int from, int to, int piece, int color) {
		if (color != stm)
			return false;

		if (isCastle(from, to, piece, color)) 
		{
			if (!isLegalCastle(from, to, piece, color)) return false;
			clearAllCastleRights(color);

			return true;			
		} else if (!isPseudoLegal(from, to, piece, color))
			return false;

		// check pins/checks etc.
		doMove(from, to, piece, color);

		// store move state -- kingInCheck calls pseudoLegalMv routines, which
		// update the movestate
		// which is not wanted, since we need to undo the move...
		int tmp_capturedPiece = capturedPiece;
		int tmp_promotedPiece = promotedPiece;
		Boolean tmp_moveIsCapture = moveIsCapture;
		Boolean tmp_moveIsPromotion = moveIsPromotion;
		Boolean tmp_moveIsPromotionCapture = moveIsPromotionCapture;
		Boolean tmp_moveIsCastle = moveIsCastle;
		Boolean tmp_moveIsEP = moveIsEP;

		Boolean inCheck = kingInCheck(color);

		// restore move state
		capturedPiece = tmp_capturedPiece;
		promotedPiece = tmp_promotedPiece;
		moveIsCapture = tmp_moveIsCapture;
		moveIsPromotion = tmp_moveIsPromotion;
		moveIsPromotionCapture = tmp_moveIsPromotionCapture;
		moveIsCastle = tmp_moveIsCastle;
		moveIsEP = tmp_moveIsEP;

		if (inCheck) {
			//System.out.println("..illegal move, in check!");
			undoMove(to, from, piece, color);
			return false;
		}
			
		// move is legal -- update position data 
		if (pieceOn[to] == Piece.KING.P()) clearCastleRights(color); // in case king has moved
		if (pieceOn[to] == Piece.ROOK.P() && from == Squares.A1.S() && color == WHITE) clearCastleRights(W_QS);
		if (pieceOn[to] == Piece.ROOK.P() && from == Squares.H1.S() && color == WHITE) clearCastleRights(W_KS);
		if (pieceOn[to] == Piece.ROOK.P() && from == Squares.A8.S() && color == BLACK) clearCastleRights(B_QS);
		if (pieceOn[to] == Piece.ROOK.P() && from == Squares.H8.S() && color == BLACK) clearCastleRights(B_KS);
		
		// update EP square
		EP_SQ = 0;
		if (color == WHITE)
		{
			if (piece == Piece.PAWN.P() && to - from == 16)
			{
				EP_SQ = to - 8;
			}
		}
		else
		{
			if (piece == Piece.PAWN.P() && from - to == 16)
			{
				EP_SQ = to + 8;
			}	
		}
		// save fen state
		++displayedMove;
		FenPositions.add(new ArrayList<String>());		
		FenPositions.get(displayedMove).add(toFen());
		
		undoMove(to, from, piece, color);

		return true;
	}
	
	public String getPosition(int idx, int mvidx)
	{
		if (idx < 0 || idx > FenPositions.size()-1) return "";
		return FenPositions.get(idx).get(mvidx);
	}
	
	public int getDisplayedMoveIdx() { return displayedMove; }
	
	public void setDisplayedMoveIdx(int idx) { displayedMove = idx; }
	
	public int maxDisplayedMoveIdx() { return FenPositions.size()-1; }
	
	public Boolean setPositionFromFenStrings(int idx, int mvidx)
	{
		if (idx < 0 || idx > FenPositions.size()-1) return false;
		if (mvidx < 0 || mvidx > FenPositions.get(idx).size()-1) return false;
		if (!Load(FenPositions.get(idx).get(mvidx))) return false;
		displayedMove = idx;
		return true;
	}
	

	public void clearMoveData() {
		capturedPiece = -1;
		promotedPiece = -1;
		moveIsEP = false;
		moveIsCapture = false;
		moveIsPromotion = false;
		moveIsPromotionCapture = false;
		moveIsCastle = false;
	}

	public Boolean isPseudoLegal(int from, int to, int piece, int color) {
		if (pieceColorAt(to) == color)
			return false;
		switch (piece) {
		case 0: // pawn
			return pseudoLegalPawnMove(from, to, color);
		case 1: // knight
			return pseudoLegalKnightMove(from, to, color);
		case 2: // bishop
			return pseudoLegalBishopMove(from, to, color);
		case 3: // rook
			return pseudoLegalRookMove(from, to, color);
		case 4: // queen
			return pseudoLegalQueenMove(from, to, color);
		case 5: // king
			return pseudoLegalKingMove(from, to, color);
		}
		return false;
	}

	
    public Boolean isCastle(int from, int to, int piece, int color) {
		if (piece != Piece.KING.P())
		{
			//System.out.println("isCastle piece != king");
			return false;
		}
		if (getPiece(from) != Piece.KING.P())
		{
			//System.out.println("isCastle wrong from sq");
			return false;
		}
		if (from != (color == WHITE ? Squares.E1.S() : Squares.E8.S()))
		{
			//System.out.println("isCastle wrong from sq2");
			return false;
		}

		if (color == WHITE) {
			if (to != Squares.G1.S() && to != Squares.C1.S())
			{
				//System.out.println("isCastle wrong to sq " + to + " for to sq.." + Squares.G1.S());
				return false;
			}
		} else {
			if (to != Squares.G8.S() && to != Squares.C8.S())
				return false;
		}
		return true;
	}

	
	public Boolean isLegalCastle(int from, int to, int piece, int color) {
		if (!hasCastleRights(to, color))
			return false;

		int sqLeft1 = (color == WHITE ? Squares.D1.S() : Squares.D8.S());
		int sqLeft2 = (color == WHITE ? Squares.C1.S() : Squares.C8.S());
		int sqRight1 = (color == WHITE ? Squares.F1.S() : Squares.F8.S());
		int sqRight2 = (color == WHITE ? Squares.G1.S() : Squares.G8.S());

		if (to == sqRight2) {
			if (!isEmpty(sqRight1) && !isEmpty(sqRight2))
			{
				//System.out.println("isLegalCastle rsqs2 not empty");
				return false;
			}
			if (isAttacked(sqRight1, color) || isAttacked(sqRight2, color))
			{
				//System.out.println("isLegalCastle rsqs1 not empty");
				return false;
			}
			if (kingInCheck(color))
			{
				//System.out.println("isLegalCastle king in check .. ");
				return false;
			}
		} else {
			if (!isEmpty(sqLeft1) && !isEmpty(sqLeft2))
				return false;
			if (isAttacked(sqRight1, color) || isAttacked(sqRight2, color))
				return false;
			if (kingInCheck(color))
				return false;
		}
		moveIsCastle = true;
		return true;
	}

	public Boolean hasCastleRights(int to, int color) {
		int ks = (color == WHITE ? (crights & 1) : (crights & 4));
		int qs = (color == WHITE ? (crights & 2) : (crights & 8));
		if (color == WHITE) {
			if (to == Squares.G1.S() && (ks == 1))
			{
				//System.out.println("hasCastleRights has ks crs");
				return true;
			}
			else if (to == Squares.C1.S() && (qs == 2))
			{
				//System.out.println("hasCastleRights has qs crs");
				return true;
			}
		} else {
			if (to == Squares.G8.S() && (ks == 4))
				return true;
			else if (to == Squares.C8.S() && (qs == 8))
				return true;
		}
		//System.out.println("hasCastleRights no correct crs");
		return false;
	}

	public void clearAllCastleRights(int color)
	{
		//System.out.println("all cr before: " + crights);
		if (color == WHITE)
		{
			crights = (crights & 12);
		}
		else crights = (crights & 3);
		//System.out.println("all cr after: " + crights);
	}
	
	public void clearCastleRights(int side)
	{
		//System.out.println("cr before: side " + side + " "  + crights);
		crights = (crights & (~side));
		//System.out.println("all cr after: " + crights);
	}
	
	int rowOf(int from) {
		return (from >> 3);
	}

	int colOf(int from) {
		return (from & 7);
	}

	Boolean onBoard(int to) {
		return (to >= 0 && to <= 63);
	}

	Boolean isEmpty(int s) {
		return pieceOn[s] == Piece.PIECE_NONE.P();
	}

	Boolean enemyOn(int s, int color) {
		return (colorOn[s] == color && pieceOn[s] != Piece.PIECE_NONE.P());
	}

	int colDiff(int s1, int s2) {
		return Math.abs(colOf(s1) - colOf(s2));
	}

	int rowDiff(int s1, int s2) {
		return Math.abs(rowOf(s1) - rowOf(s2));
	}

	Boolean onCol(int s1, int s2) {
		return colDiff(s1, s2) == 0;
	}

	Boolean onRow(int s1, int s2) {
		return rowDiff(s1, s2) == 0;
	}

	Boolean onDiag(int s1, int s2) {
		return colDiff(s1, s2) == rowDiff(s1, s2);
	}

	private Boolean pseudoLegalPawnMove(int from, int to, int color) {
		int enemy = (color == WHITE ? BLACK : WHITE);
		int forward1 = (color == WHITE ? 8 : -8);
		int forward2 = (color == WHITE ? 16 : -16);
		int capRight = (color == WHITE ? 7 : -7);
		int capLeft = (color == WHITE ? 9 : -9);
		
		Boolean on4 = (color == WHITE ? (rowOf(from) == 4) : (rowOf(from) == 3));
		Boolean on7 = (color == WHITE ? (rowOf(from) == 6) : (rowOf(from) == 1));
		Boolean on2 = (color == WHITE ? (rowOf(from) == 1) : (rowOf(from) == 6));
		
		if (on2) {
			if ((from + forward1) == to && isEmpty(to))
				return true;
			else if ((from + forward2) == to && isEmpty(to))
			{
				return true;
			}
			else if ((from + capRight) == to && enemyOn(to, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsCapture = true;
				return true;
			} else if ((from + capLeft) == to && enemyOn(to, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsCapture = true;
				return true;
			}
		} else if (on4 && EP_SQ == to) // EP move?
		{
			int epTo = (color == WHITE ? to-8 : to+8);
			if ((from + capRight) == to && enemyOn(epTo, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsEP = true;
				return true;
			} else if ((from + capLeft) == to && enemyOn(epTo, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsEP = true;
				return true;
			}
		}		
		else if (on7) {
			if ((from + forward1) == to && isEmpty(to)) {
				moveIsPromotion = true;
				return true;
			} else if ((from + capRight) == to && enemyOn(to, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsPromotionCapture = true;
				return true;
			} else if ((from + capLeft) == to && enemyOn(to, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsPromotionCapture = true;
				return true;
			}
		} else {
			if ((from + forward1) == to && isEmpty(to))
				return true;
			else if ((from + capRight) == to && enemyOn(to, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsCapture = true;
				return true;
			} else if ((from + capLeft) == to && enemyOn(to, enemy) && colDiff(from, to) == 1 && onBoard(to)) {
				moveIsCapture = true;
				return true;
			}
		}

		// TODO: handle promotions
		return false;
	}

	private Boolean pseudoLegalKnightMove(int from, int to, int color) {
		int[] deltas = { 16 - 1, 16 + 1, 2 + 8, 2 - 8, -16 + 1, -16 - 1, -2 - 8, -2 + 8 };
		int enemy = (color == WHITE ? BLACK : WHITE);

		for (int j = 0; j < deltas.length; ++j) {
			int t = deltas[j] + from;
			if (t != to)
				continue;
			else if (!onBoard(t) || (!isEmpty(to) && !enemyOn(to, enemy)))
				return false;
			else if (onBoard(t) && isEmpty(to) && !enemyOn(to, enemy))
				return true;
			else if (onBoard(t) && !isEmpty(to) && enemyOn(to, enemy)) {
				moveIsCapture = true;
				return true;
			}
		}
		return false;
	}

	private Boolean pseudoLegalBishopMove(int from, int to, int color) {

		int[] deltas = { 7, 9, -7, -9 };
		int enemy = (color == WHITE ? BLACK : WHITE);
		for (int j = 0; j < deltas.length; ++j) {
			int d = deltas[j];
			int c = 1;
			int t = from + d * c;
			while (onBoard(t) && onDiag(from, t) && (isEmpty(t) || enemyOn(t, enemy))) {
				if (!isEmpty(t) && t != to)
					break;
				else if (isEmpty(t) && t == to) {
					return true;
				} else if (enemyOn(t, enemy) && t == to) {
					moveIsCapture = true;
					return true;
				}
				t = from + d * (++c);
			}
		}

		return false;
	}

	private Boolean pseudoLegalRookMove(int from, int to, int color) {
		int[] deltas = { 1, -1, 8, -8 };
		int enemy = (color == WHITE ? BLACK : WHITE);
		for (int j = 0; j < deltas.length; ++j) {
			int d = deltas[j];
			int c = 1;
			int t = from + d * c;
			while (onBoard(t) && (onRow(from, t) || onCol(from, to)) && (isEmpty(t) || enemyOn(t, enemy))) {
				if (!isEmpty(t) && t != to)
					break;
				else if (t == to && isEmpty(t))
					return true;
				else if (enemyOn(t, enemy) && t == to) {
					moveIsCapture = true;
					return true;
				}
				// else if ((isEmpty(t) || !isEmpty(to)) && t == to) return
				// true;
				t = from + d * (++c);
			}
		}
		return false;
	}

	private Boolean pseudoLegalQueenMove(int from, int to, int color) {
		if (onDiag(from, to))
			return pseudoLegalBishopMove(from, to, color);
		else if (onRow(from, to) || onCol(from, to))
			return pseudoLegalRookMove(from, to, color);
		return false;
	}

	private Boolean pseudoLegalKingMove(int from, int to, int color) {
		int[] deltas = { 1, -1, 7, 9, 8, -8, -7, -9 };
		int enemy = (color == WHITE ? BLACK : WHITE);
		for (int j = 0; j < deltas.length; ++j) {
			int d = deltas[j];
			int t = from + d;
			if (onBoard(t) && (isEmpty(t) || enemyOn(t, enemy))) {
				if (!isEmpty(t) && !enemyOn(t, enemy))
					return false;
				else if (isEmpty(t) && t == to)
					return true;
				else if (enemyOn(t, enemy) && t == to) {
					moveIsCapture = true;
					return true;
				}
			}
		}
		return false;
	}

	public Boolean doMove(int from, int to, int piece, int color) 
	{
		if (color == WHITE) 
		{
			List<Integer> wsquares = getPieceSquares(WHITE, getPiece(from));
			for (int j = 0; j < wsquares.size(); ++j)
				if (from == wsquares.get(j)) 
				{
					wPieceSquares.get(piece).remove(j); // remove from sq
					wPieceSquares.get(piece).add(to); // add to sq

				}
		} 
		else 
		{
			List<Integer> bsquares = getPieceSquares(BLACK, getPiece(from));
			for (int j = 0; j < bsquares.size(); ++j)
				if (from == bsquares.get(j)) 
				{
					bPieceSquares.get(piece).remove(j);
					bPieceSquares.get(piece).add(to);
				}
		}

		if (moveIsCapture) 
		{
			capturedPiece = pieceOn[to];
			if (color == WHITE) // remove black piece
			{
				List<Integer> bsquares = getPieceSquares(BLACK, getPiece(to));
				for (int j = 0; j < bsquares.size(); ++j)
					if (to == bsquares.get(j)) 
					{
						bPieceSquares.get(capturedPiece).remove(j); // remove piece @ to sq
					}
			}
			else
			{
				List<Integer> wsquares = getPieceSquares(WHITE, getPiece(to));
				for (int j = 0; j < wsquares.size(); ++j)
					if (to == wsquares.get(j)) 
					{
						wPieceSquares.get(capturedPiece).remove(j); // remove piece @ to sq
					}
			}
		}
		else if (moveIsEP)
		{
			int epTo = (color == WHITE ? to-8 : to+8);
			capturedPiece = pieceOn[epTo];
			if (color == WHITE) // remove black piece
			{
				List<Integer> bsquares = getPieceSquares(BLACK, getPiece(epTo));
				for (int j = 0; j < bsquares.size(); ++j)
					if (epTo == bsquares.get(j)) 
					{
						bPieceSquares.get(capturedPiece).remove(j); // remove piece @ to sq
					}
			}
			else
			{
				List<Integer> wsquares = getPieceSquares(WHITE, getPiece(epTo));
				for (int j = 0; j < wsquares.size(); ++j)
					if (epTo == wsquares.get(j)) 
					{
						wPieceSquares.get(capturedPiece).remove(j); // remove piece @ to sq
					}
			}
			pieceOn[epTo] = Piece.PIECE_NONE.P();
			colorOn[epTo] = COLOR_NONE;
		}
		else if (moveIsCastle)
		{
			if (color == WHITE) // remove black piece
			{
				int rookFrom = ( to == Squares.G1.S() ? Squares.H1.S() : Squares.A1.S());
				int rookto = ( to == Squares.G1.S() ? Squares.F1.S() : Squares.D1.S());
				List<Integer> wsquares = getPieceSquares(WHITE, getPiece(rookFrom));
				for (int j = 0; j < wsquares.size(); ++j)
					if (rookFrom == wsquares.get(j)) 
					{
						wPieceSquares.get(Piece.ROOK.P()).remove(j); // remove from sq
						wPieceSquares.get(Piece.ROOK.P()).add(rookto); // add to sq
					}				
				pieceOn[rookFrom] = Piece.PIECE_NONE.P();
				pieceOn[rookto] = Piece.ROOK.P();
				colorOn[rookFrom] = COLOR_NONE;
				colorOn[rookto] = color;
				//clearCastleRights(color);
			}
			else
			{
				int rookFrom = ( to == Squares.G8.S() ? Squares.H8.S() : Squares.A8.S());
				int rookto = ( to == Squares.G8.S() ? Squares.F8.S() : Squares.D8.S());
				List<Integer> bsquares = getPieceSquares(BLACK, getPiece(rookFrom));
				for (int j = 0; j < bsquares.size(); ++j)
					if (rookFrom == bsquares.get(j)) 
					{
						bPieceSquares.get(Piece.ROOK.P()).remove(j); // remove from sq
						bPieceSquares.get(Piece.ROOK.P()).add(rookto); // add to sq
					}				
				pieceOn[rookFrom] = Piece.PIECE_NONE.P();
				pieceOn[rookto] = Piece.ROOK.P();
				colorOn[rookFrom] = COLOR_NONE;
				colorOn[rookto] = color;
				//clearCastleRights(color);
			}

		}
				
		pieceOn[from] = Piece.PIECE_NONE.P();
		pieceOn[to] = piece;

		colorOn[from] = COLOR_NONE;
		colorOn[to] = color;

		stm = (stm == WHITE ? BLACK : WHITE);
	
		if (moveIsCastle)
		{
			// save fen state (only place it works.....:/)
			++displayedMove;
			FenPositions.add(new ArrayList<String>());		
			FenPositions.get(displayedMove).add(toFen());
		}
		
		return true;
	}

	public Boolean undoMove(int from, int to, int piece, int color) {

		if (color == WHITE) {
			List<Integer> wsquares = getPieceSquares(WHITE, getPiece(from));
			for (int j = 0; j < wsquares.size(); ++j)
				if (from == wsquares.get(j)) {
					wPieceSquares.get(piece).remove(j);
					wPieceSquares.get(piece).add(to);

				}
		} else {
			List<Integer> bsquares = getPieceSquares(BLACK, getPiece(from));
			for (int j = 0; j < bsquares.size(); ++j)
				if (from == bsquares.get(j)) {
					bPieceSquares.get(piece).remove(j);
					bPieceSquares.get(piece).add(to);
				}
		}
		if (moveIsCapture) {
			if (color == WHITE) // add back white piece (do move updated stm)
			{
				bPieceSquares.get(capturedPiece).add(from);
			} else {
				wPieceSquares.get(capturedPiece).add(from);
			}
		}
		else if (moveIsEP)
		{
			int epTo = (color == WHITE ? from-8 : from+8);
			if (color == WHITE) // remove black piece
			{
				bPieceSquares.get(capturedPiece).add(epTo);
			}
			else
			{
				wPieceSquares.get(capturedPiece).add(epTo);
			}
			pieceOn[epTo] = Piece.PAWN.P();
			colorOn[epTo] = (color == WHITE ? BLACK : WHITE);
		}
		else if (moveIsCastle)
		{
			if (color == WHITE) // remove black piece
			{
				int rookFrom = ( from == Squares.G1.S() ? Squares.F1.S() : Squares.D1.S());
				int rookto = ( from == Squares.F1.S() ? Squares.H1.S() : Squares.A1.S());
				List<Integer> wsquares = getPieceSquares(WHITE, getPiece(rookFrom));
				for (int j = 0; j < wsquares.size(); ++j)
					if (rookFrom == wsquares.get(j)) 
					{
						wPieceSquares.get(Piece.ROOK.P()).remove(j); // remove from sq
						wPieceSquares.get(Piece.ROOK.P()).add(rookto); // add to sq
					}				
				pieceOn[rookFrom] = Piece.PIECE_NONE.P();
				pieceOn[rookto] = Piece.ROOK.P();
				colorOn[rookFrom] = COLOR_NONE;
				colorOn[rookto] = color;
				//clearCastleRights(color);
			}
			else
			{
				int rookFrom = ( to == Squares.G8.S() ? Squares.F8.S() : Squares.D8.S());
				int rookto = ( to == Squares.F8.S() ? Squares.H8.S() : Squares.A8.S());
				List<Integer> bsquares = getPieceSquares(BLACK, getPiece(rookFrom));
				for (int j = 0; j < bsquares.size(); ++j)
					if (rookFrom == bsquares.get(j)) 
					{
						bPieceSquares.get(Piece.ROOK.P()).remove(j); // remove from sq
						bPieceSquares.get(Piece.ROOK.P()).add(rookto); // add to sq
					}				
				pieceOn[rookFrom] = Piece.PIECE_NONE.P();
				pieceOn[rookto] = Piece.ROOK.P();
				colorOn[rookFrom] = COLOR_NONE;
				colorOn[rookto] = color;
				//clearCastleRights(color);
			}
		}
		pieceOn[from] = (moveIsCapture ? capturedPiece : Piece.PIECE_NONE.P());
		pieceOn[to] = piece;

		colorOn[from] = (moveIsCapture ? stm : COLOR_NONE);
		colorOn[to] = color;

		stm = (stm == WHITE ? BLACK : WHITE);

		return true;
	}

	// c denotes the color of the king "in check" .. it is only called after a
	// "do-move"
	// and do-move updates the current side to move, so if white just made a
	// move, stm=black, and we want
	// to check if white's king is in check (so c == white).
	public Boolean kingInCheck(int c) {
		int ks = getPieceSquares(c, Piece.KING.P()).get(0); // should only ever
															// be 1 king
		int enemy = (c == WHITE ? BLACK : WHITE);

		// pawn checks
		List<Integer> psquares = getPieceSquares(enemy, Piece.PAWN.P());
		for (int j = 0; j < psquares.size(); ++j) {
			int to = psquares.get(j);
			if (pseudoLegalPawnMove(ks, to, c))
				return true;
		}

		// knight checks
		List<Integer> nsquares = getPieceSquares(enemy, Piece.KNIGHT.P());
		for (int j = 0; j < nsquares.size(); ++j) {
			int to = nsquares.get(j);
			if (pseudoLegalKnightMove(ks, to, c))
				return true;
		}

		// bishop checks .. return a list of "to" squares being the enemy
		// bishops
		List<Integer> bsquares = getPieceSquares(enemy, Piece.BISHOP.P());
		for (int j = 0; j < bsquares.size(); ++j) {
			int to = bsquares.get(j);
			if (pseudoLegalBishopMove(ks, to, c))
				return true;
		}

		// rook checks
		List<Integer> rsquares = getPieceSquares(enemy, Piece.ROOK.P());
		for (int j = 0; j < rsquares.size(); ++j) {
			int to = rsquares.get(j);
			if (pseudoLegalRookMove(ks, to, c))
				return true;
		}

		// queen checks
		List<Integer> qsquares = getPieceSquares(enemy, Piece.QUEEN.P());
		for (int j = 0; j < qsquares.size(); ++j) {
			int to = qsquares.get(j);
			if (pseudoLegalQueenMove(ks, to, c))
				return true;
		}

		return false;
	}

	public Boolean isAttacked(int from, int c) {
		int enemy = (c == WHITE ? BLACK : WHITE);

		// pawn checks
		List<Integer> psquares = getPieceSquares(enemy, Piece.PAWN.P());
		for (int j = 0; j < psquares.size(); ++j) {
			int to = psquares.get(j);
			if (pseudoLegalPawnMove(from, to, c))
				return true;
		}

		// knight checks
		List<Integer> nsquares = getPieceSquares(enemy, Piece.KNIGHT.P());
		for (int j = 0; j < nsquares.size(); ++j) {
			int to = nsquares.get(j);
			if (pseudoLegalKnightMove(from, to, c))
				return true;
		}

		// bishop checks .. return a list of "to" squares being the enemy
		// bishops
		List<Integer> bsquares = getPieceSquares(enemy, Piece.BISHOP.P());
		for (int j = 0; j < bsquares.size(); ++j) {
			int to = bsquares.get(j);
			if (pseudoLegalBishopMove(from, to, c))
				return true;
		}

		// rook checks
		List<Integer> rsquares = getPieceSquares(enemy, Piece.ROOK.P());
		for (int j = 0; j < rsquares.size(); ++j) {
			int to = rsquares.get(j);
			if (pseudoLegalRookMove(from, to, c))
				return true;
		}

		// queen checks
		List<Integer> qsquares = getPieceSquares(enemy, Piece.QUEEN.P());
		for (int j = 0; j < qsquares.size(); ++j) {
			int to = qsquares.get(j);
			if (pseudoLegalQueenMove(from, to, c))
				return true;
		}

		return false;
	}
}
