/*
###############################################################################
Author: Mohammed Alghamdi
Class name : CoursesActivity
purpose: This is model view class that is responsible for courses activity
  interaction with the user.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
  onActivityResult() -> It receives the intent from FlashCardsActivity or NotesActivity that
     holds the data of the added or modified flash cards or notes..
  createCoursesView() -> It creates the view that show the courses and the lessons..
  getSentCards() -> this method return sentCards, so that it can send FlashCardEntity array to
     FlashCardsActivity.
  updateSentCards() -> this method change the value of sentCards in case the user edit them after
     it is sent to FlashCardsActivity
###############################################################################
 */


package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
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

public class CoursesActivity extends Activity {

    // used to access database; static to access editing it from FlashCardsActivity
    private static DataRepository repository;
    private final int ADDING_CODE_NOTE = 55; // used as requestCode for startActivityForResult()
    private final int ENTER_CODE_NOTE = 57; // used as requestCode for startActivityForResult()
    private final int ADDING_CODE_CARD = 66; // used as requestCode for startActivityForResult()
    private String selectedCourse; // used to recognize the selected course.
    private String distination; // it is used to distinquish whether the activity will use notes or cards
    private NoteList retreivedNotes; // the retrieved notes stored in the phone
    // the retrieved cards stored in the phone,; static to access editing it from FlashCardsActivity
    private static FlashCardsList retreivedCards;
    private static FlashCardEntity[] sentCards; // to send the selected lesson's cards to FlashCardActivity

    Button removeBtn;
    Button addBtn; // add new course  or lesson
    Button homeBtn;
    Button showBtn;
    Button []clickedCourse; // it is the last clicked course, so that it would be easier to access it.
    LinearLayout courseBtnLayout; // it is a layout that contain all courses buttons
    LinearLayout lessonSpinnerLayout; // it includes the lesson spinner.
    Spinner lessonSpinner;
    ArrayAdapter<String> arrSpinnerAdaptor; // it stores the strings shown in the lesson spinner.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses);
        distination = this.getIntent().getStringExtra("finalDistanation");

        // Accessing database to retrieve the data
        repository = DataRepository.getInstance(
                AppDatabase.getInstance(getBaseContext()));
        Executor dbThread = Executors.newSingleThreadExecutor();
        dbThread.execute(() -> {
            if (distination.equals("notes")) {
                repository.getNotes(getBaseContext());
            } else {
                repository.getCards(getBaseContext());
            }
        });

        // initializing views
        removeBtn = findViewById(R.id.remove_courses);
        addBtn = findViewById(R.id.add_courses);
        homeBtn = findViewById(R.id.home_courses);
        showBtn = findViewById(R.id.enter_lesson);
        showBtn.setVisibility(View.INVISIBLE);
        lessonSpinner = findViewById(R.id.lesson_spinner);
        courseBtnLayout = findViewById(R.id.coursesBtnLayout);
        lessonSpinnerLayout = findViewById(R.id.lesson_spinner_layout);

        // Distributing notes/cards' views on the main thread when
        // setRetreivedDataListener() is called after remarks are retrieved from database.
        repository.setRetreivedDataListener((recieveddata) ->{
            if (distination.equals("notes")) {
                retreivedNotes = (NoteList) recieveddata;
            } else {// for cards class
                retreivedCards = (FlashCardsList) recieveddata;
            }
            runOnUiThread(() -> createCoursesView());
        });


        // click button event handling for enterBtn
        showBtn.setOnClickListener((btn) -> {
            // changing the lesson button to grey, so that user knows it is not clicked
            clickedCourse[0].setBackground(null);
            clickedCourse[0].setBackgroundColor(getResources().getColor(R.color.grey));

            //preparing for showing the lesson on either FlashCardsActivity or NotesActivity
            if (distination.equals("notes")) {
                Intent enterIntent = new Intent(CoursesActivity.this,
                        NotesActivity.class);
                Gson lessonGson = new Gson();
                String selectedLesson = lessonSpinner.getSelectedItem().toString();
                String lessonJson = lessonGson.toJson(retreivedNotes.getNote(selectedCourse,
                        selectedLesson));
                enterIntent.putExtra("lessonNote", lessonJson);
                startActivityForResult(enterIntent, ENTER_CODE_NOTE);
            } else {
                Intent enterIntent = new Intent(CoursesActivity.this,
                        FlashCardsActivity.class);
                String selectedLesson = lessonSpinner.getSelectedItem().toString();
                sentCards = retreivedCards.getLessonCards(selectedCourse, selectedLesson);
                startActivity(enterIntent);
                finish();
            }
        });


