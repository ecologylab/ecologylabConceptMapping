#!/bin/bash

if [ $# -eq 1 ]; then
  echo 'usage: $0 <backup-file-path>'
  exit
fi

pg_dump -U quyin -Fc -O -x --no-tablespaces -f '$1' wikiparsing3
