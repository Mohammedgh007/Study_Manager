/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : NoteDao
purpose: this is a model file that stores methods that will be used as
 procedures for NoteEntity
Methods:
  - getAllNotes() -> it returns all fields' data on the table
    as a list<NoteEntity>
  - addNote(added) -> it adds a field to the the table.
  - updateCards(updated) -> it updates a field/s in the table. The parameter
    is the object after updating.
  - deleteCards(deleted) -> it removes deleted from the table.
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

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;

@Dao
public interface NoteDao {


    @Query("SELECT * FROM NoteEntity")
    public List<NoteEntity> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addNote(NoteEntity... added);

    @Delete
    public void deleteNote(NoteEntity deleted);

    @Update
    public void updateNote(NoteEntity update);
}
