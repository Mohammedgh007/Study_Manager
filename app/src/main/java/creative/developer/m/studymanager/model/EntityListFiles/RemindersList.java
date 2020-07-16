/*
###############################################################################
Author: Mohammed Alghamdi
Class name : RemindersList
purpose: This is a model class that handles storing reminders objects in a data structure.
Methods:
    add (added) -> it adds added to reminders list.
    getAll () -> getter for the field reminders that stores all reminders as a List<RemindersEntity>.
    remove(removed) -> it removes an reminder object from reminders list.
    update(update) -> it updates an reminder object from reminders list.
    getLastID() -> getter for lastID.
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;

import java.util.ArrayList;
import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.ReminderEntity;

public class RemindersList {

    private List<ReminderEntity> remindersList;
    private int lastID;

    // this constructor creates an object based on the given list of retrieved reminders.
    public RemindersList(List<ReminderEntity> retrievedReminders) {
        remindersList = new ArrayList<>();
        lastID = 0;
        for(ReminderEntity reminder : retrievedReminders){
            remindersList.add(reminder);
            lastID = Math.max(lastID, reminder.getReminderID());
            System.out.println(lastID);
        }
    }


    /*
    * It appends the given reminder object to the list of reminders.
    * @param added is the reminder object
    */
    public void add(ReminderEntity added) {
        remindersList.add(added);
    }


    /*
    * It returns all the reminders from the reminders' list.
    */
    public List<ReminderEntity> getAll() {return remindersList;}


    /*
    * It removes the given object from the reminders' list.
    * @param removed is the object of ReminderEntity that will be removed.
    */
    public void remove(ReminderEntity removed) {
        for (int i = 0; i < remindersList.size(); i++) {
            if (remindersList.get(i).getReminderID() == removed.getReminderID()) {
                remindersList.remove(i);
                return;
            }
        }
    }


    /*
    * It updates the given in the reminders' list.
    * @param updated is the updated version of the object compared to the version in the list.
    */
    public void update(ReminderEntity updated) {
        for (int i = 0; i < remindersList.size(); i++) {
            if (remindersList.get(i).getReminderID() == updated.getReminderID()) {
                remindersList.set(i, updated);
                return;
            }
        }
    }


    // this is a getter for the field lastID
    public int getLastID() {return lastID;}
}
