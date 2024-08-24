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

start_x=$(date +%s.%N)
time_limit=15
step=0.02
iterations=$(echo "$time_limit / $step" | bc)

while read -r task_id; do
  read -r query
  start=$(date +%s.%N)
#  echo "$task_id $query"
#  echo "------------------------"

  should_kill_process=true

  db2 "$query" | sed '/^$/d' > "results/$task_id" &
  pid=$!
  for ((i=0; i<$iterations; i++)); do
    sleep $step
    if ! ps -p $pid > /dev/null; then
      should_kill_process=false
      break
    fi
  done

  if $should_kill_process; then
    kill $pid
    echo "$task_id#Time limit exceeded"
    continue
  fi

  if [ ! -e "results/$task_id"  ];
  then
      echo "$task_id#File is not created "
      continue
  fi

  last_line=$(tail -1 "results/$task_id")
  if ! echo "$last_line" | grep -qE '[0-9]+ record\(s\) selected\.'; then
    echo "$task_id#Number of rows missing"
    continue
  fi

  end=$(date +%s.%N)
  duration=$(echo "$end - $start" | bc)
  number_of_rows=$(echo "$last_line" | sed 's/[^0-9]*\([0-9]\+\).*/\1/')
  echo "$task_id#$duration#$number_of_rows"
done < $1

end_x=$(date +%s.%N)
duration=$(echo "$end_x - $start_x" | bc)
echo "$duration"

#db2 connect reset > /dev/null
connection_result="$(db2 connect reset)"
if [[ $connection_result != *completed\ successfully*  ]]; then
  echo "Server error | Error while disconnecting"
  exit 1
fi

rm "$1"

exit 0