// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.operation.SubSystem;

public class GyroSystem extends SubSystem {
    ADXRS450_Gyro gyro = new ADXRS450_Gyro();
    double offset = 0;

    ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("GyroSystem");
    NetworkTableEntry gyroAngleEntry = shuffleboardTab.add("Angle", 0).withWidget(BuiltInWidgets.kGyro).getEntry();
    NetworkTableEntry gyroReadyEntry = shuffleboardTab.add("Ready", false).getEntry();

    public GyroSystem() {
        super();
    }

    /**
     * reset to targeted angle
     */
    public void reset(double angle) {
        this.gyro.reset();
        offset = angle;
    }

    /**
     * reset to zero
     */
    public void reset() {
        this.reset(0);
    }

    /**
     * return true if succeed
     */
    public boolean calibrate() {
        this.gyro.calibrate();
        return true;
    }

    public boolean isReady() {
        return this.gyro.isConnected();
    }

    /**
     * get angle in degrees. this will continue from 360 to 361 degrees
     */
    public double getAngle() {
        return this.gyro.getAngle();
    }

    @Override
    protected void update() {
        // send data to dashboards here
        gyroAngleEntry.setNumber(this.getAngle());
        gyroReadyEntry.setBoolean(this.isReady());
    }
}
