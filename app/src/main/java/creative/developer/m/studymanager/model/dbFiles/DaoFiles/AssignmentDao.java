/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : AssignmentDao
purpose: this is a model file that stores methods that will be used as
 procedures for AssignmemtEntity
Methods:
  - getAllAssignments() -> it returns all fields' data on AssignmentEntity table
    as a list<AssignmentEntity>
  - getAssginmentByNotifyID() -> it is used to search and get an assignment by its notifyID
  - InsertAssignments(added) -> it adds a field to the AsignmentEntity tables.
  - updateAssignment(updated) -> it updates a field in the table. The parameter
    is the object after updating.
  - deleteAssignment(deleted) -> it removes delted from the table.
  - clearAll() -> it removes all feilds from the table; it is used for debugging.
###############################################################################
 */
package creative.developer.m.studymanager.model.dbFiles.DaoFiles;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;


@Dao
public interface AssignmentDao {

    @Query("SELECT * FROM AssignmentsEntity")
    List<AssignmentsEntity> getAllAssignments();

    @Query("SELECT * FROM AssignmentsEntity WHERE notificationID == :notifyID")
    AssignmentsEntity getAssginmentByNotifyID(String notifyID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertAssignments(AssignmentsEntity... assignmentObjects);

    @Update
    void updateAssignment(AssignmentsEntity updated);

    @Delete
    void deleteAssignment(AssignmentsEntity delted);

    @Query("DELETE FROM AssignmentsEntity")
    void clearAll();
}
