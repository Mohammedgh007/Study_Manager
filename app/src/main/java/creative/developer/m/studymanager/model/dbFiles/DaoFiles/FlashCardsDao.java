/*
###############################################################################
Author: Mohammed Alghamdi
Interface name : FlashCardsDao
purpose: this is a model file that stores methods that will be used as
 procedures for FlashCardEntity
Methods:
  - getAllCards() -> it returns all fields' data on the table
    as a list<FlashCardEntity>
  - InsertCards(added) -> it adds a field to the the table.
  - updateCards(updated) -> it updates a field/s in the table. The parameter
    is the object after updating.
  - deleteCards(deleted) -> it removes deleted from the table.
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.DaoFiles;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;

@Dao
public interface FlashCardsDao {

    @Query("SELECT * FROM FlashCardEntity")
    List<FlashCardEntity> getAllCards();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCards(FlashCardEntity added);

    @Update
    void updateCards(FlashCardEntity... updated);

    @Delete
    void deleteCards(FlashCardEntity... deleted);
}
