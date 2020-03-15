/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AssignmentActivity
purpose: This is model view class that is responsible for assignment fragment
  interaction with the user.
Methods:
  onCreateView() -> It encapsulates/manages all the interaction.
  onActivityResult() -> It receives the intent from AddAssignmentActivity that
     holds the data of the added assignment.
  createAssignemntsView() -> It creates the view that show all assignments.
  getViewedTime() -> It returns the string that is used to output the time to the user.
  getDueDateStrView(year, mont, day) -> it returns the string that will outputted on the peach
    labels that show the due date.
  doEditing() -> it handles the event of editing, which is modifying the view to fit editing mode.
###############################################################################
 */


package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.model.EntityListFiles.AssignmentsList;
import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;

import static android.app.Activity.RESULT_OK;

public class AssignmentActivity extends Fragment {

    private DataRepository repository; // used to access database
    private final int ADDING_CODE = 55; // used as requestCode for startActivityForResult()
    private final int EDITING_CODE = 66; // used as requestCode for startActivityForResult()
    private AssignmentsList retreivedAssignemnts; // the retrieved assignments stored in the phone
    boolean isEditing = false; // true when the user click on edit button
    // declaring views; the first two map depends on assignmentId as a key while the third dueDate
    private LinearLayout assignmentsLayout; // Layout that's holds all added assignments
    private HashMap<Integer, LinearLayout> oneAssignmentLayouts; // layout of a single assignment
    private HashMap<Integer, LinearLayout>  buttonsLayouts; // it stores all the layout of check buttons.
    private HashMap<String, TextView> dueDateViews; // it stores all peach text views of dueDates
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
        // Accessing database to retrieve the data then show it in the view.
        repository = DataRepository.getInstance(
                AppDatabase.getInstance(activityMain.getBaseContext()));
        Executor dbThread = Executors.newSingleThreadExecutor();
        dbThread.execute(() -> {
            retreivedAssignemnts = repository.getAssignments(activityMain.getBaseContext());
            activityMain.runOnUiThread(() -> createAssignemntsView(retreivedAssignemnts));
        });

        // initializing views
        View root = inflater.inflate(R.layout.assignment, container, false);
        assignmentsLayout = root.findViewById(R.id.assignemt_layout);
        addBtn = root.findViewById(R.id.add_assignment);


