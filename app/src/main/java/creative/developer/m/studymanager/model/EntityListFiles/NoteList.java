/*
###############################################################################
Author: Mohammed Alghamdi
Class name : NoteList
purpose: This is a model class that handles storing notes objects as a data structure.
Methods:
    containLesson(course, lesson) -> it returns true if the lesson exist.
    getLessonsCourse(course) -> it returns string of the course.
    getNote(course, lesson) -> returns a NoteEntity object
    removeNote(course, lesson) -> remove a note object from notesMap.
    addNote(added) -> adds added object to notesMap
    updateNote(updated) -> updates the value of updated on noteMaps.
    getLastID() -> getter for the field lastID.
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;

public class NoteList{

    private TreeMap<String, List<NoteEntity>> notesMap;
    private int lastID;

    public NoteList (List<NoteEntity> receivedList) {
        notesMap = new TreeMap<>();
        for (NoteEntity note : receivedList) {
            if (!notesMap.containsKey(note.getCourse())){
                notesMap.put(note.getCourse(), new ArrayList<>());
            }
            notesMap.get(note.getCourse()).add(note);
            lastID = Math.max(lastID, note.getNoteID());
        }
    }


    // this is a getter for the field lastID
    public int getLastID() {return lastID;}


    // this returns the list of lesson for the inputted course
    public List<String> getLessonsCourse (String course) {
        List<String> lessonsList = new ArrayList<>();
        if (this.notesMap.get(course) != null) {
            for (NoteEntity note : this.notesMap.get(course)) {
                lessonsList.add(note.getLesson());
            }
        }
        return lessonsList;
    }


    // this returns a particular note object depending on the lesson and the course
    public NoteEntity getNote(String course, String lesson) {
        for (NoteEntity note : notesMap.get(course)) {
            if (note.getLesson().equals(lesson)) {
                return note;
            }
        }
        return null;
    }


    // this method returns true if the lesson and the course exist on this class structure
    public boolean containLesson(String course, String lesson) {
        if (notesMap.containsKey(course)) {
            int i = 0;
            while (i < notesMap.get(course).size()) {
                if (notesMap.get(course).get(i).getLesson().equalsIgnoreCase(lesson)) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    /*
    this method removes NoteEntity object from this class structure and from the database
    @PARAM: course is the course of the removed note
    @PARAM: lesson is the lesson of the removed note
    @return the removed note.
     */
    public NoteEntity removeNote(String course, String lesson) {
        // removing from this class structure
        int i = 0;
        NoteEntity removed = null;
        while (i < notesMap.get(course).size()) {
            if (lesson.equalsIgnoreCase(notesMap.get(course).get(i).getLesson())) {
                removed = notesMap.get(course).remove(i);
                return removed;
            }
            i++;
        }
        return removed;
    }

    /*
    this method add the noteEntity object to this class structure and to the database
    @PARAM: added is the NoteEntity object that will be added.
     */
    public void addNote(NoteEntity added) {
        // adding on this class structure
        if (!notesMap.containsKey(added.getCourse())) {
            notesMap.put(added.getCourse(), new ArrayList<>());
        }
        notesMap.get(added.getCourse()).add(added);
    }


    /*
    this method updates the noteEntity object on this class structure and on the database
    @PARAM: updated is the NoteEntity object that has been updated.
     */
    public void updateNote (NoteEntity updated) {
        // updating on this class structure
        boolean isUpdated = false;
        List<NoteEntity> searchedList = notesMap.get(updated.getCourse());
        int i = 0;
        while (i < searchedList.size() && !isUpdated) {
            if (searchedList.get(i).getNoteID() == updated.getNoteID()) {
                searchedList.set(i, updated);
                isUpdated = true;
            } else {
                i++;
            }
        }
    }
}
