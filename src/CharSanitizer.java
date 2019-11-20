import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CharSanitizer {

    private Map<Character, Integer> charMap;

    public CharSanitizer() {
        charMap = new HashMap<>();
        for (int i = 0; i < 65536; i++) {
            charMap.put((char)i, i);
        };

        blacklist(0, 32);
        blacklist(128, 160);
        blacklist(1480, 1487);
        blacklist(1515, 1541);
        blacklist(1564, 1565);
        blacklist(1867, 1868);
        blacklist(1970, 1983);
        blacklist(2043, 2047);
        //charMap.forEach((character, integer) -> System.out.println(String.valueOf(character) + " " + integer));
        //System.out.println(toChar(6));
    }

    public void blacklist(char character) {
        if (charMap.get(character) != null) {
            charMap.remove(character);
            for (int i = character; i < 65536; i++) {
                if (charMap.get((char)i) != null) charMap.replace((char)i, charMap.get((char)i) - 1);
            }
        }
    }

    public void blacklist(int lowBound, int highBound) {
        for(int i = highBound; i >= lowBound; i--) {
            blacklist((char)i);
        }
    }

    public char toChar(int num) {
        for (Map.Entry<Character, Integer> entry : charMap.entrySet()) {
            if (entry.getValue() == num) {
            return entry.getKey();
            }
        }
        return 'z';
    }

    public int toInt(char c) {
        return charMap.get(c);
    }

    public Set<Character> useableValues() {
        Set<Character> ret = new HashSet<>();
        for (Map.Entry<Character, Integer> entry: charMap.entrySet()) {
            ret.add(entry.getKey());
        }
        return ret;
    }

    public void printMap() {
        System.out.println(charMap.toString());
    }
}