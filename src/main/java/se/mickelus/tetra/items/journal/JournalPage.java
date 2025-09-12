package se.mickelus.tetra.items.journal;

public enum JournalPage {
    CRAFT("CRFT"),
    STRUCTURES("STRC"),
    SYSTEM("SYST");

    public String label;

    JournalPage(String label) {
        this.label = label;
    }
}
