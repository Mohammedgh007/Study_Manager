/*
###############################################################################
Author: Mohammed Alghamdi
Class name : CourseEntity
purpose: This is a model class that is used to represent a single course
    as an object and as a row on a database table called CourseEntity.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "name", unique = true)})
public class CourseEntity{

    // fields as a obj or columns on the database
    // If the course does not schedule in a particular, then that day time is assigned to empty string ""
    @PrimaryKey()
    private int courseID;
    private String name;
    private String location;
    private String sunFrom; //It stores the starting time as hh:mm like 14:20
    private String sunTo;
    private String monFrom;
    private String monTo;
    private String tueFrom;
    private String tueTo;
    private String wedFrom;
    private String wedTo;
    private String thrFrom;
    private String thrTo;
    private String friFrom;
    private String friTo;

    public CourseEntity(int courseID, String name, String location,
                        String sunFrom, String sunTo, String monFrom, String monTo,
                        String tueFrom, String tueTo, String wedFrom, String wedTo,
                        String thrFrom, String thrTo, String friFrom, String friTo) {
        this.courseID = courseID;
        this.name = name;
        this.location = location;
        this.sunFrom = sunFrom;
        this.sunTo = sunTo;
        this.monFrom = monFrom;
        this.monTo = monTo;
        this.tueFrom = tueFrom;
        this.tueTo = tueTo;
        this.wedFrom = wedFrom;
        this.wedTo = wedTo;
        this.thrFrom = thrFrom;
        this.thrTo = thrTo;
        this.friFrom = friFrom;
        this.friTo = friTo;
    }

    // getters mostly for Room usage
    public int getCourseID() { return courseID; }

    public String getName() { return name; }

    public String getSunFrom() { return sunFrom;  }

    public String getSunTo() { return sunTo;  }

    public String getMonFrom() { return monFrom; }

    public String getMonTo() {return monTo; }

    public String getTueFrom() { return tueFrom; }

    public String getTueTo() { return tueTo; }

    public String getWedFrom() { return wedFrom; }

    public String getWedTo() { return wedTo; }

    public String getThrFrom() {  return thrFrom;   }

    public String getThrTo() {   return thrTo;   }

    public String getFriFrom() {  return friFrom;  }

    public String getFriTo() {    return friTo;     }

    public String getLocation() {return location;}


    // setters mostly for Room usage
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public void setName(String name) { this.name = name; }

    public void setSunFrom(String sunFrom) { this.sunFrom = sunFrom; }

    public void setSunTo(String sunTo) { this.sunTo = sunTo; }

    public void setMonFrom(String monFrom) { this.monFrom = monFrom; }

    public void setMonTo(String monTo) { this.monTo = monTo;  }

    public void setTueFrom(String tueFrom) { this.tueFrom = tueFrom; }

    public void setTueTo(String tueTo) { this.tueTo = tueTo;  }

    public void setWedFrom(String wedFrom) { this.wedFrom = wedFrom; }

    public void setWedTo(String wedTo) { this.wedTo = wedTo; }

    public void setThrFrom(String thrFrom) { this.thrFrom = thrFrom; }

    public void setThrTo(String thrTo) { this.thrTo = thrTo;}

    public void setFriFrom(String friFrom) { this.friFrom = friFrom;   }

    public void setFriTo(String friTo) { this.friTo = friTo;  }

    public void setLocation(String location) {this.location = location;}

    // get the starting hours of the classes  as an int
    // note: if the day has no class in it, then -1 is returned
    @Ignore
    public int[] getFromHourNum() {
        int hours[] = new int[6];
        hours[0] = (!sunFrom.equals("")) ? Integer.parseInt(sunFrom.substring(0, sunFrom.indexOf(":"))) : -1;
        hours[1] = (!monFrom.equals("")) ? Integer.parseInt(monFrom.substring(0, monFrom.indexOf(":"))) : -1;
        hours[2] = (!tueFrom.equals("")) ? Integer.parseInt(tueFrom.substring(0, tueFrom.indexOf(":"))) : -1;
        hours[3] = (!wedFrom.equals("")) ? Integer.parseInt(wedFrom.substring(0, wedFrom.indexOf(":"))) : -1;
        hours[4] = (!thrFrom.equals("")) ? Integer.parseInt(thrFrom.substring(0, thrFrom.indexOf(":"))) : -1;
        hours[5] = (!friFrom.equals("")) ? Integer.parseInt(friFrom.substring(0, friFrom.indexOf(":"))) : -1;
        return hours;
    }
    // get the ending hours of the classes  as an int
    // note: if the day has no class in it, then -1 is returned
    @Ignore
    public int[] getToHourNum() {
        int hours[] = new int[6];
        hours[0] = (!sunTo.equals("")) ? Integer.parseInt(sunTo.substring(0, sunTo.indexOf(":"))) : -1;
        hours[1] = (!monTo.equals("")) ? Integer.parseInt(monTo.substring(0, monTo.indexOf(":"))) : -1;
        hours[2] = (!tueTo.equals("")) ? Integer.parseInt(tueTo.substring(0, tueTo.indexOf(":"))) : -1;
        hours[3] = (!wedTo.equals("")) ? Integer.parseInt(wedTo.substring(0, wedTo.indexOf(":"))) : -1;
        hours[4] = (!thrTo.equals("")) ? Integer.parseInt(thrTo.substring(0, thrTo.indexOf(":"))) : -1;
        hours[5] = (!friTo.equals("")) ? Integer.parseInt(friTo.substring(0, friTo.indexOf(":"))) : -1;
        return hours;
    }
    // get the starting minutes of the classes  as an int
    @Ignore
    public int[] getFromMinuteNum() {
        int[] minutes = new int[6];
        minutes[0] = (!sunFrom.equals("")) ? Integer.parseInt(sunFrom.substring(sunFrom.indexOf(":") + 1)) : -1;
        minutes[1] = (!monFrom.equals("")) ? Integer.parseInt(monFrom.substring(monFrom.indexOf(":") + 1)) : -1;
        minutes[2] = (!tueFrom.equals("")) ? Integer.parseInt(tueFrom.substring(tueFrom.indexOf(":") + 1)) : -1;
        minutes[3] = (!wedFrom.equals("")) ? Integer.parseInt(wedFrom.substring(wedFrom.indexOf(":") + 1)) : -1;
        minutes[4] = (!thrFrom.equals("")) ? Integer.parseInt(thrFrom.substring(thrFrom.indexOf(":") + 1)) : -1;
        minutes[5] = (!friFrom.equals("")) ? Integer.parseInt(friFrom.substring(friFrom.indexOf(":") + 1)) : -1;
        return minutes;
    }
    // get the ending minutes of the classes  as an int
    @Ignore
    public int[] getToMinuteNum() {
        int[] minutes = new int[6];
        minutes[0] = (!sunTo.equals("")) ? Integer.parseInt(sunTo.substring(sunTo.indexOf(":") + 1)) : -1;
        minutes[1] = (!monTo.equals("")) ? Integer.parseInt(monTo.substring(monTo.indexOf(":") + 1)) : -1;
        minutes[2] = (!tueTo.equals("")) ? Integer.parseInt(tueTo.substring(tueTo.indexOf(":") + 1)) : -1;
        minutes[3] = (!wedTo.equals("")) ? Integer.parseInt(wedTo.substring(wedTo.indexOf(":") + 1)) : -1;
        minutes[4] = (!thrTo.equals("")) ? Integer.parseInt(thrTo.substring(thrTo.indexOf(":") + 1)) : -1;
        minutes[5] = (!friTo.equals("")) ? Integer.parseInt(friTo.substring(friTo.indexOf(":") + 1)) : -1;
        return minutes;
    }
}
