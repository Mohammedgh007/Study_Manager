/*
###############################################################################
Class name : HomeActivity
purpose: This is model view class that is responsible for home activity
  interaction with the user.
Methods:
  onCreate -> It encapsulates/manages all the interaction.
  onActivityResult -> It receives the intent from gellary to save the image.
###############################################################################
 */

package creative.developer.m.studymanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HomeActivity extends Activity {

    final int editRequestCode = 123; // used for editBtn's clicking event.
    // two variables used for accessing memory to store schedule photo.
    SharedPreferences imageRef; // access memory
    SharedPreferences.Editor imageEditor; // for editing
    // Declaring views
    ImageView scheduleImg;
    Button editBtn;
    Button remarksBtn;
    Button assignmentBtn;
    Button notesBtn;
    Button cardsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Initializing views
        scheduleImg = findViewById(R.id.course_schedule_home);
        editBtn = findViewById(R.id.edit_schedule_home);
        remarksBtn = findViewById(R.id.remarks_home);
        assignmentBtn = findViewById(R.id.assignment_home);
        notesBtn = findViewById(R.id.class_note_home);
        cardsBtn = findViewById(R.id.flashcards_home);

        // showing the proper background for the schedule view
        imageRef = this.getPreferences(Context.MODE_PRIVATE);
        if (imageRef.contains(getString(R.string.schedulePhotoIdMemory))){
            String uriStr = imageRef.getString(getString(R.string.schedulePhotoIdMemory),"");
            scheduleImg.setImageURI(Uri.parse(uriStr));
        } // else { the app show a photo to ask the user for selecting a schrdule photo}

        // Going to assignment activity
        assignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AssignmentActivity.class);
                startActivity(intent);
            }
        });

        // Going to remarks activity
        remarksBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RemarksActivity.class);
                startActivity(intent);
            }
        });

        // Going to class notes activity
        notesBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CoursesActivity.class);
                intent.putExtra("finalDistanation", "notes");
                startActivity(intent);
            }
        });

        // Going to flash cards activity
        cardsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CoursesActivity.class);
                intent.putExtra("finalDistanation", "cards");
                startActivity(intent);
            }
        });

        // editing schedule photo
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, editRequestCode);
            }
        });
    }

    // this method recieve the image from gallery for editBtn's intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == editRequestCode) {
            Uri imageUri = data.getData();
            scheduleImg.setImageURI(imageUri);

            imageEditor = imageRef.edit();
            imageEditor.putString(getString(R.string.schedulePhotoIdMemory), imageUri.toString());
            imageEditor.commit();
        }
    }
}
