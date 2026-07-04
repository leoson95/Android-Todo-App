import os
import re

kt_files = [
    "app/src/main/java/com/example/util/ReminderReceiver.kt",
    "app/src/main/java/com/example/util/JalaliCalendar.kt"
]

for file in kt_files:
    if not os.path.exists(file): continue
    with open(file, "r") as f:
        content = f.read()
    
    if "context.getString" in content and "import com.example.R" not in content:
        content = content.replace("package com.example.util", "package com.example.util\n\nimport com.example.R")
        
    with open(file, "w") as f:
        f.write(content)

