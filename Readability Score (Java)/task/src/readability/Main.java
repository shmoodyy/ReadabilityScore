package readability;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    static String syllableRegex = "[aeiouyAEIOUY]+[^e.\\s]|[aiouyAEIOUY]+\\b|\\b[^aeiouyAEIOUY0-9.']+e\\b";

    public static void main(String[] args) throws IOException {

        FileReader file = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(file);
        StringBuilder fileString = new StringBuilder();

        String line;
        char[] characters;
        String[] words, sentences, syllables, polysyllables;
        double wordCount = 0f, sentenceCount = 0f, characterCount = 0f, totalSyllables = 0f, polyCount = 0f;

        while((line = br.readLine()) != null) {
            fileString.append(line);
            words = line.split("[,:'\"]?[\\s]+[,:'\"]?");
            sentences = line.split("[.!?]");
            characters = line.replaceAll("\\s", "").toCharArray();
            syllables = line.split(syllableRegex);
            wordCount += words.length;
            sentenceCount += sentences.length;
            characterCount += characters.length;
            totalSyllables  += syllables.length;

            for (String singleWord : words) {
                if (singleWord.charAt(singleWord.length() - 1) == 'e') {
                    singleWord = singleWord.substring(0, singleWord.length() - 1);
                } //IMPORTANT METHOD FOR COUNTING REGEX MATCHES:
                int syllableAmount = (int) Pattern.compile(syllableRegex).matcher(singleWord).results().count();
                polyCount+= syllableAmount >= 3 ? 1 : 0;
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.printf("The text is:%n%s%n%nWords: %d%nSentences: %d%nCharacters: %d%n" +
                        "Syllables: %d%nPolysyllables: %d%n", fileString, (int) wordCount, (int) sentenceCount
                , (int) characterCount, (int) totalSyllables, (int) polyCount);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String scoreRequested = scanner.next();
        scoreCalc(scoreRequested, wordCount, sentenceCount, characterCount, totalSyllables, polyCount);
    }


    static void scoreCalc(String scoreReq, double countW ,double countS, double countC, double countSy, double countP) {
        double aRIScore = autoReadabilityIndex(countW, countS, countC);
        double fKScore = fleschKincaid(countW, countS, countSy);
        double smogScore = smogIndex(countS, countP);
        double cLScore = colemanLiau(countW, countS, countC);
        String printAVG = String.format("This text should be understood in average by %.2f-year-olds. %n"
                , (aRIScore + fKScore + smogScore + cLScore) / 4);
        String printedScores = scoreReq.equalsIgnoreCase("all")
                ? String.format("%nAutomated Readability Index: %.2f%s%nFlesch–Kincaid readability tests: %.2f%s%n"
                        + "Simple Measure of Gobbledygook: %.2f%s%nColeman–Liau index: %.2f%s%n%n%s", aRIScore,
                ages(aRIScore), fKScore, ages(fKScore), smogScore, ages(smogScore), cLScore, ages(cLScore), printAVG)
                : scoreReq.equalsIgnoreCase("ARI") ? String.format("Automated Readability Index: %.2f%s%n"
                , aRIScore, ages(aRIScore))
                : scoreReq.equalsIgnoreCase("FK") ? String.format("Flesch–Kincaid readability tests: " +
                        "%.2f%s%n", fKScore, ages(fKScore))
                : scoreReq.equalsIgnoreCase("SMOG") ? String.format("Simple Measure of Gobbledygook: " +
                        "%.2f%s%n", smogScore, ages(smogScore))
                : scoreReq.equalsIgnoreCase("CL") ? String.format("Coleman–Liau index: %.2f%s%n"
                , cLScore, ages(cLScore)) : "Not a request!";
        System.out.print(printedScores);
    }

    static String ages(double score) {
        double ceilingScore = Math.ceil(score);
        return " (about " + (ceilingScore == 14 ? "22" : ceilingScore == 13 ? "18" : ceilingScore >= 12 ? "17"
                : ceilingScore >= 11 ? "16" : ceilingScore >= 10 ? "15" : ceilingScore >= 9 ? "14"
                : ceilingScore >= 8 ? "13" : ceilingScore >= 7 ? "12" : ceilingScore >= 6 ? "11"
                : ceilingScore >= 5 ? "10" : ceilingScore >= 4 ? "9" : ceilingScore >= 3 ? "8"
                : ceilingScore >= 2 ? "7" : "6") + "-year-olds).";
    }

    static double colemanLiau(double countW, double countS, double countC) {
        return 0.0588 * (100 * (countC / countW)) - (0.296 * (100 * (countS / countW))) - 15.8;
    }

    static double smogIndex(double countS, double countP) {
        return 1.043 * Math.sqrt(countP * (30 / countS)) + 3.1291;
    }

    static double fleschKincaid(double countW, double countS, double countSyl) {
        return 0.39 * (countW / countS) + 11.8 * (countSyl / countW) - 15.59;
    }

    static double autoReadabilityIndex(double countW, double countS, double countC) {
        return 4.71 * (countC / countW) + 0.5 * (countW / countS) - 21.43;
    }
}