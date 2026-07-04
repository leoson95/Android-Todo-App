import os
import re
import xml.etree.ElementTree as ET

replacements = [
    ("افزودن کار جدید", "add_task_title", "Add New Task"),
    ("اطلاعات اصلی", "main_info", "Main Information"),
    ("عنوان کار", "task_title_label", "Task Title"),
    ("توضیحات یا یادداشت (اختیاری)", "task_desc_label", "Description or Note (Optional)"),
    ("دسته‌بندی کارها", "task_category", "Task Category"),
    ("افزودن دسته‌بندی", "add_category_content_desc", "Add Category"),
    ("زیرکارها (زنجیره فعالیت‌ها)", "subtasks_chain", "Subtasks (Activity Chain)"),
    ("عنوان زیرکار", "subtask_title_label", "Subtask Title"),
    ("افزودن", "add", "Add"),
    ("یادآوری و زنگ هشدار", "reminder_alarm", "Reminder & Alarm"),
    ("🕒 ساعت ", "clock_hour", "🕒 Time "),
    ("تکرار یادآوری:", "repeat_reminder", "Repeat Reminder:"),
    ("بدون تکرار", "no_repeat", "No Repeat"),
    ("روزانه", "daily", "Daily"),
    ("یک در میان", "every_other_day", "Every Other Day"),
    ("هفتگی", "weekly", "Weekly"),
    ("این کار هر روز در ساعت بالا یادآوری می‌شود.", "repeat_desc_daily", "This task will be reminded every day at the above time."),
    ("این کار از تاریخ بالا، یک روز در میان یادآوری می‌شود.", "repeat_desc_eod", "This task will be reminded every other day from the above date."),
    ("این کار هر هفته در همین روز یادآوری می‌شود.", "repeat_desc_weekly", "This task will be reminded every week on this day."),
    ("انصراف", "cancel", "Cancel"),
    ("ذخیره کار", "save_task", "Save Task"),
    ("بستن", "close", "Close"),
    ("دسته‌بندی جدید", "new_category", "New Category"),
    ("نام دسته‌بندی", "category_name_label", "Category Name"),
    ("مثلاً: ورزش ⚽️", "category_name_hint2", "e.g., Sports ⚽️"),
    ("انتخاب رنگ:", "choose_color", "Choose Color:"),
    ("ذخیره", "save", "Save"),
    ("مدیریت دسته‌بندی‌ها", "manage_categories", "Manage Categories"),
    ("افزودن دسته‌بندی جدید", "add_new_category", "Add New Category"),
    ("ویرایش", "edit", "Edit"),
    ("انتقال به بالا", "move_up", "Move Up"),
    ("انتقال به پایین", "move_down", "Move Down"),
    ("حذف", "delete", "Delete"),
    ("مدیریت یادآوری‌ها", "manage_reminders", "Manage Reminders"),
    ("بازگشت", "back", "Back"),
    ("یادآوری‌های فعال کارها", "active_reminders", "Active Task Reminders"),
    ("هیچ یادآوری فعالی ثبت نشده است.", "no_active_reminders", "No active reminders set."),
    ("رفع مشکل نوتیفیکیشن در گوشی‌های شیائومی", "xiaomi_fix", "Fix Notification Issue on Xiaomi Phones"),
    ("اگر نوتیفیکیشن‌های یادآوری را دریافت نمی‌کنید، مراحل زیر را در گوشی شیائومی، پوکو یا ردمی خود بررسی کنید:", "xiaomi_fix_desc", "If you don't receive reminder notifications, check the following steps on your Xiaomi, Poco, or Redmi phone:"),
    ("۱. تنظیمات گوشی (Settings) را باز کنید.", "xiaomi_step1", "1. Open phone Settings."),
    ("۲. به بخش برنامه‌ها (Apps) و سپس مدیریت برنامه‌ها (Manage Apps) بروید.", "xiaomi_step2", "2. Go to Apps > Manage Apps."),
    ("۳. برنامه «کارهای من» را پیدا کرده و انتخاب کنید.", "xiaomi_step3", "3. Find and select the 'My Tasks' app."),
    ("۴. گزینه «شروع خودکار» (Autostart) را فعال کنید تا برنامه بتواند در ساعت مشخص یادآوری را اجرا کند.", "xiaomi_step4", "4. Enable 'Autostart' so the app can run reminders at specific times."),
    ("۵. در بخش «صرفه‌جویی در باتری» (Battery Saver)، وضعیت را به «بدون محدودیت» (No Restrictions) تغییر دهید.", "xiaomi_step5", "5. In 'Battery Saver', change the status to 'No Restrictions'."),
    ("۶. مطمئن شوید که در بخش «اعلان‌ها» (Notifications)، مجوزهای لازم برای زنگ زدن و نمایش پاپ‌آپ فعال باشند.", "xiaomi_step6", "6. Make sure necessary permissions for ringing and pop-ups are enabled in 'Notifications'."),
    ("روزانه", "daily", "Daily"),
    ("یک روز در میان", "every_other_day_2", "Every Other Day"),
    ("هفتگی", "weekly", "Weekly"),
    ("حذف یادآوری", "delete_reminder", "Delete Reminder"),
    ("انتخاب تاریخ شمسی", "select_shamsi_date", "Select Shamsi Date"),
    ("سال:", "year", "Year:"),
    ("ماه:", "month", "Month:"),
    ("تایید", "confirm", "Confirm"),
    ("مدیریت دسته‌بندی", "manage_category_prefix", "Manage Category"),
    ("آیا می‌خواهید این دسته‌بندی و تمام کارهای آن را حذف کنید یا نام آن را ویرایش نمایید؟", "category_options_desc", "Do you want to delete this category and all its tasks or edit its name?"),
    ("ویرایش نام", "edit_name", "Edit Name"),
    ("حذف دسته‌بندی", "delete_category", "Delete Category"),
    ("ویرایش دسته‌بندی", "edit_category_title", "Edit Category"),
    ("نام جدید", "new_name", "New Name"),
    ("کارهای من", "my_tasks", "My Tasks"),
    ("تغییر پوسته", "change_theme", "Change Theme"),
    ("کار جدید", "new_task", "New Task"),
    ("دسته‌بندی نشده", "uncategorized", "Uncategorized"),
    ("هیچ کاری در این دسته وجود ندارد", "no_tasks_in_category", "No tasks in this category"),
    ("کارهای انجام‌شده", "completed_tasks", "Completed Tasks"),
    (" کار", "tasks_suffix", " Tasks"),
    ("افزودن کار در این دسته", "add_task_in_category", "Add task in this category"),
    (" ساعت ", "clock_suffix", " Time "),
    (" از ", "of", " of "),
    (" زیرکار", "subtasks_suffix", " Subtasks"),
    ("حذف کار", "delete_task", "Delete Task"),
    ("هنوز هیچ کاری ثبت نکرده‌اید! 🌟", "no_tasks_yet", "You haven't added any tasks yet! 🌟"),
    ("با زدن دکمه «کار جدید» اولین کار خود را بنویسید و کارهای بزرگ را به بخش‌های کوچک تقسیم کنید.", "add_first_task_desc", "Tap 'New Task' to write your first task and break large tasks into smaller parts."),
    ("یادآوری کارها", "reminder_channel_name", "Task Reminders"),
    ("کانال اعلان یادآوریهای لیست کارها", "reminder_channel_desc", "Notification channel for task list reminders"),
    ("یادآوری کار", "reminder_notification_title", "Task Reminder"),
    ("انجام شد", "done", "Done"),
    ("۱۰ دقیقه بعد", "snooze_10m", "10 Minutes Later")
]

