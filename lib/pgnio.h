#ifndef HEDWIG_PGNIO_H
#define HEDWIG_PGNIO_H

#include <string.h>
#include <vector>
#include <iostream>
#include <fstream>

#include "board.h"
#include "move.h"

struct pgn_data
{
  pgn_data() : event(0), site(0), date(0), round(0), white(0), black(0), result(0) 
  {
  }
  ~pgn_data()
  {
    if (event) { delete event; event = 0; }
    if (site) { delete site; site = 0; }
    if (date) { delete date; date = 0; }
    if (round) { delete round; round = 0; }
    if (white) { delete white; white = 0; }
    if (black) { delete black; black = 0; }
    if (result) { delete result; result = 0; }
  }
  char * event;
  char * site;
  char * date;
  char * round;
  char * white;
  char * black;
  char * result;
  std::vector<std::vector<std::string>> moves; // [wmoves][bmoves]
};

class pgn_io
{
  std::ofstream * ofile;
  std::ifstream * ifile;
  pgn_data * data;

  public:
    pgn_io(char * filename);
    ~pgn_io();

    bool load(Board& b);
    bool parse(Board& b);
    bool save(char * filename);

    bool parse_tags(std::string& line);
    bool parse_moves(Board& b, BoardData& pd, std::string& line);

    // given a san move - return the U16 encoded move
    U16 san_to_move(Board& b, std::string& s);
    int parse_piece(char& c);
    int to_square(std::string& s);
    U16 find_move_row(Board& b, int row, int to, int piece);
    U16 find_move_col(Board& b, int col, int to, int piece);
    U16 find_move(Board& b, int to, int piece);
    
    // given a U16 encoded move, convert to proper SAN format.
    char * move_to_san(U16 & m);

    void pgn_strip(std::string& move);
};

#endif
