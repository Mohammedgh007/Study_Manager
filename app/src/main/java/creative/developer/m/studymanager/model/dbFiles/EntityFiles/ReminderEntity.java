/*
###############################################################################
Author: Mohammed Alghamdi
Class name : ReminderEntity
purpose: This is a model class that is used to represent a single reminder
    as an object and as a row on a database table called ReminderEntity
Methods:
    - getDaysList() -> return the days as a list of strings.
    - getHourNum() -> return the hour as a single integer.
    - getMinuteNum() ->return the minute as a single integer.
    - removeDay() -> It removes the day that user's has been shown the reminder if the reminder
        is not repeated. It returns true if there's no more days.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class ReminderEntity {

    // fields as a obj or columns on the database
    @PrimaryKey
    private int reminderID;
    private String title;
    private String notificationTime; // hh:mm like 14:20
    private String disc;
    private String days; // first three letters of days separated by commas like "mon,wed"
    private boolean isOn;
    private boolean isRepeated;


    public ReminderEntity(int reminderID, String title, String notificationTime,
                            String disc, String days, boolean isRepeated) {
        this.reminderID = reminderID;
        this.title = title;
        this.notificationTime = notificationTime;
        this.disc = disc;
        this.days = days;
        this.isOn = true;
        this.isRepeated = isRepeated;
    }

    // getters mostly for Room usage
    public int getReminderID(){
        return this.reminderID;
    }

    public String getTitle (){
        return this.title;
    }

    public String getDisc(){
        return this.disc;
    }

    public boolean getIsRepeated() {return isRepeated;}

    public String getDays(){
        return this.days;
    }

    public boolean getIsOn () {
        return this.isOn;
    }

    public String getNotificationTime() { return notificationTime;  }

    // get the hour as an int
    @Ignore
    public int getHourNum() {
        String time = notificationTime;
        return Integer.parseInt(time.substring(0, time.indexOf(":")));
    }
    // get the minute as an int
    @Ignore
    public int getMinuteNum() {
        String time = this.notificationTime;
        return Integer.parseInt(time.substring(time.indexOf(":") + 1));
    }

    // get the days as list of string
    @Ignore
    public List<String> getDaysList() {
        return Arrays.asList(days.split(","));
    }

    // setters for Room usage
    public void setAssignmentID(int reminderID) {
        this.reminderID = reminderID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setIsOn(boolean val) {
        this.isOn = val;
    }

    public void setIsRepeated(boolean val) {
        this.isRepeated = val;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    @Ignore
    // it removes the day that the user has seen its reminder.
    // return true to indicate that there's no more days alarm the user in the future.
    public boolean removeDay() {
        if (!isRepeated) {
            if (days.length() <= 3) {
                days = "";
                return true;
            } else {
                days = days.substring(4);
                return false;
            }
        } else {
            return false;
        }
    }
}
