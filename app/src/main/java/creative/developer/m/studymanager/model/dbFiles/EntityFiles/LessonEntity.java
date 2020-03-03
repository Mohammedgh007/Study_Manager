/*
###############################################################################
Author: Mohammed Alghamdi
Class name : LessonEntity
purpose: This is a model class that is used to represent a single lesson
    as an object and as a row on a database table called LessonEntity
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;


import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "name", unique = true)})
public class LessonEntity {

    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int lessonID;
    private String name;


    public LessonEntity (String name) { // name is the lesson of the lesson
        this.name = name;
    }


    // getters
    public int getLessonID() { return lessonID; }

    public String getName() { return name; }


    // setters
    public void setLessonID(int lessonID) { this.lessonID = lessonID; }

    public void setName(String name) { this.name = name; }
}
