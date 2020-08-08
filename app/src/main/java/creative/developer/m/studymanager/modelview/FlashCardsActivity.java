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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import creative.developer.m.studymanager.R;
import creative.developer.m.studymanager.model.dbFiles.EntityFiles.FlashCardEntity;
import creative.developer.m.studymanager.model.modelCoordinators.FlashCardCoordinator;

public class FlashCardsActivity extends AppCompatActivity {

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
    private List<FlashCardEntity> recievedCards;
    private String courseName;
    private String lessonName;


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

        // accessing the model to get the cards
        Intent recvIntent = this.getIntent();
        courseName = recvIntent.getStringExtra("course");
        lessonName = recvIntent.getStringExtra("lesson");
        FlashCardCoordinator model = FlashCardCoordinator.getInstance(this);
        recievedCards = model.getLessonCards(courseName, lessonName);
        Collections.shuffle(recievedCards); // to shuffle cards


        // filling cardTV with the text of the first question.
        showQuestion("flip");


        // click event handling which showing the next card question
        nextBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            // determining the next card number if current is not the last.
            if (currCardIndex + 1 != recievedCards.size()) {
                currCardIndex++;
                showQuestion("slide down");
            } else {
                showQuestion("shake");
            }

            btn.setClickable(true);
        });


        // click event handling which is showing the previous card question
        prevBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            // determining the previous card number if current is not the first.
            if (currCardIndex != 0) {
                currCardIndex--;
                showQuestion("slide up");
            } else {
                showQuestion("shake");
            }

            btn.setClickable(true);
        });


        // click event handling which is showing the answer side of the card
        flipBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            if (isQuestion) {
                // changing the card background
                backgroundCard.setBackgroundResource(R.color.card_answer);

                // creating the shown text
                String showedText = getResources().getString(R.string.card) + " " + (currCardIndex + 1) + " " +
                        getResources().getString(R.string.outOf) + " " + recievedCards.size()
                        + "\n\n" + recievedCards.get(currCardIndex).getAnswer();

                //preparing animations.
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(backgroundCard,
                        "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(backgroundCard,
                        "scaleX", 0f, 1f);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        cardTV.setText(showedText);
                        oa2.start();
                    }
                });
                oa1.start();

                isQuestion = false;
            } else {
                showQuestion("flip");
            }

            btn.setClickable(true);
        });


        // click event handling which restart showing the cards after shuffling them again.
        restartBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            Collections.shuffle(Arrays.asList(recievedCards));
            currCardIndex = 0;
            showQuestion("flip");
            isQuestion = true;

            btn.setClickable(true);
        });


        // click event handling which is taking the user to AddFlashCard to edit the lesson's cards
        editBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);

            Intent editIntent = new Intent(FlashCardsActivity.this,
                    AddFlashCardActivity.class);
            editIntent.putExtra("purpose", "editing");
            editIntent.putExtra("course", recvIntent.getStringExtra("course"));
            editIntent.putExtra("lesson", recvIntent.getStringExtra("lesson"));
            startActivityForResult(editIntent, EDIT_CODE);

            btn.setClickable(true);
        });


        // click event handling which is closing this activity to go back to CoursesActivity
        finishBtn.setOnClickListener((btn) -> {
            btn.setClickable(false);
            Intent intent = new Intent(FlashCardsActivity.this, MainActivity.class);
            intent.putExtra("finalDistanation", "cards");
            startActivity(intent);
            btn.setClickable(true);
            finish();
        });
    }


    // let the app launches CourseActivity via MainActivity instead of closing the whole app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FlashCardsActivity.this, MainActivity.class);
        intent.putExtra("finalDistanation", "cards");
        startActivity(intent);
        finish();
    }


    // this method show the question on cardTV
    // animType is either "flip", "slide up", "slide down", or "shake".
    private void showQuestion(String animType) {
        // changing the card background
        backgroundCard.setBackgroundResource(R.color.card_question);

        // creating the shown text
        String showedText = getResources().getString(R.string.card) + " " + (currCardIndex + 1) + " " +
                getResources().getString(R.string.outOf) + " " + recievedCards.size()
                + "\n\n" + recievedCards.get(currCardIndex).getQuestion();

        //preparing animations
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (animType.equals("flip")){ // for flipping the card
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(backgroundCard,
                    "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(backgroundCard,
                    "scaleX", 0f, 1f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.setDuration(300);
            oa2.setDuration(300);
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardTV.setText(showedText);
                    oa2.start();
                }
            });
            oa1.start();
        // for going to another card
        } else if (animType.equals("slide down") || animType.equals("slide up")) {
            ObjectAnimator translation1;
            ObjectAnimator translation2;
            if (animType.equals("slide down")) {
                translation1 = ObjectAnimator.ofFloat(backgroundCard,
                        "translationY", 0, -screenHeight);
                translation2 = ObjectAnimator.ofFloat(backgroundCard,
                        "translationY", 0);
            } else {
                translation1 = ObjectAnimator.ofFloat(backgroundCard,
                        "translationY", 0, 2 * screenHeight);
                translation2 = ObjectAnimator.ofFloat(backgroundCard,
                        "translationY", 0);
            }
            translation1.setInterpolator(new DecelerateInterpolator());
            translation2.setInterpolator(new DecelerateInterpolator());
            translation1.setDuration(500);
            translation2.setDuration(3);
            translation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    backgroundCard.setVisibility(View.INVISIBLE);
                    translation2.start();
                }
            });
            translation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardTV.setText(showedText);
                    backgroundCard.setVisibility(View.VISIBLE);
                }
            });
            translation1.start();
        } else { // for showing the user that there's no more cards to the right or left.
            ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(backgroundCard,
                    "translationY", 0, screenHeight / 7, 0, -screenHeight / 7, 0);
            shakeAnim.setInterpolator(new DecelerateInterpolator());
            shakeAnim.setDuration(500);
            shakeAnim.start();
        }

        isQuestion = true;
    }

    // this method handles receiving the sent data by AddFlashActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_CODE) {
            // saving the change on lesson's flash cards
            Toast.makeText(getBaseContext(),
                    getResources().getString(R.string.cardEdited), Toast.LENGTH_LONG).show();
            FlashCardCoordinator model = FlashCardCoordinator.getInstance(this);
            recievedCards = model.getLessonCards(courseName, lessonName);
            // showing the appropriate card
            currCardIndex = 0;
            showQuestion("flip");
        }
    }


}
