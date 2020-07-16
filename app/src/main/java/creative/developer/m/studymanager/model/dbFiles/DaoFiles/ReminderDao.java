/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : ReminderDao
purpose: this is a model file that stores methods that will be used as
 procedures for ReminderEntity
Methods:
  - getAllReminderss() -> it returns all fields' data on ReminderEntity table
    as a list<ReminderEntity>
  - getReminderByID() -> it is used to search and get a reminder by its ID
  - InsertReminder(added) -> it adds a field to the ReminderEntity tables.
  - updateReminder(updated) -> it updates a field in the table. The parameter
    is the object after updating.
  - deleteReminder(deleted) -> it removes deleted from the table.
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

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM ReminderEntity")
    List<ReminderEntity> getAllReminders();

    @Query("SELECT * FROM ReminderEntity WHERE reminderID == :id")
    ReminderEntity getReminderByID(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReminders(ReminderEntity... added);

    @Update
    void updateReminders(ReminderEntity... updated);

    @Delete
    void deleteReminders(ReminderEntity... deleted);
}
