package uk.ac.mmu.game.shared;

// WARN: these are immutable. If it turns out it needs operations on it, then this needs to be a class
public record GridPosition(int x, int y) {
    public String toString() {
        return "[" + x + "," + y + "]";
    } 
}