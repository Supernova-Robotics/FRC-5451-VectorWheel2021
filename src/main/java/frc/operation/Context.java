// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.operation;

/** Object used to represent the running context of operation */
public class Context { // a class that provide running context
    OpManager opManager;
    OpMode opMode;
    double timeTotal;
    double timeDelta;

    public OpManager getOpManager(){
        return opManager;
    }

    public OpMode getOpMode(){
        return opMode;
    }

    public double getTimeDelta(){
        return timeDelta;
    }

    public double getTimeTotal(){
        return timeTotal;
    }
}