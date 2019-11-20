import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;


public class MainClass {

    File inputFile;
    File outputFile;
    CharSanitizer charSanitizer;
    
    public static void main(String[] args) {
        if (args.length == 2) {
            new MainClass(args[0], args[1], true);
        } else if (args.length == 3) {
            new MainClass(args[0], args[1], false);
        } else {
            System.out.println("Error: expected two arguments <inputPath> and <outputPath>");
        } 
    }

    public MainClass(String inputPath, String outputPath, boolean compressing) {
        charSanitizer = new CharSanitizer();
        long startTime = System.currentTimeMillis();
        inputFile = new File(inputPath);
        outputFile = new File(outputPath);
        if(!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch(Exception e) {

            }
        }
        if(compressing) {
            if (inputFile.exists()) {
                compress();
            }
        } else {
            if (inputFile.exists()) {
                decompress();
            }
        }
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("finished job in " + totalTime + "ms.");
    }

    public void compress() {
        StringBuilder rawText = new StringBuilder(readFile(inputFile));
        int rawBytes = rawText.toString().getBytes().length;
        StringBuilder dict = new StringBuilder();
        boolean compressable = true;
        Set<Character> useableChars = charSanitizer.useableValues();
        char[] chars = rawText.toString().toCharArray();
        for (char character : chars) {
            useableChars.remove(character);
        }
        Iterator iterator = useableChars.iterator();

        while(compressable) {
            BestStringFinder stringFinder = new BestStringFinder(rawText.toString(), 3, 64);
            if (stringFinder.foundString()) {
                String bestPhrase = stringFinder.getBestString();
                String replace = String.valueOf(iterator.next());
                System.out.println("Best phrase was '" + bestPhrase + "' repeated " + stringFinder.getBestRepetitions() + " times. Replaced with " + replace + ".");
                while (rawText.indexOf(bestPhrase) >= 0) {
                    rawText.replace(rawText.indexOf(bestPhrase), rawText.indexOf(bestPhrase) + bestPhrase.length(), replace);
                }
                System.out.println("Translating '" + bestPhrase + "' to " + replace + ", length " + bestPhrase.length());
                System.out.println("inserting " + replace + charSanitizer.toChar(bestPhrase.length()) + bestPhrase);
                dict.insert(0, replace + charSanitizer.toChar(bestPhrase.length()) + bestPhrase);

            } else {
                compressable = false;
                rawText.insert(0, charSanitizer.toChar(rawText.length() + 1));
                System.out.println(charSanitizer.toChar(rawText.length() + 1) + " " + (rawText.length() + 1));
                rawText.append(dict.toString());
            }
        }
        
        System.out.println(rawText.toString());
        writeFile(rawText.toString());
        int finalSize = rawText.toString().getBytes().length;
        System.out.println("\ncompressed to " + finalSize + "B from " + rawBytes + "B.");
        System.out.println("");
    }

    public void decompress() {
        String rawText = readFile(inputFile, "UTF-16");
        int dictBreak = charSanitizer.toInt(rawText.charAt(0));
        System.out.println("break at " + dictBreak);
        StringBuilder dictString = new StringBuilder(rawText.substring(dictBreak));
        StringBuilder compressedText = new StringBuilder(rawText.substring(1, dictBreak));
        Map<String, String> dict = new HashMap<>();

        while (dictString.length() > 1) {
            String symbol = String.valueOf(dictString.charAt(0));

            String replace = dictString.substring(2, charSanitizer.toInt(dictString.charAt(1)) + 2);
            dictString.replace(0, charSanitizer.toInt(dictString.charAt(1)) + 2, "");

            System.out.println("Translating " + symbol + " to " + replace);

            while (compressedText.indexOf(symbol) >= 0) {
                compressedText.replace(compressedText.indexOf(symbol), compressedText.indexOf(symbol) + 1, replace);
            }
        }
       
        //dict.forEach((symbol, replace) -> System.out.println("Translating " + symbol + " to " + replace));

        writeFile(compressedText.toString());
    }

    /**
     * Reads all text from a file into a single string.
     * @param file
     * @return Raw text contained in the file.
     */
    public String readFile(File file) {  
            try {
                Scanner reader = new Scanner(inputFile);
                StringBuilder builder = new StringBuilder();
                while(reader.hasNext()) {
                    builder.append(reader.next() + " ");
                }
                reader.close();
                return builder.toString();
            } catch (Exception e) {

            }
            return "";
    }

    public String readFile(File file, String charset) {  
        try {
            Scanner reader = new Scanner(inputFile, Charset.forName(charset));
            StringBuilder builder = new StringBuilder();
            while(reader.hasNext()) {
                builder.append(reader.next() + " ");
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {

        }
        return "";
}

    public void writeFile(String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, Charset.forName("UTF-16")));
            writer.write(text);
            writer.close();
        } catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }

}