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
	Info::BuildInfo::greeting();

	// fill the global chess data arrays for moves/analysis
	if (Globals::init()) printf("..global arrays initialized\n");

	// the magic bitboard initialization
	Magic::init();
	if (!Magic::check_magics())
	{
		printf("..!!ERROR : attack hash failed to initialize properly, abort!\n");
		std::cin.get();
		return EXIT_FAILURE;
	}
	else printf("..attack hash arrays initialized\n");

	// fill the zobrist arrays for transposition table hashing
	if (!Zobrist::init())
	{
		printf("..!!ERROR : failed to initialize zobrist keys, abort!\n");
		std::cin.get();
		return EXIT_FAILURE;
	}
	else printf("..zobrist keys initialized\n");


	// initialize the threadpool (timer thread, master + worker threads)
	//timer_thread->init();
	//Threads.init();

	// parse args and start working
	if (argc >= 1) parse_args(argc, argv);
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
	  pgn_io pgn(argv[j+1], "testdb.bin");	  
	  if (!pgn.parse(b))
	    {
	      printf("..ERROR: failed to parse %s correctly\n", argv[j+1]);
	    }

	}
      else if (!strcmp(argv[j], "-match")) printf("..match mode\n");
      else if (!strcmp(argv[j], "-bench")) printf("..benchmark\n");
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
