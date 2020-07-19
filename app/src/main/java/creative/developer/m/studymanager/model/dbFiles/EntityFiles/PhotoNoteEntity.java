/*
###############################################################################
Author: Mohammed Alghamdi
Class name : PhotoNoteEntity
purpose: This is a model class that is used to represent a single photo that's attached to a note
    as an object and as a row on a database table called PhotoNoteEntity.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = NoteEntity.class, parentColumns = "noteID",
        childColumns = "noteID", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class PhotoNoteEntity {

    // fields as a obj or columns on the database
    @PrimaryKey
    private int photoID;
    private String photoUrl; // url that locates photo's location in the memory.
    private int noteID;

    public PhotoNoteEntity(int photoID, String photoUrl, int noteID) {
        this.photoID = photoID;
        this.photoUrl = photoUrl;
        this.noteID = noteID;
    }

    ///////////// getters
    public int getPhotoID() {
        return photoID;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getNoteID() {
        return noteID;
    }

    /////// setters

    public void setPhotoID(int photoID) {
        this.photoID = photoID;
    }
}
