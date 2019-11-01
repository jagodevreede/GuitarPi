#!/bin/bash
scp pi@guitarpi.local:/home/pi/guitar-player/*.conf ./
rsync -avz --exclude 'target' --exclude '*.conf' ./ pi@guitarpi.local:/home/pi/guitar-player --delete

