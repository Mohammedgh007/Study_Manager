/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AddCourseActivity
purpose: This is model view class that is responsible for the popup window of adding a course.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
  onTimeSet() -> It's called when the user sets the time on time's dialogue.
  isInputsValid() -> It returns true in case that the user has inputted all the fields correctly.
  getTimeArr() -> it returns the time as an array that can be used for the model.
  preFillFields() -> it pre-fills fields of AddCourseActivity in the user wants to edit a course.
  applySameTime() -> It's used to assign a day the same time as other days.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.modelCoordinators.CoursesCoordinator;
import creative.developer.m.studymanager.view.StringMaker;

public class AddCourseActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener{

    // stores the inputted times
    private int[] startMin;
    private int[] finishMin;
    private int[] startHour;
    private int[] finishHour;
    // It determines whether the time dialog is for starting time or ending time
    private boolean isStartTime;
    // It determines the day index in time arrays ^^. sun is 0, and fri is 5
    private int dayIndex;
    // the views
    private EditText courseET;
    private EditText roomET;
    private CheckBox sameTimeCB;
    private CheckBox[] daysCk; // It stores all checkboxes for the selecting days
    private Button[] daysBtn; // It stores all the buttons for selecting time of checked day.
    private Button doneBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course);

        // initializing the time
        startMin = new int[6];
        finishMin = new int[6];
        startHour = new int[6];
        finishHour = new int[6];

        // initializing the views
        courseET = findViewById(R.id.course_name_add_courses);
        roomET = findViewById(R.id.location_name_add_courses);
        sameTimeCB = findViewById(R.id.checkBox_same_time_add_course);
        daysCk = new CheckBox[6];
        daysBtn = new Button[6];
        daysCk[0] = findViewById(R.id.sunday_checkbox_add_course);
        daysBtn[0] = findViewById(R.id.select_time_sun_add_course);
        daysCk[1] = findViewById(R.id.monday_checkbox_add_course);
        daysBtn[1] = findViewById(R.id.select_time_mon_add_course);
        daysCk[2] = findViewById(R.id.tue_checkbox_add_course);
        daysBtn[2] = findViewById(R.id.select_time_tue_add_course);
        daysCk[3] = findViewById(R.id.wed_checkbox_add_course);
        daysBtn[3] = findViewById(R.id.select_time_wed_add_course);
        daysCk[4] = findViewById(R.id.thursday_checkbox_add_course);
        daysBtn[4] = findViewById(R.id.select_time_thr_add_course);
        daysCk[5] = findViewById(R.id.fri_checkbox_add_course);
        daysBtn[5] = findViewById(R.id.select_time_fri_add_course);
        doneBtn = findViewById(R.id.add_add_course);
        cancelBtn = findViewById(R.id.cancel_add_course);

        // the getting the model's instance
        CoursesCoordinator model = CoursesCoordinator.getInstance(this);

        // checking if the user wants to edit a course, so that the view will be modified.
        Intent recieved = this.getIntent();
        String purpose = recieved.getExtras().getString("porpuse");
        final CourseEntity editedCourse; // final because it will be used only to retrieve values from.
        if (purpose.equals("editing")) {
            // getting the course object from the model.
            editedCourse = model.getCourse(recieved.getStringExtra("courseName"));
            preFillFields(editedCourse);

            // change the text of "add" button to "finish"
            doneBtn.setText(getResources().getString(R.string.done));
        } else {
            editedCourse = null;
        }


        // handling the event of clicking on same time check box,
        sameTimeCB.setOnCheckedChangeListener((btnView, isChecked) -> {
            if (isChecked && startMin[dayIndex] != 0) {
                for (int day = 0; day < 6; day++) {
                    applySameTime(day);
                }
            }
        });


        // handling the event of changing the check box value that's hiding/showing select time button
        for (int i = 0; i < 6; i++) {
            daysCk[i].setOnCheckedChangeListener((btnView, isChecked) -> {
                btnView.setClickable(false);
                if (isChecked) {
                    // note: button's tag and i have the same value
                    int day = Integer.parseInt(btnView.getTag().toString());
                    daysBtn[day].setVisibility(View.VISIBLE);
                    if (sameTimeCB.isChecked() && startMin[dayIndex] != 0) { // setup the time for this day as others
                        applySameTime(day);
                    }
                    dayIndex = day;
                } else {
                    daysBtn[Integer.parseInt(btnView.getTag().toString())].setVisibility(View.INVISIBLE);
                }
                btnView.setClickable(true);
            });
        }

        // handling the event of clicking "selecet time" button that's showing time's dialogue
        for (int i = 0; i < 6; i++) {
            daysBtn[i].setOnClickListener((view) -> {
                view.setClickable(false);
                // note i and tag have the same value
                dayIndex = Integer.parseInt(view.getTag().toString());
                isStartTime = true;
                Calendar cal = Calendar.getInstance();
                int currHour = cal.get(Calendar.HOUR_OF_DAY)               ;
                int currMinute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog =  new TimePickerDialog(AddCourseActivity.this,
                        AddCourseActivity.this, currHour,
                        currMinute, DateFormat.is24HourFormat(this));
                dialog.setMessage(getResources().getString(R.string.inputStartTime));
                dialog.show();
                view.setClickable(true);
            });
        }


        doneBtn.setOnClickListener((view) -> {
            String[] tt = getTimeArr("start");
            System.out.println("tttt + " + tt[0]);
            if(isInputsValid()) {
                String[] startTime = getTimeArr("start");
                String[] finishTime = getTimeArr("finish");
                if (purpose.equals("editing")) { // if this screen is opened for editing a course
                    CourseEntity updatedCourse = new CourseEntity(editedCourse.getCourseID(),
                            courseET.getText().toString().toUpperCase(),
                            roomET.getText().toString(), startTime[0], finishTime[0]
                            , startTime[1], finishTime[1]
                            , startTime[2], finishTime[2]
                            , startTime[3], finishTime[3]
                            , startTime[4], finishTime[4]
                            , startTime[4], finishTime[5]);
                    model.updateCourse(updatedCourse);
                } else { // if this screen is opened for adding a course
                    model.addCourse(courseET.getText().toString().toUpperCase(),
                            roomET.getText().toString(), startTime[0], finishTime[0]
                            , startTime[1], finishTime[1]
                            , startTime[2], finishTime[2]
                            , startTime[3], finishTime[3]
                            , startTime[4], finishTime[4]
                            , startTime[4], finishTime[5]);
                }
                Intent sendBack = new Intent(AddCourseActivity.this, HomeActivity.class);
                sendBack.putExtra("courseName", courseET.getText().toString().toUpperCase());
                setResult(RESULT_OK, sendBack);
                finish();
            }
        });

        cancelBtn.setOnClickListener((view) -> finish());
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isStartTime) {
            isStartTime = false;
            startMin[dayIndex] = minute;
            startHour[dayIndex] = hourOfDay;
            Calendar cal = Calendar.getInstance();
            int currHour = cal.get(Calendar.HOUR_OF_DAY)               ;
            int currMinute = cal.get(Calendar.MINUTE);
            TimePickerDialog dialog =  new TimePickerDialog(AddCourseActivity.this,
                    AddCourseActivity.this, currHour,
                    currMinute, DateFormat.is24HourFormat(this));
            dialog.setMessage(getResources().getString(R.string.inputFinishTime));
            dialog.show();
        } else {// after selecting the start time
            finishMin[dayIndex] = minute;
            finishHour[dayIndex] = hourOfDay;
            if (sameTimeCB.isChecked()) { // if the class happens at the same time at selected days
                for (int i = 0; i < 6; i++) {
                    startMin[i] = startMin[dayIndex];
                    finishMin[i] = finishMin[dayIndex];
                    startHour[i] = startHour[dayIndex];
                    finishHour[i] = finishHour[dayIndex];
                }
                for (int i = 0; i < 6; i++) {
                    dayIndex = i;
                    daysBtn[dayIndex].setText(StringMaker.getViewedTime(startHour[dayIndex],
                            startMin[dayIndex], finishHour[dayIndex], finishMin[dayIndex], this));
                }
            } else {
                daysBtn[dayIndex].setText(StringMaker.getViewedTime(startHour[dayIndex],
                        startMin[dayIndex], finishHour[dayIndex], finishMin[dayIndex], this));
            }
        }
    }

    /**
     * It checks if the user has provided a valid data to add a new course
     * @return true if the inputs are valid
     */
    private boolean isInputsValid() {
        // checking if the user has inputted non-used course's name
        String inputtedStr = courseET.getText().toString().toUpperCase();
        if (inputtedStr.equals("") ) {
            Toast.makeText(getBaseContext(),
                    R.string.inputCourseNameFirst, Toast.LENGTH_LONG).show();
            return false;
        } else if (MainActivity.coursesStr.contains(inputtedStr)) {
            Toast.makeText(getBaseContext(),
                    R.string.inputNonUsedCourse, Toast.LENGTH_LONG).show();
            return false;
        }
        // checking if the user has inputted the location
        else if (roomET.getText().toString().equals("")) {
            Toast.makeText(getBaseContext(),
                    R.string.inputRoom, Toast.LENGTH_LONG).show();
            return false;
        }
        // checking if the user has inputted a time for each selected day
        for (int i = 0; i < 6; i++) {
            if (daysCk[i].isChecked() &&
                    daysBtn[i].getText().toString().equals(getResources().getString(R.string.selectTime))) {
                Toast.makeText(getBaseContext(),
                        R.string.inputRoom, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }


    /**
     * It's used to create time strings that's used by the model to add or edit a course.
     * @param type is either start(referring to start time) or finish(referring to finish time)
     * @return string[] that's used for the model
     */
    private String[] getTimeArr(String type) {
        String[] time = new String[6];
        String hour;
        String minute;
        for (int i = 0; i < 6; i++) {
            if (daysCk[i].isChecked()) {
                if (type.equals("start")) {
                    if (startHour[i] < 10) { // making sure hours have 2 digits as a string
                        hour  = "0" + startHour[i];
                    } else {
                        hour = Integer.toString(startHour[i]);
                    }
                    if (startMin[i] < 10) { // making sure minutes or days have 2 digits as a string
                        minute = "0" + startMin[i];
                    } else {
                        minute = Integer.toString(startMin[i]);
                    }

                    time[i] = hour + ":" + minute;
                } else {
                    if (finishHour[i] < 10) { // making sure hours have 2 digits as a string
                        hour  = "0" + finishHour[i];
                    } else {
                        hour = Integer.toString(finishHour[i]);
                    }
                    if (finishMin[i] < 10) { // making sure minutes or days have 2 digits as a string
                        minute = "0" + finishMin[i];
                    } else {
                        minute = Integer.toString(finishMin[i]);
                    }

                    time[i] = hour + ":" + minute;
                }
            } else{
                time[i] = ""; // empty string to represent that the class does not occur in this day
            }
        }
        return time;
    }


    /**
     * It's used to pre-fill the fields of AddCourseAssignment in case a user wants to update some info
     * about the course.
     * @param editedCourse holds all the data related to the course.
     */
    private void preFillFields(CourseEntity editedCourse) {
        // filling the course name and the course location
        courseET.setText(editedCourse.getName()); // course name
        roomET.setText(editedCourse.getLocation()); // location

        // filling the times
        int[] startCourseMin = editedCourse.getFromMinuteNum();
        int[] startCourseHour = editedCourse.getFromHourNum();
        int[] finishCourseMin = editedCourse.getToMinuteNum();
        int[] finishCourseHour = editedCourse.getToHourNum();
        for (int day = 0; day < 6; day++) { // day 0 is sunday and day 5 Friday
            if (startCourseMin[day] != -1) { // -1 indicates that there's no class at that day.
                daysCk[day].setChecked(true); // check the correspondent day
                daysBtn[day].setVisibility(View.VISIBLE); // let the button be visible.

                // store the minutes and hours in the arrays
                startMin[day] = startCourseMin[day];
                startHour[day] = startCourseHour[day];
                finishMin[day] = finishCourseMin[day];
                finishHour[day] = finishCourseHour[day];

                // show the text for the button
                daysBtn[day].setText(StringMaker.getViewedTime(startHour[day],
                        startMin[day], finishHour[day], finishMin[day], this));

                dayIndex = day; // to facilitate using applySameTime()
            }
        }
    }


    /**
     * It's called to set a day's time like the others
     * @param day is 0-5 where 0 is sunday and 5 is friday
     */
    private void applySameTime(int day) {
        startMin[day] = startMin[dayIndex];
        finishMin[day] = finishMin[dayIndex];
        startHour[day] = startHour[dayIndex];
        finishHour[day] = finishHour[dayIndex];
        daysBtn[day].setText(StringMaker.getViewedTime(startHour[day],
                startMin[day], finishHour[day], finishMin[day], this));
    }

}
