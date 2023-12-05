if [ "$#" -ne 1 ] ; then
  echo "Usage: file"
  exit 1
fi

db2 connect to stud2020

mkdir -p results

startX=$(date +%s)

while read -r task_id; do
  read -r query
  start=$(date +%s)
#  echo "$task_id $query"
#  echo "------------------------"
  db2 "$query" > "results/$task_id"
  end=$(date +%s)
  echo "$task_id: $((end - start))"
done < $1

endX=$(date +%s)
echo "$task_id: $((endX - startX))"

db2 connect reset

rm "$1"

exit 0