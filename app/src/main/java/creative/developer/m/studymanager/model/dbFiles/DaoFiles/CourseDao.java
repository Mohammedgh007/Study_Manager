/*
###############################################################################
Interface name : CourseDao
Author: Mohammed Alghamdi
purpose: this is a model file that stores methods that will be used as
 procedures for CourseEntity
Methods:
  - getAllCourses() -> it returns all fields' data on the table
    as a list<CourseEntity>
  - InsertCourse(added) -> it adds a field to the the table.
  - deleteCourse(deleted) -> it removed deleted field/s from the table.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.DaoFiles;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;

@Dao
public interface CourseDao {

    @Query("SELECT * FROM CourseEntity")
    List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity> getAllCourses();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void InsertCourse(creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity... courseEntities);

    @Delete
    void deleteCourse(CourseEntity... courseEntities);
}
