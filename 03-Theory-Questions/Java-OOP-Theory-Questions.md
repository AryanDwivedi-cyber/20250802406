# 10 Java OOP Theory Questions — Advanced Level

## 1. Explain the four pillars of OOP with examples.
**Answer:** The four pillars are encapsulation, inheritance, polymorphism, and abstraction. Encapsulation protects data by keeping fields private and providing methods to access them. Inheritance allows a class to reuse and extend another class. Polymorphism allows one reference or method name to represent different behaviours, such as method overriding. Abstraction hides implementation details and exposes essential operations through abstract classes and interfaces. Together, these concepts make programs modular, reusable, and easier to maintain.

## 2. Differentiate method overloading and method overriding.
**Answer:** Overloading means using the same method name with different parameter lists in the same class or an inherited context. It is resolved at compile time. Overriding occurs when a subclass provides its own implementation of an inherited method with a compatible signature. It supports runtime polymorphism. Return type alone cannot distinguish overloaded methods.

## 3. Why is encapsulation important? How is it achieved in Java?
**Answer:** Encapsulation bundles data and the methods that operate on it, while restricting direct access to internal state. In Java it is commonly achieved by declaring fields `private` and providing controlled public methods such as getters, setters, or domain-specific operations. Validation can be performed before changing an object's state.

## 4. Compare an abstract class and an interface.
**Answer:** An abstract class can have instance fields, constructors, concrete methods, and abstract methods. A class can extend only one class. An interface defines a contract that a class can implement alongside other interfaces. Interfaces can contain abstract, default, static, and private methods. Use an abstract class when related classes share state or implementation; use an interface to describe a capability or contract.

## 5. What is runtime polymorphism in Java?
**Answer:** Runtime polymorphism occurs when an overridden instance method is selected according to the actual object type at runtime. For example, `Animal a = new Dog(); a.sound();` calls `Dog`'s overridden `sound()` method. This lets code work with a common parent type while allowing each subclass to provide its own behaviour.

## 6. Explain exception handling and the purpose of try, catch, finally, throw, and throws.
**Answer:** Exception handling prevents abnormal conditions from terminating a program unexpectedly. `try` encloses code that may fail; `catch` handles a matching exception; `finally` runs during normal or exceptional completion in typical control flow and is often used for cleanup. `throw` explicitly throws an exception object. `throws` declares exceptions that a method may pass to its caller.

## 7. Differentiate checked and unchecked exceptions.
**Answer:** Checked exceptions are checked by the compiler and must be caught or declared, for example `IOException`. Unchecked exceptions are subclasses of `RuntimeException` and do not have this compile-time requirement, for example `NullPointerException` and `ArithmeticException`. Checked exceptions often represent recoverable external conditions; unchecked exceptions often indicate programming errors, though context matters.

## 8. What is multithreading? Explain two ways to create a thread.
**Answer:** Multithreading allows multiple threads of execution within one process. It can improve responsiveness and allow tasks to overlap. A class can extend `Thread` and override `run()`, or implement `Runnable` and pass its object to a `Thread`. Call `start()` to schedule a new thread; calling `run()` directly does not start a separate thread. Shared mutable data may require synchronization.

## 9. What is the Java Collections Framework? Compare ArrayList and HashSet.
**Answer:** The Collections Framework provides interfaces and classes for storing and processing groups of objects. `ArrayList` is a resizable list that preserves insertion order and permits duplicates. `HashSet` stores unique elements and does not guarantee iteration order. Choose a list when position or duplicates matter; choose a set when uniqueness matters.

## 10. Explain the role of `equals()` and `hashCode()` in hash-based collections.
**Answer:** `equals()` defines logical equality between objects. `hashCode()` provides an integer used by hash-based collections to locate buckets. If two objects are equal according to `equals()`, they must return the same hash code. Unequal objects may share a hash code. When overriding `equals()`, override `hashCode()` consistently as well, or collections such as `HashMap` and `HashSet` may behave incorrectly.
