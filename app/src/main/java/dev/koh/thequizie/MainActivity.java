package dev.koh.thequizie;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button trueBtn;
    private Button falseBtn;
    private TextView questionTextView;
    private TextView scoreTextView;
    private ProgressBar progressBar;

    private int currentQID;
    private int currentProgress;
    private int incrementProgressBar;
    private int currentScore;
    private int totalQuestionCount;
    private boolean isGameOver;
    private boolean isStateChanged;

    private Bundle bundleSavedState;

    private BundleKeys bundleKeys;
    private QuestionPOJO currentQuestion;
    private List<QuestionPOJO> questionsBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bundleSavedState = savedInstanceState;
        major();
    }

    private void major() {

        //  Time Stamp : 26th July 2K19, 07:19 PM..!!

        init();

    }

    private void init() {

        //  Initialize View Elements
        trueBtn = findViewById(R.id.idTrueBtnMA);
        falseBtn = findViewById(R.id.idFalseBtnMA);
        scoreTextView = findViewById(R.id.idScoreTextViewMA);
        progressBar = findViewById(R.id.idProgressBarMA);
        questionTextView = findViewById(R.id.idQuestionTextViewMA);

        initializeOnClickListener();

        if (bundleSavedState != null) restorePreviousState();
        else {
            initializeQuestionBank();
            initializeMemberVariables();
            updateQuestion();
        }

        progressBar.setProgress(currentProgress); //  Set Progress Bar initially to 1
        scoreTextView.setText(String.format(Locale.getDefault(),
                "Score: %d/%d", currentScore, totalQuestionCount));

    }

    private void restorePreviousState() {

        //  Time Stamp : 28th July 2K19, 09:45 PM..!!
        bundleKeys = new BundleKeys();

        isGameOver = bundleSavedState.getBoolean(bundleKeys.KEY_IS_GAME_OVER);
        currentProgress = bundleSavedState.getInt(bundleKeys.KEY_CURRENT_PROGRESS);
        incrementProgressBar = bundleSavedState.getInt(bundleKeys.KEY_INCREMENT_PROGRESS_BAR);
        currentScore = bundleSavedState.getInt(bundleKeys.KEY_CURRENT_SCORE);
        currentQID = bundleSavedState.getInt(bundleKeys.KEY_CURRENT_QUESTION_ID);
        totalQuestionCount = bundleSavedState.getInt(bundleKeys.KEY_TOTAL_QUESTION_COUNT);

        int[] remainingQIDs = bundleSavedState.getIntArray(bundleKeys.KEY_REMAINING_QUESTION_IDS);
        boolean[] remainingCorrectAns = bundleSavedState.getBooleanArray(bundleKeys.KEY_REMAINING_CORRECT_ANS);

        //  Restore Question Bank
        if (remainingQIDs != null && remainingCorrectAns != null) {

            questionsBank = new LinkedList<>();
            currentQuestion = new QuestionPOJO(remainingQIDs[0], remainingCorrectAns[0]);

            for (int i = 0; i < remainingQIDs.length; i++)
                questionsBank.add(new QuestionPOJO(remainingQIDs[i], remainingCorrectAns[i]));

        }

        //  Restore Previous Question
        questionTextView.setText(currentQID);

    }

    private void initializeMemberVariables() {

        isGameOver = false;
        currentScore = 0;
        currentProgress = 1;
        totalQuestionCount = questionsBank.size();
        incrementProgressBar = (int) Math.ceil(100.0 / totalQuestionCount);

    }

    private void initializeQuestionBank() {

        questionsBank = new LinkedList<>();

        questionsBank.add(new QuestionPOJO(R.string.question_1, true));
        questionsBank.add(new QuestionPOJO(R.string.question_2, true));
        questionsBank.add(new QuestionPOJO(R.string.question_3, true));
        questionsBank.add(new QuestionPOJO(R.string.question_4, true));
        questionsBank.add(new QuestionPOJO(R.string.question_5, true));
        questionsBank.add(new QuestionPOJO(R.string.question_6, false));
        questionsBank.add(new QuestionPOJO(R.string.question_7, true));
        questionsBank.add(new QuestionPOJO(R.string.question_8, false));
        questionsBank.add(new QuestionPOJO(R.string.question_9, true));
        questionsBank.add(new QuestionPOJO(R.string.question_10, true));
        questionsBank.add(new QuestionPOJO(R.string.question_11, false));
        questionsBank.add(new QuestionPOJO(R.string.question_12, false));
        questionsBank.add(new QuestionPOJO(R.string.question_13, true));
    }

    private void initializeOnClickListener() {

        View.OnClickListener trueFalseBtnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGameOver) return;
                if (v.getId() == R.id.idTrueBtnMA) checkAnswer(true);
                else if (v.getId() == R.id.idFalseBtnMA) checkAnswer(false);
            }

        };

        trueBtn.setOnClickListener(trueFalseBtnOnClickListener);
        falseBtn.setOnClickListener(trueFalseBtnOnClickListener);
    }

    private void checkAnswer(boolean userSelection) {

        if (currentQuestion.isCorrectAnswer() == userSelection) {

            displayToastMsg(R.string.correct_toast);
            currentScore++;

            //  Update the scoreTextView
            scoreTextView.setText(String.format(Locale.getDefault(),
                    "Score: %d/%d", currentScore, totalQuestionCount));

        } else displayToastMsg(R.string.incorrect_toast);

        if (!isGameOver()) updateQuestion();

    }

    private void updateQuestion() {

        //  Generate Random Number to select a question randomly from the Question Bank
        SecureRandom random = new SecureRandom();
        int questionIndex = random.nextInt(questionsBank.size());
        currentQuestion = questionsBank.get(questionIndex);
        currentQID = currentQuestion.getQuestionId();

        //  Remove the current Question from the Question Bank
        //  so it doesn't appears again in next turn
        questionsBank.remove(questionIndex);

        //  Update the questionTextView with the current question
        //  i.e. random question from the Question Bank
        questionTextView.setText(currentQID);

        //  Increment the progressBar
        progressBar.incrementProgressBy(incrementProgressBar);

    }

    private void showGameCompletionAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.title_gca);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(String.format(Locale.getDefault(),
                "Your Score: %d/%d", currentScore, totalQuestionCount));

        alertDialog.setPositiveButton(R.string.positive_btn_text_gca, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bundleSavedState = null;
                init();
            }
        });

        alertDialog.setNegativeButton(R.string.negative_btn_text_gca, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();

    }

    private boolean isGameOver() {

        if (questionsBank.size() == 0) {
            isGameOver = true;
            displayToastMsg(R.string.toast_msg_gca);
            showGameCompletionAlertDialog();
            return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(bundleKeys.KEY_IS_GAME_OVER, isGameOver);
        outState.putInt(bundleKeys.KEY_INCREMENT_PROGRESS_BAR, incrementProgressBar);
        outState.putInt(bundleKeys.KEY_CURRENT_SCORE, currentScore);
        outState.putInt(bundleKeys.KEY_TOTAL_QUESTION_COUNT, totalQuestionCount);
        outState.putInt(bundleKeys.KEY_CURRENT_QUESTION_ID, currentQID);
        outState.putInt(bundleKeys.KEY_CURRENT_PROGRESS, progressBar.getProgress());

        int qSize = questionsBank.size();
        int[] remainingQIDs = new int[qSize];
        boolean[] remainingCorrectAns = new boolean[qSize];
        for (int i = 0; i < qSize; i++) {
            remainingQIDs[i] = questionsBank.get(i).getQuestionId();
            remainingCorrectAns[i] = questionsBank.get(i).isCorrectAnswer();
        }
        outState.putIntArray(bundleKeys.KEY_REMAINING_QUESTION_IDS, remainingQIDs);
        outState.putBooleanArray(bundleKeys.KEY_REMAINING_CORRECT_ANS, remainingCorrectAns);

        bundleSavedState = outState;

    }

    private void displayToastMsg(int stringID) {
        Toast.makeText(this, stringID, Toast.LENGTH_SHORT).show();
    }

    private class BundleKeys {

        private final String KEY_IS_GAME_OVER = "KEY_GAME_OVER";
        private final String KEY_CURRENT_SCORE = "KEY_CURRENT_SCORE";
        private final String KEY_CURRENT_QUESTION_ID = "KEY_CURRENT_QUESTION_ID";
        private final String KEY_TOTAL_QUESTION_COUNT = "KEY_QUESTION_COUNT";
        private final String KEY_INCREMENT_PROGRESS_BAR = "KEY_INCREMENT_PROGRESS_BAR";
        private final String KEY_CURRENT_PROGRESS = "KEY_CURRENT_PROGRESS";
        private final String KEY_REMAINING_QUESTION_IDS = "KEY_REMAINING_QUESTION_IDS";
        private final String KEY_REMAINING_CORRECT_ANS = "KEY_REMAINING_CORRECT_ANS";

    }

}

/*
 *  Date Created  : 26th July 2K19, 07:19 PM..!!
 *
 *  Init Commit - [The Quizie App]
 *
 *  1. Screen Rotation Event Handled by using bundleSavedState - onSaveStateInstance() method
 *  2. QuestionPOJO Class added which acts as a Model for each Question its Correct Ans.
 *
 *  Code Developed By,
 *  ~K.O.H..!! ^__^
 */
