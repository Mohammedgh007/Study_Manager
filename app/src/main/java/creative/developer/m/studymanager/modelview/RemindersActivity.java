package creative.developer.m.studymanager.modelview;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;
import creative.developer.m.studymanager.model.modelCoordinators.ReminderCoordinator;

import static android.app.Activity.RESULT_OK;

public class RemindersActivity extends Fragment implements Observer {

    private ReminderCoordinator model;
    private final int ADDING_CODE = 55; // used as requestCode for startActivityForResult()
    private final int EDITING_CODE = 66; // used as requestCode for startActivityForResult()
    boolean isEditing = false; // true when the user click on edit button
    // declaring views; the two map depends on reminderId as a key
    private LinearLayout remindersLayout; // Layout that's holds all added assignments
    private HashMap<Integer, LinearLayout> oneReminderLayouts; // layout of a single assignment
    private HashMap<Integer, LinearLayout>  buttonsLayouts; // it stores all the layout of switch buttons.
    private Button addBtn;
    private Activity activityMain;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityMain = (Activity) context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        activityMain = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // initializing views
        View root = inflater.inflate(R.layout.activity_reminders, container, false);
        remindersLayout = root.findViewById(R.id.reminder_layout);
        addBtn = root.findViewById(R.id.add_reminder);

        // Accessing database to retrieve the data then show it in the view.
        model = ReminderCoordinator.getInstance(activityMain.getBaseContext());
        model.addObserver(this);
        if (model.getReminders() != null) {
            // to avoid race condition; it will be called on update() otherwise.
            createReminderssView(model.getReminders());
        }


