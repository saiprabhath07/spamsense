# SpamSense

SpamSense is a pure Java spam classification project that uses a Naive Bayes model built from scratch to detect spam and ham messages. It includes CSV dataset loading, text preprocessing, model training, evaluation metrics, and a simple Swing GUI for interactive prediction.

## What It Does

- Reads a CSV file with `label` and `message` columns.
- Preprocesses text (lowercase, punctuation removal, tokenization, stopword removal).
- Trains a Naive Bayes classifier using word‑frequency counts and Laplace smoothing.
- Predicts whether a custom message is **spam** or **ham**.
- Shows an accuracy report with total samples, correct predictions, false positives, and false negatives.

## Features

- Pure Java, no external libraries
- Naive Bayes text classifier with log probabilities and Laplace smoothing
- CSV dataset loader that handles common SMS‑style format
- Simple Java Swing GUI for training and prediction
- Interactive prediction from a text area
- Model evaluation and accuracy report panel

## Project Structure

```text
SpamSense/
├── data/
│   └── emails.csv
├── src/
│   ├── Main.java
│   ├── ui/
│   │   └── SpamSenseGUI.java
│   ├── model/
│   │   └── NaiveBayes.java
│   ├── preprocessing/
│   │   └── TextProcessor.java
│   └── data/
│       └── DataLoader.java
├── run.sh
└── run.bat
```

## How It Works

### 1. Data Loading
`DataLoader.java` reads the dataset from a CSV file with two columns:

- `label` → `spam` or `ham`
- `message` → the message text

### 2. Text Preprocessing
`TextProcessor.java` prepares the text before training and prediction by:

- converting text to lowercase
- removing punctuation
- splitting text into words
- removing common stopwords

### 3. Model Training
`NaiveBayes.java` trains on the processed messages using:

- word frequency counts with `HashMap`
- separate counts for spam and ham
- Laplace smoothing
- log probabilities for prediction

### 4. Prediction
The model predicts whether a new message is spam or ham based on the learned probabilities.

### 5. Evaluation
The classifier can calculate:

- total samples
- correct predictions
- accuracy
- false positives
- false negatives

## Requirements

- Java 21
- No external libraries
- CSV dataset in this format:

```csv
label,message
ham,Hello how are you
spam,Win cash now
```

## Important CSV Note

If your message text contains commas, standard CSV requires those fields to be wrapped in double quotes, or you can replace commas with periods inside the message text to avoid parsing issues. [web:73]

Example:

```csv
label,message
spam,"Congratulations, you won a prize"
ham,"Hey, are you free tonight"
```

## How to Run

### Linux / macOS

```bash
chmod +x run.sh
./run.sh
```

### Windows

```bat
run.bat
```

## GUI Usage

# SpamSense

SpamSense is a pure Java spam classification project that uses a Naive Bayes model built from scratch to detect spam and ham messages. It includes CSV dataset loading, text preprocessing, model training, evaluation metrics, and a simple Swing GUI for interactive prediction.

## What It Does

- Reads a CSV file with `label` and `message` columns.
- Preprocesses text (lowercase, punctuation removal, tokenization, stopword removal).
- Trains a Naive Bayes classifier using word‑frequency counts and Laplace smoothing.
- Predicts whether a custom message is **spam** or **ham**.
- Shows an accuracy report with total samples, correct predictions, false positives, and false negatives.

## Features

- Pure Java, no external libraries
- Naive Bayes text classifier with log probabilities and Laplace smoothing
- CSV dataset loader that handles common SMS‑style format
- Simple Java Swing GUI for training and prediction
- Interactive prediction from a text area
- Model evaluation and accuracy report panel

## Project Structure

```text
SpamSense/
├── data/
│   └── emails.csv
├── src/
│   ├── Main.java
│   ├── ui/
│   │   └── SpamSenseGUI.java
│   ├── model/
│   │   └── NaiveBayes.java
│   ├── preprocessing/
│   │   └── TextProcessor.java
│   └── data/
│       └── DataLoader.java
├── run.sh
└── run.bat
```

## How It Works

### 1. Data Loading
`DataLoader.java` reads the dataset from a CSV file with two columns:

- `label` → `spam` or `ham`
- `message` → the message text

### 2. Text Preprocessing
`TextProcessor.java` prepares the text before training and prediction by:

- converting text to lowercase
- removing punctuation
- splitting text into words
- removing common stopwords

### 3. Model Training
`NaiveBayes.java` trains on the processed messages using:

- word frequency counts with `HashMap`
- separate counts for spam and ham
- Laplace smoothing
- log probabilities for prediction

### 4. Prediction
The model predicts whether a new message is spam or ham based on the learned probabilities.

### 5. Evaluation
The classifier can calculate:

- total samples
- correct predictions
- accuracy
- false positives
- false negatives

## Requirements

- Java 21
- No external libraries
- CSV dataset in this format:

```csv
label,message
ham,Hello how are you
spam,Win cash now
```

## Important CSV Note

If your message text contains commas, standard CSV requires those fields to be wrapped in double quotes, or you can replace commas with periods inside the message text to avoid parsing issues. [web:73]

Example:

```csv
label,message
spam,"Congratulations, you won a prize"
ham,"Hey, are you free tonight"
```

## How to Run

### Linux / macOS

```bash
chmod +x run.sh
./run.sh
```

### Windows

```bat
run.bat
```

## GUI Usage

After running the project:

1. Open the SpamSense window.
2. Click **Load CSV** to browse and select a dataset (optional).
3. Click **Train Model** to load the data and train the classifier.
4. Type a custom message into the message area.
5. Click **Predict** to see the prediction (`SPAM` or `HAM`).
6. Click **Show Report** to view accuracy, false positives, and false negatives.

## Example Messages

### Ham
```text
Hey, are we meeting at 6 pm today?
```

### Spam
```text
Congratulations! You have won a free gift. Claim now!
```

## Example Output

Prediction:
- `Prediction: SPAM`
- `Prediction: HAM`

Report example (values depend on your dataset):

```text
Accuracy Report
---------------
Total samples: 5574
Correct: 5500
Accuracy: 98.67%
False positives: 20
False negatives: 54
```

## Notes

- This project is designed for learning how spam classification works internally.
- The implementation follows object‑oriented design, where each class has one clear responsibility.
- The GUI is built using Java Swing.
- The current accuracy depends on the quality and size of the dataset used for training.

## Future Improvements

- Train/test split instead of evaluating on the same dataset
- Better CSV parsing for quoted and multiline messages
- Export prediction results
- Display a raw probability score for each prediction
- More polished UI styling and layout
