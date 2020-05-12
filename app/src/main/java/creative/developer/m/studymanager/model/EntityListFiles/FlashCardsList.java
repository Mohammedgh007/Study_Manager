/*
###############################################################################
Author: Mohammed Alghamdi
Class name : FlashCardsList
purpose: This is a model class that handles storing cards objects.
Methods:
    addCard(addedCards) -> it adds a card object to cardsMap.
    removeCard(removed) -> it removes the given card.
    removeLesson(course, lesson) -> It removes all the cards associated with the given course and
        lesson.
    updatedCard(updated) -> It updates the given card on cardsMap.
    containLesson(course, lesson) -> it returns true if the lesson exist.
    getCourseLessons(course) -> it returns string of the course.
    getLessonCards(course, lesson) -> it returns the card objects of the given course and lesson.
    getLastID() -> It's getter for the field lastID
###############################################################################
 */

package creative.developer.m.studymanager.model.EntityListFiles;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import creative.developer.m.studymanager.model.dbFiles.DataRepository;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;

public class FlashCardsList {

    // used to manage cards. It stores as Map<courseStr, Map<LessonStr, List<FlashCardEntity> > >
    private HashMap<String, TreeMap<String, List<FlashCardEntity>>> cardsMap;
    private int lastID;


    public FlashCardsList(List<FlashCardEntity> receivedCards) {
        cardsMap = new HashMap<>();
        for (FlashCardEntity card : receivedCards) {
            // checking existing of the course the outter map
            if (!cardsMap.containsKey(card.getCourse())) {
                cardsMap.put(card.getCourse(), new TreeMap<>());
            }
            // checking existing of the lesson on an inner map
            if (!cardsMap.get(card.getCourse()).containsKey(card.getLesson())) {
                cardsMap.get(card.getCourse()).put(card.getLesson(), new ArrayList<>());
            }
            // adding the object
            cardsMap.get(card.getCourse()).get(card.getLesson()).add(card);

            lastID = Math.max(lastID, card.getCardID());
        }
    }

    // this is a getter for the field lastID
    public int getLastID() {return lastID;}

    /*
    * It adds a card to cardsMap
    * @param added is the card that will be added.
    */
    public void addCard(FlashCardEntity added) {
        String course = added.getCourse();
        String lesson = added.getLesson();
        if (!cardsMap.containsKey(course)) {
            cardsMap.put(course, new TreeMap<>());
        }
        if (!cardsMap.get(course).containsKey(lesson)) {
            cardsMap.get(course).put(lesson, new ArrayList<>());
        }
        cardsMap.get(course).get(lesson).add(added);
    }

    /*
    * It removes all the flash card from cardsMap.
    * @param course is the course's name.
    * @param lesson is the lesson's name.
    */
    public void removecard(FlashCardEntity removed) {
        String course = removed.getCourse();
        String lesson = removed.getLesson();
        int i = 0;
        for (FlashCardEntity card : cardsMap.get(course).get(lesson)) {
            if (card.getCardID() == removed.getCardID()) {
                break;
            }
            i++;
        }
        cardsMap.get(course).get(lesson).remove(i);
    }

    /*
    * It removes all the cards associated with the given lesson from cardsMap.
    * @param course is the course's name.
    * @param lesson is the lesson's name.
    * @return the removed cards.
    */
    public List<FlashCardEntity> removeLesson(String course, String lesson) {
        return cardsMap.get(course).remove(lesson);
    }


    /*
    * It updates the lesson's card on cardsMap
    * @param updatedCards are the cards' objects after modification.
    * @pre-condition the method assume the default value ""
    */
    public void updateLesson(List<FlashCardEntity> updatedCards) {
        String course = updatedCards.get(0).getCourse();
        String lesson = updatedCards.get(0).getLesson();
        // removing the old version
        if (cardsMap.get(course) == null) {
            cardsMap.remove("");
        } else if (cardsMap.get(course).get(lesson) == null ) {
            cardsMap.get(course).remove("");
        } else {
            cardsMap.get(course).remove(lesson);
        }

        // adding the updated version.
        for (FlashCardEntity card : updatedCards) {
            addCard(card);
        }
    }


    /*
    * It returns true if the lesson exist for the given course.
    * @param course is the course's name.
    * @param lesson is the lesson's name.
    */
    public boolean containLesson(String course, String lesson) {
        if (cardsMap.containsKey(course)) {
            return cardsMap.get(course).containsKey(lesson);
        }
        return false;
    }


    /*
    * It returns the card objects of the given lesson and course.
    * @param course is the course's name.
    * @param lesson is the lesson's name.
    */
    public List<FlashCardEntity> getLessonCards (String course, String lesson) {
        return cardsMap.get(course).get(lesson);
    }


    /*
    * It returns the lessons' names of the given course.
    * @param course is the course's name.
    */
    public List<String> getCourseLessons (String course) {
        List<String> lessons = new ArrayList<>();
        if (cardsMap.get(course) != null) {
            for (String lesson : cardsMap.get(course).keySet()) {
                lessons.add(lesson);
            }
        }
        return lessons;
    }

}
