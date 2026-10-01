---
title: JavaScript's Déjà Vu: Repeating Java's Finalization Mistake
date: 2024-06-30
summary: A critique of FinalizationRegistry through the lessons of Java's finalization mechanism.
tag: JavaScript
draft: false
---

Let's dive into JavaScript’s `FinalizationRegistry` class, an attempt to emulate Java’s deprecated and buggy `finalize()` method.

For those unfamiliar, Java’s `finalize()` method allowed objects to clean up after themselves before being reclaimed by the garbage collector. JavaScript’s `FinalizationRegistry` offers developers a similar capability.

Given that both Java and JavaScript have automated garbage collection, this seems like a handy feature. However, there’s a significant catch: neither language guarantees that the cleanup code will run immediately after garbage collection—or even at all.

Yes, you read that correctly. Your cleanup code might never run. So why include such a feature?

In Java’s case, `finalize()` was a poor design choice that was later corrected. Surprisingly, JavaScript introduced the `FinalizationRegistry` class after Java had already deprecated `finalize()` and offered better alternatives.

As a developer who works with both Java and JavaScript, I can’t help but notice JavaScript's peculiar obsession with mimicking Java. How peculiar? JavaScript added `FinalizationRegistry` in 2021, three years after Java deprecated `finalize()`!

This isn’t to say that JavaScript shouldn’t adopt Java-like features. It should! Every language borrows features from others to improve itself and enhance the developer experience. But adopting a feature known to be problematic and unreliable makes little sense.

The TC39 proposal that introduced `FinalizationRegistry` results in behavior that varies across different JavaScript VMs and can be influenced by their garbage collection strategies. Testing code that uses `FinalizationRegistry` highlights its unpredictability.

In Node.js, testing `FinalizationRegistry` involves:
1. Invoking the script with the `--expose-gc` flag.
2. Nullifying the object’s reference that’s registered.
3. Explicitly calling the `gc()` method on the `global` object.

In Google Chrome, you need to click the `Collect Garbage` icon in the Performance tab to see your code's effects.

It's like the Grim Reaper of garbage collection asking for any final wishes before reclaiming memory, but then ignoring those wishes altogether.

Perhaps there’s a scenario where `FinalizationRegistry` shines, but so far, I haven’t found one.

Want to see code? Head over to the following gist:

https://gist.github.com/pratikgchaudhari/35e7af37b4df369d02103f05db5782c1

#java #javascript #finalize #finalizationregistry #garbagecollection