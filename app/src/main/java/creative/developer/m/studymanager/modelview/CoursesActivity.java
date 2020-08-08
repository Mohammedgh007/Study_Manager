/*
###############################################################################
Author: Mohammed Alghamdi
Class name : CoursesActivity
purpose: This is model view class that is responsible for courses fragment
  interaction with the user.
Methods:
  onCreateView() -> It encapsulates/manages all the interaction.
  onActivityResult() -> It receives the intent from FlashCardsActivity or NotesActivity that
     holds the data of the added or modified flash cards or notes..
  createCoursesView() -> It creates the view that show the courses and the lessons..
  getExistingCourses() -> this method returns the set of existing courses.
  handleHoldBtn(selectedLesson, selectedCourse, selectedBtn) -> this method is used as button's hold
    click for a lesson's button. It shows a popup drop list for either deleting a lesson or editing the lesson.
  handleClickingLesson() -> This method handle the event of clicking a lesson's button.
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.model.EntityListFiles.FlashCardsList;
import creative.developer.m.studymanager.model.EntityListFiles.NoteList;
import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.modelCoordinators.FlashCardCoordinator;
import creative.developer.m.studymanager.model.modelCoordinators.NoteCoordinator;

import static android.app.Activity.RESULT_OK;

public class CoursesActivity extends Fragment implements Observer {

    private NoteCoordinator noteModel;
    private FlashCardCoordinator cardModel;
    private final int ADDING_CODE_NOTE = 55; // used as requestCode for startActivityForResult()
    private final int ENTER_CODE_NOTE = 57; // used as requestCode for startActivityForResult()
    private final int ADD_COURSSE_CODE = 59; // used as requestCode for startActivityForResult()
    private final int ADDING_CODE_CARD = 66; // used as requestCode for startActivityForResult()
    private final int EDIT_CARDS_CODE = 60; // used as requestCode for startActivityForResult()
    private String selectedCourse; // used to recognize the selected course.
    private String distination; // it is used to distinquish whether the activity will use notes or cards
    private Activity activityMain;
    public static int count = 0;
    private static Set<String> coursesSet;

    // views
    Button addLessonBtn;
    Button addCourseBtn;
    Button []clickedCourse; // it is the last clicked course, so that it would be easier to access it.
    LinearLayout courseBtnLayout; // it is a layout that contain all courses buttons
    LinearLayout lessonsBtnLayout; // it is a layout that contain all courses buttons

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
        distination = MainActivity.getCoursesDistination();
        clickedCourse = new Button[1];


        // initializing views
        View root = inflater.inflate(R.layout.courses, container, false);
        addCourseBtn = root.findViewById(R.id.add_course_courses);
        courseBtnLayout = root.findViewById(R.id.coursesBtnLayout);
        addLessonBtn = root.findViewById(R.id.add_lesson_courses);
        lessonsBtnLayout = root.findViewById(R.id.lessonsBtnLayout_courses);

        // Accessing model to retrieve the data then show on the view
        if (distination.equals("notes activity")) {
            noteModel = NoteCoordinator.getInstance(activityMain.getBaseContext());
            noteModel.addObserver(this);
            cardModel = null;
            if (noteModel.getCoursesNames() != null) {
                // to avoid race condition; it will be called on update() otherwise.
                createCoursesView();
            }
        } else {
            cardModel = FlashCardCoordinator.getInstance(activityMain.getBaseContext());
            cardModel.addObserver(this);
            noteModel = null;
            if (cardModel.getCoursesNames() != null) {
                // to avoid race condition; it will be called on update() otherwise.
                createCoursesView();
            }
        }


        // handling the event of adding a course
        addCourseBtn.setOnClickListener((view -> {
            Intent popupIntent = new Intent(activityMain, AddCourseActivity.class);
            startActivityForResult(popupIntent, ADD_COURSSE_CODE);
        }));

        // click button event handling for addLessonBtn. It will go to either AddNote or AddCard
        addLessonBtn.setOnClickListener((btn) -> {
            // make sure that there is a selected course
            if (clickedCourse[0] == null || clickedCourse[0].getBackground().getConstantState()
                    != getResources().getDrawable(R.color.peach).getConstantState()) {
                Toast.makeText(activityMain.getBaseContext(), getResources().getString(R.string.selectCourseBefore)
                        , Toast.LENGTH_LONG).show();
                return;
            }

            // changing the lesson button to grey, so that user knows it is not clicked
            if ( clickedCourse[0] != null) {
                clickedCourse[0].setBackground(null);
                clickedCourse[0].setBackgroundColor(getResources().getColor(R.color.grey));
            }

            if (distination.equals("notes activity")) { // for notes
                Intent addIntent = new Intent(activityMain, AddNoteActivity.class);
                addIntent.putExtra("courseStr", selectedCourse);
                startActivityForResult(addIntent, ADDING_CODE_NOTE);
            } else { // for flashcards
                Intent addIntent = new Intent(activityMain, AddFlashCardActivity.class);
                addIntent.putExtra("purpose", "adding");
                addIntent.putExtra("course", selectedCourse);
                startActivityForResult(addIntent, ADDING_CODE_CARD);
            }

        });

        return root;
    }


    // this method creates the buttons for selecting a course and a lesson
    private void createCoursesView () {
        Button courseBtn;
        clickedCourse = new Button[1];
        LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // cleanup the courses' set
        View addCourseBtnTemp = courseBtnLayout.getChildAt(0);
        courseBtnLayout.removeAllViews();
        courseBtnLayout.addView(addCourseBtnTemp);

        // clear previous view's appearance for the lessons
        TextView tempTV = (TextView) lessonsBtnLayout.getChildAt(0);
        Button tempAddCourse = (Button) lessonsBtnLayout.getChildAt(1);
        lessonsBtnLayout.removeAllViews();
        System.out.println("the count is " + lessonsBtnLayout.getChildCount());
        lessonsBtnLayout.addView(tempTV);
        lessonsBtnLayout.addView(tempAddCourse);


        for (String course : coursesSet) {
            // creating the button for each course
            courseBtn = new Button(activityMain.getBaseContext());
            courseBtn.setLayoutParams(layoutParamsBtn);
            courseBtn.setText(course);

            // click event handling
            courseBtn.setOnClickListener((btn) -> {
                btn.setClickable(false);

                // clear previous view's appearance for the lessons
                lessonsBtnLayout.removeAllViews();
                System.out.println("the count is " + lessonsBtnLayout.getChildCount());
                lessonsBtnLayout.addView(tempTV);
                lessonsBtnLayout.addView(tempAddCourse);

                // adjusting the background of this button to peach color with changing the color to
                // grey for the last clicked button
                if (clickedCourse[0] != null) {
                    clickedCourse[0].setBackground(null);
                    clickedCourse[0].setBackgroundColor(getResources().getColor(R.color.grey));
                }
                btn.setBackgroundColor(getResources().getColor(R.color.peach));
                clickedCourse[0] = (Button) btn;
                selectedCourse = course;

                //controlling the content of the lessons' list
                lessonsBtnLayout.setVisibility(View.VISIBLE);
                List<String> lessonsList;
                if (distination.equals("notes activity")) {
                    lessonsList = noteModel.getLessonsList(course);
                } else {
                    lessonsList = cardModel.getLessonsList(course);
                }

                // adding buttons for each lesson that belongs to the selected course
                LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                btn.setBackgroundResource(R.color.peach);
                for (String lesson : lessonsList) {
                    Button addedLesson = new Button(activityMain);
                    addedLesson.setLayoutParams(btnParam);
                    addedLesson.setText(lesson);
                    addedLesson.setOnClickListener((view) -> handleClickingLesson(lesson, course));
                    addedLesson.setOnLongClickListener((view) -> handleHoldBtn(course, lesson, addedLesson));
                    lessonsBtnLayout.addView(addedLesson);
                }

                btn.setClickable(true);
            });

            // adding the button to the layout
            courseBtnLayout.addView(courseBtn);
        }
    }


    // this method receives the intent from AddNoteActivity, AddCardActivity, NotesActivity
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        Gson gson = new Gson();
        if (requestCode == ADDING_CODE_NOTE && resultCode == RESULT_OK) { // for adding notes
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.noteAdded), Toast.LENGTH_LONG).show();
            createCoursesView();
        } else if (requestCode == ENTER_CODE_NOTE && resultCode == RESULT_OK) { // for viewing notes
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.noteUpdated), Toast.LENGTH_LONG).show();
        } else if (requestCode == ADDING_CODE_CARD && resultCode == RESULT_OK) { // for adding cards
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.cardsAdded), Toast.LENGTH_LONG).show();
            createCoursesView();
        } else if (requestCode == ADD_COURSSE_CODE && resultCode == RESULT_OK) {
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.courseAdded), Toast.LENGTH_LONG).show();
            String addedCourse = data.getExtras().getString("added course");
            coursesSet.add(addedCourse);
            if (distination.equals("notes activity")) {
                noteModel.addCourse(addedCourse);
            } else {
                cardModel.addCourse(addedCourse);
            }
            createCoursesView();
        } else if (requestCode == EDIT_CARDS_CODE && resultCode == RESULT_OK) {
            Toast.makeText(activityMain.getBaseContext(),
                    getResources().getString(R.string.cardEdited), Toast.LENGTH_LONG).show();
        }
    }


    /*
    this method handles the event of clicking a lesson button.
    @param: lessonStr is the string that identifies the name of the lesson.
    @param: courseStr is the string that identifies the name of the course.
     */
    private void handleClickingLesson(String lessonStr, String courseStr) {
        //preparing for showing the lesson on either FlashCardsActivity or NotesActivity
        if (distination.equals("notes activity")) {
            Intent enterIntent = new Intent(activityMain, NotesActivity.class);
            enterIntent.putExtra("lesson", lessonStr);
            enterIntent.putExtra("course", courseStr);
            startActivityForResult(enterIntent, ENTER_CODE_NOTE);
        } else {
            Intent enterIntent = new Intent(activityMain, FlashCardsActivity.class);
            enterIntent.putExtra("lesson", lessonStr);
            enterIntent.putExtra("course", courseStr);
            startActivity(enterIntent);
            activityMain.finish();
        }

    }

    /*
    this method returns a set of existing course, so that it is easier to send the list to
    another activity
     */
    protected static Set<String> getExistingCourses() {
        return coursesSet;
    }


    /*
    this method is used as button's hold click for a lesson's button. It shows a popup drop list for
    either deleting a lesson or editing the lesson.
    @param: courseStr is the name of the course
    @param: lessonStr is the name of the lesson
    @param: selectedBtn is the lesson's button
     */
    private boolean handleHoldBtn(String courseStr, String lessonStr, Button selectedBtn) {
        // showing popup list
        PopupMenu menu = new PopupMenu(activityMain.getBaseContext(), selectedBtn);
        menu.getMenu().add(R.string.deleteLesson);
        menu.getMenu().add(R.string.editLesson);
        menu.show();

        // handling deleting the lesson
        menu.getMenu().getItem(0).setOnMenuItemClickListener((view) -> {
            // removing from the view
            Toast.makeText(activityMain.getBaseContext(), getResources().getString(R.string.lessonRemoved),
                    Toast.LENGTH_LONG).show();
            lessonsBtnLayout.removeView(selectedBtn);

            // removing the data from the model
            Executor dbThread = Executors.newSingleThreadExecutor();
            if (distination.equals("notes activity")) {
                noteModel.removeNote(courseStr, lessonStr);
            } else {
                cardModel.removeLesson(courseStr, lessonStr);
            }

            return true;
        });

        // handling editing the lesson
        menu.getMenu().getItem(1).setOnMenuItemClickListener((view) -> {
            if (distination.equals("notes activity")) {
                handleClickingLesson(lessonStr, courseStr); // same as a regular click
            } else {
                Intent editIntent = new Intent(activityMain, AddFlashCardActivity.class);
                editIntent.putExtra("purpose", "editing");
                editIntent.putExtra("course", courseStr);
                editIntent.putExtra("lesson", lessonStr);
                startActivityForResult(editIntent, EDIT_CARDS_CODE);
            }
            return true;
        });
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FlashCardCoordinator) {
            coursesSet = ((FlashCardCoordinator) o).getCoursesNames();
        } else {
            coursesSet = ((NoteCoordinator) o).getCoursesNames();
        }
        System.out.println("course is" + coursesSet);
        activityMain.runOnUiThread(() -> createCoursesView());
    }
}
