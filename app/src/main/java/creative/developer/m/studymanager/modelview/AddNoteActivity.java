/*
###############################################################################
Class name : AddNotesActivity
purpose: This is model view class that is responsible for adding a flash card or editing
   an existing one
Methods:
  onCreate() -> It encapsulates/manages most the interaction.
  isInputValid() -> It returns boolean value about whether the user have given valid inputs or not.
  showPhotos() -> It shows the photos in the gallery.
  handleLongClick() -> It handles the event of holding the photo.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import creative.developer.m.studymanager.model.EntityListFiles.NoteList;
import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.modelCoordinators.NoteCoordinator;

public class AddNoteActivity extends Activity {

    private static final int ADDING_PHOTO_CODE = 43;
    private static final int PERMISSION_CODE = 33;
    private List<String> existedNotes; // names of the existed lessons on the selected course.
    private List<String> photosUrl;
    private LinearLayout imagesLayout;
    private Button addNoteBtn;
    private Button cancelBtn;
    private Button addPhotoBtn;
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
        addPhotoBtn = findViewById(R.id.add_photo_add_note);
        imagesLayout = findViewById(R.id.images_layout_addNote);
        cancelBtn = findViewById(R.id.cancel_addnote);
        courseET = findViewById(R.id.course_add_note);
        courseET.setText(courseName);
        lessonET = findViewById(R.id.lesson_add_note);
        recoredNoteET = findViewById(R.id.recorded_note_te);

        photosUrl = new ArrayList<>();

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
                        recoredNoteET.getText().toString(), photosUrl);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


        // click event handling for picking a photo from user's gallery to attach that photo to this note
        addPhotoBtn.setOnClickListener((btn) -> {
            // check if the permission is granted
            if(ActivityCompat.checkSelfPermission(AddNoteActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, ADDING_PHOTO_CODE);
            } else { // ask permission
                ActivityCompat.requestPermissions(AddNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
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

    // this method recieve the images from gallery for addPhotoBtn's intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==ADDING_PHOTO_CODE) {
            String addedUrl = "";
            ImageView photoBtn;
            Space gap;
            LinearLayout.LayoutParams attrImg = new LinearLayout.LayoutParams(
                    (int)getResources().getDimension(R.dimen.photo_note_width_small),
                    LinearLayout.LayoutParams.MATCH_PARENT);
            attrImg.setMargins(3, 0, 0, 0);
            LinearLayout.LayoutParams attrSpace = new LinearLayout.LayoutParams(
                    (int)getResources().getDimension(R.dimen.photos_space),
                    LinearLayout.LayoutParams.MATCH_PARENT);
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    gap = new Space(this);
                    gap.setLayoutParams(attrSpace);
                    imagesLayout.addView(gap);

                    addedUrl = data.getClipData().getItemAt(i).getUri().toString();
                    photosUrl.add(addedUrl);
                    System.out.println("added url ->" + addedUrl);

                    photoBtn = new ImageView(this);
                    photoBtn.setLayoutParams(attrImg);
                    photoBtn.setImageURI(Uri.parse(addedUrl));
                    photoBtn.setTag(addedUrl);
                    photoBtn.setOnClickListener((btn) -> {
                        showPhoto(btn.getTag().toString());
                    });
                    imagesLayout.addView(photoBtn);
                }
            } else if (data.getData() != null) {
                gap = new Space(this);
                gap.setLayoutParams(attrSpace);
                imagesLayout.addView(gap);

                addedUrl = data.getData().toString();
                photosUrl.add(addedUrl);
                System.out.println("added url ->" + addedUrl);

                photoBtn = new ImageView(this);
                photoBtn.setLayoutParams(attrImg);
                photoBtn.setImageURI(Uri.parse(addedUrl));
                photoBtn.setTag(addedUrl);
                photoBtn.setOnClickListener((btn) -> {
                    showPhoto((String) btn.getTag());
                });
                photoBtn.setOnLongClickListener((btn) -> handleLongClick((String) btn.getTag(), btn));
                imagesLayout.addView(photoBtn);
            }
        }
    }


    // This method shows the pictures on the gallery
    private void showPhoto(String photoUrl) {
        System.out.println("uri is " + photoUrl);
        // It shows the selected photo first, and the rest at the position as in imagesLayout
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(photoUrl), "image/*");
        startActivity(intent);
    }


    // This method handles long click event for the photo's note.
    // It either shows the photo or removes it.
    private Boolean handleLongClick(String photoUrl, View holdBtn) {
        // showing popup list
        PopupMenu menu = new PopupMenu(this, holdBtn);
        menu.getMenu().add("Show the photo");
        menu.getMenu().add("Remove the photo");
        menu.show();

        menu.getMenu().getItem(0).setOnMenuItemClickListener((item) -> {
            showPhoto(photoUrl);
            return true;
        });

        menu.getMenu().getItem(1).setOnMenuItemClickListener((item) -> {
            int removeIndex = 0;
            for (int i = 0; i < photosUrl.size(); i++) {
                if (photosUrl.get(i).equals(photoUrl)) {
                    removeIndex = i;
                }
            }
            photosUrl.remove(removeIndex);
            imagesLayout.removeView(holdBtn);
            return true;
        });
        return true;
    }

}
