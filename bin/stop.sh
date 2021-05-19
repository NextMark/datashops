#!/bin/bash

if [ $# -le 0 ]; then
  echo "special stop service name."
  exit 1
fi
server=$1
kill $(cat pid/datashops-${server}.pid)
echo "datashops-${server} is shutting down"
