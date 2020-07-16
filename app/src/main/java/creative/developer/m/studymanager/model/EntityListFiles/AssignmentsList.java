/*
###############################################################################
Author: Mohammed Alghamdi
Class name : AssignmentList
purpose: This is a model class that handles storing assignemnts objects in a data structure.
Methods:
    add (added) -> it adds added to organizedAssignments.
    getAll () -> getter for the field organizedAssignments
    remove(removed) -> it removes an assignment object from organizedAssignments
    update(outdated, update) -> it updates an assignment object from organizedAssignments
    getLastID() -> getter for lastID.
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.AssignmentEntity;

public class AssignmentsList {


    // used for createAssignemntsView() in AssignmentActivity because it separates based on days.
    // Also, it sorts assignments based on their deadlines.
    private TreeMap<String, ArrayList<AssignmentEntity>> organizedAssignments;
    private int lastID;

    // this constructors assigns the field organizedAssignments.
    public AssignmentsList(List<AssignmentEntity> assignemntsList) {
        this.organizedAssignments = null;
        lastID = 1;
        setAssignments(assignemntsList);
    }

    /*
    this method assigns the value of assignment with data. Next, it uses assignments to compute
    organizedAssignments that is used to store the assignments' object in a organized way.
    @PARAM: data is the retrieved list of assignments from the data base.
     */
    private void setAssignments (List<AssignmentEntity> data) {

        // assignments will be organized in a nested structure. Outer one(sortedMap) will store
        // each element as list of assignments that have the same dueDate.
        organizedAssignments = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return AssignmentEntity.getDateVal(s).compareTo(AssignmentEntity.getDateVal(t1));
            }
        });
        int i = 0;
        AssignmentEntity assignment;
        while (i < data.size()) {
            assignment = data.get(i);
            lastID = Math.max(lastID, assignment.getAssignmentID());

            if (!organizedAssignments.containsKey(assignment.getDueDate())) {
                organizedAssignments.put(assignment.getDueDate(), new ArrayList<>());
            }
            organizedAssignments.get(assignment.getDueDate()).add(assignment);
            i++;
        }

        // sorting inner structure, which assignments within the same day.
        for (ArrayList<AssignmentEntity> list : organizedAssignments.values()) {
            Collections.sort(list);
        }

    }

    /*
    * it adds the given assignment to organizedAssignment
    * @param added is the added assignment.
    */
    public void add(AssignmentEntity added) {
        System.out.println("testing dueDate " +  added.getDueDate());
        System.out.println("dates " + organizedAssignments.keySet());
        if (!organizedAssignments.containsKey(added.getDueDate())) {
            organizedAssignments.put(added.getDueDate(), new ArrayList<>());
        }
        organizedAssignments.get(added.getDueDate()).add(added);
    }

    // this is a getter for the field organizedAssignments
    public TreeMap<String, ArrayList<AssignmentEntity>> getAll () {
        return this.organizedAssignments;
    }

    // this method remove the given assignment from organizedAssignments
    public void remove (AssignmentEntity removed) {
        organizedAssignments.get(removed.getDueDate()).remove(removed);
        if (organizedAssignments.get(removed.getDueDate()).isEmpty()) {
            organizedAssignments.remove(removed.getDueDate());
        }
    }

    // this method update the given assignment at organizedAssignment.
    public void update(AssignmentEntity outdated, AssignmentEntity updated) {
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


    // this is a getter for the field lastID
    public int getLastID() {return lastID;}
}
