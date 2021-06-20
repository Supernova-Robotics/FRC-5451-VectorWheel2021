// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.operations;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.operation.*;
import frc.robot.Robot;

public class HeadlessDrive extends Operation {
    private double speed;

    public HeadlessDrive(double speedMul) {
        super();
        speed = speedMul;
    }

    @Override
    protected OpState invoke(Context context) {
        // do some initiallization here
        return OpState.RUNNING;
    }

    @Override
    protected OpState execute(Context context) {
        if(context.getOpMode() != OpMode.TELEOP){
            this.checkAndStop();
            return OpState.FINISHED;
        }

        if(!Robot.gyro.isReady()){
            this.report(ReportType.ERROR, "Gyro is not Ready!");
            return OpState.RUNNING;
        }
        
        boolean has_ownership = Robot.chassis.capture(this);
        if(has_ownership){

            double forward = -1 * Robot.stick.getRawAxis(1);
            double right = Robot.stick.getRawAxis(0);
            double turn = -1 * Robot.stick.getRawAxis(4);

            Translation2d direction = new Translation2d(right, forward);
            double gyroFacing = Robot.gyro.getAngle() / 180 * Math.PI;
            direction = direction.rotateBy(new Rotation2d(gyroFacing));

            Robot.chassis.drive(direction.getY() * this.speed, direction.getX() * this.speed, turn * this.speed);
        }

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
