package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NaiveBayes {

    private final Map<String, Integer> spamWordCounts;
    private final Map<String, Integer> hamWordCounts;
    private final Set<String> vocabulary;

    private int spamMessageCount;
    private int hamMessageCount;
    private int totalSpamWords;
    private int totalHamWords;
    private boolean trained;

    /**
     * Stores the latest evaluation report after calling evaluate().
     */
    private EvaluationResult lastEvaluation;

    /**
     * Constructs an empty Naive Bayes classifier.
     */
    public NaiveBayes() {
        this.spamWordCounts = new HashMap<>();
        this.hamWordCounts = new HashMap<>();
        this.vocabulary = new HashSet<>();
        this.spamMessageCount = 0;
        this.hamMessageCount = 0;
        this.totalSpamWords = 0;
        this.totalHamWords = 0;
        this.trained = false;
        this.lastEvaluation = null;
    }

    /**
     * Trains the classifier on tokenized messages and their labels.
     *
     * @param tokenizedMessages List of token lists
     * @param labels            List of labels ("spam" or "ham")
     */
    public void train(List<List<String>> tokenizedMessages, List<String> labels) {
        resetModel();

        for (int i = 0; i < tokenizedMessages.size(); i++) {
            List<String> tokens = tokenizedMessages.get(i);
            String label = labels.get(i).toLowerCase();

            if (label.equals("spam")) {
                spamMessageCount++;
                for (String token : tokens) {
                    spamWordCounts.put(token, spamWordCounts.getOrDefault(token, 0) + 1);
                    vocabulary.add(token);
                    totalSpamWords++;
                }
            } else if (label.equals("ham")) {
                hamMessageCount++;
                for (String token : tokens) {
                    hamWordCounts.put(token, hamWordCounts.getOrDefault(token, 0) + 1);
                    vocabulary.add(token);
                    totalHamWords++;
                }
            }
        }

        trained = true;
    }

    /**
     * Predicts whether a tokenized message is spam or ham using
     * log probabilities and Laplace smoothing.
     *
     * @param tokens Tokenized input message
     * @return Predicted label: "spam" or "ham"
     */
    public String predict(List<String> tokens) {
        ensureTrained();

        int totalMessages = spamMessageCount + hamMessageCount;
        int vocabularySize = Math.max(vocabulary.size(), 1);

        double logSpamProb = Math.log((double) spamMessageCount / totalMessages);
        double logHamProb = Math.log((double) hamMessageCount / totalMessages);

        for (String token : tokens) {
            int spamCount = spamWordCounts.getOrDefault(token, 0);
            int hamCount = hamWordCounts.getOrDefault(token, 0);

            double tokenGivenSpam = (spamCount + 1.0) / (totalSpamWords + vocabularySize);
            double tokenGivenHam = (hamCount + 1.0) / (totalHamWords + vocabularySize);

            logSpamProb += Math.log(tokenGivenSpam);
            logHamProb += Math.log(tokenGivenHam);
        }

        return logSpamProb > logHamProb ? "spam" : "ham";
    }

    /**
     * Evaluates the classifier on tokenized messages and labels.
     * Calculates accuracy, false positives, and false negatives.
     *
     * @param tokenizedMessages List of token lists
     * @param labels            True labels
     * @return EvaluationResult containing performance metrics
     */
    public EvaluationResult evaluate(List<List<String>> tokenizedMessages, List<String> labels) {
        ensureTrained();

        int correct = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        for (int i = 0; i < tokenizedMessages.size(); i++) {
            String actual = labels.get(i).toLowerCase();
            String predicted = predict(tokenizedMessages.get(i));

            if (predicted.equals(actual)) {
                correct++;
            } else {
                if (predicted.equals("spam") && actual.equals("ham")) {
                    falsePositives++;
                } else if (predicted.equals("ham") && actual.equals("spam")) {
                    falseNegatives++;
                }
            }
        }

        double accuracy = tokenizedMessages.isEmpty()
                ? 0.0
                : (correct * 100.0) / tokenizedMessages.size();

        lastEvaluation = new EvaluationResult(
                tokenizedMessages.size(),
                correct,
                accuracy,
                falsePositives,
                falseNegatives
        );

        return lastEvaluation;
    }

    /**
     * Returns the last saved evaluation report.
     *
     * @return Latest evaluation result, or null if evaluation has not been run
     */
    public EvaluationResult getLastEvaluation() {
        return lastEvaluation;
    }

    /**
     * Returns whether the model has been trained.
     *
     * @return true if trained, otherwise false
     */
    public boolean isTrained() {
        return trained;
    }

    /**
     * Returns the total vocabulary size learned by the model.
     *
     * @return Number of unique tokens in the vocabulary
     */
    public int getVocabularySize() {
        return vocabulary.size();
    }

    /**
     * Returns the number of spam messages seen during training.
     *
     * @return Spam message count
     */
    public int getSpamMessageCount() {
        return spamMessageCount;
    }

    /**
     * Returns the number of ham messages seen during training.
     *
     * @return Ham message count
     */
    public int getHamMessageCount() {
        return hamMessageCount;
    }

    /**
     * Resets all learned values so the model can be retrained from scratch.
     */
    private void resetModel() {
        spamWordCounts.clear();
        hamWordCounts.clear();
        vocabulary.clear();
        spamMessageCount = 0;
        hamMessageCount = 0;
        totalSpamWords = 0;
        totalHamWords = 0;
        trained = false;
        lastEvaluation = null;
    }

    /**
     * Throws an exception if predict/evaluate is called before training.
     */
    private void ensureTrained() {
        if (!trained) {
            throw new IllegalStateException("Model has not been trained yet.");
        }
    }

    /**
     * Immutable result object that stores evaluation metrics.
     *
     * @param totalSamples    Number of tested samples
     * @param correctSamples  Number of correct predictions
     * @param accuracy        Accuracy percentage
     * @param falsePositives  Ham predicted as spam
     * @param falseNegatives  Spam predicted as ham
     */
    public record EvaluationResult(
            int totalSamples,
            int correctSamples,
            double accuracy,
            int falsePositives,
            int falseNegatives
    ) {
    }
}