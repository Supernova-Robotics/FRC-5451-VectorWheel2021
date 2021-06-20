package frc.robot.utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;



public class VectorWheel {
    TalonFX motorTurning;
    TalonFX motorDriving;

    CANCoder coder;
    double coderZero;

    double currentAngle = 0;
    double currentDirection = 1;
    double angleOffset;

    public VectorWheel(int turningPort, int drivingPort, int coderPort, double coderZero) {
        motorTurning = new TalonFX(turningPort);
        motorDriving = new TalonFX(drivingPort);

        if (coderPort < 0) {
            coder = null;
        } else {
            coder = new CANCoder(coderPort);
        }

        this.coderZero = coderZero;

        this.calibrate();
    }
    public void calibrate(){
        motorTurning.setSelectedSensorPosition(0);
        motorDriving.setSelectedSensorPosition(0);

        double rawStartAngle = this.getCoder();
        double startAngle = rawStartAngle - this.coderZero;
        this.angleOffset = this.toTrueAngle(startAngle);
    }

    public double getCoder(){
        if(this.coder == null){
            return -114514;
        }
        return this.coder.getAbsolutePosition() / 180.0 * Math.PI;
    }

    public double getAngleOffset(){
        return this.angleOffset;
    }

    public double getCurrentAngle(){
        return this.currentAngle;
    }

    /**
     * 
     * @param speed double from -1 to 1
     * @param angle double from -pi to pi
     */
    public void drive(double speed, double angle) {
        angle = angle - this.angleOffset;

        if (speed > 0.05) {
            AngleDirection ret = this.toNearestAngleDirected(angle);
            this.currentAngle = ret.angle;
            this.currentDirection = ret.direction;
        }
        motorTurning.set(ControlMode.Position, angleToEncoder(currentAngle));
        motorDriving.set(ControlMode.PercentOutput, speed * this.currentDirection);
    }

    /**
     * 
     * @param x right
     * @param y forward
     */
    public void driveXY(double x, double y) {
        double speed = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double angle = Math.atan2(y, x);

        this.drive(speed, angle);
    }

    /**
     * 
     * @param angle -pi to pi
     * @return the corrosponding encoder value
     */
    private double angleToEncoder(double angle) {
        return 1.0 * angle / (2 * Math.PI) * 25800;
    }

    private double toTrueAngle(double angle) {
        double trueAngle = angle % (2 * Math.PI);
        if (trueAngle < 0) {
            trueAngle += 2 * Math.PI;
        }
        return trueAngle;
    }

    private AngleDirection toNearestAngleDirected(double targetAngle) {
        AngleDirection ret = new AngleDirection();
        ret.angle = this.currentAngle;
        ret.direction = this.currentDirection;

        double currentTrueAngle = toTrueAngle(this.currentAngle);
        double targetTrueAngle = toTrueAngle(targetAngle);

        double offset = toTrueAngle(targetTrueAngle - currentTrueAngle);

        if (offset < Math.PI) {
            // upper half
            if (offset < Math.PI / 2) {
                // First quadrant
                // offset as it is
                ret.direction = 1;
            } else {
                // Second quadrant
                // reverse direction
                offset -= Math.PI;
                ret.direction = -1;
            }
        } else {
            // lower half
            offset -= 2 * Math.PI; // map to negative
            if (offset > -Math.PI / 2) {
                // Forth quadrant
                // offset as it is
                ret.direction = 1;
            } else {
                // Third quadrant
                // reverse direction
                offset += Math.PI;
                ret.direction = -1;
            }
        }
        ret.angle += offset;
        return ret;
    }
}