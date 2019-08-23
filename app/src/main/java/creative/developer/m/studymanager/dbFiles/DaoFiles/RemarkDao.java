package creative.developer.m.studymanager.dbFiles.DaoFiles;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import creative.developer.m.studymanager.dbFiles.EntityFiles.RemarkEntity;

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
