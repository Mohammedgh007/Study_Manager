package creative.developer.m.studymanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.EntityListFiles.RemarksList;
import creative.developer.m.studymanager.dbFiles.AppDatabase;
import creative.developer.m.studymanager.dbFiles.DataRepository;
import creative.developer.m.studymanager.dbFiles.EntityFiles.RemarkEntity;

public class RemarksActivity extends Activity {

    DataRepository repository; // used to access database
    final int ADDING_CODE = 55; // used as requestCode for startActivityForResult()
    final int EDITING_CODE = 66; // used as requestCode for startActivityForResult()
    RemarksList retreivedREmarks; // the retrieved assignments stored in the phone
    boolean isEditing = false; // true when the user click on edit button
    // declaring views; the first two map depends on assignmentId as a key while the third dueDate
    LinearLayout remarksLayout; // Layout that's holds all added assignments
    HashMap<Integer, LinearLayout> oneRemarkLayouts; // layout of a single assignment
    HashMap<Integer, LinearLayout>  buttonsLayouts; // it stores all the layout of check buttons.
    HashMap<String, TextView> dueDateViews; // it stores all peach text views of dueDates
    Button editBtn;
    Button addBtn;
    Button homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remarks);

        // Accessing database to retrieve the data
        repository = DataRepository.getInstance(
                AppDatabase.getInstance(getBaseContext()));
        Executor dbThread = Executors.newSingleThreadExecutor();
        dbThread.execute(() -> {
            repository.getRemarks(getBaseContext());
        });


        // initializing views
        remarksLayout = findViewById(R.id.remarks_layout);
        editBtn = findViewById(R.id.edit_remark);
        addBtn = findViewById(R.id.add_remark);
        homeBtn = findViewById(R.id.home_remark);


        // Distributing assignments' views on the main thread when
        // setRetreivedDataListener() is called after remarks are retrieved from database.
        repository.setRetreivedDataListener((recivedRemarks) -> {
            retreivedREmarks =  (RemarksList) recivedRemarks;
            runOnUiThread(() -> createAssignemntsView(retreivedREmarks));
        });

        // taking the user back to home screen when clicking on homeBtn
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemarksActivity.this,
                        HomeActivity.class);
                startActivity(intent);
            }
        });


        // editing and deleting assignment when clicking editBtn
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                if (!isEditing) {
                    // hiding add and home buttons buttons
                    homeBtn.setVisibility(View.GONE);
                    addBtn.setVisibility(View.GONE);
                    editBtn.setText("Done Editing");
                    isEditing = true;

                    Button changeBtn;
                    Button deleteBtn;
                    LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                            (int) getResources().getDimension(R.dimen.size_edit_btn) ,// for buttons
                            (int) getResources().getDimension(R.dimen.size_edit_btn));
                    for (RemarkEntity remark : retreivedREmarks.getRemarksList()) {
                        // setting change button; on the left
                        layoutParamsBtn.leftMargin = 0;
                        changeBtn = new Button(getBaseContext());
                        changeBtn.setLayoutParams(layoutParamsBtn);
                        changeBtn.setBackgroundResource(R.drawable.edit_icon);
                        changeBtn.setOnClickListener((changingBtn) -> {
                            Intent intent = new Intent(RemarksActivity.this,
                                    AddRemarkActivity.class);
                            Gson formattedAssignment = new Gson();
                            String stringRemark = formattedAssignment.toJson(remark);
                            intent.putExtra("remarkObj", stringRemark);
                            intent.putExtra("porpuse", "editing");
                            startActivityForResult(intent, EDITING_CODE);
                        });

                        // setting delete button; on the right
                        layoutParamsBtn.leftMargin = 10;
                        deleteBtn = new Button(getBaseContext());
                        deleteBtn.setLayoutParams(layoutParamsBtn);
                        deleteBtn.setBackgroundResource(R.drawable.delete_icon);
                        deleteBtn.setOnClickListener((deletingBtn) -> {
                            remarksLayout.removeView(oneRemarkLayouts.
                                    get(remark.getRemarkID()));
                            Executor deletionThread = Executors.newSingleThreadExecutor();
                            deletionThread.execute(() -> repository.deleteRemark(remark));
                            retreivedREmarks.removeRemark(remark);
                        });

                        // setting the layout by replacing check button with change and delete
                        buttonsLayouts.get(remark.getRemarkID()).addView(changeBtn, 0);
                        buttonsLayouts.get(remark.getRemarkID()).addView(deleteBtn, 1);
                    }

                } else {
                    // bring back the two buttons
                    homeBtn.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.VISIBLE);
                    editBtn.setText("Edit");
                    isEditing = false;

                    // recreating assignments view so that they are sorted
                    remarksLayout.removeAllViews();
                    createAssignemntsView(retreivedREmarks);
                }

                v.setClickable(true);
            }
        });


        // taking user to AddAssignmentActivity to input details of an added assignment.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemarksActivity.this,
                        AddRemarkActivity.class);
                intent.putExtra("porpuse", "adding");
                startActivityForResult(intent, ADDING_CODE);
            }
        });
    }

    /*
    This function create the view for remarks
    @PARAM: remarks is an object that contains all retrieved remarks from the database.
     */
    private void createAssignemntsView (RemarksList remarks) {
        TextView RemarkIfno;
        String info;

        // each remark is in a single layout that is oneRemarkLayout, and it has 2
        // layouts, infoLayout for the textView, buttonsLayout for the buttons.
        LinearLayout oneRemarkLayout;
        LinearLayout infoLayout;
        oneRemarkLayouts = new HashMap<>();
        buttonsLayouts = new HashMap<>();
        dueDateViews = new HashMap<>();

        // this is used for the layout of text view
        LinearLayout.LayoutParams layoutParamsInfo = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsInfo.weight = 0.75f;
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
        LinearLayout.LayoutParams layoutParamsRemark = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRemark.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutParamsRemark.setMargins(20, 20, 20, 0);

        for (RemarkEntity remark : retreivedREmarks.getRemarksList()) {
            // setting text
            info = remark.getTitle() + "\n";
            info += "Date: " + getViewedDate(remark.getDate()) + " ";
            info += "time: " + getViewedTime(remark.getHourNum(),
                    remark.getMinuteNum()) + "\n";
            info += "description: " + remark.getDisc();

            // setting the remark layout
            oneRemarkLayout = new LinearLayout(getBaseContext());
            remarksLayout.addView(oneRemarkLayout);
            oneRemarkLayout.setLayoutParams(layoutParamsRemark);

            // setting layout of the remark's information
            infoLayout = new LinearLayout(getBaseContext());
            oneRemarkLayout.addView(infoLayout);
            infoLayout.setLayoutParams(layoutParamsInfo);

            // setting layout of the remark's check button
            buttonsLayouts.put(remark.getRemarkID(), new LinearLayout(getBaseContext()));
            oneRemarkLayout.addView(buttonsLayouts.get(remark.getRemarkID()));
            buttonsLayouts.get(remark.getRemarkID()).setLayoutParams(layoutParamsButtons);

            // setting TextView
            RemarkIfno = new TextView(getBaseContext());
            RemarkIfno.setText(info);

            // adding textview to their layouts
            infoLayout.addView(RemarkIfno);

            // adding oneRemarksLayout to the hashMap
            oneRemarkLayouts.put(remark.getRemarkID(), oneRemarkLayout);
        }
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
    this method creates the string for view remark's date
    @PARAM: date is the date field on RemarkEntity's object
    @return: it returns String
     */
    private String getViewedDate (String date) {
        int monthNum = Integer.parseInt(date.substring(date.indexOf(":") + 1, date.indexOf(";")));
        String month = new DateFormatSymbols(Locale.getDefault()).getShortMonths()[monthNum - 1];
        String day = date.substring(date.indexOf(";") + 1);
        return month + " " + day;
    }

    // this method receives intent from AddRemarkActivity that store details of an added
    // remark.
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDING_CODE && resultCode == RESULT_OK) {
            // creating object for the added assignment
            String recievedRemarkStr = data.getExtras().getString("createdRemark");
            Gson gson = new Gson();
            RemarkEntity addedRemark = gson.fromJson(recievedRemarkStr,
                    RemarkEntity.class);

            // adding the remark to the remark list in the view.
            retreivedREmarks.addRemark(addedRemark);
            // views are removed to create a new sorted assignments view.
            remarksLayout.removeAllViews();
            createAssignemntsView(retreivedREmarks);

            // adding the assignment to the database
            Executor insertingEx = Executors.newSingleThreadExecutor();
            insertingEx.execute(() -> repository.addRemark(addedRemark));
        } else if (requestCode == EDITING_CODE && resultCode == RESULT_OK) {
            // updating the textview's info of the edited remark
            Gson gson = new Gson();
            String updatedRemarkStr = data.getExtras().getString("createdRemark");
            RemarkEntity updatedRemark = gson.fromJson(updatedRemarkStr,
                    RemarkEntity.class);
            String oldRemarkStr = data.getExtras().getString("outdatedRemark");
            RemarkEntity oldRemark = gson.fromJson(oldRemarkStr, RemarkEntity.class);

            String title = updatedRemark.getTitle();
            String disc = updatedRemark.getDisc();

            String info = title + "\n";
            info += "Date: " + getViewedDate(updatedRemark.getDate()) + " ";
            info += "time: " + getViewedTime(updatedRemark.getHourNum(),
                    updatedRemark.getMinuteNum()) + "\n";
            info += "description: " + disc;
            ((TextView)((LinearLayout) oneRemarkLayouts.get(oldRemark.getRemarkID())
                    .getChildAt(0)).getChildAt(0)).setText(info);

            // updating assignments list's reference
            retreivedREmarks.updateRemark(updatedRemark);

            // update it the database
            Executor databaseThread = Executors.newSingleThreadExecutor();
            databaseThread.execute(() -> repository.updateRemark(updatedRemark));
        }
    }
}
