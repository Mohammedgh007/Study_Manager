package creative.developer.m.studymanager.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class RemarkEntity  implements Comparable{


    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int remarkID;
    private String title;
    private String disc;
    private String time; // hh:mm like 14:20
    private String date; // yyyy:mm;dd I used : with ; to facilitate using substring.


    public RemarkEntity (String title, String disc, String time, String date) {
        this.title = title;
        this.disc = disc;
        this.time = time;
        this.date = date;
    }

    // getters mostly fot Room usage
    public int getRemarkID() { return remarkID; }

    public String getTitle() { return title;   }

    public String getDisc() { return disc; }

    public String getTime() { return time; }

    public String getDate() { return date; }

    public void setRemarkID(int remarkID) {
        this.remarkID = remarkID;
    }

    // setters mostly for Room usage
    public void setTitle(String title) { this.title = title; }

    public void setDisc(String disc) { this.disc = disc; }

    public void setTime(String time) { this.time = time; }

    public void setDate(String date) { this.date = date; }

    // used for sorting assignments by time. hours * 60 + minutes.
    @Ignore
    public int getTimeVal(){
        return Integer.parseInt(this.time.substring(0, this.time.indexOf(":"))) * 60 +
                Integer.parseInt(this.time.substring(this.time.indexOf(":") + 1));
    }

    // used for sorting remarks by date. year * 365 + month * 29 + day
    @Ignore
    public int getDateVal(){
        String date = this.date;
        return Integer.parseInt(date.substring(0, date.indexOf(":"))) * 365 +
                Integer.parseInt(date.substring(date.indexOf(":") + 1,
                        date.indexOf(";"))) * 29 +
                Integer.parseInt(date.substring(date.indexOf(";") + 1));
    }

    // get the hour as an int
    @Ignore
    public int getHourNum() {
        String time = this.time;
        return Integer.parseInt(time.substring(0, time.indexOf(":")));
    }
    // get the minute as an int
    @Ignore
    public int getMinuteNum() {
        String time = this.time;
        return Integer.parseInt(time.substring(time.indexOf(":") + 1));
    }
    // get the day as an int
    @Ignore
    public int getDayNum() {
        String date = this.date;
        return Integer.parseInt(date.substring(date.indexOf(";") + 1));
    }
    // get the month as an int
    @Ignore
    public int getMonthNum() {
        String date = this.date;
        return Integer.parseInt(date.substring(date.indexOf(":") + 1, date.indexOf(";")));
    }
    // get the year as an int
    @Ignore
    public int getYearNum() {
        String date = this.date;
        return Integer.parseInt(date.substring(0, date.indexOf(":")));
    }

    @Override
    public int compareTo(Object o) {
        RemarkEntity compared = (RemarkEntity) o;
        if (compared.getDateVal() != this.getDateVal()) { // both are not on the same day
            return Integer.compare(this.getDateVal(), compared.getDateVal());
        } else { // both are on the same day
            return Integer.compare(this.getTimeVal(), compared.getTimeVal());
        }

    }
}
