package creative.developer.m.studymanager.EntityListFiles;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.dbFiles.AppDatabase;
import creative.developer.m.studymanager.dbFiles.DataRepository;
import creative.developer.m.studymanager.dbFiles.EntityFiles.AssignmentsEntity;
import creative.developer.m.studymanager.dbFiles.EntityFiles.NoteEntity;

public class NoteList {

    private List<NoteEntity> notesList; // temprary list
    private TreeMap<String, List<NoteEntity>> notesMap;
    private Context context;
    private DataRepository repository; // used to access database

    public NoteList (Context context) {
        this.context = context;
        this.notesList = null;
    }

    public void setNotesList(List<NoteEntity> notesList) {
        this.notesList = notesList;
        notesMap = new TreeMap<>();
        for (NoteEntity note : notesList) {
            if (!notesMap.containsKey(note.getCourse())){
                notesMap.put(note.getCourse(), new ArrayList<>());
            }
            notesMap.get(note.getCourse()).add(note);
        }
    }

    // this returns the list of lesson for the inputted course
    public List<NoteEntity> getNoteCourse (String course) {
        return this.notesMap.get(course);
    }

    // this returns the whole map
    public TreeMap<String, List<NoteEntity>> getNotesMap () {
        return this.notesMap;
    }

    public void removeNote(NoteEntity removed) {
        boolean isRemoved = false;
        List<NoteEntity> searched = notesMap.get(removed.getCourse());
        int i = 0;
        while (i < searched.size() && !isRemoved) {
            if (searched.get(i).getNoteID() == removed.getNoteID()) {
                searched.remove(i);
                isRemoved = true;
            }
        }
    }

    public void addNote(NoteEntity added) {
        notesMap.get(added.getCourse()).add(added);
    }

    public void updateNote (NoteEntity updated) {
        boolean isUpdated = false;
        List<NoteEntity> searched = notesMap.get(updated.getCourse());
        int i = 0;
        while (i < searched.size() && !isUpdated) {
            if (searched.get(i).getNoteID() == updated.getNoteID()) {
                searched.set(i, updated);
                isUpdated = true;
            }
        }
    }
}
