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

# check if file with solution exists
if [ ! -e "$solution_path"  ];
then
    echo "Server error | Solution for task is missing"
    exit 1
fi

function execute_query() {
  connection_result="$(db2 connect to stud2020 user student using matf2024.)"
#  echo $connection_result
  if [[ $connection_result != *Database\ Connection\ Information*  ]]; then
    echo "Server error | Unable to connect to database"
    exit 1
  fi

  db2 "$query" | sed '/^$/d' > "$user_path"
  db2 connect reset > /dev/null
}

export -f execute_query
export query="$query"
export user_path="$user_path"

start=$(date +%s.%N)
timeout 15 bash -c "execute_query"
# if last command failed
if [ $? -ne 0 ]; then
  echo "User error | Time limit exceeded"
#  user file does not exists => failed creation
  if [ -e "$user_path"  ]; then
    rm "$user_path"
  fi
  exit 1
fi
end=$(date +%s.%N)


if [ ! -e "$user_path"  ]; then
  echo "Server error | User file creation failed"
  exit 1
fi

# first line of user and solution file => column names
user_end=$(tail -1 "$user_path")
solution_end=$(tail -1 "$solution_path")

# last line of user and solution file => number of rows
user_start=$(head -1 "$user_path")
solution_start=$(head -1 "$solution_path")

# Syntax error
if [[ $user_start == DB2* ]] || [[ $user_start == SQL* ]]; then
  echo "User error | Not executing"
  rm "$user_path"
  exit 1
fi

# Check if number of rows is matching
if [ "$user_end" != "$solution_end" ]; then
  echo "User error | Incorrect number of rows"
  rm "$user_path"
  exit 1
fi

# difference between column names: case insensitive ignoring white spaces
first_row_diff=$(diff -bBi <(echo "$user_start") <(echo "$solution_start"))
if [ "$first_row_diff" ]; then
  echo "User error | Wrong column names"
  rm "$user_path"
  exit 1
fi
shopt -u nocasematch

difference=$(diff -bBi <(tail -n +3 "$user_path") <(tail -n +3 "$solution_path") | head -n 1)
# if diff is not empty return incorrect, could be due incorrect order by and typos
if [ -n "$difference" ]; then
    echo "User error | Incorrect"
else
  duration=$(echo "$end - $start" | bc)
  echo "OK | Execution time $duration"
fi

rm "$user_path"

exit 0