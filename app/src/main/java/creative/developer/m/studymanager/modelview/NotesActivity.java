/*
###############################################################################
Class name : NotesActivity
purpose: This is model view class that is responsible for notes activity
  interaction with the user.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
  showPhotos() -> It shows the photos in the gallery.
  handleLongClick() -> It handles the event of holding the photo.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import java.util.ArrayList;
import java.util.List;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.modelCoordinators.NoteCoordinator;

public class NotesActivity extends Activity {

    private static final int ADDING_PHOTO_CODE = 23;
    private static final int PERMISSION_CODE = 3;
    private List<String> shownPhotosUrl;
    private List<String> removedPhotosUrl;
    private List<String> addedPhotosUrl;
    private LinearLayout imagesLayout;
    private TextView courseTV;
    private TextView lessonTV;
    private EditText noteET;
    // once it's clicked, the activity will be closed with saving the changes on the note.
    private Button closeSaveBtn;
    private Button closeNoSaveBtn;
    private Button addPhotoBtn;
    private NoteEntity lessonNoteObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        // initializing views
        addPhotoBtn = findViewById(R.id.add_photo_Note);
        imagesLayout = findViewById(R.id.images_layout_Note);
        courseTV = findViewById(R.id.course_note);
        lessonTV = findViewById(R.id.lesson_note);
        noteET = findViewById(R.id.note_te_note);
        closeSaveBtn = findViewById(R.id.close_save_note);
        closeNoSaveBtn = findViewById(R.id.close_notsave_note);

        removedPhotosUrl = new ArrayList<>();
        addedPhotosUrl = new ArrayList<>();


        // filling text views and edit text.
        Intent received = this.getIntent();
        NoteCoordinator model = NoteCoordinator.getInstance(this);
        lessonNoteObj = model.getLessonNote(received.getStringExtra("course"),
                received.getStringExtra("lesson"));
        courseTV.setText(lessonNoteObj.getCourse());
        lessonTV.setText(lessonNoteObj.getLesson());
        noteET.setText(lessonNoteObj.getNotes());


        shownPhotosUrl = model.getLessonPhotosUrls(lessonNoteObj.getCourse(),
                lessonNoteObj.getLesson());

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            for (String uri : shownPhotosUrl) {
                imagesLayout.addView(getImageView(uri));
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.permissionExternalStorage),
                    Toast.LENGTH_LONG).show();
        }



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
            model.updateNote(lessonNoteObj);
            for (String addedUri : addedPhotosUrl)
                model.addPhoto(addedUri, lessonNoteObj.getNoteID());
            for (String removedUri : removedPhotosUrl)
                model.removePhoto(removedUri, lessonNoteObj.getNoteID());
            finish();
        });

        // click event handling for picking a photo from user's gallery to attach that photo to this note
        addPhotoBtn.setOnClickListener((btn) -> {
            // check if the permission is granted
            if(ActivityCompat.checkSelfPermission(NotesActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, ADDING_PHOTO_CODE);
            } else { // ask permission
                ActivityCompat.requestPermissions(NotesActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        });

    }

    // this method recieve the images from gallery for addPhotoBtn's intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==ADDING_PHOTO_CODE) {
            String addedUrl = "";

            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    addedUrl = data.getClipData().getItemAt(i).getUri().toString();
                    shownPhotosUrl.add(addedUrl);
                    addedPhotosUrl.add(addedUrl);
                    imagesLayout.addView(getImageView(addedUrl));
                }
            } else if (data.getData() != null) {
                addedUrl = data.getData().toString();
                shownPhotosUrl.add(addedUrl);
                addedPhotosUrl.add(addedUrl);
                imagesLayout.addView(getImageView(addedUrl));
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
        menu.getMenu().add(R.string.showPhoto);
        menu.getMenu().add(R.string.removePhoto);
        menu.show();

        menu.getMenu().getItem(0).setOnMenuItemClickListener((item) -> {
            showPhoto(photoUrl);
            return true;
        });

        menu.getMenu().getItem(1).setOnMenuItemClickListener((item) -> {
            removedPhotosUrl.add(photoUrl);

            int removeIndex = 0;
            for (int i = 0; i < shownPhotosUrl.size(); i++) {
                if (shownPhotosUrl.get(i).equals(photoUrl)) {
                    removeIndex = i;
                }
            }
            shownPhotosUrl.remove(removeIndex);
            imagesLayout.removeView(holdBtn);
            return true;
        });
        return true;
    }


    // this method returns the ImageView objects that 's used to show photoNote
    private ImageView getImageView(String uri) {
        LinearLayout.LayoutParams attrSpace = new LinearLayout.LayoutParams(
                (int)getResources().getDimension(R.dimen.photos_space),
                LinearLayout.LayoutParams.MATCH_PARENT);
        Space gap = new Space(this);
        gap.setLayoutParams(attrSpace);
        imagesLayout.addView(gap);

        LinearLayout.LayoutParams attrImg = new LinearLayout.LayoutParams(
                (int)getResources().getDimension(R.dimen.photo_note_width_small),
                LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView photoBtn = new ImageView(this);
        photoBtn.setLayoutParams(attrImg);
        photoBtn.setImageURI(Uri.parse(uri));
        photoBtn.setTag(uri);
        photoBtn.setOnClickListener((btn) -> {
            showPhoto(btn.getTag().toString());
        });
        photoBtn.setOnLongClickListener((btn) -> handleLongClick(uri, btn));
        return photoBtn;
    }

}
