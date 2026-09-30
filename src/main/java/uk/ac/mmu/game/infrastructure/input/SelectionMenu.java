package uk.ac.mmu.game.infrastructure.input;

import java.util.List;

import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public class SelectionMenu {
    public static int selectFromOptions(List<String> options, TextOutputHandler output, InputSource input) {
        output.println("Please choose one of the following:");

        int index = 0;

        for (String opt : options) {
            index++;
            output.println(index + " - " + opt);
        }

        boolean chosen = false;

        while (!chosen) {
            int choice = input.getNextInt("Please enter a valid number", output);

            try {
                @SuppressWarnings("unused")
                String _boundsTest = options.get(choice - 1);

                return choice - 1;
            } catch (IndexOutOfBoundsException _e) {
                output.println("Please choose an option in the correct range");
            }
        }

        return -1;
    }
}
