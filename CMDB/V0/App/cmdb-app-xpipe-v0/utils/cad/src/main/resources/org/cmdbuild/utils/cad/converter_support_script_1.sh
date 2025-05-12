#!/bin/bash

src_file="$1"
target_file="$2"

src_dir="`mktemp -d`"
target_dir="`mktemp -d`"

cp -v "$src_file" "$src_dir"/file.dwg

echo "execute oda file converter"

xvfb-run -a -s '-screen 0 1600x1200x24+32' /usr/bin/ODAFileConverter "$src_dir" "$target_dir" ACAD2018 DXF 0 1 || echo "error: oda file converter exit with nonzero status code" >&2

if [ -e "$target_dir"/* ]; then
    cp -v "$target_dir"/* "$target_file"
else
    echo "error: unable to find target file" >&2
fi

rm -rf "$src_dir" "$target_dir"
