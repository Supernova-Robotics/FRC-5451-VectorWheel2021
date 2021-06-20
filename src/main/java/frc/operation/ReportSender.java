// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.operation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is the default message report handler class. Reported message will be
 * sent to sysout, and will be displayed on SmartdashBoard
 */
public class ReportSender implements ReportHandler {
    ArrayList<ReportItem> reportItemList = new ArrayList<ReportItem>();

    ArrayList<NetworkTableEntry> messageEntries = new ArrayList<NetworkTableEntry>();
    ArrayList<NetworkTableEntry> warningEntries = new ArrayList<NetworkTableEntry>();
    ArrayList<NetworkTableEntry> errorEntries = new ArrayList<NetworkTableEntry>();

    double timeCurrent = -1;
    double timeLastUpdate = -1;

    public ReportSender(int maxCount){
        ShuffleboardTab tab = Shuffleboard.getTab("Operation Log");
        initLayout(tab, "Message", maxCount, messageEntries);
        initLayout(tab, "Warning", maxCount, warningEntries);
        initLayout(tab, "Error", maxCount, errorEntries);
        SmartDashboard.putString("Operation Log", "");
    }

    void initLayout(ShuffleboardTab tab, String title, int maxCount, ArrayList<NetworkTableEntry> entries){
        ShuffleboardLayout listLayout = tab.getLayout(title, "List Layout");
        for (int i = 0; i < maxCount; i += 1) {
            NetworkTableEntry entry = listLayout.add(String.format("%s %d", title, i), "str").getEntry();
            entries.add(entry);
        }
    }

    @Override
    public void reportMessage(Operation operation, ReportType type, String message) {
        ReportItem item = new ReportItem();
        item.operation = operation;
        item.type = type;
        item.message = message;
        item.timeStamp = timeCurrent;

        System.out.println(item.toString());
        reportItemList.add(item);

        SmartDashboard.putString("Operation Log", item.toString());
    }

    @Override
    public void updateReport(Context context) {
        timeCurrent = context.getTimeTotal();
        if(timeLastUpdate == -1){
            timeLastUpdate = timeCurrent;
        }
        double timeDelta = timeCurrent - timeLastUpdate;
        if (timeDelta < 0.1) {
            return;
        }
        timeLastUpdate = timeCurrent;

        ArrayList<String> messageArrayList = new ArrayList<String>();
        ArrayList<String> warningArrayList = new ArrayList<String>();
        ArrayList<String> errorArrayList = new ArrayList<String>();

        ArrayList<ReportItem> tempList = (ArrayList<ReportItem>) reportItemList.clone();
        Iterator<ReportItem> iter = tempList.iterator();
        while (iter.hasNext()) {
            ReportItem item = iter.next();
            if (timeCurrent - item.timeStamp > 10) {
                // delete old message
                this.reportItemList.remove(item);
            } else {
                // add to a category list
                switch (item.type) {
                    case MESSAGE:
                        messageArrayList.add(item.toString());
                        break;

                    case WARNING:
                        warningArrayList.add(item.toString());
                        break;

                    case ERROR:
                        errorArrayList.add(item.toString());
                        break;

                    default:
                        break;
                }
            }
        }

        pushShuffleBoard(messageEntries, messageArrayList);
        pushShuffleBoard(warningEntries, warningArrayList);
        pushShuffleBoard(errorEntries, errorArrayList);

    }

    static void pushShuffleBoard(List<NetworkTableEntry> entries, List<String> data){
        int size = data.size();
        int maxCount = entries.size();

        if (size > maxCount){
            data = data.subList(size - maxCount, size);
        }

        for (int i = 0; i < maxCount; i += 1) {
            String str;
            if (i < size){
                str = data.get(i);
            } else{
                str = "";
            }
            entries.get(i).setString(str);
        }
    }
}

/**
 * struct to store a report
 */
class ReportItem {
    double timeStamp;
    Operation operation;
    ReportType type;
    String message;

    @Override
    public String toString() {
        int min;
        double sec;
        min = (int) (timeStamp / 60.0);
        sec = timeStamp % 60;

        String typeStr = type.name();
        String operationName = operation.getClass().getName();

        String out = String.format("[%02d:%05.2f][%s]%s: %s", min, sec, typeStr, operationName, message);
        return out;
    }
}
