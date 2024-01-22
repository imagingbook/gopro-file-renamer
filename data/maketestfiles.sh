#!/bin/bash
# Script to create a bunch of random GoPro test files:
for v in {0723..0735}	# v = video number
do
	for c in {01..05}; 	# c = chapter number
	do 
		echo creating GH$c$v.xxx
		head -c 100k </dev/urandom >GH$c$v.MP4
		sleep .1
		head -c 10k  </dev/urandom >GH$c$v.LRV
		sleep .1
		head -c 1k   </dev/urandom >GH$c$v.THM
		sleep .1
	done
done