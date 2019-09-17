/*
###############################################################################
Class name : NotesActivity
purpose: This is model view class that is responsible for notes activity
  interaction with the user.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;

public class NotesActivity extends Activity {

    private TextView courseTV;
    private TextView lessonTV;
    private EditText noteET;
    // once it's clicked, the activity will be closed with saving the changes on the note.
    private Button closeSaveBtn;
    private Button closeNoSaveBtn;
    private NoteEntity lessonNoteObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        // initializing views
        courseTV = findViewById(R.id.course_note);
        lessonTV = findViewById(R.id.lesson_note);
        noteET = findViewById(R.id.note_te_note);
        closeSaveBtn = findViewById(R.id.close_save_note);
        closeNoSaveBtn = findViewById(R.id.close_notsave_note);


        // filling text views and edit text from the sent intent after initializing NoteEntity's obj
        Intent received = this.getIntent();
        Gson gson = new Gson();
        lessonNoteObj = gson.fromJson(received.getExtras().getString("lessonNote"), NoteEntity.class);
        courseTV.setText(lessonNoteObj.getCourse());
        lessonTV.setText(lessonNoteObj.getLesson());
        noteET.setText(lessonNoteObj.getNotes());


        // click event handling for closeNoSaveBtn, which is just to close the activity without
        // the changes on noteET
        closeNoSaveBtn.setOnClickListener((btn) -> {
            Intent sent = new Intent(NotesActivity.this, CoursesActivity.class);
            setResult(Activity.RESULT_CANCELED, sent); // to prevent saving the editing
            finish();
        });


        // click event handling for closeSaveBtn, which is closing the page with saving what has
        // been changed on noteET
        closeSaveBtn.setOnClickListener((btn) -> {
            Intent sent = new Intent(NotesActivity.this, CoursesActivity.class);
            setResult(Activity.RESULT_OK, sent); // to enable saving the editing
            lessonNoteObj.setNotes(noteET.getText().toString());
            String updatedLessonJson = gson.toJson(lessonNoteObj);
            sent.putExtra("updatedNote", updatedLessonJson);
            finish();
        });

    }
}
