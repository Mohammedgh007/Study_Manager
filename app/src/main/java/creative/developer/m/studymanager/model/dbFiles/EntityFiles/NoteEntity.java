/*
###############################################################################
Author: Mohammed Alghamdi
Class name : NoteEntity
purpose: This is a model class that is used to represent a signle notes' class
    as an object and as a field on a database table called NoteEntity
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (foreignKeys = @ForeignKey(entity =  CourseEntity.class,
        parentColumns = "name", childColumns = "course", onDelete = ForeignKey.CASCADE))
public class NoteEntity{

    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int noteID;
    private String course;
    private String lesson;
    private String notes;

    public NoteEntity (String course, String lesson, String notes) {
        this.course = course;
        this.lesson = lesson;
        this.notes = notes;
    }


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
