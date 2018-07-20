package hangman;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Set;
import java.util.Random;

/**
 * Created by Kevin on 1/27/2018.
 */

public class Main {
    public static void main(String[] args) throws IOException {
        String path = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);
        if (wordLength < 2) {
            System.out.println("Invalid value for Word Length");
            System.exit(1);
        }
        if (guesses < 1) {
            System.out.println("Invalid value for number of guesses");
            System.exit(1);
        }
        hangman game = new hangman();
        File file = new File(path);
        game.startGame(file, wordLength);

        for(int i = guesses; i > 0; i--) {
            System.out.println("\nYou have " + i + " guesses left.");
            char[] charArray = game.guessesSoFar.toString().toCharArray();
            Arrays.sort(charArray);
            game.guessesSoFar = new StringBuilder(new String(charArray));
            System.out.print("Used letters: ");
            if (game.guessesSoFar != null) {
                for (int x = 0; x < game.guessesSoFar.length(); x++) {
                    System.out.print(game.guessesSoFar.charAt(x) + " ");
                }
            }
            System.out.println("\nWord: " + game.builtWord);
            System.out.print("Enter guess: ");
            try {
                Scanner userInput = new Scanner(System.in);
                String input = userInput.nextLine();
                if (input.length() != 1) {
                    System.out.println("Invalid Input");
                    i++;
                    continue;
                }
                if (!Character.isLetter(input.charAt(0))) {
                    System.out.println("Invalid Input");
                    i++;
                    continue;
                }
                Set<String> doubleCheck = game.makeGuess(input.charAt(0));
                if (doubleCheck == null) {
                    throw new hangman.GuessAlreadyMadeException();
                }
                if (doubleCheck.size() == 1) {
                    if (doubleCheck.contains(game.builtWord.toString())) {
                        System.out.println("You Win! The word was " + game.builtWord.toString());
                        System.exit(0);
                    }
                }
            }
            catch(hangman.GuessAlreadyMadeException e) {
                i++;
            }
        }
        int finalSize = game.prunedDict.size();
        String[] finalArray = new String[finalSize];
        int j = 0;
        for (String key: game.prunedDict) {
            finalArray[j] = key;
            j++;
        }
        Random rand = new Random();
        System.out.println("You lose. The correct word was: " + finalArray[rand.nextInt(finalSize)]);
    }
}
