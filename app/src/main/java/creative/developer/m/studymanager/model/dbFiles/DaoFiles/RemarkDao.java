/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : RemarkDao
purpose: this is a model file that stores methods that will be used as
 procedures for RemarkEntity
Methods:
  - getAllRemarks() -> it returns all fields' data on the table
    as a list<RemarkEntity>
  - addRemark(added) -> it adds a field to the the table.
  - updateRemarks(updated) -> it updates a field/s in the table. The parameter
    is the object after updating.
  - deleteRemarks(deleted) -> it removes deleted from the table.
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

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;

@Dao
public interface RemarkDao {

    @Query("SELECT * FROM RemarkEntity")
    List<RemarkEntity> getAllRemarks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRemark(RemarkEntity... RemarkEntity);

    @Update
    void updateRemark(RemarkEntity obj);

    @Delete
    void deleteRemark(RemarkEntity obj);
}
