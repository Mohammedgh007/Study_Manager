package creative.developer.m.studymanager.dbFiles;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import creative.developer.m.studymanager.dbFiles.DaoFiles.AssignmentDao;
import creative.developer.m.studymanager.dbFiles.DaoFiles.NoteDao;
import creative.developer.m.studymanager.dbFiles.DaoFiles.RemarkDao;
import creative.developer.m.studymanager.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.dbFiles.EntityFiles.RemarkEntity;

@Database(entities = {AssignmentsEntity.class, RemarkEntity.class, NoteEntity.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "StudyManagerDB03";
    private static AppDatabase sInstance;
    private Context context;

    public abstract AssignmentDao assignmentDao();
    public abstract RemarkDao remarkDao();
    public abstract NoteDao noteDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        System.out.println("istance is " + sInstance);
        return sInstance;
    }



}
