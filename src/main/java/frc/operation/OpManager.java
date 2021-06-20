// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.operation;

import java.util.ArrayList;
import java.util.Iterator;

import edu.wpi.first.wpilibj.Timer;

/** The operation Manager */
public class OpManager implements ReportHandler{
    ArrayList<Operation> opList = new ArrayList<Operation>();
    ArrayList<Operation> opbufList = new ArrayList<Operation>(); // to avoid modify during iteration
    ArrayList<SubSystem> subsystemList = new ArrayList<SubSystem>();
    OpMode opMode = OpMode.NONE;
    ReportHandler reportHandler;

    Timer timer = new Timer();
    double timeLastUpdate = 0;
    double timeDelta = 0;

    /** Initiallize the manager */
    public void init(ReportHandler handler) {
        this.reportHandler = handler;
        timer.reset();
        timer.start();
        timeLastUpdate = timer.get();
        timeDelta = 0;
    }

    /** Initiallize the manager with the default ReportHandler */
    public void init() {
        this.init(new ReportSender(3));
    }

    /** Register the subsystem so that the update method can be called */
    public void register(SubSystem subsystem){
        if (!this.subsystemList.contains(subsystem)) {
            this.subsystemList.add(subsystem);
        }
    }

    public void setMode(OpMode mode) {
        this.opMode = mode;
        if (mode == OpMode.DISABLED) {
            this.interruptNonDaemon();
        }
    }

    /** call this periodically to run all operations. */
    public void update() {
        // update delta time
        double timeCurrent = timer.get();
        timeDelta = timeCurrent - timeLastUpdate;
        timeLastUpdate = timeCurrent;

        // append the buffer list
        opList.addAll(opbufList);
        opbufList.clear();

        cleanupOperationList();

        // run operations
        Iterator<Operation> iter = opList.iterator();
        Context context = this.getContext();

        while (iter.hasNext()) {
            Operation op = iter.next();
            OpState state = op.execute(context);
            op.opState = state;
        }

        // run update in subsystems
        for (SubSystem subSystem : this.subsystemList) {
            subSystem.update();
        }

        this.reportHandler.updateReport(context);
    }

    /**
     * Start a new operation in parallel
     * 
     * @param operation instance of the operation to start
     */
    public void startOperation(Operation operation) {
        operation.opManager = this;
        Context context = this.getContext();
        // call invoke
        OpState state = operation.invoke(context);
        operation.opState = state;
        // add to list if not ended
        if (operation.isEnded()) {
            return;
        } else {
            opbufList.add(operation);
        }
    }

    /** interrupt all operations */
    public void interruptAll() {
        Iterator<Operation> iter = opList.iterator();
        while (iter.hasNext()) {
            Operation op = iter.next();
            op.interrupt();
        }
    }

    public void interruptNonDaemon(){
        Iterator<Operation> iter = opList.iterator();
        while (iter.hasNext()) {
            Operation op = iter.next();
            if (!op.opDaemon) {
                op.interrupt();
            }
        }
    }

    @Override
    public void reportMessage(Operation operation, ReportType type, String message) {
        reportHandler.reportMessage(operation, type, message);
    }

    /**generate a context object */
    Context getContext(){
        Context context = new Context();
        context.opMode = this.opMode;
        context.opManager = this;
        context.timeDelta = this.timeDelta;
        context.timeTotal = this.timeLastUpdate;
        return context;
    }

    /** check if all operation has ended */
    public boolean allOperationEnded() {
        Iterator<Operation> iter = opList.iterator();
        while (iter.hasNext()) {
            Operation op = iter.next();
            if (op.isEnded()) {
                return true;
            }
        }
        return false;
    }

    private boolean cleanupOperationList() {
        ArrayList<Operation> tempList = (ArrayList<Operation>)opList.clone();
        Iterator<Operation> iter = tempList.iterator();
        boolean hasRunning = false;
        while (iter.hasNext()) {
            Operation op = iter.next();
            if (op.isEnded()) {
                opList.remove(op);
            } else {
                hasRunning = true;
            }
        }
        return hasRunning;
    }

    @Override
    public void updateReport(Context context) {/**do nothing */}
}
