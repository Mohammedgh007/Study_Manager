/*
###############################################################################
Author: Mohammed Alghamdi
Class name : SelectEditedCourse
purpose: This is model view class that is responsible for:
    1- selecting a pre-added course.
    2- determine if the user wants to edit or delete the selected course.
    3- send back 1 and 2 to  HomeActivity.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
###############################################################################
 */
package creative.developer.m.studymanager.modelview;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.modelCoordinators.CoursesCoordinator;

public class SelectEditedCourse extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_edited_course);

        // making the size of this activity as a popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.7));

        // the getting the model's instance
        CoursesCoordinator model = CoursesCoordinator.getInstance(this);


        // initializes the variables that are used for the view.
        LinearLayout coursesLayout = findViewById(R.id.courses_select_edited_course);
        Button cancelBtn = findViewById(R.id.cancelbtn_select_edited_course);


        // handling the event of clicking the cancel button
        cancelBtn.setOnClickListener((btn) -> {
            Intent sendBack = new Intent(SelectEditedCourse.this, HomeActivity.class);
            setResult(RESULT_CANCELED, sendBack);
            finish();
        });

        // perpare to set the layout of courses
        TextView courseTV; // textview for course's name
        LinearLayout oneCourseLayout;
        Button deleteBtn;
        Button editBtn;
        Map<String, LinearLayout> buttonsLayoutMap = new HashMap<>(); // it maps course's name to buttons' layout.
        // this is used for the layout of a single course
        LinearLayout.LayoutParams layoutParamsCourse = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsCourse.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutParamsCourse.setMargins(20, 20, 20, 0);
        // this is used for the layout of text view
        LinearLayout.LayoutParams layoutParamsCourseName = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsCourseName.weight = 0.60f;
        LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.size_edit_btn) ,// for buttons
                (int) getResources().getDimension(R.dimen.size_edit_btn));
        // this is used for the layout of buttons
        LinearLayout.LayoutParams layoutParamsButtons = new LinearLayout.LayoutParams(
                0, (int)getResources().getDimension(R.dimen.size_edit_btn) + 10);
        layoutParamsButtons.weight = 0.40f;
        layoutParamsButtons.setLayoutDirection(LinearLayout.VERTICAL);

        // set the layout for course
        for (String courseName : model.getCoursesStr()) {
            // adding a single course layout to the layout of courses.
            oneCourseLayout = new LinearLayout(getBaseContext());
            coursesLayout.addView(oneCourseLayout);
            oneCourseLayout.setLayoutParams(layoutParamsCourse);

            // adding the textview that hold the course name.
            courseTV = new TextView(getBaseContext());
            courseTV.setText(courseName);
            courseTV.setLayoutParams(layoutParamsCourseName);
            oneCourseLayout.addView(courseTV);

            // adding the layout of the buttons
            buttonsLayoutMap.put(courseName, new LinearLayout(getBaseContext()));
            buttonsLayoutMap.get(courseName).setLayoutParams(layoutParamsButtons);
            oneCourseLayout.addView(buttonsLayoutMap.get(courseName));

            // setting change button; on the left
            layoutParamsBtn.leftMargin = 0;
            editBtn = new Button(getBaseContext());
            editBtn.setLayoutParams(layoutParamsBtn);
            editBtn.setBackgroundResource(R.drawable.edit_icon);
            editBtn.setOnClickListener((changingBtn) -> {
                Intent sendBack = new Intent(SelectEditedCourse.this,
                        HomeActivity.class);
                sendBack.putExtra("courseName", courseName);
                sendBack.putExtra("porpuse", "editing");
                setResult(RESULT_OK, sendBack);
                finish();
            });

            // setting delete button; on the right
            layoutParamsBtn.leftMargin = 10;
            deleteBtn = new Button(getBaseContext());
            deleteBtn.setLayoutParams(layoutParamsBtn);
            deleteBtn.setBackgroundResource(R.drawable.delete_icon);
            deleteBtn.setOnClickListener((deletingBtn) -> {
                Intent sendBack = new Intent(SelectEditedCourse.this,
                        HomeActivity.class);
                sendBack.putExtra("courseName", courseName);
                sendBack.putExtra("porpuse", "deleting");
                setResult(RESULT_OK, sendBack);
                finish();
            });

            // adding delete button and edit button to the layout of buttons
            buttonsLayoutMap.get(courseName).addView(editBtn);
            buttonsLayoutMap.get(courseName).addView(deleteBtn);
        }

    }
}
