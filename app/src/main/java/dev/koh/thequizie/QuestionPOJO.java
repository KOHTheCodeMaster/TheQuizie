package dev.koh.thequizie;

class QuestionPOJO {

    private int questionId;
    private boolean correctAnswer;

    QuestionPOJO(int questionId, boolean correctAnswer) {
        this.questionId = questionId;
        this.correctAnswer = correctAnswer;
    }

    int getQuestionId() {
        return questionId;
    }

    boolean isCorrectAnswer() {
        return correctAnswer;
    }

}
