/*
###############################################################################
Author: Mohammed Alghamdi
Class name : FlashCardsList
purpose: This is a model class that handles storing cards objects.
Methods:
    setList(recievedCards) -> assign the value of cardsList then assign the value
    of cardsMap, which will be used to manage cards' obejcts, based on it.
    getInstance(): return the instance of the class.
    addLesson(addedCards) -> it adds a group of cards' objects to cardsMap.
    removeLesson(course, lesson) -> it removes a lesson's cards' objects.
    containLesson(course, lesson) -> it returns true if the lesson exist.
    getCoursesSet() -> it returns strings that are courses' names.
    getCourseLessons(course) -> it returns string of the course.
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

    private List<FlashCardEntity> cardsList; // used temporarily to store the incoming data from db.
    // used to manage cards. It stores as Map<courseStr, Map<LessonStr, List<FlashCardEntity> > >
    private HashMap<String, TreeMap<String, List<FlashCardEntity>>> cardsMap;
    private Context context;
    private DataRepository repository; // used to access database
    private static FlashCardsList instance; // to avoid sending heavy object between activities


    public FlashCardsList(Context context) {
        this.cardsList = null;
        this.context = context;
    }

    public static FlashCardsList getInstance() {return instance;}

    public void setList(List<FlashCardEntity> recievedCards) {
        cardsList = recievedCards;

        // building the map for managing the flash cards entities
        cardsMap = new HashMap<>();
        for (FlashCardEntity card : cardsList) {
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
        }
        instance = this;
    }

    public void addLesson(List<FlashCardEntity> addedCards) {
        String course = addedCards.get(0).getCourse();
        String lesson = addedCards.get(0).getLesson();
        System.out.println("lesson in list" + lesson);
        if (!cardsMap.containsKey(course)) {
            cardsMap.put(course, new TreeMap<>());
        }
        cardsMap.get(course).put(lesson, addedCards);
    }

    public void removeLesson(String course, String lesson) {
        cardsMap.get(course).remove(lesson);
    }

    public boolean containLesson(String course, String lesson) {
        if (cardsMap.containsKey(course)) {
            return cardsMap.get(course).containsKey(lesson);
        }
        return false;
    }

    public Set<String> getCoursesSet() {
        return cardsMap.keySet();
    }

    public FlashCardEntity[] getLessonCards (String course, String lesson) {
        FlashCardEntity[] cards = new FlashCardEntity[cardsMap.get(course).get(lesson).size()];
        int i = 0;
        for (FlashCardEntity card : cardsMap.get(course).get(lesson)) {
            cards[i] = card;
            i++;
        }
        return cards;
    }

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
