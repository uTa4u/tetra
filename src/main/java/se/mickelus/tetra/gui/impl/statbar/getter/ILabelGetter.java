package se.mickelus.tetra.gui.impl.statbar.getter;

public interface ILabelGetter {
    String getLabel(double value, double diffValue, boolean flipped);
}
