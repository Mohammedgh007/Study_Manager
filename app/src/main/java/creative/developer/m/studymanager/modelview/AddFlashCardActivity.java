/*
###############################################################################
Class name : AddFlashCardActivity
purpose: This is model view class that is responsible for adding a flash card or editing
   an existing one
Methods:
  onCreate() -> It encapsulates/manages most the interaction.
  addCardBtn() -> It adds a button to the view.
  storeCard() -> It stores the question's and the answer's field for on cards field.
  showCardData(currBtn) -> It fills question's and answer's with the data of the selected card.
  isInputValid() -> It returns boolean value about whether the user have given valid inputs or not.
###############################################################################
 */


package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import creative.developer.m.studymanager.model.EntityListFiles.FlashCardsList;
import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.modelCoordinators.FlashCardCoordinator;


public class AddFlashCardActivity extends Activity {

    // declaring views
    private LinearLayout cardSetLayout; // it contains the cards' buttons
    private HorizontalScrollView scrollViewCards;  // it contains cardSetlayout
    private TextView courseET;
    private EditText lessonET;
    private EditText questionET;
    private EditText answerET;
    private Button card1Btn;
    private Button addCardBtn;
    private Button removeCardBtn;
    private Button finishLessonBtn;
    private Button cancelBtn;

    private Button clicked; // it is used to access the last clicked button.

