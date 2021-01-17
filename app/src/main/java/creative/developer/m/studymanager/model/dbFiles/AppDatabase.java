/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AppDatebase
purpose: This is a model class. It is used to generate a new database or find
  an existing one.
Methods:
    - getInstance(context) -> it generates a new data base or find an existing one then
      it returns an object that can be used to interact with the data base.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import creative.developer.m.studymanager.model.dbFiles.DaoFiles.AssignmentDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.CourseDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.FlashCardsDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.LessonDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.NoteDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.PhotoNoteDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.RemarkDao;
import creative.developer.m.studymanager.model.dbFiles.DaoFiles.ReminderDao;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.LessonEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.PhotoNoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;

@Database(entities = {AssignmentEntity.class, RemarkEntity.class, CourseEntity.class,
        NoteEntity.class, LessonEntity.class, FlashCardEntity.class, ReminderEntity.class,
        PhotoNoteEntity.class},
        version = 12, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "StudyManagerDB00";
    private static AppDatabase sInstance;

    public abstract AssignmentDao assignmentDao();
    public abstract RemarkDao remarkDao();
    public abstract CourseDao courseDao();
    public abstract NoteDao noteDao();
    public abstract LessonDao lessonDao();
    public abstract FlashCardsDao flashCardsDao();
    public abstract ReminderDao reminderDao();
    public abstract PhotoNoteDao photoNoteDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return sInstance;
    }

}
