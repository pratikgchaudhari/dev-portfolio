---
title: Soft Links in Linux
date: 2025-09-12
summary: Create and inspect symbolic links, with examples for files, directories, and relative paths.
tag: Linux
draft: false
---

Soft links are pointers to files or directories, allowing access from multiple locations without duplicating the original data.
- **Use the `ln` command**:
    - Syntax: `ln -s [source_file_or_directory] [link_name]`
    - `-s`: Specifies a symbolic (soft) link.
    - `source_file_or_directory`: Path to the original file or directory.
    - `link_name`: Name or path of the soft link to create.
- **Example to create a soft link to a file**:
    - Command: `ln -s /path/to/original/file.txt /path/to/link`
    - Creates a soft link named `link` pointing to `file.txt`.
- **Example to create a soft link to a directory**:
    - Command: `ln -s /path/to/original/dir /path/to/link_dir`
    - Creates a soft link named `link_dir` pointing to `dir`.
- **Verify the link**:
    - Use: `ls -l`
    - Soft links are shown with an `l` in the permissions and an arrow (`->`) pointing to the source.
- **Relative vs. absolute paths**:
    - Use absolute paths for stability if the source might move.
    - Use relative paths for portability within the same directory structure.
- **Overwrite existing links**:
    - Add `-f` to force overwrite: `ln -sf [source] [link_name]`.
- **Remove a soft link**:
    - Use: `rm [link_name]`
    - This deletes the link without affecting the source.
- **Check for broken links**:
    - If the source is deleted or moved, the link becomes broken (dangling).
    - Use `ls -l` to identify broken links (they may appear in red in some terminals).
- **Permissions**:
    - Soft links inherit the source’s permissions but can be created/deleted by users with appropriate access to the link’s location.