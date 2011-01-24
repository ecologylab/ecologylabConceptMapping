#!/bin/bash

if (($# != 4)); then
	echo "split line based task into sub-tasks."
	echo "usage: <input-file> <number-of-splits> <unique-prefix> <op-script>"
	echo "    <op-script> should accept 2 arguments: <input-file> <output-file>."
else
	for currentf in $(ls $3-split-*); do
		echo "removing $currentf..."
		rm -rf $currentf
	done
  
	total_line_count=$(wc -l < $1)
	split_line_count=$(($total_line_count / $2 + 1))
	split -d -l $split_line_count $1 $3-split-

	for splitf in $(ls $3-split-*); do
		echo "$4 $splitf $splitf.result >$splitf.log 2>&1"
	done
fi
