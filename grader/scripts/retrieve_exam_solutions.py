import subprocess
import json
import re

class Rok():
    def __init__(self, folder, task_file, out_file, task_id):
        self.folder = folder
        self.task_file = task_file
        self.out_file = out_file
        self.task_id = task_id

# if blank result will be in file specified in out_file
# one_file = "radovi_2023.json"
one_file = ""
# if False it will name folder by student alas
private_info = False

rokovi_array = [
    Rok("/home/milica/Desktop/rbp_radovi/januar1_rbp/pregledno/prva", "1.sql", "jan_grupa1_1.json", "1011"),
    Rok("/home/milica/Desktop/rbp_radovi/januar1_rbp/pregledno/prva", "2.sql", "jan_grupa1_2.json", "1012"),
    Rok("/home/milica/Desktop/rbp_radovi/januar1_rbp/pregledno/druga", "1.sql", "jan_grupa2_1.json", "1021"),
    Rok("/home/milica/Desktop/rbp_radovi/januar1_rbp/pregledno/druga", "2.sql", "jan_grupa2_2.json", "1022"),

    Rok("/home/milica/Desktop/rbp_radovi/januar2_rbp/pregledano", "1.sql", "feb_1.json", "1031"),
    Rok("/home/milica/Desktop/rbp_radovi/januar2_rbp/pregledano", "2.sql", "feb_2.json", "1032"),

    Rok("/home/milica/Desktop/rbp_radovi/jun_rbp/pregledano", "1.sql", "jun_1.json", "1041"),
    Rok("/home/milica/Desktop/rbp_radovi/jun_rbp/pregledano", "2.sql", "jun_2.json", "1042"),

    Rok("/home/milica/Desktop/rbp_radovi/sep_rbp/pregledano", "1.sql", "sep_1.json", "1051"),
    Rok("/home/milica/Desktop/rbp_radovi/sep_rbp/pregledano", "2.sql", "sep_2.json", "1052")
]

i = 0
all_tasks = []
for rok in rokovi_array:
    files = subprocess.run('find  {} | grep {}'.format(rok.folder, rok.task_file), shell=True, text=True, stdout=subprocess.PIPE).stdout.strip().split('\n')

    tasks = []
    for file in files:
        try:
            # print(file)
            file_content = open(file, "r").read()
            without_comments = re.sub(r'--.*?\n', "", file_content)
            solution = without_comments.replace("\n", " ").replace("\t", " ")
            if private_info:
                student = i
                i+=1
            else:
                student = re.findall(r'_(m[i|r|v]\d+)', file)[0] + "_" + rok.task_id

            task = {
                "requestId": student,
                "taskId": rok.task_id,
                "solution": solution
            }
            # print(json.dumps(task))
            tasks.append(task)
        except Exception as err:
            print("Error in file", file)
            print(err)

    file = ""
    if one_file == "":
        file = "rokovi/" + rok.out_file
        with open(file, "w") as f:
            f.write(json.dumps(tasks))
    else:
        # file = "rokovi/" + one_file
        all_tasks.append(tasks)
    # print(json.dumps(tasks))

if one_file != "":
    with open("rokovi/" + one_file, "w") as f:
        f.write(json.dumps(all_tasks))