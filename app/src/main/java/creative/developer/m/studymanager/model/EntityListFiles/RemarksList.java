/*
###############################################################################
Author: Mohammed Alghamdi
Class name : RemarksList
purpose: This is a model class that handles storing remarks objects in a data structure.
Methods:
    setRemarksList(recievedList) -> assign the value of remarksList then sort it.
    getRemarksList () -> getter for the field remarksList
    removeRemark(deleted) -> it removes a remark object from remarksList
    updateRemark(updated) -> it updates a remark object from remarksList
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.RemarkEntity;

public class RemarksList {

    private List<RemarkEntity> remarksList; // store all remarks
    private DataRepository repository; // used to remove old remarks
    private Context context;

    public RemarksList(Context context) {
        this.context = context;
    }

    // this method assign the value of remarksList with sorting it
    public void setRemarksList (List<RemarkEntity> recievedList) {
        this.remarksList = recievedList;
        Collections.sort(recievedList);
    }

    // this is a getter for the field remarksList
    public List<RemarkEntity> getRemarksList () {
        return this.remarksList;
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
