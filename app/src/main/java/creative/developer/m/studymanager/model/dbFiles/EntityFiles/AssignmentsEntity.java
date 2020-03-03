/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AssignmentEntity
purpose: This is a model class that is used to represent a single assignment
    as an object and as a row on a database table called AssignmentEntity
Methods:
    - getTimeVal() -> return the registered time as a single integer that can be
         used for sorting.
    - getDateVal() -> return the registered date as a single integer that can be
         used for sorting.
    - getHourNum() -> return the hour as a single integer.
    - getMinuteNum() ->return the minute as a single integer.
    - getDayNum() -> return the day as a single integer.
    - getMonthNum() -> return the month as a single integer.
    - getYearNum() -> return the year as a single integer.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AssignmentsEntity implements Comparable {


    // fields as a obj or columns on the database
    @PrimaryKey (autoGenerate = true)
    private int assignmentID;
    private String course;
    private String notificationID;
    private String notificationTime; // # d/h ; d for day and h for hour
    private String disc;
    private String dueTime; // hh:mm like 14:20
    private String dueDate; // yyyy:mm;dd I used : with ; to facilitate using substring.
    private boolean isMarked;


    public AssignmentsEntity(String course, String notificationID, String notificationTime,
                             String disc, String dueDate, String dueTime) {
        this.course = course;
        this.notificationID = notificationID;
        this.notificationTime = notificationTime;
        this.disc = disc;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.isMarked = false;
    }

    // getters mostly for Room usage
    public int getAssignmentID(){
        return this.assignmentID;
    }

    public String getCourse (){
        return this.course;
    }

    public String getNotificationID(){
        return this.notificationID;
    }

    public String getDisc(){
        return this.disc;
    }

    public String getDueTime(){
        return this.dueTime;
    }

    public String getDueDate(){
        return this.dueDate;
    }

    public boolean getIsMarked () {
        return this.isMarked;
    }

    public String getNotificationTime() { return notificationTime;  }

    // used for sorting assignments by time. hours * 60 + minutes.
    @Ignore
    public int getTimeVal(){
        return Integer.parseInt(this.dueTime.substring(0, this.dueTime.indexOf(":"))) * 60 +
                Integer.parseInt(this.dueTime.substring(this.dueTime.indexOf(":") + 1));
    }

    // used for sorting assignments by date. (year - 2015) * 365 + month * 29 + day
    @Ignore
    public static Integer getDateVal(String dueDate){
        // -2015 is a minor optimization
        return (Integer.parseInt(dueDate.substring(0, dueDate.indexOf(":"))) - 2015) * 365
                 + Integer.parseInt(dueDate.substring(dueDate.indexOf(":") + 1,
                        dueDate.indexOf(";"))) * 29 +
                Integer.parseInt(dueDate.substring(dueDate.indexOf(";") + 1));
    }

    // get the hour as an int
    @Ignore
    public int getHourNum() {
        String time = this.getDueTime();
        return Integer.parseInt(time.substring(0, time.indexOf(":")));
    }
    // get the minute as an int
    @Ignore
    public int getMinuteNum() {
        String time = this.getDueTime();
        return Integer.parseInt(time.substring(time.indexOf(":") + 1));
    }
    // get the day as an int
    @Ignore
    public int getDayNum() {
        String date = this.getDueDate();
        return Integer.parseInt(date.substring(date.indexOf(";") + 1));
    }
    // get the month as an int
    @Ignore
    public int getMonthNum() {
        String date = this.getDueDate();
        return Integer.parseInt(date.substring(date.indexOf(":") + 1, date.indexOf(";")));
    }
    // get the year as an int
    @Ignore
    public int getYearNum() {
        String date = this.getDueDate();
        return Integer.parseInt(date.substring(0, date.indexOf(":")));
    }

    // setters for Room usage
    public void setAssignmentID(int assignmentID) {
        this.assignmentID = assignmentID;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setIsMarked(boolean val) {
        this.isMarked = val;
    }

    public void setNotificationTime(String notificationTime) {
        notificationTime = notificationTime;
    }

    @Override // the sooner in terms of time, regardless of date, is smaller.
    public int compareTo(Object o) {
        AssignmentsEntity compared = (AssignmentsEntity) o;
        return Integer.compare(this.getTimeVal(), compared.getTimeVal());
    }
}
