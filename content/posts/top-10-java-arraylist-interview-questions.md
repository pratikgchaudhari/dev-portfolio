---
title: Top 10 Java ArrayList Interview Questions
date: 2025-09-13
summary: Interview questions on ArrayList storage, resizing, access costs, iteration, and thread safety.
tag: Java
draft: false
---

1. **What is an ArrayList in Java?**  
   > A dynamic array-based data structure that allows resizing and provides fast random access.

2. **How does ArrayList differ from an array?**  
   > ArrayList is resizable, while arrays have a fixed size.

3. **What is the default initial capacity of an ArrayList?**  
   > The default initial capacity is 10.

4. **How does ArrayList handle resizing?**  
   > It grows by 50% of its current size when full, copying elements to a new array.

5. **Is ArrayList synchronized?**  
   > No, ArrayList is not synchronized; use Collections.synchronizedList for thread safety.

6. **What is the time complexity of get() and set() in ArrayList?**  
   > Both get() and set() have O(1) time complexity.

7. **How do you add an element to a specific index in ArrayList?**  
   > Use the add(index, element) method to insert at a specific position.

8. **What happens if you add an element beyond the ArrayList’s size?**  
   > It throws an IndexOutOfBoundsException.

9. **How can you iterate over an ArrayList?**  
   > Use a for loop, enhanced for loop, Iterator, or forEach method.

10. **What is the difference between ArrayList and LinkedList?**  
    > ArrayList uses a dynamic array for fast access; LinkedList uses a doubly-linked list for faster insertions/deletions.