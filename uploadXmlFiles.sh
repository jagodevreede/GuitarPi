#!/usr/bin/env bash
echo "Uploading xml files..."
scp /Users/jagodevreede/git/guitar-player/*.xml pi@guitarpi:./IdeaProjects/guitar-player
echo "Uploading yml files..."
scp /Users/jagodevreede/git/guitar-player/*.yml pi@guitarpi:./IdeaProjects/guitar-player
echo "Uploading xml done"