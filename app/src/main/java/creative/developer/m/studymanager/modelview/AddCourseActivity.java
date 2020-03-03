/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AddCourseActivity
purpose: This is model view class that is responsible for the popup window of adding a course.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import creative.developer.m.studymanager.R;

public class AddCourseActivity extends AppCompatActivity {

    // the views
    private EditText courseET;
    private Button doneBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course);

        // setting the activity as a popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.6), (int) (height * 0.6));

        // initializing the views
        courseET = findViewById(R.id.course_name_add_courses);
        doneBtn = findViewById(R.id.finish_add_course);
        cancelBtn = findViewById(R.id.cancel_add_course);

        doneBtn.setOnClickListener((view) -> {
            String inputtedStr = courseET.getText().toString().toUpperCase();
            if(!inputtedStr.equals("") && !inputtedStr.equals("") &&
                    !CoursesActivity.getExistingCourses().contains(inputtedStr)) {
                Intent sendBack = new Intent(AddCourseActivity.this, CoursesActivity.class);
                sendBack.putExtra("added course", courseET.getText().toString().toUpperCase());
                setResult(RESULT_OK, sendBack);
                finish();
            } else if (CoursesActivity.getExistingCourses().contains(inputtedStr)) {
                Toast.makeText(getBaseContext(),
                        "Please input non-used course's name", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(),
                        "Please input the course name first", Toast.LENGTH_LONG).show();
            }
        });

        cancelBtn.setOnClickListener((view) -> finish());
    }
}
