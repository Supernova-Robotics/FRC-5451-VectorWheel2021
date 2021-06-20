// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.operations;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.operation.*;
import frc.robot.Robot;
import frc.robot.subsystems.Chassis;
import frc.robot.utils.BoardUtils;
import frc.robot.utils.VectorWheel;

public class TestOp extends Operation {

    // custom properties
    private double speed;
    ShuffleboardTab tab = Shuffleboard.getTab("TESTOP");
    NetworkTableEntry coderEntry = BoardUtils.tryAddEntry(tab, "Coder", -114514);
    NetworkTableEntry angleOffsetEntry = BoardUtils.tryAddEntry(tab, "AngleOffset", -114514);
    NetworkTableEntry currentAngleEntry = BoardUtils.tryAddEntry(tab, "CurrentAngle", -114514);

    public TestOp(double speedMul) {
        super();
        speed = speedMul;
    }

    @Override
    protected OpState invoke(Context context) {
        // do some initiallization here
        // Robot.chassis.getWheel(2).calibrate();
        Robot.chassis.testDrive(2, 0, 0);
        return OpState.RUNNING;
    }

    @Override
    protected OpState execute(Context context) {
        if(context.getOpMode() != OpMode.TELEOP){
            this.checkAndStop();
            return OpState.FINISHED;
        }
        
        boolean has_ownership = Robot.chassis.capture(this);
        if(has_ownership){
            double left = Robot.stick.getRawAxis(1);
            double right = Robot.stick.getRawAxis(5);

            Robot.chassis.testDrive(2, left * this.speed, right*Math.PI);
            
        }
        VectorWheel wheel = Robot.chassis.getWheel(2);
        coderEntry.setNumber(wheel.getCoder());
        currentAngleEntry.setNumber(wheel.getCurrentAngle());
        angleOffsetEntry.setNumber(wheel.getAngleOffset());

        return OpState.RUNNING;
    }

    @Override
    protected void onInterrupt(Context context) {
        // cleanup when the operation is interrupted
        this.checkAndStop();
    }

    public static boolean poll(Context context) {
        // a convinent method to let other code know if this operation is runnable
        return true;
    }

    void checkAndStop(){
        if(Robot.chassis.isCurrentOwner(this)){
            Robot.chassis.testDrive(0, 0, 0);
        }
    }
}