        // taking user to AddAssignmentActivity to input details of an added assignment.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    Intent intent = new Intent(activityMain,
                            AddAssignmentActivity.class);
                    intent.putExtra("porpuse", "adding");
                    startActivityForResult(intent, ADDING_CODE);
                } else {
                    doEditing(); // to close editing mode.
                }
            }
        });


        return root;
    }


    // this method receives intent from AddAssignmentActivity that store details of an added
    // assignemnt.
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDING_CODE && resultCode == RESULT_OK) {
            // creating object for the added assignment
            String recievedAssignmentStr = data.getExtras().getString("createdAssignment");
            Gson gson = new Gson();
            AssignmentsEntity addedAssignment = gson.fromJson(recievedAssignmentStr,
                    AssignmentsEntity.class);

            // adding the assignment to the database
            Executor insertingEx = Executors.newSingleThreadExecutor();
            insertingEx.execute(() -> {
                repository.addAssignment(addedAssignment);
                // reopen this fragment
                this.getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AssignmentActivity()).commit();
            });

        } else if (requestCode == EDITING_CODE && resultCode == RESULT_OK) {
            // updating the textview's info of the edited assignment
            Gson gson = new Gson();
            String updatedAssignmentStr = data.getExtras().getString("createdAssignment");
            AssignmentsEntity updatedAssignment = gson.fromJson(updatedAssignmentStr,
                    AssignmentsEntity.class);
            String oldAssignmentStr = data.getExtras().getString("outdatedAssignment");
            AssignmentsEntity oldAssignment = gson.fromJson(oldAssignmentStr, AssignmentsEntity.class);

            String course = updatedAssignment.getCourse();
            String disc = updatedAssignment.getDisc();

            String info = course + "\n";
            info += "Due time: " + getViewedTime(updatedAssignment.getHourNum(),
                    updatedAssignment.getMinuteNum()) + "\n";
            info += "description: " + disc;
            ((TextView)((LinearLayout) oneAssignmentLayouts.get(oldAssignment.getAssignmentID())
                    .getChildAt(0)).getChildAt(0)).setText(info);

            // updating assignments list's reference
            retreivedAssignemnts.updateAsssignment(oldAssignment, updatedAssignment);

            // update it the database
            Executor databaseThread = Executors.newSingleThreadExecutor();
            databaseThread.execute(() -> repository.updateAssignment(updatedAssignment));
        }
    }


    /*
    This function create the view for assignments
    @PARAM: assignments is an object that contains all retrieved assignment from the database.
     */
    private void createAssignemntsView (AssignmentsList assignments) {
        TextView assignmentIfno;
        String info;
        Button markedBtn;
        TextView dueDateSingleView;
        int lastHeight = 120; // used to leave an empty at the the bottom of the assignment layout

        // each assignment is in a single layout that is oneAssignmentLayout, and it has 2
        // layouts, infoLayout for the textView, buttonsLayout for the buttons.
        LinearLayout oneAssignmentLayout;
        LinearLayout infoLayout;
        oneAssignmentLayouts = new HashMap<>();
        buttonsLayouts = new HashMap<>();
        dueDateViews = new HashMap<>();

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
        // this is used for the dueDate views
        LinearLayout.LayoutParams layoutParamsDueDate = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsDueDate.topMargin = 30;
        // this is used for the layout of a single assignment
        LinearLayout.LayoutParams layoutParamsAssignment = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsAssignment.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutParamsAssignment.setMargins(20, 20, 20, 0);
        for (List<AssignmentsEntity> dayList: assignments.getAssignments().values()) {
            // creating textview that shows the due day date
            dueDateSingleView = new TextView(activityMain.getBaseContext());
            assignmentsLayout.addView(dueDateSingleView);
            dueDateViews.put(dayList.get(0).getDueDate(), dueDateSingleView);
            dueDateSingleView.setLayoutParams(layoutParamsDueDate);
            dueDateSingleView.setBackgroundResource(R.color.peach);
            dueDateSingleView.setPadding(15, 0, 0, 0);
            dueDateSingleView.setTextSize(30);
            dueDateSingleView.setText(getDueDateStrView(dayList.get(0).getYearNum(),
                    dayList.get(0).getMonthNum(), dayList.get(0).getDayNum()));

            for (AssignmentsEntity assignment : dayList) {
                // setting text
                info = assignment.getCourse() + "\n";
                info += "Due time: " + getViewedTime(assignment.getHourNum(),
                        assignment.getMinuteNum()) + "\n";
                info += "description: " + assignment.getDisc();

                // setting the assignment layout
                oneAssignmentLayout = new LinearLayout(activityMain.getBaseContext());
                assignmentsLayout.addView(oneAssignmentLayout);
                oneAssignmentLayout.setLayoutParams(layoutParamsAssignment);

                // setting layout of the assignment's information
                infoLayout = new LinearLayout(activityMain.getBaseContext());
                infoLayout.setOnLongClickListener( (v) -> {
                    doEditing();
                    return false;
                });
                oneAssignmentLayout.addView(infoLayout);
                infoLayout.setLayoutParams(layoutParamsInfo);

                // setting layout of the assignment's check button
                buttonsLayouts.put(assignment.getAssignmentID(), new LinearLayout(activityMain.getBaseContext()));
                oneAssignmentLayout.addView(buttonsLayouts.get(assignment.getAssignmentID()));
                buttonsLayouts.get(assignment.getAssignmentID()).setLayoutParams(layoutParamsButtons);

                // setting TextView
                assignmentIfno = new TextView(activityMain.getBaseContext());
                assignmentIfno.setText(info);

                // setting button's view
                markedBtn = new Button(activityMain.getBaseContext());
                markedBtn.setLayoutParams(layoutParamsBtn);
                if (assignment.getIsMarked()) {
                    markedBtn.setBackgroundResource(R.drawable.check_mark_icon);
                } else {
                    markedBtn.setBackgroundResource(R.drawable.check_mark_notdone);
                }

                // event of clicking the button that's updating isMarked
                markedBtn.setOnClickListener((btn) -> {
                    btn.setClickable(false);
                    Executor btnExecutor = Executors.newSingleThreadExecutor();
                    btnExecutor.execute(() -> {
                        assignment.setIsMarked(!assignment.getIsMarked());
                        repository.updateAssignment(assignment);
                    });
                    if (btn.getBackground().getConstantState() == getResources().
                            getDrawable(R.drawable.check_mark_icon).getConstantState()) {
                        btn.setBackgroundResource(R.drawable.check_mark_notdone);
                    } else {
                        btn.setBackgroundResource(R.drawable.check_mark_icon);
                    }
                    btn.setClickable(true);
                });

                // adding button and textview to thier layouts
                infoLayout.addView(assignmentIfno);
                buttonsLayouts.get(assignment.getAssignmentID()).addView(markedBtn);

                // adding oneAssignmentsLayout to the hashMap
                oneAssignmentLayouts.put(assignment.getAssignmentID(), oneAssignmentLayout);
                lastHeight = Math.max(lastHeight, oneAssignmentLayout.getHeight());
            }
        }

        // this section is to append a transparent layout, so that there is an empty space below the
        // lowest assignment to prevent an overlap between add's button and the lowest assignment.
        LinearLayout.LayoutParams layoutParamsTran = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT   , lastHeight);
        LinearLayout tranLayout = new LinearLayout(activityMain);
        tranLayout.setLayoutParams(layoutParamsTran);
        assignmentsLayout.addView(tranLayout);
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

    /*
    this method creates the String for showing due dates on the view
    @PARAM: year is integer that represent the dueDate's year
    @PARAM: month is integer that represent the dueDate's month
    @PARAM: day is integer that represent the dueDate's day
    @return: a string used for dueDate view
     */
    private String getDueDateStrView(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(year, month - 1, day); // month starts from 0
        String viewStr = "Due ";
        if (date.equals(dueDate)) {
            viewStr += "today";
        } else if (dueDate.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR) == 1) {
            viewStr += "tomorrow";
        } else { // January is 0 not 1 at getMonths()
            viewStr += new DateFormatSymbols(Locale.getDefault()).getMonths()[month - 1] +
                    " " + day;
        }
        return viewStr;
    }


    // this method handles the event of editing assignemnts when the users click edit or hold on
    // an assignment
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
            for (List<AssignmentsEntity> assignmentList :
                    retreivedAssignemnts.getAssignments().values()) {
                for (AssignmentsEntity assignment : assignmentList) {
                    // setting change button; on the left
                    layoutParamsBtn.leftMargin = 0;
                    changeBtn = new Button(activityMain.getBaseContext());
                    changeBtn.setLayoutParams(layoutParamsBtn);
                    changeBtn.setBackgroundResource(R.drawable.edit_icon);
                    changeBtn.setOnClickListener((changingBtn) -> {
                        Intent intent = new Intent(activityMain,
                                AddAssignmentActivity.class);
                        Gson formattedAssignment = new Gson();
                        String stringAssignment = formattedAssignment.toJson(assignment);
                        intent.putExtra("assignmentObj", stringAssignment);
                        intent.putExtra("porpuse", "editing");
                        startActivityForResult(intent, EDITING_CODE);
                    });

                    // setting delete button; on the right
                    layoutParamsBtn.leftMargin = 10;
                    deleteBtn = new Button(activityMain.getBaseContext());
                    deleteBtn.setLayoutParams(layoutParamsBtn);
                    deleteBtn.setBackgroundResource(R.drawable.delete_icon);
                    deleteBtn.setOnClickListener((deletingBtn) -> {
                        assignmentsLayout.removeView(oneAssignmentLayouts.
                                get(assignment.getAssignmentID()));
                        Executor deletionThread = Executors.newSingleThreadExecutor();
                        deletionThread.execute(() -> repository.deleteAssignment(assignment));
                        retreivedAssignemnts.removeAssignment(assignment);
                    });

                    // setting the layout by replacing check button with change and delete
                    buttonsLayouts.get(assignment.getAssignmentID()).removeAllViews();
                    buttonsLayouts.get(assignment.getAssignmentID()).addView(changeBtn);
                    buttonsLayouts.get(assignment.getAssignmentID()).addView(deleteBtn);
                    System.out.println(assignment.getAssignmentID());
                    System.out.println("Testing " + buttonsLayouts.get
                            (assignment.getAssignmentID()).getChildCount());
                }
            }

        } else {
            // bring back the add button
            addBtn.setBackgroundResource(R.drawable.add_btn_icon);
            isEditing = false;

            // recreating assignments view so that they are sorted
            assignmentsLayout.removeAllViews();
            createAssignemntsView(retreivedAssignemnts);
        }

    }
}
