package cours_android.intech.cat_quizz;

public class State {
    private int questionNumber;
    private int catAfection;
    private int score;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getCatAfection() {
        return catAfection;
    }

    public void setCatAfection(int catAfection) {
        this.catAfection = catAfection;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
