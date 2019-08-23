package creative.developer.m.studymanager.dbFiles.DaoFiles;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import creative.developer.m.studymanager.dbFiles.EntityFiles.NoteEntity;

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
