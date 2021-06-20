// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;



import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.operation.SubSystem;
import frc.robot.utils.VectorWheel;

public class Chassis extends SubSystem {
    VectorWheel wheels[];
    Translation2d wheelPositions[];

    ShuffleboardTab shuffleTab = Shuffleboard.getTab("Chassis");
    NetworkTableEntry ySpeedEntry = shuffleTab.add("Forward", 0).getEntry();
    NetworkTableEntry xSpeedEntry = shuffleTab.add("Right", 0).getEntry();
    NetworkTableEntry zSpeedEntry = shuffleTab.add("Turn", 0).getEntry();

    /**
     * y+ is forward, x+ is right, turn is counter clockwise
     */
    public Chassis(int turnings[], int drivings[], Translation2d positions[], int coders[], double coderZeros[]) {
        super();

        // init wheels
        wheels = new VectorWheel[4];
        wheelPositions = positions.clone();

        for (int i = 0; i < 4; i++) {
            wheels[i] = new VectorWheel(turnings[i], drivings[i], coders[i], coderZeros[i]);
        }
    }

    /**
     * Test individual motor
     */
    public void testDrive(int n, double speed, double angle) {
        wheels[n].drive(speed, angle);
    }

    public VectorWheel getWheel(int n){
        return this.wheels[n];
    }

    /**
     * y+ is forward, x+ is right, turn is counter clockwise
     */
    public void drive(double forward, double right, double turnZ) {
        ySpeedEntry.setNumber(forward);
        xSpeedEntry.setNumber(right);
        zSpeedEntry.setNumber(turnZ);

        turnZ = turnZ * 0.5;
        Translation2d xyVec = new Translation2d(right, forward);

        // calc velocity of each wheel
        Translation2d[] wheelVelocities = new Translation2d[4];
        double maxLenth = 0;

        for (int i = 0; i < 4; i++) {
            Translation2d velocity = new Translation2d();
            velocity = velocity.plus(xyVec);

            Translation2d turnVec = new Translation2d();
            turnVec = turnVec.plus(wheelPositions[i]);
            turnVec = turnVec.rotateBy(new Rotation2d(2 * Math.PI / 4));
            turnVec = turnVec.times(turnZ);

            wheelVelocities[i] = velocity.plus(turnVec);

            double l = wheelVelocities[i].getNorm();
            if (l > maxLenth) {
                maxLenth = l;
            }
        }

        // scale down if greater than one
        if (maxLenth > 1) {
            for (int i = 0; i < 4; i++) {
                wheelVelocities[i] = wheelVelocities[i].times(1.0 / maxLenth);
            }
        }

        if (maxLenth < 0.1) {
            for (int i = 0; i < 4; i++) {
                wheels[i].drive(0, 0);
            }
        } else {
            // drive
            for (int i = 0; i < 4; i++) {
                Translation2d velocity = wheelVelocities[i];
                wheels[i].driveXY(velocity.getX(), velocity.getY());
            }
        }
    }

    @Override
    protected void update() {
        // send data to dashboards
    }
}
