#!/bin/bash

if (($# != 5)); then
	echo "Usage: <input-file> <number-of-splits> <unique-prefix> <map-script> <reduce-script>"
	echo "    <map-script> should accept 2 arguments: <input-file> <output-file>, to carry out computation;"
	echo "    <reduce-script> should accept a list of file names to combine them into one single result file."
else
	total_line_count=$(wc -l < $1)
	split_line_count=$(($total_line_count / $2 + 1))
	split -d -l $split_line_count $1 $3-split-

	split_files=$(ls $3-split-*)
	for splitf in $split_files; do
		$4 $splitf $splitf-result &
	done
	split_file_count=0
	for splitf in $split_files; do
		split_file_count=$(($split_file_count + 1))
	done
	echo split_file_count = $split_file_count

	result_file_count=0
	until (($split_file_count == $result_file_count)); do
		result_files=$(ls $3-split-*-result)
		for resultf in $result_files; do
			result_file_count=$(($result_file_count + 1))
		done
		echo result_file_count = $result_file_count
		sleep 3
	done

	$5 $result_files
fi

