/*
###############################################################################
Class name : HomeActivity
purpose: This is model view class that is responsible for home fragment.
  interaction with the user.
Methods:
  onCreateView() -> It encapsulates/manages all the interaction.
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import creative.developer.m.studymanager.R;

import static android.app.Activity.RESULT_OK;

public class HomeActivity extends Fragment {

    final int EDIT_REQUEST_CODE = 123; // used for editBtn's clicking event.
    final int PROMPT_PERMISSION_CODE = 332; // used for onRequestPermissionsResult()
    // two variables used for accessing memory to store schedule photo.
    private SharedPreferences imageRef; // access memory
    private SharedPreferences.Editor imageEditor; // for editing
    // Declaring views
    private ImageView scheduleImg;
    private Button editBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // initializing views
        View root = inflater.inflate(R.layout.home_activity, container, false);
        scheduleImg = root.findViewById(R.id.course_schedule_home);
        editBtn = root.findViewById(R.id.edit_schedule_home);

        // showing the proper background for the schedule view after making sure I get permission
        // to access the device memory, otherwise, I will prompt fot that permission
        imageRef = getActivity().getPreferences(Context.MODE_PRIVATE);
        imageEditor = imageRef.edit();
        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            if (imageRef.contains(getString(R.string.schedulePhotoIdMemory))){
                String uriStr = imageRef.getString(getString(R.string.schedulePhotoIdMemory),"");
                scheduleImg.setBackground(null);
                scheduleImg.setImageURI(Uri.parse(uriStr));
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PROMPT_PERMISSION_CODE);
        }


        // opening the schedule by the gallery when the user click on it
        scheduleImg.setOnClickListener((evt) -> {
            // if the user has choose a picture.
            if (imageRef.contains(getString(R.string.schedulePhotoIdMemory))) {
                String uriStr = imageRef.getString(getString(R.string.schedulePhotoIdMemory), "");
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setDataAndType(Uri.parse(uriStr), "image/*");
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

        return root;
    }


    // this method recieve the image from gallery for editBtn's intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
        if (requestCode == PROMPT_PERMISSION_CODE) {
            // checking the permission is granted, if so then change background
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String uriStr = imageRef.getString(getString(R.string.schedulePhotoIdMemory),"");
                scheduleImg.setBackground(null);
                scheduleImg.setImageURI(Uri.parse(uriStr));
            } else { // if it is not granted, then show a message
                Toast.makeText(getActivity(), "Please allow the app to access the memory, " +
                        "so that the app can show photos", Toast.LENGTH_LONG).show();
            }
        }
    }
}
