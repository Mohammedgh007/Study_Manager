/*
###############################################################################
Class name : StringMaker
purpose: This is a view class that handles creating strings that will be shown to the user.
Methods:
    + getViewedTime(hour: int, minute: int): String -> get hour and minute and returns hour:minute pm
    + getViewedTime(startHour: int, startMinute: int, endHour: int, endMinute: int,
        activity: AppCompatActivity): String
        -> get the period text in format startHour:startMinute pm - endHour:endMinute pm.
###############################################################################
 */

package creative.developer.m.studymanager.view;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import creative.developer.m.studymanager.R;

public class StringMaker {

    /*
    this method creates the string that will be used to show the time for the user.
    @PARAM: hour is an intger that range between 0-23
    @PARAM: minute is int 0-59 that represent minutes.
    @return: String for viewing time
     */
    public static String getViewedTime (int hour, int minute) {
        String strHour;
        if (hour == 0) {
            strHour = "12";
        } else if (hour < 13) {
            strHour = Integer.toString(hour);
        } else { // if it is between 13-23
            strHour = Integer.toString(hour - 12);
        }
        String strMinute;
        if (minute < 10) {
            strMinute = "0" + minute;
        } else {
            strMinute = Integer.toString(minute);
        }
        String period = (hour < 12) ? "a.m" : "p.m";
        return strHour + ":" + strMinute + " " + period;
    }


    /*
    this method creates the string that will be used to show the time's period for the user.
    @PARAM startHour is an int that range between 0-23 for the starting hour.
    @PARAM startMinute is an int 0-59 that represent the starting minute.
    @PARAM endHour is an int that range between 0-23 for the ending hour.
    @PARAM endMinute is an int 0-59 that represent the ending minute.
    @param context is the object whose activity is calling this method.
    @return: String for viewing time
     */
    public static String getViewedTime(int startHour, int startMinute, int endHour, int endMinute,
                                       Context context) {
        // computing for the start time.
        String period = (startHour < 12) ? "a.m" : "p.m";
        String hourStr;
        if (startHour == 0) { // if it is 12 am
            hourStr = "12";
        } else if (startHour < 13) {
            hourStr = Integer.toString(startHour);
        } else if (startHour < 22) {
            hourStr = "0" + (startHour - 12);
        } else {
            hourStr = Integer.toString(startHour - 12);;
        }
        String shownText = hourStr + ":" + startMinute + " " + period;
        // computing for the end time.
        period = (endHour < 12) ? "a.m" : "p.m";
        if (endHour == 0) { // if it is 12 am
            hourStr = "12";
        } else if (endHour < 13) {
            hourStr = Integer.toString(endHour);
        } else if (endHour < 22) {
            hourStr = "0" + (endHour - 12);
        } else {
            hourStr = Integer.toString(endHour - 12);;
        }
        // output for RTL languages
        if(context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            shownText = hourStr + ":" + endMinute + " " + period + " - " + shownText;
        } else { // output for LTR languages
            shownText += " " + context.getResources().getString(R.string.to) + " ";
            shownText += hourStr + ":" + endMinute + " " + period;
        }

        return shownText;
    }
}
