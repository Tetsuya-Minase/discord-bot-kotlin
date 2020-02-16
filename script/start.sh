#!/bin/bash

# change directory
cd

# start server
screen -d -m -S minecraft java -Xms752m -Xmx2G -d64 -jar server.jar nogui

exit 0
