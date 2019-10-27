#!/bin/bash
rsync -avz ./ pi@guitarpi.local:/home/pi/guitar-player --delete

