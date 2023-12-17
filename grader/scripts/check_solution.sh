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


db2 "$query" > "$user_path"

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
else
  diff -bB "$user_path" "$solution_path" | head -n 10
fi


db2 connect reset > /dev/null
rm "$user_path"

exit 0