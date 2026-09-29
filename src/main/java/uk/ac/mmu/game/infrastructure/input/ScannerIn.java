package uk.ac.mmu.game.infrastructure.input;

import java.util.Scanner;

import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public class ScannerIn implements InputSource {
    private final Scanner scanner;

    public ScannerIn() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getNextLine() {
        return scanner.nextLine();
    }

    @Override
    public int getNextInt(String retryMessage, TextOutputHandler output) {
        int chosenInt = Integer.MIN_VALUE;
        boolean selected = false;
        while (!selected) {
            String input = scanner.nextLine();
        
            try{
                chosenInt = Integer.parseInt(input.strip());
                selected = true;
            } catch (NumberFormatException _e) {
                output.println(retryMessage);
                selected = false;
            } 
        }
        return chosenInt;
    }

}