    // it is used to store all cards' questions and answers. the key is the button text
    private HashMap<String, FlashCardEntity> cards;
    private static int lastAddedIndex; // it is used to keep track of the last added card number
    private int cardsNum; // the number of added cards so far



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_flash_cards);

        String purpose = this.getIntent().getStringExtra("purpose");
        String courseName = this.getIntent().getStringExtra("course");
        String lessonName = null;
        if (!purpose.equals("adding")) {
            lessonName = this.getIntent().getStringExtra("lesson");
        }

        // initializing views
        scrollViewCards = findViewById(R.id.scroll_view_card_add_flash);
        cardSetLayout = findViewById(R.id.cards_set_layout);
        courseET = findViewById(R.id.course_add_flash);
        courseET.setText(courseName);
        lessonET = findViewById(R.id.lesson_add_flash);
        questionET = findViewById(R.id.question_add_flash);
        answerET = findViewById(R.id.answer_add_flash);
        card1Btn = findViewById(R.id.card1);
        addCardBtn = findViewById(R.id.add_card_add_flash);
        removeCardBtn = findViewById(R.id.remove_card_add_flash);
        finishLessonBtn = findViewById(R.id.add_add_flash);
        cancelBtn = findViewById(R.id.cancel_add_flash);
        clicked = card1Btn;

        // initializing the model's instance
        FlashCardCoordinator model = FlashCardCoordinator.getInstance(this);

        // initializing cards with the view appearance.
        cards = new HashMap<>();
        lastAddedIndex = 1;
        if (purpose.equals("adding")) { // adding a lesson
            cards.put(getResources().getString(R.string.card1), model.addCard("", "", "", ""));
            cardsNum = 1;
        } else { // editing a lesson
            // preparing cards and buttons for recieving the data from FlashCardsActivity
            String cardStr = getResources().getString(R.string.card) + " ";
            List<FlashCardEntity> cardsList = model.getLessonCards(courseName, lessonName);
            cardsNum = cardsList.size();
            cards.put(cardStr + lastAddedIndex, cardsList.get(0));
            showCardData(card1Btn);
            for (int i = 1; i < cardsNum; i++) {
                lastAddedIndex++;
                cards.put(cardStr + lastAddedIndex, cardsList.get(i));
                addCardBtn();
            }

            // changing the view for editing to disable editing the lesson's name
            finishLessonBtn.setText(getResources().getString(R.string.finishEdit));
            courseET.setEnabled(false);
            courseET.setText(courseName);
            lessonET.setEnabled(false);
            lessonET.setText(lessonName);
        }

        // click event handling which is adding a button view with its FlashCardEntity
        addCardBtn.setOnClickListener((btn) -> {
            // creating an empty FlashCardEntity
            lastAddedIndex++;
            cardsNum++;
            String id = getResources().getString(R.string.card) + " " + lastAddedIndex;
            cards.put(id, model.addCard("", "", "", ""));
            addCardBtn();
        });


        // click event handling for a card1 button which is showing its content
        card1Btn.setOnClickListener((btn) -> {
            btn.setClickable(false);
            // changing the view text color to distinguish the current card
            clicked.setTextColor(Color.BLACK);
            card1Btn.setTextColor(getResources().getColor(R.color.peach));

            storeCard(); // store question and answer inputs into cards
            clicked = card1Btn;
            showCardData(clicked); // show question's and answer's of select card
            btn.setClickable(true);
        });


        // click event handling which is removing the selected card
        removeCardBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            if (cardsNum >= 2 && clicked != null) { // if the remaining cards are more than 1
                cardsNum--;
                // removing from the view and the model
                model.removecard(cards.remove(clicked.getText().toString()));
                cardSetLayout.removeView(clicked);
                // selecting a current card
                clicked = (Button) cardSetLayout.getChildAt(1);
                showCardData(clicked);
                clicked.setTextColor(getResources().getColor(R.color.peach));
            } else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.cantDeleteLastCard),
                        Toast.LENGTH_LONG).show();
            }

            btn.setClickable(true);
        });


        // click event handling which is cancelling adding/editing a lesson for flash cards
        cancelBtn.setOnClickListener((btn) -> {
            Intent intent;
            if (purpose.equals("adding")) {
                intent = new Intent(AddFlashCardActivity.this,
                        CoursesActivity.class);
            } else {
                intent = new Intent(AddFlashCardActivity.this,
                        FlashCardsActivity.class);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.lessonNotedited),
                        Toast.LENGTH_SHORT).show();
            }
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        });


        // click event handling which is finish adding or editing the lesson
        finishLessonBtn.setOnClickListener((btn) -> {
            // if the user is editing cards, or the input is valid for adding cards.
            if (!purpose.equals("adding") || isInputValid(model.getLessonsList(courseName))) {
                storeCard();// store data of the shown card

                // assigning course and lesson fields for cards
                String lesson = lessonET.getText().toString().toUpperCase().trim();
                for (FlashCardEntity card : cards.values()) {
                    card.setCourse(courseName);
                    card.setLesson(lesson);
                }

                // updating the model
                model.updateLesson(new ArrayList<>(cards.values()));

                // closing the activity
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    // it adds a button to the view
    private void addCardBtn() {
        // initializing the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 10;
        Button newBtn = new Button(this);
        newBtn.setLayoutParams(layoutParams);
        String text = getResources().getString(R.string.card) + " " + lastAddedIndex;
        newBtn.setText(text);

        // adding it to the view
        cardSetLayout.addView(newBtn);

        scrollViewCards.smoothScrollTo(newBtn.getScrollX(), 0); // it shows scrolling panel

        // click event handling
        newBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);
            // changing the view text color to distinguish the current card
            clicked.setTextColor(Color.BLACK);
            newBtn.setTextColor(getResources().getColor(R.color.peach));

            storeCard(); // store question and answer inputs into cards
            clicked = newBtn;
            showCardData(clicked);
            btn.setClickable(true);
        });
    }

    // it captures the inputted text for one card into cards
    private void storeCard() {
        String id = clicked.getText().toString().trim();
        cards.get(id).setQuestion(questionET.getText().toString().trim());
        cards.get(id).setAnswer(answerET.getText().toString().trim());
    }


    // it fills question's and answer's fields with the save text
    private void showCardData(Button currBtn) {
        FlashCardEntity currCard = cards.get(currBtn.getText().toString());
        questionET.setText(currCard.getQuestion());
        answerET.setText(currCard.getAnswer());
    }


    // this method returns true if the user types a valid input on this activity's fields.
    // Also, it provides a feedback for the user in order for providing a valid input
    // @param: lessons stores all lessons' names of the given course.
    private boolean isInputValid(List<String> lessons){
        // check if lesson and course fields are filled
        String course = courseET.getText().toString().toUpperCase().trim();
        String lesson = lessonET.getText().toString().toUpperCase().trim();
        if (lesson.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.fillLessonField),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        // check if the lesson already exist or not
        if (lessons.contains(lesson)) {
            Toast.makeText(this, getResources().getString(R.string.lessonNameRepeated),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


}
