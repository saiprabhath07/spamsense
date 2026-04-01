@echo off
if not exist bin mkdir bin
javac -d bin src\Main.java src\ui\SpamSenseGUI.java src\data\DataLoader.java src\model\NaiveBayes.java src\preprocessing\TextProcessor.java
java -cp bin Main
pause