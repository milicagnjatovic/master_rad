if [ "$#" -ne 1 ] ; then
  echo "Usage: file"
  exit 1
fi

db2 connect to stud2020 > /dev/null

mkdir -p results

startX=$(date +%s.%N)

while read -r task_id; do
  read -r query
  start=$(date +%s.%N)
#  echo "$task_id $query"
#  echo "------------------------"

  skip=true

#  db2 "$query" | sed '/^$/d' > "results/$task_id"

  db2 "$query" | sed '/^$/d' > "results/$task_id" &
  pid=$!
  for ((i=0; i<200; i++)); do
    sleep 0.02s
    if ! ps -p $pid > /dev/null; then
      skip=false
      break
    fi
  done

  if $skip; then
    kill $pid
    echo "$task_id | Time limit exceeded"
    continue
  fi

  if [ ! -e "results/$task_id"  ];
  then
      echo "$task_id | File is not created "
      continue
  fi

  lastline=$(tail -1 "results/$task_id")
#  echo $(head -1 "results/$task_id")
  if [[ ! $lastline == *record* ]] ; then
    echo "$task_id | Number of rows missing"
    continue
  fi

  end=$(date +%s.%N)
  duration=$(echo "$end - $start" | bc)
  numberOfRows=$(echo "$lastline" | sed 's/[^0-9]*\([0-9]\+\).*/\1/')
  echo "ID: $task_id | Time: $duration | No. rows: $numberOfRows"
done < $1

endX=$(date +%s.%N)
duration=$(echo "$endX - $startX" | bc)
echo "Total time: $duration"

db2 connect reset > /dev/null

 rm "$1"

exit 0