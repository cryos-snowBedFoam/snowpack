#!/bin/bash

# use  nohup *.sh > fout  to redirect output to named file

TOOL="valgrind --tool=callgrind --simulate-cache=yes"
TOOL="/software/bin/valgrind --tool=memcheck --leak-check=full --show-reachable=yes --track-origins=yes --log-fd=2 "
TOOL="time"
TOOL=""

${TOOL} /home/jafarima/testIncLib/bin/snowpack -c cfg/ioSN.ini -b 2014-09-10T00:00 -e 2015-07-01T00:00
