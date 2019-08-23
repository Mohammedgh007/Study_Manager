/*
###############################################################################
Class name : AssignmentObject
purpose: This is a model class that is used to represent a signle assignment
   assignment as an object. Also, it is part of the app database.
Methods:
  onCreate -> It encapsulates/manages all the interaction.
  onActivityResult -> It receives the intent from AddAssignmentActivity that
     holds the data of the added assignment.
###############################################################################
 */

package creative.developer.m.studymanager.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AssignmentsEntity implements Comparable {


    // fields as a obj or columns on the database
    @PrimaryKey (autoGenerate = true)
    private int assignmentID;
    private String course;
    private String significance;
    private String disc;
    private String dueTime; // hh:mm like 14:20
    private String dueDate; // yyyy:mm;dd I used : with ; to facilitate using substring.
    private boolean isMarked;


    public AssignmentsEntity(String course, String significance, String disc, String dueDate,
                             String dueTime) {
        this.course = course;
        this.significance = significance;
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

    public String getSignificance(){
        return this.significance;
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

    // used for sorting assignments by time. hours * 60 + minutes.
    @Ignore
    public int getTimeVal(){
        return Integer.parseInt(this.dueTime.substring(0, this.dueTime.indexOf(":"))) * 60 +
                Integer.parseInt(this.dueTime.substring(this.dueTime.indexOf(":") + 1));
    }

    // used for sorting assignments by date. year * 365 + month * 29 + day
    @Ignore
    public static Integer getDateVal(String dueDate){
        return Integer.parseInt(dueDate.substring(0, dueDate.indexOf(":"))) * 365 +
                Integer.parseInt(dueDate.substring(dueDate.indexOf(":") + 1,
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

    public void setSignificance(String significance) {
        this.significance = significance;
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

    @Override
    public int compareTo(Object o) {
        AssignmentsEntity compared = (AssignmentsEntity) o;
        return Integer.compare(this.getTimeVal(), compared.getTimeVal());
    }
}
