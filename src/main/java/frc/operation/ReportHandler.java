// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.operation;

/** Add your docs here. */
public interface ReportHandler {
    public void reportMessage(Operation operation, ReportType type, String message);
    public void updateReport(Context context);
}
