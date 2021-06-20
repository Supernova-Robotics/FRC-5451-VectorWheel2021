# FRC Operation System
A customly made command system, aiming to be more flexable and easier to use than the offical one.

**Still in very early development stage. You should not use this.**

This is a lightweight and flexable parallel task system for iterative(timed) robot.
It aims to provide a simpler while more powerful framwork than the offical command system.
To distinguish from the offical one, the "commands" in this system is called "operations"

# Overview
This system provides three functionalities:
- Parallel tasking
- Task priority
- A way to send messages/warnings/errors to user

All you need to care about when using this system is:
- the Operation class
- the Subsystem class
- the OpManager class

This is how these classes work together:

First of all, The entire robot is broken down into subsystems in the same way the offical system does
it. You simply warp the hardwares into higher level functions. However, this system provides a different way to handle priority("required subsystem" in the offical system). This allows dynamically change the priority of tasks(operations) and capture and release subsystems when ever you want. This simplifies the structure and relationship between commands(operations) and allow more flexability.

Then, you write all operations (i.e. turning motors, flashing lights, or monitoring cameras) in subclasses of the Operation class. The Operation class provide methods for you to override to handle parallel tasking, messaging, and the priority stuff.

Finally, the OpManager is used to manage the operations. It keep a list of operations and call everything needed. You don't have a lot of do with this class, just make a instance of it when the program start, initiallize it, and call "update" in each iteration.

# How to use
## Preparations
You need to instanciate a `OpManager` class in `Robot.java` and call its `update()` method in each iteration. Then you need to call the `setMode()` method each time the robot enters a different mode (like Disabled, Teleop, etc.). This tells the manager to stop some operations when needed. And that's all!

## Adding a Subsystem
Make a new subclass of the `Subsystem` class (Becareful, use the one in `***.frc.operation`, not the offical one in `***.first.wpilibj2.command`). For this class, you don't need to override anything, just impliment your wraper functions and give it a cool name.

Then, to make it accessable to operations (which we will talk about next), you need to instanciate the class you made and assign it to a `public static` variable. This make it accessable from any part of your code. Generally, global variables are not good practices, but in this case it is used since it improves the readability so much. 

## Adding an Operation
Finally we are getting to the interesting part.

`Operation` here is roughly the same as `CommandBase` in the offical command system, with a little difference in structure.

### Constructor
First, you may want to have a constructor. You can add parameters in your constructor as all instances of the operation will be created by user code, which means you can pass whatever you want to it.

In addition, you can set `opPriority` and `opDaemon` like this:
```java
opPriority = 1;
opDaemon = false;
```

`opPriority` is assessed when your operation trys to gain ownership of some subsystems. If this operation has the same or higher priority than the current owner, you get the ownership, and the ownership will be returned to the original owner when you release it. The default value is 0. (more on this later)

`opDaemon` defines weather this operation will be interrupted when the robot is in Disabled mode. The name comes from a linux concept of background process. Set this to true to prevent interruption when switched to Disabled mode. Default to false.

### Override methods
There are four methods you need to know:
```java
protected OpState invoke(Context context);
protected OpState execute(Context context);
protected void onInterrupt(Context context);
public static boolean poll(Context context);
```

Their names are pretty self explanitory, and you can override them to impliment you own logic. There are also an `TemplateOperation.java` that you can use to save time.

The `invoke()` method is called once when the operation start. Overriding is optional, but you may need it to do some initiallization, such as gaining ownership of needed subsystems.

The `execute()` method is called after `invoke()` is called. It will be called repeatly if the operation is a long-running one (explained below). Use this as the main body of your operation. You must override this or the complie will fail.

Note that the return value of `invoke()` and `execute()` are `OpState`. This is a enum value that determines whether this operation will be condinued. Return `RUNNING` to ask for another call(s) on `execute()`, return `CANCELED` or `FINISHED` to end the operation. `WAITING` and `INTERRUPTED` are meant to be used by the `OpManager`, but you can still use them if needed (But avoid this as much as you can!).

The `onInterrupt` is called when the operation is interrupted. Override is optional. You many want to do some final cleanup in this.

The `poll` is added as a "helper". It should return the availablity of this operation in the context, i.e. whether this operation can be runned in the context. You can call this before deciding to start a operation. Note that this is a static method, so no instanciation is needed.

Finally, the `Context` object. This object gives some useful information on the current situation, such as time, delta time since last run, teleop mode or auto mode, etc. More will be added in the future.

### Priority and Ownership
The priority and ownership system is introduced to prevent conflicts between operations. When two different operations tries to control one subsystem at the same time, strange things can happen (motor shaking like crazy).

This system will not prevent you from doing anything, but provides a good rule to follow which can make your code safer.

Before you call any functions in any subsystems, you should check if you "own" the subsystem using `<some_subsystem>.isCurrentOwner(this);`. If you are the current owner, you can do any thing on it safely. Otherwise, it means that some other operation is using it, and things can go wrong if this operation access it as well.

The subsystem keep its owners in a stack. When an operation captures (gains ownership) it, the operation is added on top of the stack, overriding the old owner. When this operation releases (gives up ownership), it is removed from the stack and the original owner gains ownership again. Also, the subsystem removes ended operations automatically.

To gain ownership of a subsystem, use `<some_subsystem>.capture(this);`. It will compare the priority of its current owner and this operation, and will return true if you have equal or higher priority, meaning the capture successes (or you can check again using `isCurrentOwner()`). To give up the ownership, call `<some_subsystem>.release(this);`. This makes the subsystem accessable by other operations again.

### Starting and Interrupting
To start a new operation, you need to make a new instance of the operation and send it to the OpManager by `startOperation()`. The OpManager is accessable through the Context object if you are inside a operation. You can also call `this.runOperation()` inside a operation. This will block until the new operation is done and return the end state of the operation, so you should only use instant-finishing operation with it.

To interrupt an operation, call `operation.interrupt()`. It will finish up and be removed from the OpManager list.

# Starting the Robot
Usually you need to "manually" start several operations in the `Robot.java` when the robot boots. After this, you can let these operations start new operations. To terminate all operations (like when the robot is switched from Auto to Teleop), you can call `OpManager.interruptAll()` or `OpManager.interruptNonDaemon()`, depending on what you want.

# TODOs
- add helper classes like sequential/parallel operations
- add helper classes on reading handle input and using motor output (a little out of scope, maybe in another project)
