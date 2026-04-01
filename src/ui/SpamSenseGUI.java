package ui;

import data.DataLoader;
import data.DataLoader.EmailRecord;
import model.NaiveBayes;
import model.NaiveBayes.EvaluationResult;
import preprocessing.TextProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpamSenseGUI extends JFrame {

    private final DataLoader dataLoader;
    private final TextProcessor textProcessor;
    private final NaiveBayes classifier;

    private final JTextArea messageArea;
    private final JTextArea reportArea;
    private final JLabel resultLabel;
    private final JLabel datasetLabel;
    private final JButton trainButton;
    private final JButton predictButton;
    private final JButton reportButton;
    private final JButton loadButton;
    private final JButton clearButton;
    private final JButton exitButton;

    private String datasetPath;
    private List<EmailRecord> records;
    private List<List<String>> tokenizedMessages;
    private List<String> labels;

    public SpamSenseGUI() {
        this.dataLoader = new DataLoader();
        this.textProcessor = new TextProcessor();
        this.classifier = new NaiveBayes();

        this.datasetPath = "data/emails.csv";
        this.records = new ArrayList<>();
        this.tokenizedMessages = new ArrayList<>();
        this.labels = new ArrayList<>();

        setTitle("SpamSense");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Build layout
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: title and dataset label
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("SpamSense - Spam Classifier");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(title, BorderLayout.NORTH);

        datasetLabel = new JLabel("Dataset: " + datasetPath);
        top.add(datasetLabel, BorderLayout.SOUTH);

        // Center: message area and report area
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel left = new JPanel(new BorderLayout(5, 5));
        JLabel inputLabel = new JLabel("Enter message:");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        messageArea = new JTextArea();
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        JScrollPane messagePane = new JScrollPane(messageArea);

        resultLabel = new JLabel("Prediction: Model not trained");

        left.add(inputLabel, BorderLayout.NORTH);
        left.add(messagePane, BorderLayout.CENTER);
        left.add(resultLabel, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(5, 5));
        JLabel reportLabel = new JLabel("Report:");
        reportLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setText("Load dataset and train the model.");
        JScrollPane reportPane = new JScrollPane(reportArea);

        right.add(reportLabel, BorderLayout.NORTH);
        right.add(reportPane, BorderLayout.CENTER);

        center.add(left);
        center.add(right);

        // Bottom: buttons
        JPanel bottom = new JPanel(new GridLayout(1, 6, 5, 5));

        loadButton = new JButton("Load CSV");
        trainButton = new JButton("Train Model");
        predictButton = new JButton("Predict");
        reportButton = new JButton("Show Report");
        clearButton = new JButton("Clear");
        exitButton = new JButton("Exit");

        bottom.add(loadButton);
        bottom.add(trainButton);
        bottom.add(predictButton);
        bottom.add(reportButton);
        bottom.add(clearButton);
        bottom.add(exitButton);

        // Add to frame
        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        setVisible(true);

        // Attach actions
        loadButton.addActionListener(e -> loadDatasetFromChooser());
        trainButton.addActionListener(e -> trainModel());
        predictButton.addActionListener(e -> predictMessage());
        reportButton.addActionListener(e -> showReport());
        clearButton.addActionListener(e -> clearFields());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void loadDatasetFromChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select CSV file");
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            datasetPath = file.getAbsolutePath();
            datasetLabel.setText("Dataset: " + datasetPath);
            reportArea.setText("Dataset selected. Now click \"Train Model\".");
        }
    }

    private void trainModel() {
        try {
            records = dataLoader.loadCSV(datasetPath);

            if (records.isEmpty()) {
                showError("No valid records found in CSV.");
                return;
            }

            tokenizedMessages.clear();
            labels.clear();

            for (EmailRecord r : records) {
                tokenizedMessages.add(textProcessor.process(r.message()));
                labels.add(r.label());
            }

            classifier.train(tokenizedMessages, labels);
            EvaluationResult eval = classifier.evaluate(tokenizedMessages, labels);

            reportArea.setText(
                "Model trained.\n" +
                "Total messages: " + records.size() + "\n" +
                "Spam: " + classifier.getSpamMessageCount() + "\n" +
                "Ham: " + classifier.getHamMessageCount() + "\n" +
                "Vocabulary: " + classifier.getVocabularySize() + "\n" +
                String.format("Accuracy: %.2f%%\n", eval.accuracy())
            );

            resultLabel.setText("Prediction: Model trained");

        } catch (IOException e) {
            showError("Can't read CSV:\n" + e.getMessage());
        } catch (Exception e) {
            showError("Training error:\n" + e.getMessage());
        }
    }

    private void predictMessage() {
        if (!classifier.isTrained()) {
            showError("Train model first.");
            return;
        }

        String msg = messageArea.getText().trim();
        if (msg.isEmpty()) {
            showError("Enter a message.");
            return;
        }

        String result = classifier.predict(textProcessor.process(msg));
        resultLabel.setText("Prediction: " + result.toUpperCase());
    }

    private void showReport() {
        if (!classifier.isTrained()) {
            showError("Train model first.");
            return;
        }

        EvaluationResult r = classifier.getLastEvaluation();
        if (r == null) r = classifier.evaluate(tokenizedMessages, labels);

        reportArea.setText(
            "Accuracy Report\n" +
            "---------------\n" +
            "Total samples: " + r.totalSamples() + "\n" +
            "Correct: " + r.correctSamples() + "\n" +
            String.format("Accuracy: %.2f%%\n", r.accuracy()) +
            "False positives: " + r.falsePositives() + "\n" +
            "False negatives: " + r.falseNegatives()
        );
    }

    private void clearFields() {
        messageArea.setText("");
        resultLabel.setText("Prediction: Cleared");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}