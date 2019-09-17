/*
###############################################################################
Class name : HomeActivity
purpose: This is model view class that is responsible for home activity
  interaction with the user.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
  onActivityResult() -> It receives the intent from gellary to save the image.
  onRequestPermissionsResult() -> this method handles the response of the user when the user is
    prompted for gaining an external memory permission.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import creative.developer.m.studymanager.R;

public class HomeActivity extends Activity {

    final int EDIT_REQUEST_CODE = 123; // used for editBtn's clicking event.
    final int PROMPT_PERMISSION_CODE = 332; // used for onRequestPermissionsResult()
    // two variables used for accessing memory to store schedule photo.
    private SharedPreferences imageRef; // access memory
    private SharedPreferences.Editor imageEditor; // for editing
    // Declaring views
    private ImageView scheduleImg;
    private Button editBtn;
    private Button remarksBtn;
    private Button assignmentBtn;
    private Button notesBtn;
    private Button cardsBtn;

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

        // showing the proper background for the schedule view after making sure I get permission
        // to access the device memory, otherwise, I will prompt fot that permission
        imageRef = this.getPreferences(Context.MODE_PRIVATE);
        imageEditor = imageRef.edit();
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            if (imageRef.contains(getString(R.string.schedulePhotoIdMemory))){
                String uriStr = imageRef.getString(getString(R.string.schedulePhotoIdMemory),"");
                scheduleImg.setBackground(null);
                scheduleImg.setImageURI(Uri.parse(uriStr));
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PROMPT_PERMISSION_CODE);
        }

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
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    // this method recieve the image from gallery for editBtn's intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            Uri imageUri = data.getData();
            scheduleImg.setBackground(null);
            scheduleImg.setImageURI(imageUri);

            imageEditor = imageRef.edit();
            imageEditor.putString(getString(R.string.schedulePhotoIdMemory), imageUri.toString());
            imageEditor.commit();
        }
    }

    // this method handles the response of the user when the user is prompted for gaining an
    // external memory permission
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        System.out.println("in");
        if (requestCode == PROMPT_PERMISSION_CODE) {
            // checking the permission is granted, if so then change background
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String uriStr = imageRef.getString(getString(R.string.schedulePhotoIdMemory),"");
                scheduleImg.setBackground(null);
                scheduleImg.setImageURI(Uri.parse(uriStr));
            } else { // if it is not granted, then show a message
                Toast.makeText(this, "Please allow the app to access the memory, " +
                        "so that the app can show the selected photo", Toast.LENGTH_LONG).show();
            }
        }
    }
}
