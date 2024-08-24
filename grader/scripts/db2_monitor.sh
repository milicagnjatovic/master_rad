db2 connect to stud2020
while true; do
  execution=$(db2 "SELECT APPLICATION_HANDLE FROM SYSIBMADM.MON_CURRENT_SQL WHERE ELAPSED_TIME_SEC>3000 OR TOTAL_CPU_TIME>3000000 OR QUERY_COST_ESTIMATE>25000")
  ids=$(echo "$execution" | sed "1,3d" | head -n -2 | tr '\n' ',' | head -c -1)
  echo "IDS: $ids"
  if [ "$ids" ]; then
    db2 "force application ( $ids )"
    echo killed
  fi
  sleep 1
done
