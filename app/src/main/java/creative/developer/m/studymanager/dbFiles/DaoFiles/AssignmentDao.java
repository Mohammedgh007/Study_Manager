/*
###############################################################################
Interface name : AssignmentObject
purpose: This is a model class that is used to represent a signle assignment
   assignment as an object. Also, it is part of the app database.
Methods:
  onCreate -> It encapsulates/manages all the interaction.
  onActivityResult -> It receives the intent from AddAssignmentActivity that
     holds the data of the added assignment.
###############################################################################
 */
package creative.developer.m.studymanager.dbFiles.DaoFiles;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import creative.developer.m.studymanager.dbFiles.EntityFiles.AssignmentsEntity;


@Dao
public interface AssignmentDao {

    @Query("SELECT * FROM AssignmentsEntity")
    List<AssignmentsEntity> getAllAssignments();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertAssignments(AssignmentsEntity ... assignmentObjects);

    @Update
    void updateAssignment(AssignmentsEntity obj);

    @Delete
    void deleteAssignment(AssignmentsEntity obj);

    @Query("DELETE FROM AssignmentsEntity")
    void clearAll();
}
