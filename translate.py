import os
import re

files_to_process = [
    "app/src/main/java/com/example/ui/MainTodoScreen.kt",
    "app/src/main/java/com/example/ui/ReminderManagementScreen.kt",
    "app/src/main/java/com/example/ui/ShamsiDatePicker.kt",
    "app/src/main/java/com/example/ui/TaskBottomSheet.kt",
    "app/src/main/java/com/example/ui/CategoryDialog.kt",
    "app/src/main/java/com/example/ui/CategoryManagerBottomSheet.kt",
    "app/src/main/java/com/example/data/TodoRepository.kt",
    "app/src/main/java/com/example/util/ReminderReceiver.kt",
    "app/src/main/java/com/example/util/JalaliCalendar.kt"
]

# We will just list the lines containing Persian characters to see what needs to be translated.
for file in files_to_process:
    if not os.path.exists(file): continue
    with open(file, "r") as f:
        lines = f.readlines()
    for i, line in enumerate(lines):
        if re.search(r'[\u0600-\u06FF]', line):
            print(f"{file}:{i+1}: {line.strip()}")
