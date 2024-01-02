if [ "$#" -ne 3 ] ; then
  echo "Usage: user_path solution_path query"
  exit 1
fi

user_path="student_results/$1"
solution_path="results/$2"
query="$3"

#echo "$query"
#echo "$user_path"
#echo "$solution_path"

db2 connect to stud2020 > /dev/null


db2 "$query" | sed '/^$/d' > "$user_path"

if [ $? -ne 0 ]; then
  if [ ! -e "$user_path"  ]; then
    echo "E100 | file creation failed"
    exit 1
  fi
  echo "E200 | syntax error"
  rm "$user_path"
  exit 1
fi

if [ ! -e "$solution_path"  ];
then
    echo "E300 | result file is missing"
    rm "$user_path"
    exit 1
fi

student_end=$(tail -1 "$user_path")
solution_end=$(tail -1 "$solution_path")

if [ "$student_end" != "$solution_end" ]; then
  echo "Different number of rows"
  rm "$user_path"
  exit 1
fi


student_start=$(head -1 "$user_path")
solution_start=$(head -1 "$solution_path")

shopt -s nocasematch # case insensitive
if [[ "$student_start" != "$solution_start" ]]; then
  echo "Wrong column names"
  rm "$user_path"
  exit 1
fi
shopt -u nocasematch

diff -bBi "$user_path" "$solution_path" | head -n 10

db2 connect reset > /dev/null
rm "$user_path"

exit 0