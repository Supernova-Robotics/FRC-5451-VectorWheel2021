// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

import java.util.List;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;


public class BoardUtils {
    public static NetworkTableEntry tryAddEntry(ShuffleboardTab tab, String title, Object defaultValue){
        List<ShuffleboardComponent<?>> components = tab.getComponents();
        for (ShuffleboardComponent<?> item : components){
            if(item.getTitle() == title){
                return NetworkTableInstance.getDefault().getEntry(String.format("/Shuffleboard/%s/%s", tab.getTitle(), title));
            }
        }
        return tab.add(title, defaultValue).getEntry();
    }


}
