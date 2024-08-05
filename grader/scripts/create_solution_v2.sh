if [ "$#" -ne 1 ] ; then
  echo "Usage: file"
  exit 1
fi

if [ ! -e "$1"  ];
then
    echo "Server error | File does not exist"
    exit 1
fi

connection_result="$(db2 connect to stud2020)"
if [[ $connection_result != *Database\ Connection\ Information*  ]]; then
  echo "Server error | Unable to connect to database"
  exit 1
fi

mkdir -p results

startX=$(date +%s.%N)
time_limit=15
step=0.02
iterations=$(echo "$time_limit / $step" | bc)

while read -r task_id; do
  read -r query
  start=$(date +%s.%N)
#  echo "$task_id $query"
#  echo "------------------------"

  shouldKillProcess=true

  db2 "$query" | sed '/^$/d' > "results/$task_id" &
  pid=$!
  for ((i=0; i<$iterations; i++)); do
    sleep $step
    if ! ps -p $pid > /dev/null; then
      shouldKillProcess=false
      break
    fi
  done

  if $shouldKillProcess; then
    kill $pid
    echo "$task_id#Time limit exceeded"
    continue
  fi

  if [ ! -e "results/$task_id"  ];
  then
      echo "$task_id#File is not created "
      continue
  fi

  lastline=$(tail -1 "results/$task_id")
  if [[ ! $lastline == *record* ]] ; then
    echo "$task_id#Number of rows missing"
    continue
  fi

  end=$(date +%s.%N)
  duration=$(echo "$end - $start" | bc)
  numberOfRows=$(echo "$lastline" | sed 's/[^0-9]*\([0-9]\+\).*/\1/')
  echo "$task_id#$duration#$numberOfRows"
done < $1

endX=$(date +%s.%N)
duration=$(echo "$endX - $startX" | bc)
echo "$duration"

db2 connect reset > /dev/null

rm "$1"

exit 0