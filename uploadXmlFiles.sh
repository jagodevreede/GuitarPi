#!/usr/bin/env bash
echo "Uploading xml files..."
scp /Users/jagodevreede/git/guitar-player/music/*.xml pi@192.168.1.20:./IdeaProjects/guitar-player/music
echo "Uploading yml files..."
scp /Users/jagodevreede/git/guitar-player/*.yml pi@192.168.1.20:./IdeaProjects/guitar-player
echo "Uploading xml done"