/*
###############################################################################
Class name : HomeActivity
purpose: This is model view class that is responsible for home fragment.
  interaction with the user.
Methods:
  onCreateView() -> It encapsulates/manages all the interaction.
  createScheduleView() -> It creates the view of the schedule.
  onActivityResult() -> It receives the intent AddCoursesActivity.
  getShownText() -> It returns the text that will be shown in a schedule's
    field for a course.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.modelCoordinators.CoursesCoordinator;
import creative.developer.m.studymanager.view.StringMaker;

import static android.app.Activity.RESULT_OK;

public class HomeActivity extends Fragment implements Observer {

    final int ADDING_COURSE_CODE = 123; // used for onActivityResult() when adding a course.
    final int EDITING_SCHEDULE_CODE = 332; // used for onActivityResult() when selecting edited or deleted course.
    final int UPDATE_COURSE_CODE = 345; // used for onActivityResult() when updating a course's info.
    private CoursesCoordinator model;
    // Declaring views
    private Button addCourseBtn;
    private Button editBtn;
    private GridLayout scheduleLayout;
    private Activity activityMain; // to access context

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
        View root = inflater.inflate(R.layout.home_activity, container, false);
        addCourseBtn = root.findViewById(R.id.add_acourse_home_activity);
        editBtn = root.findViewById(R.id.edit_acourse_home_activity);
        scheduleLayout = root.findViewById(R.id.schedule_home_activity);

        // initializing model
        model = CoursesCoordinator.getInstance(activityMain.getBaseContext());
        model.addObserver(this);
        if (model.getCoursesStr() != null) { // to avoid race condition.
            createScheduleView();
        }

        // handling the event of adding a course
        addCourseBtn.setOnClickListener((view -> {
            Intent addIntent = new Intent(activityMain, AddCourseActivity.class);
            addIntent.putExtra("porpuse", "adding");
            startActivityForResult(addIntent, ADDING_COURSE_CODE);
        }));

        // handling the event of editing the schedule
        editBtn.setOnClickListener((btn) -> {
            Intent editIntent = new Intent(activityMain, SelectEditedCourse.class);
            startActivityForResult(editIntent, EDITING_SCHEDULE_CODE);
        });

        return root;
    }


    // this method recieve the image from gallery for editBtn's intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDING_COURSE_CODE && resultCode == RESULT_OK) {
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.courseAdded), Toast.LENGTH_LONG).show();
            createScheduleView();

        } else if (requestCode == EDITING_SCHEDULE_CODE && resultCode == RESULT_OK) {
            // linearly search for the course object.
            String courseName = data.getStringExtra("courseName");

            String porpuse = data.getExtras().getString("porpuse");
            if (porpuse.equals("editing")) { // preparing for editing a course from the schedule.
                // sending the course name, so that AddCourseActivity can pull up the data related to that course
                Intent updateIntent = new Intent(activityMain, AddCourseActivity.class);
                updateIntent.putExtra("courseName", courseName);
                updateIntent.putExtra("porpuse", "editing");
                startActivityForResult(updateIntent, UPDATE_COURSE_CODE);
            } else { // removing a course from the schedule
                model.removeCourse(model.getCourse(courseName), activityMain);
                createScheduleView();
                Toast.makeText(activityMain,
                        getResources().getString(R.string.theCourse) + " " +
                                courseName + " " + getResources().getString(R.string.hasBeenDeleted),
                        Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == UPDATE_COURSE_CODE && resultCode == RESULT_OK) { // show msg to indicate updating.
            createScheduleView();
            String courseName = data.getStringExtra("courseName");
            Toast.makeText(activityMain,
                    getResources().getString(R.string.theCourse) + " " +
                            courseName + " " + getResources().getString(R.string.hasBeenedEdited),
                    Toast.LENGTH_LONG).show();
        }
        MainActivity.coursesStr = model.getCoursesStr(); // update the courses' list whenever there's a change.
    }

    /*
    * It creates the view of the schedule
    */
    private void createScheduleView() {
        scheduleLayout.removeAllViews(); // clear the view from previous children first.
        String[] days = {
                getResources().getString(R.string.sunday),
                getResources().getString(R.string.monday),
                getResources().getString(R.string.tuesday),
                getResources().getString(R.string.wednesday),
                getResources().getString(R.string.thursday),
                getResources().getString(R.string.friday)
        };
        GridLayout.LayoutParams dayTVParam;
        TextView courseTV, dayTV;
        List<CourseEntity> coursesDay;
        boolean wasGrey = false; // It's used to make half of the text views grey
        for (int i = 0; i < 6; i++) {
            coursesDay = model.getCoursesDay(i);
            wasGrey = !wasGrey;
            if (!coursesDay.isEmpty()) {
                // adding days text view
                dayTVParam = new GridLayout.LayoutParams();
                dayTVParam.columnSpec = GridLayout.spec(0,1);
                dayTVParam.rowSpec = GridLayout.spec(i,1);
                dayTVParam.setGravity(Gravity.FILL);
                dayTVParam.setMargins(5, 5, 5, 5);
                dayTV = new TextView(this.getContext());
                dayTV.setLayoutParams(dayTVParam);
                dayTV.setText(days[i]);
                dayTV.setTextSize(20);
                dayTV.setBackgroundColor(getResources().getColor(R.color.peach));
                scheduleLayout.addView(dayTV);
            }
            for (int j = 0; j < coursesDay.size(); j++) { // adding text views for the courses
                courseTV = new TextView(this.getContext());
                dayTVParam = new GridLayout.LayoutParams();
                dayTVParam.columnSpec = GridLayout.spec(1 + j,1);
                dayTVParam.rowSpec = GridLayout.spec(i,1);
                dayTVParam.setGravity(Gravity.FILL);
                dayTVParam.setMargins(5, 5, 5, 5);
                courseTV.setLayoutParams(dayTVParam);

                String shownCourseText = getShownText(coursesDay.get(j), i);
                courseTV.setText(shownCourseText);
                if (!wasGrey) {
                    courseTV.setBackgroundColor(getResources().getColor(R.color.grey));
                    wasGrey = true;
                } else {
                    wasGrey = false;
                }
                scheduleLayout.addView(courseTV);
            }
        }

    }


    /**
     * It's a helper method for createScheduleView(). It creates the text that will be
     * shown for a course in the schedule.
     * @param course is the object that holds all the data related to the shown course.
     * @param  dayIndex is int 0-6 that indicates the days where sunday is 0.
     * @return String's object that has the desired text.
     */
    private String getShownText(CourseEntity course, int dayIndex) {
        String shownText = course.getName() + "\n";
        shownText += course.getLocation() + "\n";
        shownText += StringMaker.getViewedTime(course.getFromHourNum()[dayIndex],
                course.getFromMinuteNum()[dayIndex], course.getToHourNum()[dayIndex],
                course.getToMinuteNum()[dayIndex], activityMain);
        return shownText;
    }

    @Override
    public void update(Observable o, Object arg) {
        activityMain.runOnUiThread(() ->createScheduleView());
        MainActivity.coursesStr = model.getCoursesStr();
    }
}
