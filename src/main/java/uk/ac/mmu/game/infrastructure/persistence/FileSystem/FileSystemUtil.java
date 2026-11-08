package uk.ac.mmu.game.infrastructure.persistence.FileSystem;

import java.util.List;
import java.util.stream.Stream;

public final class FileSystemUtil {
    public static List<String> splitString(String string) {
        return Stream.of(string.strip().split(" ")).filter(val -> !val.strip().isEmpty()).toList();
    }
    public static Integer stringToUnsignedInt(String string) {
        try {
            return Integer.valueOf(string.strip());
        } catch (NumberFormatException _e) {
            return -1;
        }
    }
    public static Integer stringToUnsignedNonZeroInt(String string) {
        int value = stringToUnsignedInt(string);

        return value <= 0 ? -1 : value;
    }
}
