# Makefile template 

CXX = g++
RM = rm -f  # rm command
LFLAGS = -lpthread
INCS =
CFLAGS = -Wall -O3 -fomit-frame-pointer -fstrict-aliasing -fno-rtti -save-temps -std=gnu++11 $(INCS) # -g for debug

CFLAGSD = -Wall -g -O3 -fomit-frame-pointer -fstrict-aliasing -fno-rtti -save-temps -std=gnu++11 $(INCS) 
USERMACROS =

SRCS = main.cpp globals.cpp magic.cpp move.cpp board.cpp zobrist.cpp book.cpp

OBJS = $(SRCS:.cpp=.o)
OBJSCL = $(SRCSCL:.cpp=.o)

OS = $(shell uname) 
GIT_VERSION := $(shell git describe --abbrev=4 --dirty --always --tags)
USERMACROS += -DBUILD_DATE="\"$$(date)\""
USERMACROS += -DVERSION=\"$(GIT_VERSION)\"

# os
ifeq ($(OS),Darwin )
   EXE_OS = osx
   USERMACROS += -DOS=\"unix\"
endif

ifeq ($(OS),Linux )
   EXE_OS = nix
   USERMACROS += -DOS=\"unix\"
endif


.PHONY: all
all: hedwig-polyglot.exe

.PHONY: debug
debug: hedwig-polyglot-debug.exe

.PHONY: install
install:
	cp *.so ../bin

hedwig-polyglot.exe: ${OBJS}
	$(CXX) $(INCS) -o $@ $^ $(LFLAGS)

hedwig-polyglot-debug.exe: ${OBJS}
	$(CXX) $(INCS) -o $@ $^ $(LFLAGS)

%.o: %.cpp
	$(CXX) $(CFLAGS) $(USERMACROS) -c $<

.PHONY: clean
clean:
	-${RM} ${OBJS} *~ *.so *.o *.exe *.ii *.s
