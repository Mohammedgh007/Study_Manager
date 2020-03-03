/*
###############################################################################
Author: Mohammed Alghamdi
Class name : NoteList
purpose: This is a model class that handles storing notes objects as a data structure.
Methods:
    setNotesList(recievedNotesList) -> assign the value of notesMap, which is the data structure
      that stores notes' objects
    containLesson(course, lesson) -> it returns true if the lesson exist.
    getCoursesSet() -> it returns strings that are courses' names.
    getLessonsCourse(course) -> it returns string of the course.
    getNote(course, lesson) -> returns a NoteEntity object
    removeNote(course, lesson) -> remove a note object from notesMap.
    addNote(added) -> adds added object to notesMap
    updateNote(updated) -> updates the value of updated on noteMaps.
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;

public class NoteList{

    private TreeMap<String, List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity>> notesMap;
    private Context context;
    private DataRepository repository; // used to access database
    private static NoteList instance; // to avoid sending heavy object between activities

    public NoteList (Context context) {
        this.context = context;
    }

    public static NoteList getInstance() {
        return instance;
    }

    public void setNotesList(List<creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity> notesList) {
        notesMap = new TreeMap<>();
        for (creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity note : notesList) {
            if (!notesMap.containsKey(note.getCourse())){
                notesMap.put(note.getCourse(), new ArrayList<>());
            }
            notesMap.get(note.getCourse()).add(note);
        }
        instance = this;
        System.out.println(instance + "testing");
    }

    // this returns the list of lesson for the inputted course
    public List<String> getLessonsCourse (String course) {
        List<String> lessonsList = new ArrayList<>();
        if (this.notesMap.get(course) != null) {
            for (creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity note : this.notesMap.get(course)) {
                lessonsList.add(note.getLesson());
            }
        }
        return lessonsList;
    }

    // this returns the set of courses
    public Set<String> getCoursesSet() {
        return this.notesMap.keySet();
    }



    // this returns a particular note object depending on the lesson and the course
    public creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity getNote(String course, String lesson) {
        for (creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity note : notesMap.get(course)) {
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
     */
    public void removeNote(String course, String lesson) {
        // removing from this class structure
        int i = 0;
        boolean isRemoved = false;
        while (i < notesMap.get(course).size() && !isRemoved) {
            if (lesson.equalsIgnoreCase(notesMap.get(course).get(i).getLesson())) {
                notesMap.get(course).remove(i);
                isRemoved = true;
            }
            i++;
        }
        instance = this;
    }

    /*
    this method add the noteEntity object to this class structure and to the database
    @PARAM: added is the NoteEntity object that will be added.
     */
    public void addNote(creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity added) {
        // adding on this class structure
        if (!notesMap.containsKey(added.getCourse())) {
            notesMap.put(added.getCourse(), new ArrayList<>());
        }
        notesMap.get(added.getCourse()).add(added);
        instance = this;
    }


    /*
    this method updates the noteEntity object on this class structure and on the database
    @PARAM: updated is the NoteEntity object that has been updated.
     */
    public void updateNote (creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity updated) {
        // updating on this class structure
        boolean isUpdated = false;
        List<NoteEntity> searchedList = notesMap.get(updated.getCourse());
        int i = 0;
        while (i < searchedList.size() && !isUpdated) {
            if (searchedList.get(i).getNoteID() == updated.getNoteID()) {
                searchedList.set(i, updated);
                isUpdated = true;
            }
        }
        instance = this;
    }
}
