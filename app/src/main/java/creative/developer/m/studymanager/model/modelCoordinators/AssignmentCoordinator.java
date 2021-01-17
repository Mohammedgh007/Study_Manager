/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AssignmentCoordinator
purpose: This is a model class that coordinates all model classes that are used by assignments,
  so that assignment view and view model need to interact only with this class. It has an access
  to both versions of the assignment(the database and the cashed assignments).
Precondition: getAssignmentByID() need to be called within another thread.
Methods:
    getInstance() -> returns the only instance of this class. This class implements Singleton.
    getAssignments() -> returns the assignments as a tree map that sorts data based datetime.
    getAssignmentByID(id) -> returns the assignment based on the given id.
    addAssignment() -> it adds the assignment to the database and the assignmentsList.
    updateAssignment() -> it updates the assignment on the database and the assignmentsList.
    removeAssignment() -> it remove the assignment from the database and the assignmentsList
    nullifyInstance() -> It's called to make the field instance null, so that the only thing that
     needs to be updated the database.
###############################################################################
 */

package creative.developer.m.studymanager.model.modelCoordinators;

import android.content.Context;

import java.util.ArrayList;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import creative.developer.m.studymanager.model.EntityListFiles.AssignmentsList;
import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentEntity;

public class AssignmentCoordinator extends Observable {
    // When adding a row in the database, the row's id is 1 + lastId, so that ids are generated by
    // this class instead of the database. It would behave like AUTO_INCREMENT.
    private int lastId;
    private AssignmentsList assignmentsList; // cached data.
    private static AssignmentCoordinator instance = null; // this class implements Singleton design pattern
    private DataRepository repository;


    /*
    * this constructor builds the only instance.
    * @param context is the context's object of the modelview class.
    */
    private AssignmentCoordinator(Context context) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            repository = DataRepository.getInstance(AppDatabase.getInstance(context));
            System.out.println("finished db");
            assignmentsList = new AssignmentsList(repository.getAssignments());
            System.out.println("finished list");
            lastId =  assignmentsList.getLastID();
            AssignmentCoordinator.this.setChanged();
            AssignmentCoordinator.this.notifyObservers();
            System.out.println("finished creating");
        });
    }


    /*
    * This method creates and return THE object/instance of this class.
    * @param context is the context's instance of the modelview class.
    * @return AssignmentCoordinator's object.
    */
    public static AssignmentCoordinator getInstance(Context context) {
        if (instance == null) {
            instance = new AssignmentCoordinator(context);
        }
        return instance;
    }


    // returns the assignments as a tree map.
    public TreeMap<String, ArrayList<AssignmentEntity>> getAssignments() {
        if (assignmentsList == null) {
            return null; // to avoid race condition
        } else {
            return assignmentsList.getAll();
        }
    }

    /*
    * It returns the assignment based on the given ID.
    * @pre-condition: It needs to be called within non-main thread.
    */
    public AssignmentEntity getAssignmentbyID(int id) {
        return repository.getAssignmentByID(id);
    }

    /*
    * It creates assignment object then adds it to the database and assignmentList.
    * @param course is the course's name.
    * @param notificationTime has this format "num d" OR "num h" ; d for day and h for hour
    * @param disc is the user's description for the assignment.
    * @param dueDate has the following format yyyy:mm;dd I used : with ; to facilitate using substring.
    * @param dueTime has the following format "hh:mm" like 14:20
    * @return the created object of the assignment.
    */
    public AssignmentEntity addAssingment(String course, String notificationTime,
                              String disc, String dueDate, String dueTime) {
        lastId++;
        AssignmentEntity added = new AssignmentEntity(lastId, course,
                                                   notificationTime, disc, dueDate, dueTime);
        assignmentsList.add(added);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {repository.addAssignment(added); });
        return added;
    }


    /*
     * It updates the given assignment object.
     * @param outdated is the assignment's version before the modification.
     * @param updated is the assignment's version after the modification.
     */
    public void updateAssignment(AssignmentEntity outdated, AssignmentEntity updated) {
        assignmentsList.update(outdated, updated);
        Executor executer = Executors.newSingleThreadExecutor();
        executer.execute(() -> repository.updateAssignment(updated));
    }


    /*
    * It updates the given assignment object with the given fields.
    * @param outdated is the outdated version of the object.
    * @param course is the course's name.
    * @param notificationTime has this format "num d" OR "num h" ; d for day and h for hour.
    * @param disc is the user's description for the assignment.
    * @param dueDate has the following format yyyy:mm;dd I used : with ; to facilitate using substring.
    * @param dueTime has the following format "hh:mm" like 14:20.
    * @return the updated version of the assignment
    */
    public AssignmentEntity updateAssignment(AssignmentEntity outdated, String course,
                                 String notificationTime,
                                 String disc, String dueDate, String dueTime) {
        AssignmentEntity updated = new AssignmentEntity(outdated.getAssignmentID(), course,
                notificationTime, disc, dueDate, dueTime);
        updateAssignment(outdated, updated);
        return  updated;
    }


    /*
    * It removes the given assignment object.
    * @param removed is the object that will be removed.
    */
    public void removeAssingment(AssignmentEntity removed) {
        assignmentsList.remove(removed);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {repository.deleteAssignment(removed);});
    }


    /*
     * It's called to make the instance null, which forces this class to use an updated version of
     * data from the database
     */
    public static void nullifyInstance() {
        instance = null;
    }

}
