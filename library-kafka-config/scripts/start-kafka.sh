#!/bin/sh

# creates a new terminal window
function newTab() {
  if [[ $# -eq 0 ]]; then
    open -a "Terminal" "$PWD"
  else
    open -a "Terminal" "$@"
  fi
}

function newITerm2Tab() {
  local cmd=""
  local cdto="$PWD"
  local args="$@"

  echo "000, $args"

  if [ -d "$1" ]; then
    cdto=$(
      cd "$1"
      pwd
    )
    args="${@:2}"
  fi

  echo "111, $cdto, $args"

  if [ -n "$args" ]; then
    cmd="; $args"
  fi

  echo "222, $cdto, $cmd, $args"

  osascript &>/dev/null <<EOF
        tell application "iTerm2"
          tell current window
            set prevTab to current tab
            create tab with default profile
            tell current tab
              launch session "Default Session"
              tell the last session
#               set tab name to "ZooKeeper"
                write text "cd \"$cdto\"$cmd"
              end tell
            end tell
            select prevTab
          end tell
        end tell
EOF
}

KAFKA_CURR_INSTALL_PATH="/Applications/kafka_2.13-2.6.0"

read -p "Enter Kafka Install Path [$KAFKA_CURR_INSTALL_PATH]: " KAFKA_NEW_INSTALL_PATH

if [[ -z "$KAFKA_NEW_INSTALL_PATH" ]]; then
  KAFKA_INSTALL_PATH="$KAFKA_CURR_INSTALL_PATH"
else
  KAFKA_INSTALL_PATH="$KAFKA_NEW_INSTALL_PATH"
fi

echo "Kafka Install Path: $KAFKA_INSTALL_PATH"

if [ ! -d "$KAFKA_INSTALL_PATH" ]; then
  echo "Kafka Install Path does not exist"
  exit
fi

echo "changing directory to $KAFKA_INSTALL_PATH"
#newTab "$KAFKA_INSTALL_PATH"
newITerm2Tab "$KAFKA_INSTALL_PATH" "./bin/zookeeper-server-start.sh config/zookeeper.properties"
