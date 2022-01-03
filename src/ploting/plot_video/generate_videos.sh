#!/bin/bash
for D in $(find . -type d); do
  echo "generating video for $D"
  yes | ffmpeg -i "$D/%d.png" -c:v libx264 -vf fps=30 -pix_fmt yuv420p $D.mp4
done
exit 0