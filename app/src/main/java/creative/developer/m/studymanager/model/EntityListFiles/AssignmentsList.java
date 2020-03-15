/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AssignmentList
purpose: This is a model class that handles storing assignemnts objects in a data structure.
Methods:
    setAssignments(data) -> assign the value of retrievedAssignments then
       assign the value of organizedAssignments based on it.
    getAssignment () -> getter for the field organizedAssignments
    removeAssignment(removed) -> it removes an assignment object from organizedAssignments
    updateAssignment(outdated, update) -> it updates an assignment object from organizedAssignments
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import creative.developer.m.studymanager.model.dbFiles.AppDatabase;
import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentsEntity;

public class AssignmentsList {

    // used temprarly to receive data from database and for quick random access
    private List<AssignmentsEntity> retrievedAssignments;
    // used for createAssignemntsView() in AssignmentActivity because it separates based on days.
    private TreeMap<String, ArrayList<AssignmentsEntity>> organizedAssignments;
    private DataRepository repository; // used to remove old assignments
    private Context context; // repository will need a context.

    public AssignmentsList(Context context) {
        this.context = context;
        this.retrievedAssignments = null;
    }

    /*
    this method assigns the value of assignment with data. Next, it uses assignments to compute
    organizedAssignments that is used to store the assignments' object in a organized way.
    @PARAM: data is the retrieved list of assignments from the data base.
     */
    public void setAssignments (List<AssignmentsEntity> data) {
        this.retrievedAssignments = data;

        // assignments will be organized in a nested structure. Outer one(sortedMap) will store
        // each element as list of assignments that have the same dueDate.
        organizedAssignments = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return AssignmentsEntity.getDateVal(s).compareTo(AssignmentsEntity.getDateVal(t1));
            }
        });

        repository = DataRepository.getInstance(AppDatabase.getInstance(context));
        Calendar today = Calendar.getInstance();
        Calendar assignmentDate = Calendar.getInstance();
        int i = 0;
        AssignmentsEntity assignment;
        while (i < retrievedAssignments.size()) {
            assignment = retrievedAssignments.get(i);

            // months starts from 0
            assignmentDate.set(assignment.getYearNum(), assignment.getMonthNum() - 1,
                    assignment.getDayNum());
            if (today.after(assignmentDate)){ // this outer if to filter out and remove old assignments
                Executor deletingThread = Executors.newSingleThreadExecutor();
                int index = i;
                System.out.println("size is " + retrievedAssignments.size() + " i= " + i);
                AssignmentsEntity deletedAssignment = assignment;
                deletingThread.execute(() ->  repository.deleteAssignment(deletedAssignment));
                retrievedAssignments.remove(i);
            } else {
                if (!organizedAssignments.containsKey(assignment.getDueDate())) {
                    organizedAssignments.put(assignment.getDueDate(), new ArrayList<>());
                }
                organizedAssignments.get(assignment.getDueDate()).add(assignment);
                i++;
            }
        }

        // sorting inner structure
        for (ArrayList<AssignmentsEntity> list : organizedAssignments.values()) {
            Collections.sort(list);
        }

    }

    // this is a getter for the field organizedAssignments
    public TreeMap<String, ArrayList<AssignmentsEntity>> getAssignments () {
        return this.organizedAssignments;
    }

    // this method remove the given assignment from organizedAssignments
    public void removeAssignment (AssignmentsEntity removed) {
        organizedAssignments.get(removed.getDueDate()).remove(removed);
        if (organizedAssignments.get(removed.getDueDate()).isEmpty()) {
            organizedAssignments.remove(removed.getDueDate());
        }
    }

    // this method update the given assignment at organizedAssignment.
    public void updateAsssignment(AssignmentsEntity outdated, AssignmentsEntity updated) {
        System.out.println("Testing<<<>>> " + organizedAssignments.get(outdated.getDueDate()).size());
        // removing the outdated version from organizedAssignments
        for (int i = 0; i < organizedAssignments.get(outdated.getDueDate()).size(); i++) {
            if (outdated.getAssignmentID() ==
                    organizedAssignments.get(outdated.getDueDate()).get(i).getAssignmentID()) {
                organizedAssignments.get(outdated.getDueDate()).remove(i);
            }
        }
        if (organizedAssignments.get(outdated.getDueDate()).isEmpty()) {
            organizedAssignments.remove(outdated.getDueDate());
        }
        // adding updated version to organizedAssignments
        if (! organizedAssignments.containsKey(updated.getDueDate())) {
            organizedAssignments.put(updated.getDueDate(), new ArrayList<>());
        }
        organizedAssignments.get(updated.getDueDate()).add(updated);
        Collections.sort(organizedAssignments.get(updated.getDueDate()));
    }
}