        // click button event handling for homeBtn
        homeBtn.setOnClickListener((btn) -> {
            Intent homeIntent = new Intent(CoursesActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        });


        // click button event handling for addBtn. It will go to either AddNote or AddCard
        addBtn.setOnClickListener((btn) -> {
            // changing the lesson button to grey, so that user knows it is not clicked
            if ( clickedCourse[0] != null) {
                clickedCourse[0].setBackground(null);
                clickedCourse[0].setBackgroundColor(getResources().getColor(R.color.grey));
            }

            // showing lesson drop list and enter button.
            lessonSpinnerLayout.setVisibility(View.INVISIBLE);
            showBtn.setVisibility(View.INVISIBLE);

            if (distination.equals("notes")) { // for notes
                Intent addIntent = new Intent(CoursesActivity.this,
                        AddNoteActivity.class);
                startActivityForResult(addIntent, ADDING_CODE_NOTE);
            } else { // for flashcards
                Intent addIntent = new Intent(
                        CoursesActivity.this,
                        AddFlashCardActivity.class);
                addIntent.putExtra("purpose", "adding");
                startActivityForResult(addIntent, ADDING_CODE_CARD);
            }

        });


        // click button event handling for removeBtn, which is removing a lesson
        removeBtn.setOnClickListener((btn) -> {
            if (lessonSpinner.getSelectedItem() != null ) { // if a lesson is selected
                // removing from the view
                String selectedLesson = lessonSpinner.getSelectedItem().toString();
                arrSpinnerAdaptor.remove(selectedLesson);
                Toast.makeText(getBaseContext(), "The lesson been removed",
                        Toast.LENGTH_LONG).show();
                if (arrSpinnerAdaptor.getCount() == 0) {// if the course does not have any lesson left
                    courseBtnLayout.removeView(clickedCourse[0]);
                    lessonSpinnerLayout.setVisibility(View.INVISIBLE);
                    showBtn.setVisibility(View.INVISIBLE);
                }

                // removing the data from the model
                if (distination.equals("notes")) {
                    NoteEntity removed = retreivedNotes.getNote(selectedCourse, selectedLesson);
                    retreivedNotes.removeNote(selectedCourse, selectedLesson);
                    dbThread.execute(() -> repository.deleteNote(removed));

                } else {
                    dbThread.execute(() -> {
                        repository.deleteCards(retreivedCards.getLessonCards(selectedCourse,
                                selectedLesson));
                        retreivedCards.removeLesson(selectedCourse, selectedLesson);
                    });
                }

            } else { // if a lesson is not selected
                Toast.makeText(getBaseContext(), "Please select a lesson",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    // this method creates the buttons for selecting a course and controlling the lesson's spinner.
    private void createCoursesView () {
        Button courseBtn;
        courseBtnLayout.removeAllViews();
        clickedCourse = new Button[1];
        LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        boolean hasLesson = false;

        // Initializing courses set depending on this activity is opened for notes or flash cards
        Set<String> coursesSet;
        if (distination.equals("notes")) {
            coursesSet = retreivedNotes.getCoursesSet();
        } else {
            coursesSet = retreivedCards.getCoursesSet();
        }

        for (String course : coursesSet) {
            hasLesson = true;

            // creating the button for each course
            courseBtn = new Button(getBaseContext());
            courseBtn.setLayoutParams(layoutParamsBtn);
            courseBtn.setText(course);

            // click event handling
            courseBtn.setOnClickListener((btn) -> {
                btn.setClickable(false);
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
                lessonSpinnerLayout.setVisibility(View.VISIBLE);
                List<String> lessonsList;
                if (distination.equals("notes")) {
                    lessonsList = retreivedNotes.getLessonsCourse(course);
                } else {
                    lessonsList = retreivedCards.getCourseLessons(course);
                }
                List<String> spinnerList = new ArrayList<String>();
                for (String lesson : lessonsList) {
                    spinnerList.add(lesson);
                }
                arrSpinnerAdaptor = new ArrayAdapter<>(this,
                        android.R.layout.select_dialog_item, spinnerList);
                lessonSpinner.setAdapter(arrSpinnerAdaptor);

                showBtn.setVisibility(View.VISIBLE);
                btn.setClickable(true);
            });

            // adding the button to the layout
            if (hasLesson) {
                courseBtnLayout.addView(courseBtn);
            }
        }
    }


    // this method receives the intent from AddNoteActivity, AddCardActivity, NotesActivity
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
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
            createCoursesView();
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
}
