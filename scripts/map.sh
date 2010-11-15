#!/bin/bash

if (($# != 4)); then
	echo "Usage: <input-file> <number-of-splits> <unique-prefix> <map-script>"
	echo "    <map-script> should accept 2 arguments: <input-file> <output-file>, to carry out computation."
else
	for currentf in $(ls $3-split-*); do
		echo "removing $currentf..."
		rm -rf $currentf
	done
  
	total_line_count=$(wc -l < $1)
	split_line_count=$(($total_line_count / $2 + 1))
	split -d -l $split_line_count $1 $3-split-

	for splitf in $(ls $3-split-*); do
		$4 $splitf $splitf-result &
	done

	echo "all jobs mapped. please reduce results after they are done."
fi

