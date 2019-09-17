/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AppDatebase
purpose: This is a model class. It is used to generate a new data base or find
  an existing onr.
Methods:
    - getInstance(context) -> it generates a new data base or find an existing one then
      it returns an object that can be used to interact with the data base.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import creative.developer.m.studymanager.model.dbFiles.DaoFiles.AssignmentDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.CourseDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.FlashCardsDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.LessonDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.NoteDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.RemarkDao;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.LessonEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;

@Database(entities = {AssignmentsEntity.class, RemarkEntity.class, CourseEntity.class,
        NoteEntity.class, LessonEntity.class, FlashCardEntity.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "StudyManagerDB00";
    private static AppDatabase sInstance;
    private Context context;

    public abstract AssignmentDao assignmentDao();
    public abstract RemarkDao remarkDao();
    public abstract CourseDao courseDao();
    public abstract NoteDao noteDao();
    public abstract LessonDao lessonDao();
    public abstract FlashCardsDao flashCardsDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }



}
