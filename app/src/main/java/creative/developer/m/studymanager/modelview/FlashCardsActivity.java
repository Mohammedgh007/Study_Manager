/*
###############################################################################
Author: Mohammed Alghamdi
Class name : FlashCardsActivity
purpose: This is model view class that is responsible for flash cards activity
  interaction with the user.
Methods:
  onCreate() -> It encapsulates/manages all the interaction.
  onActivityResult() -> It receives the intent from AddFlashCardsActivity that
     holds the data of the modified flash cards.
  showQuestion() -> this method show the question on cardTV.
###############################################################################
 */


package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;

public class FlashCardsActivity extends Activity {

    // declaring view
    private ScrollView backgroundCard;
    private TextView cardTV;
    private Button prevBtn;
    private Button flipBtn;
    private Button nextBtn;
    private Button editBtn;
    private Button finishBtn;
    private Button restartBtn;

    private final int EDIT_CODE = 42; // used for startActivityForResult()
    private int currCardIndex = 0; // the index of the card
    private boolean isQuestion = true; // it is true when the card is showing the question side.
    static FlashCardEntity[] recievedCards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flash_cards);

        // initializing views
        backgroundCard = findViewById(R.id.card_background);
        cardTV = findViewById(R.id.card_textview_flash_cards);
        prevBtn = findViewById(R.id.prev_flash_card);
        flipBtn = findViewById(R.id.flip_flash_card);
        nextBtn = findViewById(R.id.next_flash_card);
        editBtn = findViewById(R.id.edit_flash_cards);
        finishBtn = findViewById(R.id.finish_flash_cards);
        restartBtn = findViewById(R.id.restart_flash_card);

        // recieving cards from another CoursesActivity
        recievedCards = CoursesActivity.getSentCards();
        Collections.shuffle(Arrays.asList(recievedCards)); // to shuffle cards


        // filling cardTV with the text of the first question.
        showQuestion();


        // click event handling which showing the next card question
        nextBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            // determining the next card number if current is not the last.
            if (currCardIndex + 1 != recievedCards.length) {
                currCardIndex++;
            } else {
                Toast.makeText(getBaseContext(), "This is the last card; you can press restart"
                        , Toast.LENGTH_LONG).show();
            }

            showQuestion();
            btn.setClickable(true);
        });


        // click event handling which is showing the previous card question
        prevBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            // determining the previous card number if current is not the first.
            if (currCardIndex != 0) {
                currCardIndex--;
            } else {
                Toast.makeText(getBaseContext(), "This is the first card.", Toast.LENGTH_LONG).show();
            }

            showQuestion();
            btn.setClickable(true);
        });


        // click event handling which is showing the answer side of the card
        flipBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            if (isQuestion) {
                // changing the card background
                backgroundCard.setBackgroundResource(R.color.card_answer);

                // creating the shown text
                String showedText = "card " + (currCardIndex + 1) + " out of " + recievedCards.length
                        + "\n\n";
                showedText += recievedCards[currCardIndex].getAnswer();
                cardTV.setText(showedText);
                isQuestion = false;
            } else {
                showQuestion();
            }

            btn.setClickable(true);
        });


        // click event handling which restart showing the cards after shuffling them again.
        restartBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            Collections.shuffle(Arrays.asList(recievedCards));
            currCardIndex = 0;
            showQuestion();
            isQuestion = true;

            btn.setClickable(true);
        });


        // click event handling which is taking the user to AddFlashCard to edit the lesson's cards
        editBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            Intent editIntent = new Intent(FlashCardsActivity.this,
                    AddFlashCardActivity.class);
            editIntent.putExtra("purpose", "editing");
            startActivityForResult(editIntent, EDIT_CODE);

            btn.setClickable(true);
        });


        // click event handling which is closing this activity to go back to CoursesActivity
        finishBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);
            Intent intent = new Intent(FlashCardsActivity.this, CoursesActivity.class);
            intent.putExtra("finalDistanation", "cards");
            startActivity(intent);
            btn.setClickable(true);
        });
    }


    // this method show the question on cardTV
    private void showQuestion() {
        // changing the card background
        backgroundCard.setBackgroundResource(R.color.card_question);

        // creating the shown text
        String showedText = "card " + (currCardIndex + 1) + " out of " + recievedCards.length
                + "\n\n";
        showedText += recievedCards[currCardIndex].getQuestion();
        cardTV.setText(showedText);

        isQuestion = true;
    }

    // this method handles receiving the sent data by AddFlashActivity
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_CODE) {
            // saving the change on lesson's flash cards
            recievedCards = AddFlashCardActivity.getCreatedCards(null);
            CoursesActivity.updateSentCards(recievedCards, AddFlashCardActivity.getDeletedCards());
            // showing the appropriate card
            currCardIndex = 0;
            showQuestion();
        }
    }
}
