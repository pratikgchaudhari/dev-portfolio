---
title: Dat(ing) in Linux
date: 2025-09-13
summary: Display and format dates, work with timestamps, and use the Linux date command.
tag: Linux
draft: false
---

- **Basic Usage**: `date` - Displays the current date and time in the default format (e.g., `Sat Sep 13 11:40:25 IST 2025`).
- **Format Output**: `date +FORMAT` - Customizes output using format specifiers:
    - `+%Y`: 4-digit year (e.g., 2025).
    - `+%m`: Month (01-12).
    - `+%d`: Day of the month (01-31).
    - `+%H`: Hour (00-23).
    - `+%M`: Minute (00-59).
    - `+%S`: Second (00-59).
    - `+%Z`: Time zone (e.g., IST).
    - Example: `date "+%Y-%m-%d %H:%M:%S"` outputs `2025-09-13 11:40:25`.
- **Set Date/Time**: `date -s "STRING"` - Sets system date/time (requires root privileges).
    - Example: `sudo date -s "2025-09-13 11:40:00"` sets date and time.
- **Display UTC Time**: `date -u` - Shows date/time in UTC.
- **Epoch Time**: `date +%s` - Outputs seconds since Unix epoch (Jan 1, 1970, 00:00:00 UTC).
- **Convert Epoch to Date**: `date -d @EPOCH` - Converts epoch time to human-readable format.
    - Example: `date -d @1694582425` displays the date for that epoch timestamp.
- **Relative Time**: `date -d "STRING"` - Interprets relative time (e.g., `date -d "yesterday"`, `date -d "2 days ago"`).
- **RFC 3339 Format**: `date --rfc-3339=TYPE` - Outputs date in RFC 3339 format (TYPE: `date`, `seconds`, or `ns`).
    - Example: `date --rfc-3339=seconds` outputs `2025-09-13 11:40:25+05:30`.
- **Help**: `date --help` - Displays command options and usage.
- **Man Page**: `man date` - Provides detailed documentation.