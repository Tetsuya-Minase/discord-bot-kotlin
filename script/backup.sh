#!/bin/sh

# change directory home
cd

# create backup directory
mkdir -p backup/`date +%Y%m%d`

# copy
cp minecraft/banned-ips.json backup/`date +%Y%m%d`/
cp minecraft/banned-players.json backup/`date +%Y%m%d`/
cp minecraft/eula.txt backup/`date +%Y%m%d`/
cp minecraft/hs_err_pid7602.log backup/`date +%Y%m%d`/
cp minecraft/hs_err_pid7655.log backup/`date +%Y%m%d`/
cp -r minecraft/logs/ backup/`date +%Y%m%d`/
cp minecraft/ops.json backup/`date +%Y%m%d`/
cp minecraft/server.properties backup/`date +%Y%m%d`/
cp minecraft/usercache.json backup/`date +%Y%m%d`/
cp minecraft/whitelist.json backup/`date +%Y%m%d`/
cp -r minecraft/world/ backup/`date +%Y%m%d`/

# compression
tar czf backup/`date +%Y%m%d`.tar.gz backup/`date +%Y%m%d`

# remove temp directory
rm -r backup/`date +%Y%m%d`

exit 0
