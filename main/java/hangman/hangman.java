package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class hangman implements IEvilHangmanGame {
    Set<String> prunedDict;
    int guesses;
    int maxWordLength;
    StringBuilder guessesSoFar;
    StringBuilder builtWord;

    public void hangman() {

    }


    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength) {
        this.builtWord = new StringBuilder("");
        for (int i = 0; i < wordLength; i++) {
            builtWord.append('-');
        }
        this.prunedDict = new HashSet<>();
        this.maxWordLength = wordLength;
        this.guessesSoFar = new StringBuilder("");
        try {
            String alphaReg = "(?:\\s|^)([A-Za-z]+)(?=\\s|$)";
            Scanner sc = new Scanner(dictionary);
            Pattern alpha = Pattern.compile(alphaReg);
            while (sc.hasNext()) {
                String current = sc.next();
                Matcher match = alpha.matcher(current);
                if (match.find() && current.length() == wordLength) {
                    this.prunedDict.add(current);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        if (prunedDict.size() == 0) {
            System.out.println("Dictionary file was empty.");
            System.exit(1);
        }
    }
    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException {
        try {
            String lowerGuess = guess + "";
            lowerGuess = lowerGuess.toLowerCase();
            if (lowerGuess.length() != 1) {
                throw new Exception();
            }
            if (!Character.isLetter(lowerGuess.charAt(0))) {
                System.out.println("Invalid Input");
                throw new Exception();
            }
            if (guessesSoFar.toString().contains(lowerGuess)) {
                throw new GuessAlreadyMadeException();
            }
            return takeGuesses(lowerGuess.charAt(0));
        }
        catch (GuessAlreadyMadeException e) {
            System.out.println("Guess was already made. Try again.");
            return null;
        }
        catch (Exception e) {
            System.out.println("Invalid input");
            return null;
        }
    }

    public Set<String> takeGuesses(char guess) {
        Set<String> set = null;
                guessesSoFar.append(guess);
                set = makeGuess2(guess);
                this.prunedDict.clear();
                this.prunedDict = set;
                return this.prunedDict;
        }

    public String rightMost(ArrayList<String> checkList, int position) {
        System.out.println(checkList);
        for (int i = position - 1; i >= 0; i--) {
            ArrayList<String> newList = new ArrayList<>();
            int count = 0;
            for (String key:checkList) {
                if (key.charAt(i) == '1') {
                    newList.add(key);
                    count++;
                }
            }
            if (count > 1) {
               return rightMost(newList, i);
            }
            else {
                if (count == 1) {
                    for (String key : newList) {
                        return key;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     */
    public Set<String> makeGuess2(char guess){
        guesses--;
        Set<String> sets = null;
        Hashtable<String, ArrayList<String>> groups = new Hashtable();
        try {
            for (String key : prunedDict) {
                String mapKey = "";
                String current = key;
                for (int i = 0; i < current.length(); i++) {
                    char curChar = current.charAt(i);
                    if (curChar == guess) {
                        mapKey = mapKey.concat("1");
                    } else {
                        mapKey = mapKey.concat("0");
                    }
                }
                if (groups.get(mapKey) == null) {
                    ArrayList<String> array = new ArrayList<>();
                    groups.put(mapKey, array);
                }
                groups.get(mapKey).add(current);
            }
            ArrayList<Integer> sizes = new ArrayList<>();
            ArrayList<String> maxList = new ArrayList<>();
            ArrayList<String> minList = new ArrayList<>();
            Set<String> keys = groups.keySet();
            for (String key: keys) {
                sizes.add(groups.get(key).size());
            }
            int max = Collections.max(sizes);
            int min = Collections.min(sizes);
            int count = 0;
            for (String key: keys) {
                if (groups.get(key).size() == max) {
                    count++;
                    maxList.add(key);
                }
            }
            int curMin = maxWordLength;
            int minCount = 0;
            for (String key: maxList) {
                int oneCount = maxWordLength;
                for (int j = 0; j < key.length(); j++) {
                    if (key.charAt(j) == '0') {
                        oneCount--;
                    }
                }
                if (oneCount < curMin) {
                    curMin = oneCount;
                }
            }

            for (String key: maxList) {
                int curCount = 0;
                for (int j = 0; j < key.length(); j++) {
                    if (key.charAt(j) == '1') {
                        curCount++;
                    }
                }
                if (curCount == curMin) {
                    minList.add(key);
                    minCount++;
                }
            }

            if (count == 1) {
                for (String key: keys) {
                    if (groups.get(key).size() == max) {
                        if (!key.contains("1")) {
                            System.out.println("Sorry, there are no " + guess + "'s");
                        }
                        else {
                            int countChar = 0;
                            for (int i = 0; i < key.length(); i++) {
                                if (key.charAt(i) == '1') {
                                    countChar++;
                                }
                            }
                            System.out.println("Yes, there is " + countChar + " " + guess + "'s");
                        }
                        StringBuilder temp = new StringBuilder("");
                        for (int i = 0; i < maxWordLength; i++) {
                            if (key.charAt(i) == '1') {
                                temp.append(guess);
                            }
                            else {
                                temp.append(builtWord.charAt(i));
                            }
                        }
                        builtWord = temp;
                        Set<String> biggest = new HashSet<>(groups.get(key));
                        return biggest;
                    }
                }
            }
            else {
                //Find best group.
                for (String key : maxList) {
                    if (!key.contains("1")) {
                        Set<String> biggest = new HashSet<>(groups.get(key));
                        int countChar = 0;
                        for (int i = 0; i < key.length(); i++) {
                            if (key.charAt(i) == '1') {
                                countChar++;
                            }
                        }
                        System.out.println("Sorry, there are no " + guess + "'s");
                        StringBuilder temp = new StringBuilder("");
                        for (int i = 0; i < maxWordLength; i++) {
                            if (key.charAt(i) == '1') {
                                temp.append(guess);
                            }
                            else {
                                temp.append(builtWord.charAt(i));
                            }
                        }
                        builtWord = temp;
                        return biggest;
                    }
                }
                for (String key : minList) {
                    if (minCount == 1) {
                        Set<String> biggest = new HashSet<>(groups.get(key));
                        int countChar = 0;
                        for (int i = 0; i < key.length(); i++) {
                            if (key.charAt(i) == '1') {
                                countChar++;
                            }
                        }
                        System.out.println("Yes, there is " + countChar + " " + guess + "'s");
                        StringBuilder temp = new StringBuilder("");
                        for (int i = 0; i < maxWordLength; i++) {
                            if (key.charAt(i) == '1') {
                                temp.append(guess);
                            }
                            else {
                                temp.append(builtWord.charAt(i));
                            }
                        }
                        builtWord = temp;
                        return biggest;
                    }
                }
                String rightMost = rightMost(minList, maxWordLength);
                if (rightMost != null) {
                    Set<String> biggest = new HashSet<>(groups.get(rightMost));
                    int countChar = 0;
                    for (int i = 0; i < rightMost.length(); i++) {
                        if (rightMost.charAt(i) == '1') {
                            countChar++;
                        }
                    }
                    System.out.println("Yes, there is " + countChar + " " + guess + "'s");
                    StringBuilder temp = new StringBuilder("");
                    for (int i = 0; i < maxWordLength; i++) {
                        if (rightMost.charAt(i) == '1') {
                            temp.append(guess);
                        }
                        else {
                            temp.append(builtWord.charAt(i));
                        }
                    }
                    builtWord = temp;
                    return biggest;
                }
                else {
                    System.out.println("Something went wrong with the rightMost function.");
                    System.exit(1);
                }
            }


            if (3 == 1) {
                throw new GuessAlreadyMadeException();
            }
        }
        catch (GuessAlreadyMadeException e) {

        }
        return sets;
    }
}