jalali_months = [
    ("فروردین", "month_1", "Farvardin"),
    ("اردیبهشت", "month_2", "Ordibehesht"),
    ("خرداد", "month_3", "Khordad"),
    ("تیر", "month_4", "Tir"),
    ("مرداد", "month_5", "Mordad"),
    ("شهریور", "month_6", "Shahrivar"),
    ("مهر", "month_7", "Mehr"),
    ("آبان", "month_8", "Aban"),
    ("آذر", "month_9", "Azar"),
    ("دی", "month_10", "Dey"),
    ("بهمن", "month_11", "Bahman"),
    ("اسفند", "month_12", "Esfand"),
]

jalali_days = [
    ("شنبه", "saturday", "Saturday"),
    ("یکشنبه", "sunday", "Sunday"),
    ("دوشنبه", "monday", "Monday"),
    ("سه‌شنبه", "tuesday", "Tuesday"),
    ("چهارشنبه", "wednesday", "Wednesday"),
    ("پنج‌شنبه", "thursday", "Thursday"),
    ("جمعه", "friday", "Friday"),
]

replacements.extend(jalali_months)
replacements.extend(jalali_days)

def escape_regex(s):
    return re.escape(s)

# Ensure values-fa exists
if not os.path.exists("app/src/main/res/values-fa"):
    os.makedirs("app/src/main/res/values-fa")

def add_to_xml(file_path, key, value):
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        # Check if already exists
        for elem in root.findall("string"):
            if elem.get("name") == key:
                elem.text = value
                tree.write(file_path, encoding="utf-8", xml_declaration=True)
                return
        
        elem = ET.Element("string", name=key)
        elem.text = value
        root.append(elem)
        tree.write(file_path, encoding="utf-8", xml_declaration=True)
    except Exception as e:
        pass

for p_text, key, e_text in replacements:
    add_to_xml("app/src/main/res/values/strings.xml", key, e_text)
    add_to_xml("app/src/main/res/values-fa/strings.xml", key, p_text)

# We will just do a simple sed-like replacement in kt files
kt_files = [
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

for file in kt_files:
    if not os.path.exists(file): continue
    with open(file, "r") as f:
        content = f.read()
    
    # We must replace only strings in quotes where appropriate
    # This might be tricky, let's just do it carefully for exact matches
    for p_text, key, _ in replacements:
        # replace in code like "Persian text" -> stringResource(R.string.key)
        # Note: some are variables, some need context.getString
        if "com/example/util/" in file or "com/example/data/" in file:
            content = content.replace(f'"{p_text}"', f'context.getString(com.example.R.string.{key})')
        else:
            # Composable
            content = content.replace(f'"{p_text}"', f'stringResource(R.string.{key})')
            content = content.replace(f'stringResource(R.string.{key}).toPersianDigits()', f'stringResource(R.string.{key})')
            # Fix duplicate stringResource when we run it multiple times
            
    with open(file, "w") as f:
        f.write(content)

