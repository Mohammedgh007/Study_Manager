package creative.developer.m.studymanager.dbFiles;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import creative.developer.m.studymanager.EntityListFiles.AssignmentsList;
import creative.developer.m.studymanager.EntityListFiles.NoteList;
import creative.developer.m.studymanager.EntityListFiles.RemarksList;
import creative.developer.m.studymanager.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.dbFiles.EntityFiles.RemarkEntity;

public class DataRepository {

    // this listener trigger an event once the data from database is fully retrieved.
    private retrieveCompletionEventHandler listener;
    private final AppDatabase mDatabase;
    private static DataRepository sInstance;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
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
        AssignmentsList assignments = new AssignmentsList(context);
        List<AssignmentsEntity> retrivedData = mDatabase.assignmentDao().getAllAssignments();
        if (retrivedData != null) {
            System.out.println("retrived = "+retrivedData);
            assignments.setAssignments(retrivedData);
        } else {
            System.out.println("retrived is null = "+retrivedData);
            assignments.setAssignments(new ArrayList<>());
        }
        listener.onRecievingData(assignments);
    }

    public void addAssignment(AssignmentsEntity added) {
        mDatabase.assignmentDao().InsertAssignments(added);
    }

    public void updateAssignment (AssignmentsEntity assignmentsEntity) {
        mDatabase.assignmentDao().updateAssignment(assignmentsEntity);
    }

    public void deleteAssignment (AssignmentsEntity assignmentsEntity) {
        mDatabase.assignmentDao().deleteAssignment(assignmentsEntity);
    }

    /////////////////////////// Remark

    // get all remarks from the database
    public void getRemarks(Context context) {
        List<RemarkEntity> tempList = mDatabase.remarkDao().getAllRemarks();
        RemarksList finalList = new RemarksList(context);
        if (! tempList.isEmpty()) {
            finalList.setRemarksList(tempList);
        } else {
            finalList.setRemarksList(new ArrayList<>());
        }
        listener.onRecievingData(finalList);
    }

    public void addRemark(RemarkEntity added) {
        mDatabase.remarkDao().addRemark(added);
    }

    public void updateRemark (RemarkEntity updated) {
        mDatabase.remarkDao().updateRemark(updated);
    }

    public void deleteRemark (RemarkEntity deleted) {
        mDatabase.remarkDao().deleteRemark(deleted);
    }



    ///////////////////////// Note
    // get all remarks from the database
    public void getNotes(Context context) {
        List<NoteEntity> tempList = mDatabase.noteDao().getAllNotes();
        NoteList finalList = new NoteList(context);
        if (! tempList.isEmpty()) {
            finalList.setNotesList(tempList);
        } else {
            finalList.setNotesList(new ArrayList<>());
        }
        listener.onRecievingData(finalList);
    }

    public void addNote(NoteEntity added) {
        mDatabase.noteDao().addNote(added);
    }

    public void updateNote (NoteEntity updated) {
        mDatabase.noteDao().updateNote(updated);
    }

    public void deleteNote (NoteEntity deleted) {
        mDatabase.noteDao().deleteNote(deleted);
    }


    // this interface is buiult to create an event of finishing retrieving data from database
    public interface retrieveCompletionEventHandler {
        public void onRecievingData(Object assignments);
    }

    // It clears all the data on the AssignmentEntity table
    public void clearAll() {
        mDatabase.assignmentDao().clearAll();
    }

}
