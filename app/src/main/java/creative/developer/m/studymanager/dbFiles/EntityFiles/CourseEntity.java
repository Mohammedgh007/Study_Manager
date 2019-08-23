package creative.developer.m.studymanager.dbFiles.EntityFiles;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CourseEntity {

    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int courseID;
    private String name;
}
