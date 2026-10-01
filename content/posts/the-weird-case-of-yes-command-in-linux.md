---
title: The weird case of 'yes' command in Linux
date: 2025-09-14
summary: What the yes command does, how to use it in pipelines, and why it behaves as it does.
tag: Linux
draft: false
---

The `yes` command in Linux is a simple utility that outputs a string repeatedly until interrupted.

- **Basic Functionality**:
    - Continuously prints "y" (or a specified string) to the terminal until the process is stopped (e.g., with Ctrl+C).
    - Default output is "y" followed by a newline.

- **Syntax**:
    - `yes [STRING]` — If no string is provided, it outputs "y". If a string is given, it repeats that instead (e.g., `yes hello` prints "hello" repeatedly).

- **Purpose**:
    - Primarily used to automate responses to interactive scripts or commands that prompt for confirmation (e.g., `apt-get` or `rm -i`).
    - Simulates user input by providing a constant stream of "y" or another string.

- **Common Use Cases**:
    - Automating package installations: `yes | sudo apt-get install package` to auto-confirm prompts.
    - Stress testing scripts or systems by generating continuous output.
    - Creating large test files: `yes "test" | head -n 1000 > file.txt` to write "test" 1000 times to a file.

- **Weird Traits**:
    - Extremely fast output can overwhelm terminals or consume CPU if not redirected or limited.
    - No built-in delay or rate-limiting, making it a "firehose" of text.
    - Minimalistic design: no options beyond specifying the output string (e.g., no flags for speed control).

- **Stopping the Command**:
    - Must be interrupted manually (Ctrl+C) or piped to a command that limits output (e.g., `head`, `tail`).
    - If misused (e.g., `yes > file`), it can quickly fill disk space.

- **Examples**:
    - `yes | rm -i *.txt`: Automatically confirms deletion of multiple files.
    - `yes "okay" | head -n 5`: Outputs "okay" five times and stops.
    - `yes | while true; do echo "Looping"; sleep 1; done`: Feeds "y" into a loop for automation.

- **Limitations**:
    - Only outputs one string; no support for complex patterns or multiple inputs.
    - Not ideal for nuanced automation where varied responses are needed.

- **Fun Fact**:
    - Its simplicity makes it a favorite in scripting for quick-and-dirty automation, but it’s also a classic example of a Unix tool that does one thing and does it well (or excessively).