/*
###############################################################################
Class name : AddReminderActivity
Implemented Interfaces: TimePickerDialog.OnTimeSetListener for showing time picker dialogs.
purpose: This is model view class that is responsible for adding an reminder or editing
   an existing one
Methods:
  onCreate() -> It encapsulates/manages most the interaction.
  onTimeSet() -> same ^^ but for selecting the time
  getDisplayedTimeText() -> return the text that's used to show the time on the time's button.
  getDisplayedDaysText() -> return the text that's used to show the days on the days' button.
  getTime() -> returns the time as a string that the model can use.
  isInputsValid() -> return true if the user has entered all the requires inputs,
     false otherwise.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;
import creative.developer.m.studymanager.model.modelCoordinators.ReminderCoordinator;
import creative.developer.m.studymanager.view.NotificationManagement;

public class AddReminderActivity extends Activity implements
        TimePickerDialog.OnTimeSetListener {

    //variables that store days and time values
    private List<String> selectedDays;
    private int selectedHour;
    private int selectedMinute;
    // used with OnActivityResult
    private final int SELECT_DAYS_CODE = 22;
    // for editing reminder that has been edited previously.
    ReminderEntity editedReminder;
    // views
    private EditText editTextTitle;
    private Spinner spinnerRepeat;
    private Button pickTimeBtn;
    private Button pickDaysBtn;
    private EditText editTextDisc;
    private Button addBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // initializing views
        editTextTitle = findViewById(R.id.title_addreminder);
        spinnerRepeat = findViewById(R.id.repeat_addreminder);
        pickTimeBtn = findViewById(R.id.time_addreminder);
        pickDaysBtn = findViewById(R.id.days_addreminder);
        editTextDisc = findViewById(R.id.disc_addreminder);
        addBtn = findViewById(R.id.add_addreminder);
        cancelBtn = findViewById(R.id.cancel_addreminder);

        // initialize the model
        ReminderCoordinator model = ReminderCoordinator.getInstance(this);

        // if this activity is opened to edit existing assignment, then it will pre-fill
        // all the inputs
        Intent intent = getIntent();
        if (intent.getExtras().get("porpuse").equals("editing")) {
            // getting the object from the intent
            String gsonReminderOld = intent.getStringExtra("reminderObj");
            Gson gson = new Gson();
            editedReminder = gson.fromJson(gsonReminderOld, ReminderEntity.class);

            // assigning times and days values.
            selectedDays = editedReminder.getDaysList();
            selectedHour = editedReminder.getHourNum();
            selectedMinute = editedReminder.getMinuteNum();

            // filling the views
            editTextTitle.setText(editedReminder.getTitle());
            if (!editedReminder.getIsRepeated()) { // the default is repeating weekly
                spinnerRepeat.setSelection(1);
            }
            pickTimeBtn.setText(getDisplayedTimeText());
            pickDaysBtn.setText(getDisplayedDaysText());
            editTextDisc.setText(editedReminder.getDisc());
            addBtn.setText("Finish");
        }

        // showing a dialog to select the time
        pickTimeBtn.setOnClickListener((btn) -> {
            Calendar cal = Calendar.getInstance();
            int currHour = cal.get(Calendar.HOUR_OF_DAY)               ;
            int currMinute = cal.get(Calendar.MINUTE);
            new TimePickerDialog(AddReminderActivity.this,
                    AddReminderActivity.this, currHour,
                    currMinute, DateFormat.is24HourFormat(this)).show();
        });


        // showing a dialog(SelectDaysActivity) to select the days.
        pickDaysBtn.setOnClickListener((btn) -> {
            Intent selectDaysIntent = new Intent(AddReminderActivity.this, SelectDaysActivity.class);
            startActivityForResult(selectDaysIntent, SELECT_DAYS_CODE);
        });

        addBtn.setOnClickListener((btn) -> {
            // check first that the user has entered inputs
            // alarmID is different from reminder id.
            System.out.println("hour is " + selectedHour);
            if (isInputsValid()) {
                Intent intentSendback = new Intent(AddReminderActivity.this,
                        RemindersActivity.class);
                ReminderEntity reminder = null;
                if (intent.getExtras().get("porpuse").equals("editing")) {
                    reminder = editedReminder;
                    reminder.setTitle(editTextTitle.getText().toString());
                    reminder.setNotificationTime(getTime());
                    reminder.setDisc(editTextDisc.getText().toString());
                    reminder.setDays(getDisplayedDaysText().replace(", ", ","));
                    reminder.setIsRepeated(spinnerRepeat.getSelectedItem().toString().
                            contains("every"));
                    reminder.setIsOn(true);
                    model.updateReminder(reminder);
                } else {
                    reminder = model.addReminder(
                            editTextTitle.getText().toString(),
                            getTime(),
                            editTextDisc.getText().toString(),
                            getDisplayedDaysText().replace(", ", ","),
                            spinnerRepeat.getSelectedItem().toString().contains("every")
                    );
                }
                NotificationManagement alarm;
                for (int i = 0; i < selectedDays.size(); i++) {
                    alarm = new NotificationManagement(reminder, reminder.getReminderID() + i,
                            selectedDays.get(i));
                    System.out.println("in Add id: " + reminder.getReminderID() + i);
                    alarm.setNotify(this);
                }
                setResult(RESULT_OK, intentSendback);
                finish();
            }});

        // if the user does not want to add a reminder.
        cancelBtn.setOnClickListener((btn) -> {
            Intent cancelledInt = new Intent(AddReminderActivity.this,
                    RemindersActivity.class);
            setResult(Activity.RESULT_CANCELED, cancelledInt);
            finish();
        });

    }


    // This method is called when the the user sets the time on the time dialog.
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // storing user's inputs
        selectedHour = hourOfDay;
        selectedMinute = minute;
        pickTimeBtn.setText(getDisplayedTimeText());
    }


    /*
    * It returns the string that's used to display the time.
    * @return string that display time as 12 hours system.
    */
    private String getDisplayedTimeText() {
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

        return hourStr + ":" + selectedMinute + " " + period;
    }

    /*
    * It returns the string that's used to display the days
    * @return exsmple -> "mon, wed"
    */
    private String getDisplayedDaysText() {
        String shownText = "";
        if (selectedDays.size() > 0) {
            shownText += selectedDays.get(0);
        }
        for (int i = 1 ; i < selectedDays.size(); i++) {
            shownText += ", " + selectedDays.get(i);
        }
        return shownText;
    }


    /*
    * It returns the time as a string that the model can utilize to create a reminder object.
    */
    private String getTime() {
        String time = "";
        if (selectedHour < 10) { // making sure hours have 2 digits as a string
            time  += "0" + selectedHour;
        } else {
            time += Integer.toString(selectedHour);
        }
        time += ":";
        if (selectedMinute < 10) { // making sure minutes have 2 digits as a string
            time += "0" + selectedMinute;
        } else {
            time += Integer.toString(selectedMinute);
        }
        return time;
    }


    /*
    * this method validates the input. Also, it shows feedback for the user in case one of the inputs
    * was not valid.
    * @return true if the user's input is true.
    */
    private boolean isInputsValid() {
        boolean isValid;
        // checking if the user has inputted the title
        if (editTextTitle.getText().toString().length() == 0) {
            Toast.makeText(this, "Please type the title", Toast.LENGTH_LONG).show();
            return false;
        }
        // checking if the user has selected the time
        if (pickTimeBtn.getText().toString().equals("Select time")) {
            Toast.makeText(this, "Please select the time", Toast.LENGTH_LONG).show();
            return false;
        }
        // checking if the user has selected the days
        if (pickDaysBtn.getText().toString().equals(getString(R.string.selectDays))) {
            Toast.makeText(this, "Please select the days", Toast.LENGTH_LONG).show();
            return false;
        }
        // checking if the user has selected datetime that's after the current datetime if the
        // reminder is non-repeated.
        Calendar currTime = Calendar.getInstance();
        Calendar inputTime = Calendar.getInstance();
        inputTime.set(Calendar.MINUTE, selectedMinute);
        inputTime.set(Calendar.HOUR_OF_DAY, selectedHour);
        if (spinnerRepeat.getSelectedItem().toString().contains("only") &&
                !inputTime.after(currTime) && currTime.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.SHORT, Locale.getDefault()).toLowerCase().equals(selectedDays.get(0))) {
            Toast.makeText(this, "Please select proper time and date", Toast.LENGTH_LONG).show();
            return false;
        }
        // checking if the user has inputted the description.
        if (editTextDisc.getText().toString().length() == 0) {
            Toast.makeText(this, "Please type the description", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /*
    this method gets a unique id for notifications
     */
    private int getAlarmId() {
        SharedPreferences nofiyIdCounterRef = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor notifyIdEditor = nofiyIdCounterRef.edit();
        if (!nofiyIdCounterRef.contains("alarmIDCounter")) {
            notifyIdEditor.putInt("notifyIDCounter", 1);
        }
        int alarmId = nofiyIdCounterRef.getInt("notifyIDCounter", 0);
        notifyIdEditor.putInt("notifyIDCounter",
                nofiyIdCounterRef.getInt("notifyIDCounter", 0) + 1);
        notifyIdEditor.apply();
        return alarmId;
    }


    // this is called when the user comes back from the screen of selecting days
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DAYS_CODE && resultCode == RESULT_OK) {
            selectedDays = SelectDaysActivity.getSelectDays();
            pickDaysBtn.setText(getDisplayedDaysText());
        }
    }

}
