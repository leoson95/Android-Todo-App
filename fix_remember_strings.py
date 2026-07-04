import re

files = [
    "app/src/main/java/com/example/ui/MainTodoScreen.kt",
    "app/src/main/java/com/example/ui/ReminderManagementScreen.kt"
]

for file in files:
    with open(file, "r") as f:
        content = f.read()

    # We can just extract it before `val formattedReminder` / `val dateStr = remember`.
    
    if "MainTodoScreen" in file:
        content = content.replace("val formattedReminder = remember(task.reminderTime) {", "val clockSuffixStr = stringResource(R.string.clock_suffix)\n    val formattedReminder = remember(task.reminderTime) {")
        content = content.replace('stringResource(R.string.clock_suffix)', 'clockSuffixStr')
    elif "ReminderManagementScreen" in file:
        content = content.replace("val dateStr = remember(task.reminderTime) {", "val clockSuffixStr = stringResource(R.string.clock_suffix)\n    val dateStr = remember(task.reminderTime) {")
        content = content.replace('stringResource(R.string.clock_suffix)', 'clockSuffixStr')
        
    with open(file, "w") as f:
        f.write(content)

