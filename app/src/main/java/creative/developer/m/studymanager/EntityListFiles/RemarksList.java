package creative.developer.m.studymanager.EntityListFiles;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import creative.developer.m.studymanager.dbFiles.DataRepository;
import creative.developer.m.studymanager.dbFiles.EntityFiles.RemarkEntity;

public class RemarksList {

    private List<RemarkEntity> remarksList; // store all remarks
    private DataRepository repository; // used to remove old remarks
    private Context context;

    public RemarksList(Context context) {
        this.context = context;
    }

    public void setRemarksList (List<RemarkEntity> recievedList) {
        this.remarksList = recievedList;
        Collections.sort(recievedList);
    }

    public List<RemarkEntity> getRemarksList () {
        return this.remarksList;
    }

    public void removeRemark (RemarkEntity deleted) {
        for (int i = 0; i < remarksList.size(); i++) {
            if (deleted.getRemarkID() == remarksList.get(i).getRemarkID()){
                remarksList.remove(i);
            }
        }
    }

    // this method add a remark with maintaing the sorted order
    public void addRemark(RemarkEntity added) {
        if (remarksList.isEmpty()) {
            remarksList.add(added);
        } else {
            boolean isAdded = false;
            int i = 0;
            while (i < remarksList.size() && !isAdded) {
                // comparing dates first then time
                if (added.getDateVal() < remarksList.get(i).getDateVal()) {
                    remarksList.add(i, added);
                    isAdded = true;
                } else if (added.getDateVal() == remarksList.get(i).getDateVal()) {
                    if (added.getTimeVal() <= remarksList.get(i).getTimeVal()) {
                        remarksList.add(i, added);
                        isAdded = true;
                    }
                }
                i++;
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
