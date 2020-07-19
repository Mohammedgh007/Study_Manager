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
    - getAssignmentByID(notifidyID) -> searches and gets an assignment by id.
    - addAssignment(added) -> adds an assignment to the data base.
    - updateAssignment(updated) -> updates an assignment on the data base.
    - deleteAssignment(deleted) -> removes an assignment from the data base.
    - getRemarks(context) -> retrieves all remarks from the data base.
    - addRemark(added) -> adds a remark to the database.
    - updateRemark(updated) -> updates a remark on the database
    - removeRemark(removed) -> removes a remark from the data base.
    - getNotes() -> retrieves all notes from the data base.
    - getPhotos(lastID) -> retrives all the photos' note from the database and returns the last id
        number used.
    - addNote(added, photoEntities) -> adds a note to the databse with photoEntities.
    - addPhotoNote(added) -> It adds the photo note to the table PhotoNoteEntity.
    - removeNote(removed) -> removes a note from the data base.
    - removePhotoNote(removed) -> It removes the photo note from the table PhotoNoteEntity.
    - updatesNote(updated) -> updates a note on the data base.
    - getCards(context) -> retrieves all the cards from the database.
    - addCard(added) -> adds a card to the database if it is not added yet; otherwise,
      it updates its value.
    - deletePhotoNote(deleted) -> it deletes a card from the table PhotoNoteCard.
    - deleteCards(removed) -> removes card/s from the database
    - addCourse(name) -> adds a row to CourseEntity table
    - getCoursesStr() -> returns the set of courses' names from CourseEntity table.
    - getAllReminders() -> retrieves all reminders from database
    - getReminder() -> retrieves a particular reminder.
    - addReminder(added) -> adds added to the database.
    - updateReminder(updated) -> updates updated on the database.
    - deleteReminder(removed) -> removes removed from the database.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles;


import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.CourseEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.LessonEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.PhotoNoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;

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

    public List<AssignmentEntity> getAssignments(){
        List<AssignmentEntity> retrivedData = database.assignmentDao().getAllAssignments();
        if (retrivedData != null) {
            System.out.println("retrived = "+retrivedData);
            retrivedData = filterOutdated(retrivedData);
        } else {
            System.out.println("retrived is null = "+retrivedData);
        }
        return retrivedData;
    }

    /*
    * This method filters out the assignemnts whose deadline has passed.
    * @param assignments is the assignment list
    */
    private List<AssignmentEntity> filterOutdated(List<AssignmentEntity> assignments) {
        List<AssignmentEntity> filtered = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Calendar assignmentDate = Calendar.getInstance();
        int i = 0;
        AssignmentEntity assignment;
        while (i < assignments.size()) {
            assignment = assignments.get(i);

            // months starts from 0
            assignmentDate.set(assignment.getYearNum(), assignment.getMonthNum() - 1,
                    assignment.getDayNum());
            if (today.after(assignmentDate)){
                System.out.println("in if");
                this.deleteAssignment(assignment);
                assignments.remove(i);
            } else {
                System.out.println("in else");
                filtered.add(assignment);
                i++;
            }
        }
        return filtered;
    }

    public AssignmentEntity getAssignmentByID(int id) {
        return database.assignmentDao().getAssginmentByID(id);
    }

    public void addAssignment(AssignmentEntity added) {
        database.assignmentDao().InsertAssignments(added);
    }

    public void updateAssignment (AssignmentEntity updated) {
        database.assignmentDao().updateAssignment(updated);
    }

    public void deleteAssignment (AssignmentEntity deleted) {
        database.assignmentDao().deleteAssignment(deleted);
    }

    /////////////////////////// Remark

    // get all remarks from the database
    public List<RemarkEntity> getRemarks() {
        List<RemarkEntity> retrievedList = database.remarkDao().getAllRemarks();
        if (! retrievedList.isEmpty()) {
            return  retrievedList;
        } else {
            return new ArrayList<>();
        }
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
    public List<NoteEntity> getNotes() {
        List<NoteEntity> resultList = database.noteDao().getAllNotes();
        if (! resultList.isEmpty()) {
            return resultList;
        } else {
            return new ArrayList<>();
        }
    }

    // get all photos from the database
    public HashMap<Integer, List<PhotoNoteEntity>> getPhotos(int[] lastID){
        List<PhotoNoteEntity> resultList = database.photoNoteDao().getAllPhotos();
        if (! resultList.isEmpty()) {
            HashMap<Integer, List<PhotoNoteEntity>> photos = new HashMap<>();
            for (PhotoNoteEntity photo : resultList) {
                if (!photos.containsKey(photo.getNoteID())) {
                    photos.put(photo.getNoteID(), new ArrayList<>());
                }
                photos.get(photo.getNoteID()).add(photo);
                lastID[0] = Math.max(lastID[0], photo.getPhotoID());
            }
            return photos;
        } else {
            return new HashMap<>();
        }
    }

    public void addNote(NoteEntity added, List<PhotoNoteEntity> photoEntities) {
        database.noteDao().addNote(added);
        for (PhotoNoteEntity photo : photoEntities) {
            database.photoNoteDao().addPhoto(photo);
        }
    }

    public void addPhotoNote(PhotoNoteEntity added) {
        database.photoNoteDao().addPhoto(added);
    }

    public void deletePhotoNote (PhotoNoteEntity deleted) {
        database.photoNoteDao().deletePhoto(deleted);
    }

    public void updateNote (NoteEntity updated) {
        database.noteDao().updateNote(updated);
    }

    public void deleteNote (NoteEntity deleted) {
        database.noteDao().deleteNote(deleted);
    }

    //////////////////// FlashCards

    // get all flash cards from the database
    public List<FlashCardEntity> getCards() {
        List<FlashCardEntity> resultList = database.flashCardsDao().getAllCards();
        if (! resultList.isEmpty()) {
            return resultList;
        } else {
             return new ArrayList<>();
        }
    }


    public void addCards(FlashCardEntity... added) {
        database.lessonDao().insertLesson(new LessonEntity(added[0].getLesson()));
        for (FlashCardEntity card : added) {
            database.flashCardsDao().insertCards(card);
        }
    }

    public void updateCards(FlashCardEntity... updated) {
        database.lessonDao().insertLesson(new LessonEntity(updated[0].getLesson()));
        for (FlashCardEntity card : updated) {
            database.flashCardsDao().updateCards(card);
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


    ///// Reminders
    public List<ReminderEntity> getAllReminders() {
        List<ReminderEntity> retrievedData = database.reminderDao().getAllReminders();
        if (retrievedData != null)
            return retrievedData;
        else
            return new ArrayList<>();
    }

    public ReminderEntity getReminder(String reminderID) {
        return database.reminderDao().getReminderByID(reminderID);
    }

    public void addReminder(ReminderEntity... added) {
        database.reminderDao().insertReminders(added);
    }

    public void updateReminder(ReminderEntity... updated) {
        database.reminderDao().updateReminders(updated);
    }

    public void deleteReminder(ReminderEntity... deleted) {
        database.reminderDao().deleteReminders(deleted);
    }

}
