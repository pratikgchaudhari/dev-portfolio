---
title: Linux File System Structure
date: 2025-09-07
summary: A tour of the Linux directory tree and the roles of its main system directories.
tag: Linux
draft: false
---

The Linux file system structure follows a hierarchical, tree-like organization starting from the root directory, denoted as `/`. It adheres to the Filesystem Hierarchy Standard (FHS), which defines the purpose of key directories. Below is a concise description of the Linux file system structure and the roles of its primary directories:

- **/ (Root)**: The top-level directory, containing all other directories and files.
- **/bin**: Essential binary files (executables) for basic system commands, like `ls`, `cp`, and `cat`, used by all users.
- **/boot**: Contains boot loader files, such as the kernel image and initial RAM disk, needed to start the system.
- **/dev**: Device files representing hardware devices (e.g., `/dev/sda` for a disk, `/dev/null` for a null device).
- **/etc**: System-wide configuration files, such as `/etc/passwd` for user info and `/etc/fstab` for file system mounts.
- **/home**: User home directories (e.g., `/home/username`), where personal files and user-specific configurations are stored.
- **/lib** and **/lib64**: Shared libraries required by binaries in `/bin` and `/sbin`, with `/lib64` for 64-bit libraries.
- **/media**: Mount points for removable media, like USB drives or CDs, typically managed by the system.
- **/mnt**: Temporary mount points for manually mounted file systems, such as external drives.
- **/opt**: Optional software packages, often third-party or manually installed applications.
- **/proc**: Virtual file system providing process and system information (e.g., `/proc/cpuinfo` for CPU details).
- **/root**: Home directory for the root (superuser) account, separate from `/home`.
- **/run**: Runtime data for processes, such as PID files or sockets, cleared on reboot.
- **/sbin**: System binaries for administrative tasks, like `fdisk` or `reboot`, typically for root use.
- **/srv**: Data for services hosted by the system, such as web or FTP server files.
- **/sys**: Virtual file system for system and hardware information, similar to `/proc` but focused on kernel and devices.
- **/tmp**: Temporary files, often cleared on reboot or by specific system policies.
- **/usr**: Secondary hierarchy for user-related data, including:
    - **/usr/bin**: Non-essential user binaries (e.g., `gcc`, `python`).
    - **/usr/lib**: Libraries for `/usr/bin` programs.
    - **/usr/local**: Locally installed software, not managed by the package manager.
    - **/usr/share**: Architecture-independent data, like documentation or icons.
    - **/usr/src**: Source code, often for the kernel or other software.
- **/var**: Variable data that changes during system operation, including:
    - **/var/log**: Log files (e.g., `/var/log/syslog` or `/var/log/messages`).
    - **/var/cache**: Cached data for applications.
    - **/var/spool**: Data awaiting processing, like print jobs or mail.
    - **/var/tmp**: Temporary files preserved across reboots.