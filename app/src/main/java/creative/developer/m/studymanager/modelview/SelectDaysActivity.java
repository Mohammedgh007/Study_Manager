/*
###############################################################################
Class name : SelectDaysActivity
purpose: This is model view class that is responsible for selecting the days, which is used by
    AddReminderActivity.
Methods:
  onCreate() -> It encapsulates/manages most the interaction.
  getSelectDays() -> It is used to get the days that the user have selected.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import creative.developer.m.studymanager.R;

public class SelectDaysActivity extends AppCompatActivity {

    private static List<String> selectedDays; // used as a getter for sending the days to AddReminderActivity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_days);

        selectedDays = new ArrayList<>();

        // setting the activity as a popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.7));

        // initializing check box objects.
        CheckBox[] daysCB = new CheckBox[7];
        daysCB[0] = findViewById(R.id.checkBoxDay1);
        daysCB[1] = findViewById(R.id.checkBoxDay2);
        daysCB[2] = findViewById(R.id.checkBoxDay3);
        daysCB[3] = findViewById(R.id.checkBoxDay4);
        daysCB[4] = findViewById(R.id.checkBoxDay5);
        daysCB[5] = findViewById(R.id.checkBoxDay6);
        daysCB[6] = findViewById(R.id.checkBoxDay7);
        Button doneBtn = findViewById(R.id.done_select_day);
        Button cancelBtn = findViewById(R.id.cancel_select_day);

        // Assign the dates with the check box objects
        Calendar day = Calendar.getInstance();
        String dayStr, dateStr;
        for (int i = 0; i < 7; i++) {
            day.add(Calendar.DAY_OF_MONTH, i); // update the date to the check box date
            dayStr = day.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.SHORT, Locale.getDefault()).toLowerCase();
            dateStr = day.getDisplayName(Calendar.MONTH,
                    Calendar.LONG, Locale.getDefault()).toLowerCase() + " " +
                    day.get(Calendar.DAY_OF_MONTH); //days start from 0 instead from 1.
            daysCB[i].setText(dayStr + " " + dateStr);
            day.add(Calendar.DAY_OF_MONTH, 0 - i); // return to the original date.
        }

        // event handling for selecting the lesson once clicking the button add.
        doneBtn.setOnClickListener((btn) -> {
            // adding the selected days to selectedDays
            for (int i = 0; i < 7; i++) {
                if (daysCB[i].isChecked()) {
                    selectedDays.add(daysCB[i].getText().toString().substring(0, 3));
                }
            }
            // closing this activity if the user has selected days; otherwise, tell the user to
            // select some days.
            if (selectedDays.size() == 0) {
                Toast.makeText(SelectDaysActivity.this, "Please check on the days that" +
                        " you would like to select first", Toast.LENGTH_LONG).show();
            } else {
                Intent sendBack = new Intent(SelectDaysActivity.this, AddReminderActivity.class);
                setResult(RESULT_OK, sendBack);
                finish();
            }
        });

        cancelBtn.setOnClickListener((btn) -> {
            Intent sendBack = new Intent(SelectDaysActivity.this, AddReminderActivity.class);
            setResult(RESULT_CANCELED, sendBack);
            finish();
        });

    }

    // this is a getter for the field selectedDays that is used to send the days to AddReminderActivity
    public static List<String> getSelectDays() {return selectedDays;}
}
