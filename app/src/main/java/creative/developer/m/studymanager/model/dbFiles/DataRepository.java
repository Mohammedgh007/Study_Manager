/*
###############################################################################
Author: Mohammed Alghamdi
Class name : DataRepository
purpose: This is a model class. it ease and facilitates the interaction with
  the data base.
Methods:
    - getInstance() -> It returns the instance, and this is the only way to get
      DataRepositiory object.
    - setRetreivedDataListener(listener) -> it is used to encapsulates the code
      that will be executed after activities fully retrieve the data from the data base.
    - getAssignments(context) -> retrieve all the assignments from the database.
    - addAssignment(added) -> adds an assignment to the data base.
    - updateAssignment(updated) -> updates an assignment on the data base.
    - deleteAssignment(deleted) -> removes an assignment from the data base.
    - getRemarks(context) -> retrieves all remarks from the data base.
    - addRemark(added) -> adds a remark to the database.
    - updateRemark(updated) -> updates a remark on the database
    - removeRemark(removed) -> removes a remark from the data base.
    - getNotes(context) -> retrieves all notes from the data base.
    - addNote(added) -> adds a note to the databse.
    - removeNote(removed) -> removes a note from the data base.
    - updatesNote(updated) -> updates a note on the data base.
    - getCards(context) -> retrieves all the cards from the database.
    - addCard(added) -> adds a card to the database if it is not added yet; otherwise,
      it updates its value.
    - deleteCards(removed) -> removes card/s from the database
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import creative.developer.m.studymanager.model.EntityListFiles.AssignmentsList;
import creative.developer.m.studymanager.model.EntityListFiles.FlashCardsList;
import creative.developer.m.studymanager.model.EntityListFiles.NoteList;
import creative.developer.m.studymanager.model.EntityListFiles.RemarksList;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.LessonEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;

public class DataRepository {

    // this listener trigger an event once the data from database is fully retrieved.
    private retrieveCompletionEventHandler listener;
    private final creative.developer.m.studymanager.model.dbFiles.AppDatabase database;
    private static DataRepository sInstance;

    private DataRepository(final creative.developer.m.studymanager.model.dbFiles.AppDatabase database) {
        this.database = database;
        listener = null;
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    public void setRetreivedDataListener (retrieveCompletionEventHandler listener){
        this.listener = listener;
    }

    /////////////////////////////// Assignments

    public void getAssignments(Context context){
        creative.developer.m.studymanager.model.EntityListFiles.AssignmentsList assignments = new AssignmentsList(context);
        List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity> retrivedData = database.assignmentDao().getAllAssignments();
        if (retrivedData != null) {
            System.out.println("retrived = "+retrivedData);
            assignments.setAssignments(retrivedData);
        } else {
            System.out.println("retrived is null = "+retrivedData);
            assignments.setAssignments(new ArrayList<>());
        }
        listener.onRecievingData(assignments);
    }

    public void addAssignment(creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity added) {
        database.assignmentDao().InsertAssignments(added);
    }

    public void updateAssignment (creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity updated) {
        database.assignmentDao().updateAssignment(updated);
    }

    public void deleteAssignment (AssignmentsEntity deleted) {
        database.assignmentDao().deleteAssignment(deleted);
    }

    /////////////////////////// Remark

    // get all remarks from the database
    public void getRemarks(Context context) {
        List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity> tempList = database.remarkDao().getAllRemarks();
        creative.developer.m.studymanager.model.EntityListFiles.RemarksList finalList = new RemarksList(context);
        if (! tempList.isEmpty()) {
            finalList.setRemarksList(tempList);
        } else {
            finalList.setRemarksList(new ArrayList<>());
        }
        listener.onRecievingData(finalList);
    }

    public void addRemark(creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity added) {
        database.remarkDao().addRemark(added);
    }

    public void updateRemark (creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity updated) {
        database.remarkDao().updateRemark(updated);
    }

    public void deleteRemark (RemarkEntity deleted) {
        database.remarkDao().deleteRemark(deleted);
    }



    ///////////////////////// Note

    // get all notes from the database
    public void getNotes(Context context) {
        List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity> tempList = database.noteDao().getAllNotes();
        creative.developer.m.studymanager.model.EntityListFiles.NoteList finalList = new NoteList(context);
        if (! tempList.isEmpty()) {
            finalList.setNotesList(tempList);
        } else {
            finalList.setNotesList(new ArrayList<>());
        }
        listener.onRecievingData(finalList);
    }

    public void addNote(creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity added) {
        database.courseDao().InsertCourse(new creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity(added.getCourse()));
        database.noteDao().addNote(added);
    }

    public void updateNote (creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity updated) {
        database.noteDao().updateNote(updated);
    }

    public void deleteNote (NoteEntity deleted) {
        database.noteDao().deleteNote(deleted);
    }

    //////////////////// FlashCards

    // get all flash cards from the database
    public void getCards(Context context) {
        List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity> tempList = database.flashCardsDao().getAllCards();
        creative.developer.m.studymanager.model.EntityListFiles.FlashCardsList finalList = new FlashCardsList(context);
        if (! tempList.isEmpty()) {
            finalList.setList(tempList);
        } else {
            finalList.setList(new ArrayList<>());
        }
        listener.onRecievingData(finalList);
    }


    public void addCards(creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity... added) {
        for (creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity card : added) {
            database.courseDao().InsertCourse(new CourseEntity(card.getCourse()));
            database.lessonDao().insertLesson(new LessonEntity(card.getLesson()));
            database.flashCardsDao().insertCards(card);
        }
    }

    public void deleteCards (FlashCardEntity... deleted) {
        System.out.println("length is " + deleted.length);
        if (deleted != null) {
            database.flashCardsDao().deleteCards(deleted);
        }
    }


    // this interface is buiult to create an event of finishing retrieving data from database
    public interface retrieveCompletionEventHandler {
        public void onRecievingData(Object assignments);
    }

}
