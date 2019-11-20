import java.util.*;

public class BestStringFinder {

    private String bestPhrase = "";
    private int bestCount = 0;
    private int currentMax = 0;
    private boolean sucess = false;

    public BestStringFinder(String input, int minSize, int maxSize) {
        double progress = 0.0;
        Map<String, Integer> phrases = new HashMap<>();
        Map<String, Integer> repeatedPhrases = new HashMap<>();
    
        System.out.println("");
    
        for (int length = minSize; length <= maxSize; length++) {
    
            for (int position = 1; position + length <= input.length(); position++) {
                
                String test = input.substring(position, position + length);
    
                if (phrases.get(test) != null) {
                    phrases.replace(test, phrases.get(test) + 1);
                } else {
                    phrases.put(test, 1);
                }
                for (int offset = 1; offset <= length; offset++) {
                    if (position + offset + length <= input.length()) {
                        if (input.substring(position + offset, position + offset + length).equals(test)) {
                            position++;
                        }
                    }
                }
            }
            progress = ((length / maxSize) * 100);
            System.out.print(progress + "% done, completed all " + length + " character strings, calculating up to " + maxSize + "\r"); 
        }

        System.out.println("");
    
        phrases.forEach((string, count) -> {
            if (count > 1) {
                repeatedPhrases.put(string, count);
            }
        });
               
        repeatedPhrases.forEach((string, count) -> {
            int reward = calculateSavedBits(string, count);
            if (reward > currentMax) {
                bestPhrase = string;
                currentMax = calculateSavedBits(string, count);
            }
        });

        if (currentMax > 0) {
            bestCount = repeatedPhrases.get(bestPhrase);
            sucess = true;
        }
    }

    public boolean foundString() {
        return sucess;
    }

    public String getBestString() {
        return bestPhrase;
    }

    public int getBestRepetitions() {
        return bestCount;
    }

    private int calculateSavedBits(String string, int count) {
        int dictEntry = (string.length() * 8) + 16;
        int oldSize = string.length() * count * 8;
        return oldSize - dictEntry;
    }
}