package tutoringproject.breaks;

import org.json.JSONObject;

/**
 * Created by alexlitoiu on 6/4/2015.
 */
public class Question {
    public int questionID;
    public String question;
    public String spokenQuestion;
    public String type;
    public String spokenType;
    public String format;
    public String difficulty;

    public String hint1;
    public String hint2;
    public String hint3;
    public String spokenHint1;
    public String spokenHint2;
    public String spokenHint3;

    public String spokenAnswer;
    public String spokenExplanation;
    public String explanation;
    public String feedback;

    public int value;
    public int numerator;
    public int denominator;
    public int maxTime;

    public Question (JSONObject question){
        try {
            questionID = question.getInt(Questions.KEY_QUESTION_ID);
            this.question = question.getString(Questions.KEY_QUESTION);
            spokenQuestion = question.getString(Questions.KEY_SPOKEN_QUESTION);
            type = question.getString(Questions.KEY_TYPE);
            //spokenType = question.getString(Questions.KEY_SPOKEN_TYPE);
            difficulty = question.getString(Questions.KEY_DIFFICULTY_LEVEL);
            format = Questions.FORMAT_TEXT;//question.getString(Questions.KEY_FORMAT);

            // answer is stored in a Json object
//            JSONObject a = question.getJSONObject(Questions.KEY_ANSWER);
//            // find the answer based on the format
//            if (format.equals(Questions.FORMAT_FRACTION)) {
//                numerator = a.getInt(Questions.KEY_NUMERATOR);
//                denominator = a.getInt(Questions.KEY_DENOMINATOR);
//            } else if (format.equals(Questions.FORMAT_TEXT)) {
//                value = a.getInt(Questions.KEY_VALUE);
//            }

            value = question.getInt(Questions.KEY_ANSWER);

//            JSONObject h = question.getJSONObject(Questions.KEY_HINTS);
            JSONObject m = question.getJSONObject(Questions.KEY_MISTAKES);

            explanation = m.getString((Questions.KEY_EXPLANATION));
            feedback = m.getString((Questions.KEY_FEEDBACK));


//            hint1 = h.getString(Questions.KEY_HINT1);
//            hint2 = h.getString(Questions.KEY_HINT2);
//            hint3 = h.getString(Questions.KEY_HINT3);
//            spokenHint1 = h.getString(Questions.KEY_SPOKENHINT1);
//            spokenHint2 = h.getString(Questions.KEY_SPOKENHINT2);
//            spokenHint3 = h.getString(Questions.KEY_SPOKENHINT3);

            //TODO Parse the explanation from the JSON file
            spokenAnswer = question.getString(Questions.KEY_SPOKEN_ANSWER);
            //explanation = question.getString(Questions.KEY_EXPLANATION);
            //spokenExplanation = question.getString(Questions.KEY_SPOKEN_EXPLANATION);
            maxTime = question.getInt(Questions.KEY_MAX_TIME);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
