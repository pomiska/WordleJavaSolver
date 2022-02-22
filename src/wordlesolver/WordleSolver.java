package wordlesolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 *
 * @author Miska
 */
public class WordleSolver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        WordleSolver w = new WordleSolver();
        Scanner r = new Scanner(System.in);
        System.out.println("Hello");
        while (true) {
            System.out.println("Insert a command: starters, solver, quit");
            String input = r.nextLine();
            if (input.equals("quit")) {
                break;
            } else if (input.equals("starters")) {
                w.generateListWithUniqueCharacters();
            } else if (input.equals("solver")) {
                w.manualHelper();
            } else {
                System.out.println("Wrong input");
            }
        }
    }

    public void generateListWithUniqueCharacters() throws Exception {
        //To get a list of best starting words (maybe, I don't know the optimal strategy for the game)
        Scanner s = new Scanner(new File("wordle_list.txt"));
        BufferedWriter b = new BufferedWriter(new FileWriter(new File("all_unique_chars.txt")));
        while (s.hasNextLine()) {
            String word = s.nextLine();
            HashSet<Character> set = new HashSet<>();
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                set.add(c);
            }
            if (set.size() == 5) {
                b.write(word + "\n");
                System.out.println(word);
            }
        }
        b.close();
    }

    public void manualHelper() throws Exception {
        Scanner s = new Scanner(new File("wordle_list.txt"));
        Scanner in = new Scanner(System.in);
        HashSet<String> words = new HashSet<>();
        while (s.hasNextLine()) {
            words.add(s.nextLine());
        }
        String[] solution = new String[5];
        Arrays.fill(solution, "");
        HashSet<Character> notAllowed = new HashSet<>();
        HashMap<Integer, HashSet<Character>> hasToHave = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            hasToHave.put(i, new HashSet<>());
        }
        while (true) {
            if (words.size() == 1) {
                System.out.println("There it is ^");
                break;
            }
            System.out.println("Give the word you tried (or quit)");
            String input = in.nextLine();
            if (input.equals("quit")) {
                break;
            }
            words.remove(input);
            String removeLetters = input;
            System.out.println("Give letters that fit and their position (example A 1), empty input goes to next section");
            while (true) {
                input = in.nextLine();
                if (input.equals("")) {
                    break;
                }
                int spot = Character.getNumericValue(input.charAt(2));
                solution[spot - 1] = Character.toString(input.charAt(0));
                removeLetters = removeLetters.replace(Character.toString(input.charAt(0)), "");
            }
            System.out.println("Give letters that were in the wrong position (example R 2, meaning R is in the word but not as 2nd letter), empty input goes to next section");
            while (true) {
                input = in.nextLine();
                if (input.equals("")) {
                    break;
                }
                hasToHave.get(Character.getNumericValue(input.charAt(2) - 1)).add(input.charAt(0));
                removeLetters = removeLetters.replace(Character.toString(input.charAt(0)), "");
            }
            System.out.println("Removing unsuitable words...");
            for (int i = 0; i < removeLetters.length(); i++) {
                notAllowed.add(removeLetters.charAt(i));
            }
            words = (HashSet) possibleWords(words, hasToHave, notAllowed, solution).clone();
            System.out.println("Possible words:");
            for (String word : words) {
                System.out.println(word);
            }
        }
    }

    public HashSet<String> possibleWords(HashSet<String> words, HashMap<Integer, HashSet<Character>> hasToHave, HashSet<Character> notAllowed, String[] solution) {
        HashSet<String> removeThese = new HashSet<>();
        for (String word : words) {
            boolean removed = false;
            for (int i = 0; i < word.length(); i++) {
                if (!solution[i].isEmpty()) {
                    if (solution[i].charAt(0) != word.charAt(i)) {
                        removeThese.add(word);
                        removed = true;
                        break;
                    }
                }
            }
            if (removed) {
                continue;
            }
            for (int i = 0; i < word.length(); i++) {
                if (!solution[i].isEmpty()) {
                    if (solution[i].charAt(0) == word.charAt(i)) {
                        continue;
                    }
                }
                if (hasToHave.containsKey(i)) {
                    if (hasToHave.get(i).contains(word.charAt(i))) {
                        removeThese.add(word);
                        break;
                    }
                    for (Character c : hasToHave.get(i)) {
                        if (!word.contains(c.toString())) {
                            removeThese.add(word);
                            break;
                        }
                    }
                }
                if (notAllowed.contains(word.charAt(i))) {
                    removeThese.add(word);
                    break;
                }
            }
        }
        words.removeAll(removeThese);
        return words;
    }
}
