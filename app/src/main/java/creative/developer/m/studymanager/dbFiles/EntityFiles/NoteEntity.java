package creative.developer.m.studymanager.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (foreignKeys = @ForeignKey(entity =  CourseEntity.class,
        parentColumns = "name", childColumns = "lesson", onDelete = ForeignKey.CASCADE))
public class NoteEntity{

    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int noteID;
    private String course;
    private String lesson;
    private String notes;


    // getters
    public int getNoteID() { return noteID; }

    public String getCourse () {return course;}

    public void setNoteID(int noteID) { this.noteID = noteID; }

    public String getLesson() { return lesson; }


    // setters
     public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public void setCourse(String course) {this.course = course;}

    public void setLesson(String lesson) { this.lesson = lesson; }

}
