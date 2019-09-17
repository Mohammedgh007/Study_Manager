/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : LessonDao
purpose: this is a model file that stores methods that will be used as
 procedures for LessonEntity
Methods:
  - InsertCards(added) -> it adds a field to the the table.
  - deleteCards(deleted) -> it removes deleted from the table.
###############################################################################
 */


package creative.developer.m.studymanager.model.dbFiles.DaoFiles;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.LessonEntity;

@Dao
public interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLesson(LessonEntity added);

    @Query("DELETE FROM LessonEntity WHERE name = :lessonName")
    void deleteLesson(String lessonName);
}
