package uk.ac.mmu.game.infrastructure.output;

public class StylisedPrinter {
    public static int printBanner(TextOutputHandler output, String title) {
        String mainBanner = "|=|-------|=| " + title + " |=|-------|=|";

        int lengthOfBanner = mainBanner.length();

        String headerAndFooter = "|=|";
        String bridge = "|=|";
        for (int i = 0; i < lengthOfBanner - 6; i++) {
            headerAndFooter = headerAndFooter + "-";
            bridge = bridge + " ";
        }
        
        headerAndFooter = headerAndFooter + "|=|";
        bridge = bridge + "|=|";

        output.println("\n" + headerAndFooter);
        output.println(bridge);
        output.println(mainBanner);
        output.println(bridge);
        output.println(headerAndFooter + "\n");

        return lengthOfBanner;
    }
    public static void printSubheading(TextOutputHandler output, String title) {
        String msg = "\n|=|-------|=| " + title + " |=|-------|=|";
        output.println(msg);
    }
}
