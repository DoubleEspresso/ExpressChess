#include "pgnio.h"

pgn_io::pgn_io(char * filename) : ofile(0), ifile(0), data(0)
{
  if (filename)
    {
      ifile = new std::ifstream();
      ifile->open(filename, std::ifstream::in);
    }
  data = new pgn_data();
}

pgn_io::~pgn_io()
{
  if (ofile) { delete ofile; ofile = 0; }
  if (ifile) { ifile->close(); delete ifile; ifile = 0; }
  if (data)  { delete data; data = 0; }
}

bool pgn_io::parse(Board& b)
{
  if (!ifile) return false;
  if (!ifile->is_open()) 
    {
      printf("..ERROR: failed to open pgn file, check filename.\n");
      return false;
    }
  
  std::string line; BoardData pd;
  while(std::getline(*ifile, line))
    {
      // parse line
      if (line.find("[") != std::string::npos || line.find("]") != std::string::npos) continue; // parse game tag if [ is first char.. ?
      else if (line.size() > 0 && line != "\n") 
	{
	  //printf("..found line: %s\n", line.c_str());
	  if (!parse_moves(b, pd, line)) return false;
	}      
    }
  printf("\n--- FINAL POSITION --- \n");
  b.print();
  
  return true;
}

bool pgn_io::parse_moves(Board& b, BoardData& pd, std::string& line)
{
  std::stringstream ss(line);
  std::string token;
  bool comment = false;
  int move_nb = 0; // conventionally starts with 1.
  while(ss >> std::skipws >> token )
    {
      if (token.find("{") != std::string::npos)
	{
	  comment = true;
	}
      else if (token.find("}") != std::string::npos)
	{
	  comment = false;
	  continue;
	}
      if (comment) continue;

      // check for move number
      if ( token.find(".") != std::string::npos)
	{
	  if (std::isdigit(token[0])) continue;
	}
      else 
	{
	  // strip move of all notations, checks/mates, conver to lowercase
	  pgn_strip(token);
	  U16 m = san_to_move(b, token);
	  if (m != 0) 
	    {
	      b.do_move(pd, m);
	    }
	  else 
	    {
	      printf("...ERROR parsing %s \n", token.c_str());
	      b.print();
	      return false;
	    }
	}
    }
  return true;
}

// note: returns 0 on error!
U16 pgn_io::san_to_move(Board& b, std::string& s)
{
  // moving backward through SAN string..
  int len = s.size();
  int i = s.size()-1;
  int to = -1; 

  // check castle move
  if (s == "O-O" || s == "O-O-O")
    {
      if (s == "O-O") to = (b.whos_move() == WHITE ? G1 : G8) ;
      else to = (b.whos_move() == WHITE ? C1 : C8);
      return find_move(b, to, int(KING));
    }
  else to = to_square(s); // todo: catch shorthands like ed for exd4 etc.
 
  // normal pawn moves (a4 etc. which are len 2)
  if (len <= 2 && to > 0)
    {
      //printf("..dbg in find move %s to = %d\n",s.c_str(), to);
      return find_move(b, to, int(PAWN));
    }
  
  // either piece move like ra4 or pawn capture like axb4 etc.
  i -= 2;
  char c = s[i];
  
  // is it x ? 
  if (tolower(c) == 'x') { i -= 1; c = s[i]; } // skip x's

  // if i >= 1, move includes additional info about the from
  // square (either row or col), e.g. raxb3 or r4xb3
  int row = -1; int col = -1;
  if (i >= 1) 
    {      
      //printf(" .. multiple from sqs for %s\n ", s.c_str());
      if (isdigit(s[i])) row = int(s[i]-'1');
      else col = int (s[i] - 'a');
      i -= 1;
    }

  // piece
  int piece = -1;

  if (i >= 0)
    {
      piece = parse_piece(s[i]);      
      //printf("..piece = %d for move %s\n", piece, s.c_str());
    }
  if (piece < 0) 
    {
      printf("..ERROR : invalid piece parsed from PGN move!\n");
      return 0;
    }
  else if (piece == 0)
    {
      //printf("..pawn capture %s\n", s.c_str());
      col = int(s[0]-'a'); // in case of pawn captures, we need to know the col (from sq)
    }

  // return move
  if (row != -1) return find_move_row(b, row, to, piece);
  else if (col != -1 ) return find_move_col(b, col, to, piece);
  else return find_move(b, to, piece);

  return 0;
}

U16 pgn_io::find_move(Board& b, int to, int piece)
{

  std::vector<U16> candidates;
  for (MoveGenerator mvs(b); !mvs.end(); ++mvs)
    {
      U16 m = mvs.move();     
      int p = b.piece_on(get_from(m));
      if (get_to(mvs.move()) == to && (piece == p)) candidates.push_back(m);
    }  

  return (candidates.size() == 1 ? candidates[0] : 0);  
}

U16 pgn_io::find_move_row(Board& b, int row, int to, int piece)
{
  std::vector<U16> candidates;
  for (MoveGenerator mvs(b); !mvs.end(); ++mvs)
    {
      U16 m = mvs.move();
      int f = get_from(m);
      int p = b.piece_on(f);
      if (get_to(mvs.move()) == to && (piece == p) && (ROW(f) == row)) candidates.push_back(m);
    }  
  return (candidates.size() == 1 ? candidates[0] : 0);  
}

U16 pgn_io::find_move_col(Board& b, int col, int to, int piece)
{
  std::vector<U16> candidates;
  for (MoveGenerator mvs(b); !mvs.end(); ++mvs)
    {
      U16 m = mvs.move();
      int f = get_from(m);
      int p = b.piece_on(f);
      if (get_to(mvs.move()) == to && (piece == p) && (COL(f) == col)) candidates.push_back(m);
    }  
  return (candidates.size() == 1 ? candidates[0] : 0);  
}

int pgn_io::to_square(std::string& s)
{
  int len = s.size();
  std::string tostr = s.substr(len-2, len);
  int to = -1;
  for (int j=0; j<64; ++j) 
    {
      if (SanSquares[j] == tostr)
	{
	  to = j; break;
	}
    }
  return to;
}

int pgn_io::parse_piece(char& c)
{
  if (c == 'o' || c == '0') return -1; // castle move

  int i=0; // san piece array starts at 7 for uppercase pieces (non pawn) 
  while(SanPiece[i] && i < 7)
    {
      if (c == SanPiece[i])
	{
	  return i; // piece type between 0-6;
	}
      ++i;
    }
  return 0; // pawn
}

void pgn_io::pgn_strip(std::string& move)
{
  std::string result = "";
  for (int j=0; j<move.size(); ++j)
    {
      if (move[j] == '!' || move[j] == '?' || move[j] == '+' || move[j] == '#') continue;
      //result += tolower(move[j]); // not safe to convert to lowercase (bxa4 and Bxa4 for example)
      result += move[j];
    }
  move = result;
}
