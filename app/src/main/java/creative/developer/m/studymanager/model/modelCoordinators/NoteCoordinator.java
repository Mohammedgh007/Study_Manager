/*
###############################################################################
Author: Mohammed Alghamdi
Class name : NoteCoordinator
purpose: This is a model class that coordinates all model classes that are used by notes,
  so that view model need to interact only with this class. It has an access
  to both versions of the notes(the database and the cashed notes).
Methods:
    getInstance() -> returns the only instance of this class. This class implements Singleton.
    getNotesList() -> It is getter for the field notesList. It's used to check if the instances has
     been fully initialized, so that the race condition is avoided.
    getLessonsList(course) -> returns the lessons that belongs to the given course.
    getLessonNote(course, lesson) -> returns the note of the given lesson and course.
    getLessonPhotosUris(course, lesson) -> returns the photos uri associated with the notes' lesson.
    addNote() -> it adds the note to the database and the notesList.
    updateNote() -> it updates the note on the database and the notesList.
    removenote() -> it remove the note from the database and the notesList.
    addPhoto(photoUri, noteID) -> It adds the given photo to the note whose id is given.
    removePhoto(photoUri, noteID) -> It removes the given photo to the note whose is is given.
    nullifyInstance() -> It's called to make the field instance null, so that the only thing that
     needs to be updated the database.
###############################################################################
 */


package creative.developer.m.studymanager.model.modelCoordinators;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.model.EntityListFiles.NoteList;
import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.NoteEntity;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.PhotoNoteEntity;

public class NoteCoordinator extends Observable {

    private static NoteCoordinator instance; // this class applies Singleton.
    // When adding a row in the database, the row's id is 1 + lastId, so that ids are generated by
    // this class instead of the database. It would behave like AUTO_INCREMENT.
    private int lastIDNotes;
    private int lastIDPhotos; // sikilar to lastIDNotes.
    private NoteList notesList; // cashed notes
    private HashMap<Integer, List<PhotoNoteEntity>> notePhotos;
    private DataRepository repository;


    /*
     * This method retrieve the only instance of this class
     * @param context is the Context's object of the view model class.
     */
    public static NoteCoordinator getInstance(Context context) {
        if (instance == null) {
            instance = new NoteCoordinator(context);
        }
        return instance;
    }


    /*
    * It constructs the only object of this class
    * @param context is the Context's object of the view model class.
    */
    private NoteCoordinator(Context context) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int[] lastIDPhotoTemp = {0};
            repository = DataRepository.getInstance(AppDatabase.getInstance(context));
            notesList = new NoteList(repository.getNotes());
            notePhotos = repository.getPhotos(lastIDPhotoTemp);
            lastIDPhotos = lastIDPhotoTemp[0];
            lastIDNotes = notesList.getLastID();
            NoteCoordinator.this.setChanged();
            NoteCoordinator.this.notifyObservers();
        });
    }


    // getter for the field notesList that helps to avoid the race condition
    public NoteList getNotesList() {
        return notesList;
    }

    /*
    * It returns the list of lessons' string for the given course.
    * @param course is the string of the course's name
    * @return list of strings for the lessons' name
    */
    public List<String> getLessonsList(String course) {
        return notesList.getLessonsCourse(course);
    }


    /*
    * It returns the lesson object for the given course and lesson names.
    * @param course is the string for the course's name
    * @param lesson is the string for the lesson's name
    */
    public NoteEntity getLessonNote(String course, String lesson) {
        return notesList.getNote(course, lesson);
    }

    /*
    * It returns the photos' uri associated with the lesson.
    * @param course is the string for the course's name
    * @param lesson is the string for the lesson's name
    */
    public List<String> getLessonPhotosUrls(String course, String lesson) {
        List<String> uris = new ArrayList<>();
        if (notePhotos.get(getLessonNote(course, lesson).getNoteID()) != null) {
            for (PhotoNoteEntity photo : notePhotos.get(getLessonNote(course, lesson).getNoteID()))
                uris.add(photo.getPhotoUrl());
        }
        return uris;
    }

    /*
    * It adds a note to the database and notesList.
    * @param course is the string for the course's name
    * @param lesson is the string for the lesson's name
    * @param notes is the inputted notes by the user.
    * @param photosUrls is list of string that stores photos urls.
    */
    public void addNote(String course, String lesson, String notes, List<String> photosUris) {
        lastIDNotes++;
        NoteEntity addedNote = new NoteEntity(lastIDNotes, course, lesson, notes);
        notesList.addNote(addedNote);

        List<PhotoNoteEntity> photos = new ArrayList<>();
        for(String url: photosUris) {
            lastIDPhotos++;
            photos.add(new PhotoNoteEntity(lastIDPhotos, url, lastIDNotes));
        }
        notePhotos.put(lastIDNotes, new ArrayList<>());
        for (PhotoNoteEntity photo : photos) {
            notePhotos.get(lastIDNotes).add(photo);
        }
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> repository.addNote(addedNote, photos));
    }

    /*
    * It updates the note object on the database and notesList
    * @param updated is the object version after the modification.
    */
    public void updateNote(NoteEntity updated) {
        System.out.println("in update");
        notesList.updateNote(updated);
        System.out.println("finish cashed");
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            repository.updateNote(updated);
            System.out.println("finish db");
        });
    }


    /*
    * It removes the note object from the database and notesList
     * @param course is the string for the course's name
     * @param lesson is the string for the lesson's name
    */
    public void removeNote(String course, String lesson) {
        NoteEntity removed = notesList.removeNote(course, lesson);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> repository.deleteNote(removed));
    }

    /*
     * It adds the given photo to the note whose id is given
     * @param photoUri is the uri of where the photo is stored in memory
     * @param noteID is the unique id for NoteEntity's instance
     */
    public void addPhoto(String photoUri, int noteID) {
        lastIDPhotos++;
        PhotoNoteEntity added = new PhotoNoteEntity(lastIDPhotos, photoUri, noteID);
        notePhotos.get(noteID).add(added);
        System.out.println("NoteCoord adding photo");
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> repository.addPhotoNote(added));
    }

    /*
    * It removes the given photo from the given note
    * @param photoUri is the uri of where the photo is stored in memory
    * @param noteID is the unique id for NoteEntity's instance
    */
    public void removePhoto(String photoUri, int noteID) {
        int removeIndex = -1;
        for (int i = 0; i < notePhotos.get(noteID).size(); i++) {
            if (notePhotos.get(noteID).get(i).getPhotoUrl().equals(photoUri)) {
                removeIndex = i;
            }
        }
        if (removeIndex != -1) {
            PhotoNoteEntity removed = notePhotos.get(noteID).remove(removeIndex);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> repository.deletePhotoNote(removed));
        }
    }

    /*
     * It's called to make the instance null, which forces this class to use an updated version of
     * data from the database
     */
    public static void nullifyInstance() {
        instance = null;
    }

}
