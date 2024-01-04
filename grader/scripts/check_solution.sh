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

db2 connect to stud2020 > /dev/null

# execute query to user's file
# remove blank lines in the beginning and end
db2 "$query" | sed '/^$/d' > "$user_path"

# if last command failed
if [ $? -ne 0 ]; then
#  user file does not exists => failed creation
  if [ ! -e "$user_path"  ]; then
    echo "Server error | User file creation failed"
    exit 1
  fi

#  user file exists and error is caused by something else
  echo "Server error | Unknown"
  rm "$user_path"
  exit 1
fi

# first line of user and solution file => column names
student_end=$(tail -1 "$user_path")
solution_end=$(tail -1 "$solution_path")

# last line of user and solution file => number of rows
student_start=$(head -1 "$user_path")
solution_start=$(head -1 "$solution_path")

# Syntax erros
if [[ $student_start == DB2* ]] || [[ $student_start == SQL* ]]; then
  echo "Syntax error"
  rm "$user_path"
  exit 1
fi

# Check if number of rows is matching
if [ "$student_end" != "$solution_end" ]; then
  echo "Different number of rows"
  rm "$user_path"
  exit 1
fi

# Check colum names (case insensitive)
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