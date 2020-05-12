/*
###############################################################################
Author: Mohammed Alghamdi
Class name : RemarksList
purpose: This is a model class that handles storing remarks objects in a data structure.
Methods:
    getRemarksList () -> getter for the field remarksList
    addRemark() -> it adds a remark object to remarksList with maintaining the sorted order.
    removeRemark(deleted) -> it removes a remark object from remarksList
    updateRemark(updated) -> it updates a remark object from remarksList
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;

import java.util.Collections;
import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;

public class RemarksList {

    private List<RemarkEntity> remarksList; // store all remarks
    private int lastID; // last id number for a row; this variable helps simulates AUTO_INCREMENT.

    public RemarksList(List<RemarkEntity> recievedList) {
        this.remarksList = recievedList;
        Collections.sort(recievedList); // sorts based on hours and minutes.
        for (RemarkEntity remrk : recievedList) {
            lastID = Math.max(lastID, remrk.getRemarkID());
        }
    }


    // this is a getter for the field lastID
    public int getLastID() {return lastID;}

    // this is a getter for the field remarksList
    public List<RemarkEntity> getRemarksList () {
        return this.remarksList;
    }

    /*
    * It adds the remark object to remarksList with maintaining the sorted order.
    * @param added is the object that will be added.
    */
    public void addRemark(RemarkEntity added) {
        // performing binary search to find the correct index for insertion.
        int index = (int) Math.floor(remarksList.size() / 2);
        boolean isLess = false, isGreater = false;
        while (isGreater && isLess) {
            // check the right number if it's greater.
            if ((remarksList.size() < index + 1 ||
                    remarksList.get(index).compareTo(remarksList.get(index + 1)) <= 0)) {
                isGreater = true;
            } else {
                isGreater = false;
            }
            // check the left number if it's less.
            if ((0 == index ||
                    remarksList.get(index).compareTo(remarksList.get(index - 1)) >= 0)) {
                isLess = true;
            } else {
                isLess = false;
            }
            //move to the right half if the right number is not greater
            if (!isGreater) {
                index = (int) Math.floor((remarksList.size() + index) / 2);
            } else if (!isLess) { // move to the left if the
                index = (int) Math.floor((0 + index) / 2);
            }
        }

        remarksList.add(index, added);

    }

    // this method removes the given remark object from the remarksList
    public void removeRemark (RemarkEntity deleted) {
        for (int i = 0; i < remarksList.size(); i++) {
            if (deleted.getRemarkID() == remarksList.get(i).getRemarkID()){
                remarksList.remove(i);
            }
        }
    }

    // this method update the remark, and it uses remark's id on searching
    public void updateRemark(RemarkEntity updated){
        for (int i = 0; i < remarksList.size(); i++) {
            if (remarksList.get(i).getRemarkID() == updated.getRemarkID()) {
                remarksList.set(i, updated);
            }
        }
    }
}
