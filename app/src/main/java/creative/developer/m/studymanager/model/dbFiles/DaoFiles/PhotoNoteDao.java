/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : PhotoNoteDao
purpose: this is a model file that stores methods that will be used as
 procedures for PhotoNoteEntity
Methods:
  - getAllPhotos() -> it returns all fields' data on the table
    as a list<PhotoNoteEntity>
  - addPhoto(added) -> it adds a field to the the table.
  - updatePhoto(updated) -> it updates a field/s in the table. The parameter
    is the object after updating.
  - deletePhoto(deleted) -> it removes deleted from the table.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.DaoFiles;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.PhotoNoteEntity;

@Dao
public interface PhotoNoteDao {

    @Query("SELECT * FROM PhotoNoteEntity")
    List<PhotoNoteEntity> getAllPhotos();

    @Insert
    void addPhoto(PhotoNoteEntity added);

    @Update
    void updatePhoto(PhotoNoteEntity updated);

    @Delete
    void deletePhoto(PhotoNoteEntity deleted);
}
