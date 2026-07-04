import re

file = "app/src/main/java/com/example/ui/MainTodoScreen.kt"
with open(file, "r") as f:
    content = f.read()

# Fix MainTodoScreen
content = content.replace("JalaliCalendar.formatShamsiDate(androidx.compose.ui.platform.LocalContext.current, currentTime.value)", "JalaliCalendar.formatShamsiDate(context, currentTime.value)")

# We need to make sure `val context = LocalContext.current` is defined before it.
# Wait, let's just insert it before `val liveShamsiDateStr`.
content = content.replace("val liveShamsiDateStr = remember", "val context = androidx.compose.ui.platform.LocalContext.current\n    val liveShamsiDateStr = remember")

with open(file, "w") as f:
    f.write(content)

file = "app/src/main/java/com/example/ui/ShamsiDatePicker.kt"
with open(file, "r") as f:
    content = f.read()

content = content.replace("JalaliCalendar.getPersianMonthName(selectedMonth)", "JalaliCalendar.getPersianMonthName(androidx.compose.ui.platform.LocalContext.current, selectedMonth)")
content = content.replace("JalaliCalendar.getPersianMonthName(m)", "JalaliCalendar.getPersianMonthName(androidx.compose.ui.platform.LocalContext.current, m)")

with open(file, "w") as f:
    f.write(content)

