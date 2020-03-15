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
  getSentCards() -> this method return sentCards, so that it can send FlashCardEntity array to
     FlashCardsActivity.
  updateSentCards() -> this method change the value of sentCards in case the user edit them after
     it is sent to FlashCardsActivity
  getExistingCourses() -> this method returns the set of existing courses.
  handleHoldBtn(selectedLesson, selectedCourse, selectedBtn) -> this method is used as button's hold
    click for a lesson's button. It shows a popup drop list for either deleting a lesson or editing the lesson.
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

import static android.app.Activity.RESULT_OK;

public class CoursesActivity extends Fragment {

    // used to access database; static to access editing it from FlashCardsActivity
    private static DataRepository repository;
    private final int ADDING_CODE_NOTE = 55; // used as requestCode for startActivityForResult()
    private final int ENTER_CODE_NOTE = 57; // used as requestCode for startActivityForResult()
    private final int ADD_COURSSE_CODE = 59; // used as requestCode for startActivityForResult()
    private final int ADDING_CODE_CARD = 66; // used as requestCode for startActivityForResult()
    private final int EDIT_CARDS_CODE = 60; // used as requestCode for startActivityForResult()
    private String selectedCourse; // used to recognize the selected course.
    private String distination; // it is used to distinquish whether the activity will use notes or cards
    private NoteList retreivedNotes; // the retrieved notes stored in the phone
    // the retrieved cards stored in the phone,; static to access editing it from FlashCardsActivity
    private static FlashCardsList retreivedCards;
    private static FlashCardEntity[] sentCards; // to send the selected lesson's cards to FlashCardActivity
    private static Set<String> coursesSet; // names of the courses
    private Activity activityMain;
    public static int count = 0;

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
        coursesSet = new HashSet<>();
        clickedCourse = new Button[1];

        // Accessing database to retrieve the data then show on the view
        repository = DataRepository.getInstance(
                AppDatabase.getInstance(activityMain.getBaseContext()));
        Executor dbThread = Executors.newSingleThreadExecutor();
        dbThread.execute(() -> {
            // Initializing courses set depending on this activity is opened for notes or flash cards
            coursesSet = repository.getCoursesStr();
            System.out.println("the count in exe is" + (++count));
            if (distination.equals("notes activity")) {
                retreivedNotes = repository.getNotes(activityMain.getBaseContext());;
                activityMain.runOnUiThread(() -> createCoursesView());
            } else {
                retreivedCards = repository.getCards(activityMain.getBaseContext());
                activityMain.runOnUiThread(() -> createCoursesView());
            }
        });

        // initializing views
        View root = inflater.inflate(R.layout.courses, container, false);
        addCourseBtn = root.findViewById(R.id.add_course_courses);
        courseBtnLayout = root.findViewById(R.id.coursesBtnLayout);
        addLessonBtn = root.findViewById(R.id.add_lesson_courses);
        lessonsBtnLayout = root.findViewById(R.id.lessonsBtnLayout_courses);


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
                Toast.makeText(activityMain.getBaseContext(), "Please select a course first in order" +
                        " to add a lesson", Toast.LENGTH_LONG).show();
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
                addIntent.putExtra("courseStr", selectedCourse);
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

