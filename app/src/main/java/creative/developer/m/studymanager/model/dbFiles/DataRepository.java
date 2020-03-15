/*
###############################################################################
Author: Mohammed Alghamdi
Class name : DataRepository
purpose: This is a model class. it ease and facilitates the interaction with
  the database.
Methods:
    - getInstance() -> It returns the instance, and this is the only way to get
      DataRepositiory object.
      that will be executed after activities fully retrieve the data from the data base.
    - getAssignments(context) -> retrieve all the assignments from the database.
    - getAssignmentByNotifyID(notifyID) -> searches and gets an assignment by notifyID.
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
    - addCourse(name) -> adds a row to CourseEntity table
    - getCoursesStr() -> returns the set of courses' names from CourseEntity table.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles;


import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    private final AppDatabase database;
    private static DataRepository sInstance;

    private DataRepository(final AppDatabase database) {
        this.database = database;
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


    /////////////////////////////// Assignments

    public AssignmentsList getAssignments(Context context){
        System.out.println("start assignment");
        AssignmentsList assignments = new AssignmentsList(context);
        List<AssignmentsEntity> retrivedData = database.assignmentDao().getAllAssignments();
        System.out.println("finish assignment");
        if (retrivedData != null) {
            System.out.println("retrived = "+retrivedData);
            assignments.setAssignments(retrivedData);
        } else {
            System.out.println("retrived is null = "+retrivedData);
            assignments.setAssignments(new ArrayList<>());
        }
        return assignments;
    }

    public AssignmentsEntity getAssignmentByNotifyID(String notifyID) {
        return database.assignmentDao().getAssginmentByNotifyID(notifyID);
    }

    public void addAssignment(AssignmentsEntity added) {
        database.assignmentDao().InsertAssignments(added);
    }

    public void updateAssignment (AssignmentsEntity updated) {
        database.assignmentDao().updateAssignment(updated);
    }

    public void deleteAssignment (AssignmentsEntity deleted) {
        database.assignmentDao().deleteAssignment(deleted);
    }

    /////////////////////////// Remark

    // get all remarks from the database
    public RemarksList getRemarks(Context context) {
        List<RemarkEntity> tempList = database.remarkDao().getAllRemarks();
        RemarksList finalList = new RemarksList(context);
        if (! tempList.isEmpty()) {
            finalList.setRemarksList(tempList);
        } else {
            finalList.setRemarksList(new ArrayList<>());
        }
        return finalList;
    }

    public void addRemark(RemarkEntity added) {
        database.remarkDao().addRemark(added);
    }

    public void updateRemark (RemarkEntity updated) {
        database.remarkDao().updateRemark(updated);
    }

    public void deleteRemark (RemarkEntity deleted) {
        database.remarkDao().deleteRemark(deleted);
    }



    ///////////////////////// Note

    // get all notes from the database
    public NoteList getNotes(Context context) {
        List<NoteEntity> tempList = database.noteDao().getAllNotes();
        NoteList finalList = new NoteList(context);
        if (! tempList.isEmpty()) {
            finalList.setNotesList(tempList);
        } else {
            finalList.setNotesList(new ArrayList<>());
        }
        return finalList;
    }

    public void addNote(NoteEntity added) {
        database.noteDao().addNote(added);
    }

    public void updateNote (NoteEntity updated) {
        database.noteDao().updateNote(updated);
    }

    public void deleteNote (NoteEntity deleted) {
        database.noteDao().deleteNote(deleted);
    }

    //////////////////// FlashCards

    // get all flash cards from the database
    public FlashCardsList getCards(Context context) {
        List<FlashCardEntity> tempList = database.flashCardsDao().getAllCards();
        FlashCardsList finalList = new FlashCardsList(context);
        if (! tempList.isEmpty()) {
            finalList.setList(tempList);
        } else {
            finalList.setList(new ArrayList<>());
        }
        return finalList;
    }


    public void addCards(FlashCardEntity... added) {
        for (FlashCardEntity card : added) {
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


    //// Courses
    public void addCourse (String name) {
        database.courseDao().InsertCourse(new CourseEntity(name));
    }

    public Set<String> getCoursesStr() {
        Set<String> coursesStr = new HashSet<>();
        for (CourseEntity entity : database.courseDao().getAllCourses()) {
            coursesStr.add(entity.getName());
        }
        return coursesStr;
    }

}
