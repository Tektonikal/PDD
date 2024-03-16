package tektonikal.playerdistance.config;

import eu.midnightdust.lib.config.MidnightConfig;
import tektonikal.playerdistance.ColorModes;
import tektonikal.playerdistance.Modes;

public class PDcfg extends MidnightConfig {
    @Entry(name = "Mod enabled")
    public static boolean enabled = true;
    @Entry(name = "Mode")
    public static Modes mode = Modes.DISTANCE;
    @Entry(name = "Position")
    public static Pos pos = Pos.CENTER;
    @Entry(name = "X Offset",min = -200, max = 200, isSlider = true)
    public static int xOffset;
    @Entry(name = "Y Offset",min = -200, max = 200, isSlider = true)
    public static int yOffset = -5;
    @Entry(name = "Prefix")
    public static String prefix = "[";
    @Entry(name = "Suffix")
    public static String suffix = "]";
    @Entry(name = "Show Name")
    public static boolean showName = true;
    @Entry(name = "Show Name First")
    public static boolean nameFirst;
    @Entry(name = "Change Color in Range")
    public static boolean changeInRange = true;
    @Entry(name = "Color Mode")
    public static ColorModes cm = ColorModes.CANREACH;
    @Entry(name = "Main Color")
    public static String MainCol = "88FFFFFF";
    @Entry(name = "In Range Color")
    public static String InRangeCol = "88FF0000";
    @Entry(name = "Text shadow")
    public static boolean shadow;

    public enum Pos{
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }
}