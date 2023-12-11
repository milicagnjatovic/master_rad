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


db2 "$query" > "$user_path" || echo "error"

if [ ! -e "$solution_path" ] || [ ! -e "$user_path" ]
then
    echo "User file or result file is missing"
else
  diff -bB "$user_path" "$solution_path" | head -n 10
fi


db2 connect reset > /dev/null
rm "$user_path"

exit 0