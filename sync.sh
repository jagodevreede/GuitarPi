#!/bin/bash
scp pi@guitarpi.local:/home/pi/guitar-player/*.conf ./
rsync -avz --exclude 'target' --exclude '*.conf' --exclude 'frontend' ./ pi@guitarpi.local:/home/pi/guitar-player --delete

