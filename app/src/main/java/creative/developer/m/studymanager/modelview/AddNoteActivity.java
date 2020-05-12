/*
###############################################################################
Class name : AddNotesActivity
purpose: This is model view class that is responsible for adding a flash card or editing
   an existing one
Methods:
  onCreate() -> It encapsulates/manages most the interaction.
  isInputValid() -> It returns boolean value about whether the user have given valid inputs or not.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import creative.developer.m.studymanager.model.EntityListFiles.NoteList;
import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.modelCoordinators.NoteCoordinator;

public class AddNoteActivity extends Activity {

    private List<String> existedNotes; // names of the existed lessons on the selected course.
    private Button addNoteBtn;
    private Button cancelBtn;
    private TextView courseET;
    private EditText lessonET;
    private EditText recoredNoteET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        String courseName = this.getIntent().getStringExtra("courseStr");

        // initializing views
        addNoteBtn = findViewById(R.id.add_addnote);
        cancelBtn = findViewById(R.id.cancel_addnote);
        courseET = findViewById(R.id.course_add_note);
        courseET.setText(courseName);
        lessonET = findViewById(R.id.lesson_add_note);
        recoredNoteET = findViewById(R.id.recorded_note_te);

        // initializing existedNotes to repesent the stored notes, so that the user do not input
        // two identical lessons' name for same course
        NoteCoordinator model = NoteCoordinator.getInstance(this);
        existedNotes = model.getLessonsList(courseName);

        // click eventing handling for cancelBtn, which is cancelling adding the note
        cancelBtn.setOnClickListener((btn) -> {
            Intent intent = new Intent(AddNoteActivity.this,
                    CoursesActivity.class);
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        });


        // click event handling for addNote, which is adding the note if it is valid
        addNoteBtn.setOnClickListener((btn) -> {
            if (isInputValid()) {
                System.out.println("in Adding");
                Intent intent = new Intent(AddNoteActivity.this,
                        CoursesActivity.class);
                model.addNote(courseET.getText().toString().toUpperCase().trim(),
                        lessonET.getText().toString().toUpperCase().trim(),
                        recoredNoteET.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    // this method returns true if the user has filled the fields, and the lesson field does not
    // include a written question previously. It also shows messages for the user miss inputs
    private boolean isInputValid() {
        String course = courseET.getText().toString().toUpperCase();
        String lesson = lessonET.getText().toString().toUpperCase();
        String note = recoredNoteET.getText().toString();
        System.out.println("in valid 1");
        if (!lesson.equals("") && !note.equals("")) {
            System.out.println("in valid 2");
            if (existedNotes.contains(lesson)) {
                Toast.makeText(this, "This lesson has been already added, please select" +
                        "another name for the lesson", Toast.LENGTH_LONG).show();
            } else {
                return true;
            }
        } else {
            Toast.makeText(this, "please fill all the blanks", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}
