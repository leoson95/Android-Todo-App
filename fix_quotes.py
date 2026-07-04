import re

files = ["app/src/main/res/values/strings.xml", "app/src/main/res/values-fa/strings.xml"]

for file in files:
    with open(file, "r") as f:
        content = f.read()
    
    # We must properly escape single quotes in text content of strings
    # But only inside <string ...>...</string>
    def escape_apostrophe(match):
        # match.group(1) is the <string...>
        # match.group(2) is the content
        # match.group(3) is </string>
        content = match.group(2)
        # Escape apostrophes that are not already escaped
        content = re.sub(r"(?<!\\)'", r"\'", content)
        return match.group(1) + content + match.group(3)

    content = re.sub(r'(<string[^>]*>)(.*?)(</string>)', escape_apostrophe, content, flags=re.DOTALL)
    
    with open(file, "w") as f:
        f.write(content)

