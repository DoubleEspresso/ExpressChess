#ifndef HEDWIG_PGNIO_H
#define HEDWIG_PGNIO_H

#include <string.h>
#include <vector>
#include <iostream>
#include <fstream>

#include "board.h"
#include "move.h"
#include "book.h"

// a stripped move is encoded as a from|to squares, each sq needs only 6 bits
struct U12
{
  unsigned move: 12;
};

enum Tag
{
  EVENT, SITE, DATE, ROUND, TWHITE, TBLACK, RESULT, WELO, BELO, TAG_NB, NONE
};

struct pgn_data
{
  //  EVENT, SITE, DATE, ROUND, WHITE, BLACK, RESULT, WELO, BELO, NONE
  std::string tags[TAG_NB];
  std::vector<U12> moves; // [wmoves][bmoves]
  std::vector<U64> pos_keys;
  void encode_tag(const char * tag);
};

class pgn_io
{
  std::ofstream * ofile;
  std::ifstream * ifile;
  Book * book;
  pgn_data * data;

  public:
  pgn_io(char * pgn_fname, char * db_fname);
    ~pgn_io();

    bool load(Board& b);
    bool parse(Board& b);
    bool insert_in_db();

    bool parse_tags(std::string& line);
    bool parse_moves(Board& b, BoardData& pd, std::string& line, bool& eog);
    bool parse_header_tag(std::string& line);

    // given a san move - return the U16 encoded move
    U16 san_to_move_16(Board& b, std::string& s);
    U12 to_move12(U16& m);

    int parse_piece(char& c);
    int to_square(std::string& s);
    U16 find_move_row(Board& b, int row, int to, int piece);
    U16 find_move_col(Board& b, int col, int to, int piece);
    U16 find_move_promotion(Board& b, int pp, int to, int fp, bool isCapture);
    U16 find_move(Board& b, int to, int piece);
    
    // given a U16 encoded move, convert to proper SAN format.
    char * move_to_san(U16 & m);
    void pgn_strip(std::string& move);

    // header tag hash function (FNV)
    unsigned int FNV_hash(const char* key, int len);
};

#endif
