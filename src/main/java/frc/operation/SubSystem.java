// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.operation;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The base class for subsystems. Provides a ownership/priority system.
 */
public class SubSystem {
    ArrayList<Operation> ownerList = new ArrayList<Operation>();

    /**
     * update information to dashhboards.
     * will be called each iteration.
     */
    protected void update(){
        
    }

    /**
     * Try to gain ownership of the subsystem.
     * 
     * @param operation the operation to give ownership to
     * @param force     force get the ownership regardless of the priority
     * @return true if success
     */
    public boolean capture(Operation operation, boolean force) {
        Operation curOperation = this.getCurrentOwner();
        
        if(curOperation == null){ // no current owner
            ownerList.add(operation); // add
            return true;
        }

        if (operation.opPriority >= curOperation.opPriority || force) {
            // priority check pass
            if (ownerList.contains(operation)) { // remove duplicate
                ownerList.remove(operation);
            }
            ownerList.add(operation); // add
            return true;
        } else {
            // priority check fail
            return false;
        }
    }

    /**
     * Try to gain ownership of the subsystem. Default to non-force.
     * 
     * @param operation the operation to give ownership to
     * @return true if success
     */
    public boolean capture(Operation operation) {
        return this.capture(operation, false);
    }

    /**
     * Give up the ownership
     */
    public void release(Operation operation) {
        this.ownerList.remove(operation);
    }

    /**
     * get the top ownership
     */
    public Operation getCurrentOwner() {
        this.cleanupOwnerList();

        int len = ownerList.size();
        if (len == 0) {
            return null;
        }
        return ownerList.get(len - 1); // return the last one
    }

    /**
     * check if the operation has current ownership
     * 
     * @param operation
     * @return true if yes
     */
    public boolean isCurrentOwner(Operation operation) {
        Operation owner = this.getCurrentOwner();
        if (operation == owner) {
            return true;
        }
        return false;
    }

    /**
     * clean up the oweners list, removing any ended operation.
     */
    private void cleanupOwnerList() {
        ArrayList<Operation> tempList = (ArrayList<Operation>) ownerList.clone();
        Iterator<Operation> iter = tempList.iterator();

        while (iter.hasNext()) {
            Operation op = iter.next();
            if (op.isEnded()) {
                ownerList.remove(op);
            }
        }
    }
}
