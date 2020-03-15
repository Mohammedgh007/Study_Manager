/*
###############################################################################
Class name : AddAssignmentActivity
Implemented Interfaces:  DatePickerDialog.OnDateSetListener and
   TimePickerDialog.OnTimeSetListener for showing time and date picker dialogs.
purpose: This is model view class that is responsible for adding an assignment or editing
   an existing one
Methods:
  onCreate() -> It encapsulates/manages most the interaction.
  onDateSet() -> It is called after disappearing the date picker dialog to store user's input
      and to establish showing time picker dialog.
  onTimeSet() -> same ^^ but for selecting the time
  getDueStr(num1, seperator, num2) -> this method will get the time or the date as integers to
      return as a proper String for creating an AssignmentEntity's object.
  isInputsCompleted() -> return true if the user has entered all the requires inputs,
     false otherwise.
  isNotificationTimeValid(notifyTimeStr) -> this method checks if the input notification time is
    valid or not by checking if it is in between the current time and the deadline time.
  getNotifyId() -> this method gets a unique id for notifications.
  createNotification(assignment) -> it setups the notification for the assignment.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.prefs.Preferences;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.view.AlarmManagement;

public class AddAssignmentActivity extends Activity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private final int PROMPT_PERMISSION_CODE = 1;
    // variables that store date and time values
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMinute;
    AssignmentsEntity editedAssignment;
    // views
    private EditText editTextCourse;
    private Spinner spinnerNotifyTime;
    private Button pickTimeDate;
    private EditText editTextDisc;
    private Button addBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_assignment);

        // initializing views
        editTextCourse = findViewById(R.id.name_addassignment);
        spinnerNotifyTime = findViewById(R.id.notify_time_add_assignment);
        pickTimeDate = findViewById(R.id.datetime_addassignment);
        editTextDisc = findViewById(R.id.disc_addassignment);
        addBtn = findViewById(R.id.add_addassignment);
        cancelBtn = findViewById(R.id.cancel_addassignment);

        // if this activity is opened to edit existing assignment, then it will pre-fill
        // all the inputs
        Intent intent = getIntent();
        if (intent.getExtras().get("porpuse").equals("editing")) {
            // getting the object from the intent
            String gsonAssignmentOld = intent.getStringExtra("assignmentObj");
            Gson gson = new Gson();
            editedAssignment = gson.fromJson(gsonAssignmentOld, AssignmentsEntity.class);

            // setting variables of time and date
            selectedDay = editedAssignment.getDayNum();
            selectedMonth = editedAssignment.getMonthNum();
            selectedYear = editedAssignment.getYearNum();
            selectedMinute = editedAssignment.getMinuteNum();
            selectedHour = editedAssignment.getHourNum();



            // filling the fields
            editTextCourse.setText(editedAssignment.getCourse());
            pickTimeDate.setText(timeAndDateStringView());
            editTextDisc.setText(editedAssignment.getDisc());
            addBtn.setText("Finish");
        }

        pickTimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // establishing date picker dialog
                Calendar cal = Calendar.getInstance();
                int currYear = cal.get(Calendar.YEAR);
                int currMonth = cal.get(Calendar.MONTH);
                int currDay = cal.get(Calendar.DATE);
                DatePickerDialog dateDialog = new DatePickerDialog(AddAssignmentActivity.this,
                        AddAssignmentActivity.this, currYear, currMonth, currDay);
                dateDialog.show();
                // onDateSet() then onTimeSet() will be called.
            }
        });


        // after Add's button is clicked, this code's portion will will send user's input back to
        // AssignmentActivity.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check first that the user has entered inputs
                int notifyID = (intent.getExtras().get("porpuse").equals("editing")) ?
                        Integer.parseInt(editedAssignment.getNotificationID()) : getNotifyId();
                if (isInputsValid()) {
                    AssignmentsEntity createdAssignment = new AssignmentsEntity(
                            editTextCourse.getText().toString(),
                            Integer.toString(notifyID),
                            spinnerNotifyTime.getSelectedItem().toString().substring(0, 5),
                            editTextDisc.getText().toString(),
                            (selectedYear + ":" + getDueStr(selectedMonth, ";", selectedDay)),
                            getDueStr(selectedHour, ":", selectedMinute));
                    Gson gosnAssignment = new Gson();
                    Intent intentSendback = new Intent(AddAssignmentActivity.this,
                            AssignmentActivity.class);

                    if (intent.getExtras().get("porpuse").equals("editing")) {
                        String oldAssignmentStr = gosnAssignment.toJson(editedAssignment);
                        intentSendback.putExtra("outdatedAssignment", oldAssignmentStr); // old version
                        createdAssignment.setAssignmentID(editedAssignment.getAssignmentID());
                    }
                    String assignmentStr = gosnAssignment.toJson(createdAssignment);
                    intentSendback.putExtra("createdAssignment", assignmentStr);
                    createNotification(createdAssignment);
                    setResult(RESULT_OK, intentSendback);
                    finish();

                }
            }
        });


        // if the user click on cancel's button, then this code's portion will take the user
        // back to AssignmentActivity with intention of sending nor saving non of user's inputs.
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAssignmentActivity.this,
                        AssignmentActivity.class);
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    // this event-driven method is called when the date dialog disappear to the user.
    // year, month, and dayOfMonth are user's inputs.
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // storing users' inputs of date
        selectedYear = year;
        selectedMonth = month + 1; // month start counting at 0
        selectedDay = dayOfMonth;

        // after disappearing date picker dialog, the app will establish time picker dialog
        Calendar cal = Calendar.getInstance();
        int currHour = cal.get(Calendar.HOUR_OF_DAY)               ;
        int currMinute = cal.get(Calendar.MINUTE);
        TimePickerDialog timeDialog = new TimePickerDialog(AddAssignmentActivity.this,
                AddAssignmentActivity.this, currHour,
                currMinute, DateFormat.is24HourFormat(this));
        timeDialog.show();
    }

    // this event-driven method is called when the date dialog disappear to the user.
    // hourOfDay and minute are user's inputs
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // storing user's inputs
        selectedHour = hourOfDay;
        selectedMinute = minute;
        // showing user's inputs
        Calendar cal = Calendar.getInstance();
        // Showing ->    Month day, hour:minute. example, May 5, 13:15
        pickTimeDate.setText(timeAndDateStringView());
    }


    /*
    This method takes integers that represent the date and the time to return them as a string
    that will be outputted to the user
    @return: A string that represent the date and time
     */
    private String timeAndDateStringView() {
        String period = (selectedHour < 13) ? "a.m" : "p.m";
        String hourStr;
        if (selectedHour == 0) { // if it is 12 am
           hourStr = "12";
        } else if (selectedHour < 13) {
            hourStr = Integer.toString(selectedHour);
        } else if (selectedHour < 22) {
            hourStr = "0" + (selectedHour - 12);
        } else {
            hourStr = Integer.toString(selectedHour - 12);;
        }

        return new DateFormatSymbols(Locale.getDefault()).
                getShortMonths()[selectedMonth - 1] + " " + // month counting start from 0
                selectedDay + " , " +
                hourStr + " : " +
                selectedMinute + " " + period;
    }


    /*
    this method will get the time or the date as integers to return as a proper String for
    creating an AssignmentEntity's object. The format should be ##:## like 10:12 or 05:20
    @PARAM: num1 would represent the number on the left of the format that is either month or hour
    @PARAM: num2 would represent the number on the right fo the format that is either day or minutes
    @PARAM: separator is the string in between the integer
    @return: it will returns a string that will be used directly to create AssignmentEntity's object
    */
    private String getDueStr(int num1, String separator, int num2) {
        String hourMonth, minuteDay; // the first represent hour or month.
       if (num1 < 10) { // making sure hours or month have to 2 digits as a string
            hourMonth  = "0" + num1;
        } else {
            hourMonth = Integer.toString(num1);
        }
        if (num2 < 10) { // making sure minutes or days have to 2 digits as a string
            minuteDay = "0" + num2;
        } else {
            minuteDay = Integer.toString(num2);
        }
        return hourMonth + separator + minuteDay;
    }




    /*
    this method check that whether the user entered all the inputs or not. Also,
    it outputs a message as a feedback to the user if the user does not input correctly.
    @Return: it returns true if the user entered all the inputs, false otherwise.
     */
    private boolean isInputsValid () {
        // checking for the date and time inputs
        Calendar today = Calendar.getInstance();
        Calendar inputDate = Calendar.getInstance();
        inputDate.set(selectedYear, selectedMonth - 1, selectedDay); // months starts from 0
        if ( pickTimeDate.getText().toString().equals(getResources().
                getString(R.string.selectTimeDate))) {
            Toast.makeText(getBaseContext(),
                    "Please enter the date and the time field",Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pickTimeDate.getText().toString().equals(R.string.selectTimeDate)) {
            if (today.after(inputDate)) {
                Toast.makeText(getBaseContext(),
                        "Please enter the date and the time field correctly",Toast.LENGTH_SHORT).
                        show();
                return false;
            }
        }
        // checking for the name and the description
        if (editTextCourse.getText().toString().isEmpty()) {
            Toast.makeText(getBaseContext(),
                    "Please enter the name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editTextDisc.getText().toString().isEmpty()) {
            Toast.makeText(getBaseContext(),
                    "Please enter the description", Toast.LENGTH_SHORT).show();
            return false;
        }

        // checking if the notification time is between the current time and the deadline
        if(!isNotificationTimeValid(spinnerNotifyTime.getSelectedItem().toString().substring(0, 5))) {
            Toast.makeText(getBaseContext(),
                    "Please change the notification time to either none or a proper time",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /*
    this method checks if the input notification time is valid or not by checking if it is in
    between the current time and the deadline time
    @param: notifyStr is the string that captures the notification time.
     */
    private boolean isNotificationTimeValid(String notifyStr) {
        Calendar currTime = Calendar.getInstance();

        Calendar deadlineTime = Calendar.getInstance();
        deadlineTime.set(selectedYear, selectedMonth - 1, selectedDay, selectedHour, selectedMinute);

        Calendar notifyTime = (Calendar) deadlineTime.clone();
        if (notifyStr.contains("N")) { // when the user does not want to be notified.
            return true;
        } else if(notifyStr.contains("d")) { // d for days
            String days = notifyStr.substring(0, notifyStr.indexOf(" "));
            int daysNum = 0 - Integer.parseInt(days);
            notifyTime.add(Calendar.DAY_OF_MONTH, daysNum);
        } else {
            String hours = notifyStr.substring(0, notifyStr.indexOf(" "));
            int hoursNum = 0 - Integer.parseInt(hours);
            notifyTime.add(Calendar.HOUR_OF_DAY, hoursNum);
        }
        System.out.println("currTime is " + currTime);
        System.out.println("notify is " + notifyTime);
        return currTime.before(notifyTime);
    }


    /*
    this method gets a unique id for notifications
     */
    private int getNotifyId() {
        SharedPreferences nofiyIdCounterRef = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor notifyIdEditor = nofiyIdCounterRef.edit();
        if (!nofiyIdCounterRef.contains("notifyIDCounter")) {
            notifyIdEditor.putInt("notifyIDCounter", 1);
        }
        int notifyId = nofiyIdCounterRef.getInt("notifyIDCounter", 0);
        notifyIdEditor.putInt("notifyIDCounter",
                nofiyIdCounterRef.getInt("notifyIDCounter", 0) + 1);
        notifyIdEditor.apply();
        return notifyId;
    }

    /*
    it setups the notification for the assignment
    @param: assignment is the object that the notification is created/edited for.
     */
    private void createNotification(AssignmentsEntity assignment) {
        AlarmManagement alarm = new AlarmManagement(assignment);
        alarm.setAlarm(this);
    }

}