                //controlling the content of the spinner
                lessonsBtnLayout.setVisibility(View.VISIBLE);
                List<String> lessonsList;
                if (distination.equals("notes activity")) {
                    lessonsList = retreivedNotes.getLessonsCourse(course);
                } else {
                    lessonsList = retreivedCards.getCourseLessons(course);
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
            NoteEntity added = gson.fromJson(data.getExtras().getString("addedObjNote")
                    , NoteEntity.class);
            retreivedNotes.addNote(added);
            Executor dbThread = Executors.newSingleThreadExecutor();
            dbThread.execute(() -> repository.addNote(added));
            createCoursesView();
        } else if (requestCode == ENTER_CODE_NOTE && resultCode == RESULT_OK) { // for viewing notes
            String updatedNoteJson = data.getExtras().getString("updatedNote");
            NoteEntity updatedNote = gson.fromJson(updatedNoteJson, NoteEntity.class);
            // updating on the retreivedNotes
            retreivedNotes.updateNote(updatedNote);
            // updating on the database
            Executor dbThread = Executors.newSingleThreadExecutor();
            dbThread.execute(() -> repository.updateNote(updatedNote));
        } else if (requestCode == ADDING_CODE_CARD && resultCode == RESULT_OK) { // for adding cards
            retreivedCards.addLesson(AddFlashCardActivity.getCreatedCards());
            Executor dbThread = Executors.newSingleThreadExecutor();
            dbThread.execute(() -> {
                for (FlashCardEntity card : AddFlashCardActivity.getCreatedCards()) {
                    repository.addCards(card);
                }
            });
            retreivedCards.addLesson(AddFlashCardActivity.getCreatedCards());
            createCoursesView();
        } else if (requestCode == ADD_COURSSE_CODE && resultCode == RESULT_OK) {
            Executor addDBThread = Executors.newSingleThreadExecutor();
            System.out.println(data + "testing ");
            addDBThread.execute(() -> repository.addCourse(data.getStringExtra("added course")));
            coursesSet.add(data.getStringExtra("added course"));
            createCoursesView();
        } else if (requestCode == EDIT_CARDS_CODE && resultCode == RESULT_OK) {
            // editing in database
            FlashCardEntity[] editedCards = AddFlashCardActivity.getCreatedCards(null);
            CoursesActivity.updateSentCards(editedCards, AddFlashCardActivity.getDeletedCards());
            // in retreivedCards
            String course = editedCards[0].getCourse();
            String lesson = editedCards[0].getLesson();
            retreivedCards.removeLesson(course, lesson);
            ArrayList<FlashCardEntity> cardsList = new ArrayList<>();
            for (FlashCardEntity card : editedCards) {
                cardsList.add(card);
            }
            retreivedCards.addLesson(cardsList);
        }
    }

    // this method return sentCards, so that it can send FlashCardEntity array to FlashCardsActivity
    public static FlashCardEntity[] getSentCards() {
        return sentCards;
    }

    // this method change the value of sentCards in case the user edit them after it is sent to
    // FlashCardsActivity
    public static void updateSentCards(FlashCardEntity[] updatedCards,
                                       FlashCardEntity[] deletedCards) {
        Executor dbThread = Executors.newSingleThreadExecutor();
        dbThread.execute(() -> {
            repository.addCards(updatedCards);
            repository.deleteCards(deletedCards);
        });
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
            Gson lessonGson = new Gson();
            String lessonJson = lessonGson.toJson(retreivedNotes.getNote(selectedCourse,
                    lessonStr));
            enterIntent.putExtra("lessonNote", lessonJson);
            startActivityForResult(enterIntent, ENTER_CODE_NOTE);
        } else {
            Intent enterIntent = new Intent(activityMain, FlashCardsActivity.class);
            sentCards = retreivedCards.getLessonCards(selectedCourse, lessonStr);
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
        menu.getMenu().add("Delete the lesson");
        menu.getMenu().add("Edit the lesson");
        menu.show();

        // handling deleting the lesson
        menu.getMenu().getItem(0).setOnMenuItemClickListener((view) -> {
            // removing from the view
            Toast.makeText(activityMain.getBaseContext(), "The lesson been removed",
                    Toast.LENGTH_LONG).show();
            lessonsBtnLayout.removeView(selectedBtn);

            // removing the data from the model
            Executor dbThread = Executors.newSingleThreadExecutor();
            if (distination.equals("notes activity")) {
                dbThread.execute(() -> {
                    NoteEntity removed = retreivedNotes.getNote(courseStr, lessonStr);
                    retreivedNotes.removeNote(courseStr, lessonStr);
                    repository.deleteNote(removed);
                });
            } else {
                dbThread.execute(() -> {
                    repository.deleteCards(retreivedCards.getLessonCards(courseStr, lessonStr));
                    retreivedCards.removeLesson(courseStr, lessonStr);
                });
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
                sentCards = retreivedCards.getLessonCards(selectedCourse, lessonStr);
                startActivityForResult(editIntent, EDIT_CARDS_CODE);
            }
            return true;
        });
        return true;
    }

}