        // taking user to AddReminder to input details of an added assignment.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    Intent intent = new Intent(activityMain,
                            AddReminderActivity.class);
                    intent.putExtra("porpuse", "adding");
                    startActivityForResult(intent, ADDING_CODE);
                } else {
                    doEditing(); // to close editing mode.
                }
            }
        });


        return root;
    }


    // this method receives intent from AddReminderActivity that store details of weather the user
    // have added/edited or not.
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDING_CODE && resultCode == RESULT_OK) {
            // telling the user that the assignment has been added
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.reminderAdded), Toast.LENGTH_LONG).show();
            createReminderssView(model.getReminders());
        } else if (requestCode == EDITING_CODE && resultCode == RESULT_OK) {
            // telling the user that the assignment has been edited
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.reminderUpdated), Toast.LENGTH_LONG).show();
            createReminderssView(model.getReminders());
        }
    }


    /*
    This function create the view for reminders
    @PARAM: assignments is an object that contains all retrieved reminders from the database.
     */
    private void createReminderssView(List<ReminderEntity> reminders) {
        TextView ReminderIfno;
        String info;
        Switch switchBtn;
        int lastHeight = 120; // used to leave an empty at the the bottom of the reminder layout

        // each reminder is in a single layout that is reminderLayout, and it has 2
        // layouts, infoLayout for the textView, buttonsLayout for the buttons.
        LinearLayout oneReminderLayout;
        LinearLayout infoLayout;
        oneReminderLayouts = new HashMap<>();
        buttonsLayouts = new HashMap<>();

        remindersLayout.removeAllViews(); // clearing the layout from previous use.

        // this is used for the layout of text view
        LinearLayout.LayoutParams layoutParamsInfo = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsInfo.weight = 0.75f;
        // this is used for the buttons
        ConstraintLayout.LayoutParams layoutParamsBtn = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT ,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        // this is used for the layout of buttons
        LinearLayout.LayoutParams layoutParamsButtons = new LinearLayout.LayoutParams(
                0, (int)getResources().getDimension(R.dimen.size_edit_btn) + 10);
        layoutParamsButtons.weight = 0.25f;
        layoutParamsButtons.setLayoutDirection(LinearLayout.VERTICAL);
        // this is used for the layout of a single reminder
        LinearLayout.LayoutParams layoutParamsReminder = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsReminder.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutParamsReminder.setMargins(20, 50, 20, 0);

        for (ReminderEntity reminder : reminders) {
            // setting text
            info = reminder.getTitle() + "\n";
            info += getResources().getString(R.string.timeText) + " " + getViewedTime(reminder.getHourNum(),
                    reminder.getMinuteNum()) + "\n";
            info += getResources().getString(R.string.daysText) + " " + reminder.getDaysList() + '\n';
            info += getResources().getString(R.string.repeatText) + " " +
                    (reminder.getIsRepeated() ?
                            getResources().getString(R.string.weekly) :
                            getResources().getString(R.string.once)) + "\n";
            info += getResources().getString(R.string.discText) + reminder.getDisc();

            // setting the reminder layout
            oneReminderLayout = new LinearLayout(activityMain.getBaseContext());
            remindersLayout.addView(oneReminderLayout);
            oneReminderLayout.setLayoutParams(layoutParamsReminder);

            // setting layout of the reminder's information
            infoLayout = new LinearLayout(activityMain.getBaseContext());
            infoLayout.setOnLongClickListener( (v) -> {
                doEditing();
                return false;
            });
            oneReminderLayout.addView(infoLayout);
            infoLayout.setLayoutParams(layoutParamsInfo);

            // setting layout of the reminder's check button
            buttonsLayouts.put(reminder.getReminderID(), new LinearLayout(activityMain.getBaseContext()));
            oneReminderLayout.addView(buttonsLayouts.get(reminder.getReminderID()));
            buttonsLayouts.get(reminder.getReminderID()).setLayoutParams(layoutParamsButtons);

            // setting TextView
            ReminderIfno = new TextView(activityMain.getBaseContext());
            ReminderIfno.setText(info);

            // setting button's view
            switchBtn = new Switch(activityMain.getBaseContext());
            switchBtn.setLayoutParams(layoutParamsBtn);
            switchBtn.setChecked(reminder.getIsOn());

            // event of clicking the button that's updating isOn(whether the reminder is activated or not)
            switchBtn.setOnClickListener((btn) -> {
                btn.setClickable(false);
                Executor btnExecutor = Executors.newSingleThreadExecutor();
                btnExecutor.execute(() -> {
                    reminder.setIsOn(!reminder.getIsOn());
                    model.updateReminder(reminder);
                });
                btn.setClickable(true);
            });

            // adding button and textview to thier layouts
            infoLayout.addView(ReminderIfno);
            buttonsLayouts.get(reminder.getReminderID()).addView(switchBtn);

            // adding oneAssignmentsLayout to the hashMap
            oneReminderLayouts.put(reminder.getReminderID(), oneReminderLayout);
            lastHeight = Math.max(lastHeight, oneReminderLayout.getHeight());
        }

        // this section is to append a transparent layout, so that there is an empty space below the
        // lowest reminder to prevent an overlap between add's button and the lowest reminder.
        LinearLayout.LayoutParams layoutParamsTran = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT   , lastHeight);
        LinearLayout tranLayout = new LinearLayout(activityMain);
        tranLayout.setLayoutParams(layoutParamsTran);
        remindersLayout.addView(tranLayout);
    }


    /*
    this method creates the string that will be used to show the time for the user
    @PARAM: hour is an intger that range between 0-23
    @PARAM: int that represent minutes
    @return: String for viewing time
     */
    private String getViewedTime (int hour, int minute) {
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
        String period = (hour < 13) ? "a.m" : "p.m";
        return strHour + ":" + strMinute + " " + period;
    }




    // this method handles the event of editing reminders when the users click edit or hold on
    // a reminder
    private void doEditing(){
        if (!isEditing) {
            // changing the appearance of add button.
            addBtn.setBackgroundResource(R.drawable.done_editing_icon);
            isEditing = true;

            Button changeBtn;
            Button deleteBtn;
            LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.size_edit_btn) ,// for buttons
                    (int) getResources().getDimension(R.dimen.size_edit_btn));
            for (ReminderEntity reminder : model.getReminders()) {
                // setting change button; on the left
                layoutParamsBtn.leftMargin = 0;
                changeBtn = new Button(activityMain.getBaseContext());
                changeBtn.setLayoutParams(layoutParamsBtn);
                changeBtn.setBackgroundResource(R.drawable.edit_icon);
                changeBtn.setOnClickListener((changingBtn) -> {
                    Intent intent = new Intent(activityMain,
                            AddReminderActivity.class);
                    Gson formattedReminder = new Gson();
                    String stringReminder = formattedReminder.toJson(reminder);
                    intent.putExtra("reminderObj", stringReminder);
                    intent.putExtra("porpuse", "editing");
                    startActivityForResult(intent, EDITING_CODE);
                });

                // setting delete button; on the right
                layoutParamsBtn.leftMargin = 10;
                deleteBtn = new Button(activityMain.getBaseContext());
                deleteBtn.setLayoutParams(layoutParamsBtn);
                deleteBtn.setBackgroundResource(R.drawable.delete_icon);
                deleteBtn.setOnClickListener((deletingBtn) -> {
                    remindersLayout.removeView(oneReminderLayouts.
                            get(reminder.getReminderID()));
                    model.removeReminder(reminder);
                });

                // setting the layout by replacing check button with change and delete
                buttonsLayouts.get(reminder.getReminderID()).removeAllViews();
                buttonsLayouts.get(reminder.getReminderID()).addView(changeBtn);
                buttonsLayouts.get(reminder.getReminderID()).addView(deleteBtn);
                System.out.println(reminder.getReminderID());
                System.out.println("Testing " + buttonsLayouts.get
                        (reminder.getReminderID()).getChildCount());
            }

        } else {
            // bring back the add button
            addBtn.setBackgroundResource(R.drawable.add_btn_icon);
            isEditing = false;

            // recreating assignments view so that they are sorted
            remindersLayout.removeAllViews();
            createReminderssView(model.getReminders());
        }

    }
    @Override
    public void update(Observable o, Object arg) {
        activityMain.runOnUiThread(() -> createReminderssView(model.getReminders()));
    }
}
