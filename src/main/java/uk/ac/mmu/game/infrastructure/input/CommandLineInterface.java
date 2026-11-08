package uk.ac.mmu.game.infrastructure.input;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private final static String INPUT_DECORATOR = "> ";
    private final static Scanner scanner = new Scanner(System.in);
    
    private static int getANumber() {
        boolean inputIsInt = false;
        int chosenValue = 0;

        do {
            System.out.print(INPUT_DECORATOR);

            String input = scanner.nextLine();

            try {
                chosenValue = Integer.parseInt(input);
                inputIsInt = true;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        } while(!inputIsInt);

        return chosenValue;
    }
    public static int askQuestionAndGetNumberBack(String question) {
        System.out.println(question);

        return getANumber();
    }
    public static String askQuestionAndGetStringBack(String question) {
        System.out.println(question);

        System.out.print(INPUT_DECORATOR);
        return scanner.nextLine();
    } 

    public static int pickOptionNumberFromList(String title, List<String> options) {
        System.out.println(title);
        int counter = 0;
        for (String i : options) {
            System.out.println(counter + " - " + i);
            counter++;
        }

        int choice = -1;

        do {
            choice = getANumber();

            if (choice < 0 || choice >= options.size())
                System.out.println("Please pick a number in the correct range");
        } while(choice < 0 || choice >= options.size());
        
        return choice;
    }
}
