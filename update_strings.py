import xml.etree.ElementTree as ET
import os

english_translations = {
    "app_name": "My Tasks",
    "add_task": "Add New Task",
    "edit_task": "Edit Task",
    "task_title": "Task Title",
    "task_title_hint": "e.g., Buy Groceries",
    "task_desc": "Description (Optional)",
    "category": "Category",
    "add_custom_category": "Add New Category",
    "category_name_hint": "Category Name (e.g., Sports)",
    "reminder": "Reminder",
    "date": "Date",
    "time": "Time",
    "repeat": "Repeat",
    "no_repeat": "No Repeat",
    "repeat_daily": "Daily",
    "repeat_every_other_day": "Every Other Day",
    "repeat_weekly": "Weekly",
    "completed_tasks": "Completed Tasks",
    "empty_tasks": "No tasks here! 📝",
    "empty_tasks_sub": "Tap the + button to add your first task.",
    "save": "Save",
    "cancel": "Cancel",
    "delete": "Delete",
    "edit": "Edit",
    "reminders_title": "Manage Reminders",
    "xiaomi_trouble": "Fix Notification Issues (Xiaomi)",
    "xiaomi_trouble_desc": "If you are not receiving reminder notifications, check the following steps:",
    "empty_reminders": "No active reminders set.",
    "subtasks": "Subtasks",
    "add_subtask": "Add Subtask",
    "subtask_hint": "Enter subtask title",
    "percentage_completed": "Completion Percentage"
}

def update_xml(file_path, is_english=False):
    tree = ET.parse(file_path)
    root = tree.getroot()
    for string_elem in root.findall("string"):
        name = string_elem.get("name")
        if is_english and name in english_translations:
            string_elem.text = english_translations[name]
    
    # Save back
    tree.write(file_path, encoding="utf-8", xml_declaration=True)

update_xml("app/src/main/res/values/strings.xml", is_english=True)

