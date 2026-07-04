import os
import re

kt_files = [
    "app/src/main/java/com/example/ui/MainTodoScreen.kt",
    "app/src/main/java/com/example/ui/ReminderManagementScreen.kt",
    "app/src/main/java/com/example/ui/ShamsiDatePicker.kt",
    "app/src/main/java/com/example/ui/TaskBottomSheet.kt",
    "app/src/main/java/com/example/ui/CategoryDialog.kt",
    "app/src/main/java/com/example/ui/CategoryManagerBottomSheet.kt"
]

for file in kt_files:
    if not os.path.exists(file): continue
    with open(file, "r") as f:
        content = f.read()
    
    if "stringResource" in content and "import androidx.compose.ui.res.stringResource" not in content:
        content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.res.stringResource\nimport androidx.compose.ui.Modifier")
        content = content.replace("import com.example.R\n", "")
        content = content.replace("package com.example.ui", "package com.example.ui\n\nimport com.example.R")
        
    with open(file, "w") as f:
        f.write(content)

