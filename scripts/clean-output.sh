#!/bin/bash
## 获取当前目录路径
check_style_path=$(
  cd $(dirname $0)
  pwd
)
## 获取上级目录路径
project_path=$(dirname "$PWD")
echo $project_path
rm -rf $project_path/output
echo $check_style_path
