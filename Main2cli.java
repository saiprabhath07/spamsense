import data.DataLoader;
import data.DataLoader.EmailRecord;
import model.NaiveBayes;
import model.NaiveBayes.EvaluationResult;
import preprocessing.TextProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main2cli {

    private static final String DATASET_PATH = "data/emails.csv";

    private final Scanner scanner;
    private final DataLoader dataLoader;
    private final TextProcessor textProcessor;
    private final NaiveBayes classifier;

    private List<EmailRecord> records;
    private List<List<String>> tokenizedMessages;
    private List<String> labels;

    /**
     * Creates the application and initializes dependencies.
     */
    public Main() {
        this.scanner = new Scanner(System.in);
        this.dataLoader = new DataLoader();
        this.textProcessor = new TextProcessor();
        this.classifier = new NaiveBayes();
        this.records = new ArrayList<>();
        this.tokenizedMessages = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    /**
     * Starts the console application loop.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> trainModel();
                case 2 -> predictCustomMessage();
                case 3 -> showAccuracyReport();
                case 4 -> {
                    System.out.println("\nExiting SpamSense. Goodbye.");
                    running = false;
                }
                default -> System.out.println("\nInvalid choice. Please select a valid option.");
            }
        }

        scanner.close();
    }

    /**
     * Prints the main menu.
     */
    private void printMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                  SpamSense");
        System.out.println("         Naive Bayes Spam Classifier");
        System.out.println("==================================================");
        System.out.println("1. Train model");
        System.out.println("2. Predict a custom message");
        System.out.println("3. Show accuracy report");
        System.out.println("4. Exit");
        System.out.println("--------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Reads the user's menu choice safely.
     *
     * @return Chosen integer option
     */
    private int readMenuChoice() {
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Loads the dataset, preprocesses the messages, trains the model,
     * and immediately evaluates it on the loaded dataset.
     */
    private void trainModel() {
        try {
            records = dataLoader.loadCSV(DATASET_PATH);

            if (records.isEmpty()) {
                System.out.println("\nNo records found in dataset: " + DATASET_PATH);
                return;
            }

            tokenizedMessages = new ArrayList<>();
            labels = new ArrayList<>();

            for (EmailRecord record : records) {
                tokenizedMessages.add(textProcessor.process(record.message()));
                labels.add(record.label());
            }

            classifier.train(tokenizedMessages, labels);
            EvaluationResult result = classifier.evaluate(tokenizedMessages, labels);

            System.out.println("\nModel trained successfully.");
            System.out.println("Dataset path      : " + DATASET_PATH);
            System.out.println("Total messages    : " + records.size());
            System.out.println("Spam messages     : " + classifier.getSpamMessageCount());
            System.out.println("Ham messages      : " + classifier.getHamMessageCount());
            System.out.println("Vocabulary size   : " + classifier.getVocabularySize());
            System.out.printf("Training accuracy : %.2f%%%n", result.accuracy());

        } catch (IOException e) {
            System.out.println("\nFailed to read dataset: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError while training model: " + e.getMessage());
        }
    }

    /**
     * Takes a custom message from the user, preprocesses it,
     * predicts its class, and displays the result.
     */
    private void predictCustomMessage() {
        if (!classifier.isTrained()) {
            System.out.println("\nPlease train the model first.");
            return;
        }

        System.out.println("\nEnter your message:");
        System.out.print("> ");
        String inputMessage = scanner.nextLine();

        List<String> processedTokens = textProcessor.process(inputMessage);
        String prediction = classifier.predict(processedTokens);

        System.out.println("\nPrediction result");
        System.out.println("-----------------");
        System.out.println("Message    : " + inputMessage);
        System.out.println("Prediction : " + prediction.toUpperCase());
    }

    /**
     * Displays the latest accuracy report, or computes one if needed.
     */
    private void showAccuracyReport() {
        if (!classifier.isTrained()) {
            System.out.println("\nPlease train the model first.");
            return;
        }

        EvaluationResult result = classifier.getLastEvaluation();

        if (result == null) {
            result = classifier.evaluate(tokenizedMessages, labels);
        }

        System.out.println("\nAccuracy report");
        System.out.println("--------------------------------------------------");
        System.out.println("Total samples   : " + result.totalSamples());
        System.out.println("Correct         : " + result.correctSamples());
        System.out.printf("Accuracy        : %.2f%%%n", result.accuracy());
        System.out.println("False positives : " + result.falsePositives());
        System.out.println("False negatives : " + result.falseNegatives());
        System.out.println("--------------------------------------------------");
    }

    /**
     * Program entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        new Main().start();
    }
}
