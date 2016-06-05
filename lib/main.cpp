#include "info.h"
#include "bits.h"
#include "globals.h"
#include "magic.h"
#include "zobrist.h"
#include "board.h"
#include "pgnio.h"

void parse_args(int argc, char ** argv);

int main(int argc, char ** argv)
{
  // simple info/greeting to user
  //Info::BuildInfo::greeting();
  
  // fill the global chess data arrays for moves/analysis
  if (!Globals::init()) 
    {
      printf("..global arrays initialized failed\n");
      return EXIT_FAILURE;
    }
  
  // the magic bitboard initialization
  Magic::init();
  if (!Magic::check_magics())
    {
      printf("..!!ERROR : attack hash failed to initialize properly, abort!\n");
      std::cin.get();
      return EXIT_FAILURE;
    }

  // fill the zobrist arrays for transposition table hashing
  if (!Zobrist::init())
    {
      printf("..!!ERROR : failed to initialize zobrist keys, abort!\n");
      std::cin.get();
      return EXIT_FAILURE;
    }
  
  // initialize the threadpool (timer thread, master + worker threads)
  //timer_thread->init();
  //Threads.init();
  
  // parse args and start working
  if (argc > 1) parse_args(argc, argv);
  else printf("..nothing to do, exiting\n");
  
  return EXIT_SUCCESS;
}

// parse user passed arguments here
void parse_args(int argc, char* argv[])
{
  bool dotest = false;
  int testtime = 0;
  int testdepth = 0;
  for (int j = 0; j<argc; ++j)
    {
      if (!strcmp(argv[j], "-pgn"))
	{
	  Board b;
	  std::istringstream fen(START_FEN);
	  b.from_fen(fen);
	  pgn_io pgn(argv[j+1], "testdb.bin", 500);	  
	  if (!pgn.parse(b))
	    {
	      printf("..ERROR: failed to parse %s correctly\n", argv[j+1]);
	    }

	}
      else if (!strcmp(argv[j], "-find"))
	{
	  printf("..finding %s\n", argv[j+1]);
	  Board b;
	  std::istringstream fen(argv[j+1]);
	  pgn_io pgn("/home/mjg/java-workspace-mars/ExpressChess/lib/testdb.bin");
	  pgn.find(argv[j+1]);
	}
      else if (!strcmp(argv[j], "-testfen")) 
	{
	  Board b; BoardData pd;
	  std::istringstream fen(START_FEN);
	  b.from_fen(fen);
	  printf("..start fen %s\n", b.to_fen().c_str());
	  pgn_io pgn("junk.bin");

	  std::string token = "e4";
	  U16 m = pgn.san_to_move_16(b, token);
	  b.do_move(pd, m);
	  printf("..e4 fen %s\n", b.to_fen().c_str());

	  token = "c5";
	  m = pgn.san_to_move_16(b, token);
	  b.do_move(pd, m);
	  printf("..c5 fen %s\n", b.to_fen().c_str());

	  token = "Nf3";
	  m = pgn.san_to_move_16(b, token);
	  b.do_move(pd, m);
	  printf("..nf3 fen %s\n", b.to_fen().c_str());
	}

      else if (!strcmp(argv[j], "-usett")) printf("..set option tt\n");
      else if (!strcmp(argv[j], "-ttsizekb")) printf("..set option ttsizekb\n");
      else if (!strcmp(argv[j], "-nthreads")) printf("..set option nthreads\n");
      else if (!strcmp(argv[j], "-trace")) printf("..set option eval trace\n");
      else if (!strcmp(argv[j], "-stats")) printf("..set option stats\n");
      else if (!strcmp(argv[j], "-log")) printf("..set option log\n");
      else if (!strcmp(argv[j], "-testtime")) { testtime = atoi(argv[j + 1]); j++; if (j >= argc) break; }
      else if (!strcmp(argv[j], "-testdepth")) { testdepth = atoi(argv[j + 1]); j++; if (j >= argc) break; }
    }
}

