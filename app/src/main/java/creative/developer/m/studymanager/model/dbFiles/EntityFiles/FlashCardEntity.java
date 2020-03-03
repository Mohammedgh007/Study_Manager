/*
###############################################################################
Author: Mohammed Alghamdi
Class name : FlashCardEntity
purpose: This is a model class that is used to represent a single flash card
    as an object and as a row on a database table called FlashCardEntity
###############################################################################
 */

package creative.developer.m.studymanager.model.dbFiles.EntityFiles;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = { @ForeignKey(entity =  CourseEntity.class,
        parentColumns = "name", childColumns = "course", onDelete = ForeignKey.CASCADE),
        @ForeignKey (entity =  LessonEntity.class,
        parentColumns = "name", childColumns = "lesson", onDelete = ForeignKey.CASCADE)})
public class FlashCardEntity {

    // fields as a obj or columns on the database
    @PrimaryKey(autoGenerate = true)
    private int cardID;
    private String course;
    private String lesson;
    private String question;
    private String answer;

    public FlashCardEntity(String course, String lesson, String question, String answer) {
        this.course = course;
        this.lesson = lesson;
        this.question = question;
        this.answer = answer;
    }


    // getters
    public int getCardID() { return cardID; }

    public String getCourse() { return course; }

    public String getLesson() { return lesson; }

    public String getQuestion() { return question; }

    public String getAnswer() { return answer; }


    // setters
    public void setCardID(int cardID) { this.cardID = cardID; }

    public void setCourse(String course) { this.course = course; }

    public void setLesson(String lesson) { this.lesson = lesson; }

    public void setQuestion(String question) { this.question = question; }

    public void setAnswer(String answer) { this.answer = answer; }
}
