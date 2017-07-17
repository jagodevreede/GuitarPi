#!/usr/bin/env bash
echo "Uploading xml files..."
scp /Users/jagodevreede/git/guitar-player/*.xml pi@192.168.0.100:./IdeaProjects/guitar-player
echo "Uploading yml files..."
scp /Users/jagodevreede/git/guitar-player/*.yml pi@192.168.0.100:./IdeaProjects/guitar-player
echo "Uploading xml done"