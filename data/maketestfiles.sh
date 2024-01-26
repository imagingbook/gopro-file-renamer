#!/bin/bash
# Script to create a bunch of random GoPro test files:
for v in {0723..0735}	# v = video number
do
	for c in {01..05}; 	# c = chapter number
	do 
		echo creating GH$c$v.xxx
		head -c 1k </dev/urandom >GH$c$v.MP4
		head -c 1k </dev/urandom >GH$c$v.LRV
		head -c 1k </dev/urandom >GH$c$v.THM
		sleep 1.1	# to sort files by creation date
	done
done