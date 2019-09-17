/*
###############################################################################
Author: Mohammed Alghamdi
Class name : CourseEntity
purpose: This is a model class that is used to represent a signle course
    as an object and as a field on a database table called CourseEntity
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "name", unique = true)})
public class CourseEntity {

    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int courseID;
    private String name;

    public CourseEntity(String name) {
        this.name = name;
    }

    // getters mostly for Room usage
    public int getCourseID() { return courseID; }

    public String getName() { return name; }


    // setters mostly for Room usage
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public void setName(String name) { this.name = name; }
}
