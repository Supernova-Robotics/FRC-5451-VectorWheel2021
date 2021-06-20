// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.operations;

import frc.operation.*;

public class TemplateOperation extends Operation {

    // custom properties
    public int property1 = 10;
    public double property2 = 3.14;

    public TemplateOperation(int prop1) {
        super();
        // set the priority of this operation
        opPriority = 1;
        opDaemon = false;

        property1 = prop1;
    }

    @Override
    protected OpState invoke(Context context) {
        // do some initiallization here
        return super.invoke(context);
    }

    @Override
    protected OpState execute(Context context) {
        // do whatever this operation is for
        return OpState.FINISHED;
    }

    @Override
    protected void onInterrupt(Context context) {
        // cleanup when the operation is interrupted
        super.onInterrupt(context);
    }

    public static boolean poll(Context context) {
        // a convinent method to let other code know if this operation is runnable
        return true;
    }
}
