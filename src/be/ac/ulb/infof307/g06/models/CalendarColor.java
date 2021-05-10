package be.ac.ulb.infof307.g06.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;



/**
 * Calendar colors
 */
public class CalendarColor {
    private Map<String, String> colorsMap = new HashMap<>();

    /**
     * consturctor
     */
    public CalendarColor() {
        colorsMap.put("AliceBlue", "#F0F8FF");
        colorsMap.put("AntiqueWhite", "#FAEBD7");
        colorsMap.put("Aqua", "#00FFFF");
        colorsMap.put("Aquamarine", "#7FFFD4");
        colorsMap.put("Azure", "#F0FFFF");
        colorsMap.put("Beige", "#F5F5DC");
        colorsMap.put("Bisque", "#FFE4C4");
        colorsMap.put("Black", "#000000");
        colorsMap.put("BlanchedAlmond", "#FFEBCD");
        colorsMap.put("Blue", "#0000FF");
        colorsMap.put("BlueViolet", "#8A2BE2");
        colorsMap.put("Brown", "#A52A2A");
        colorsMap.put("BurlyWood", "#DEB887");
        colorsMap.put("CadetBlue", "#5F9EA0");
        colorsMap.put("Chartreuse", "#7FFF00");
        colorsMap.put("Chocolate", "#D2691E");
        colorsMap.put("Coral", "#FF7F50");
        colorsMap.put("CornflowerBlue", "#6495ED");
        colorsMap.put("Cornsilk", "#FFF8DC");
        colorsMap.put("Crimson", "#DC143C");
        colorsMap.put("Cyan", "#00FFFF");
        colorsMap.put("DarkBlue", "#00008B");
        colorsMap.put("DarkCyan", "#008B8B");
        colorsMap.put("DarkGoldenRod", "#B8860B");
        colorsMap.put("DarkGray", "#A9A9A9");
        colorsMap.put("DarkGrey", "#A9A9A9");
        colorsMap.put("DarkGreen", "#006400");
        colorsMap.put("DarkKhaki", "#BDB76B");
        colorsMap.put("DarkMagenta", "#8B008B");
        colorsMap.put("DarkOliveGreen", "#556B2F");
        colorsMap.put("DarkOrange", "#FF8C00");
        colorsMap.put("DarkOrchid", "#9932CC");
        colorsMap.put("DarkRed", "#8B0000");
        colorsMap.put("DarkSalmon", "#E9967A");
        colorsMap.put("DarkSeaGreen", "#8FBC8F");
        colorsMap.put("DarkSlateBlue", "#483D8B");
        colorsMap.put("DarkSlateGray", "#2F4F4F");
        colorsMap.put("DarkSlateGrey", "#2F4F4F");
        colorsMap.put("DarkTurquoise", "#00CED1");
        colorsMap.put("DarkViolet", "#9400D3");
        colorsMap.put("DeepPink", "#FF1493");
        colorsMap.put("DeepSkyBlue", "#00BFFF");
        colorsMap.put("DimGray", "#696969");
        colorsMap.put("DimGrey", "#696969");
        colorsMap.put("DodgerBlue", "#1E90FF");
        colorsMap.put("FireBrick", "#B22222");
        colorsMap.put("FloralWhite", "#FFFAF0");
        colorsMap.put("ForestGreen", "#228B22");
        colorsMap.put("Fuchsia", "#FF00FF");
        colorsMap.put("Gainsboro", "#DCDCDC");
        colorsMap.put("GhostWhite", "#F8F8FF");
        colorsMap.put("Gold", "#FFD700");
        colorsMap.put("GoldenRod", "#DAA520");
        colorsMap.put("Gray", "#808080");
        colorsMap.put("Grey", "#808080");
        colorsMap.put("Green", "#008000");
        colorsMap.put("GreenYellow", "#ADFF2F");
        colorsMap.put("HoneyDew", "#F0FFF0");
        colorsMap.put("HotPink", "#FF69B4");
        colorsMap.put("IndianRed", "#CD5C5C");
        colorsMap.put("Indigo", "#4B0082");
        colorsMap.put("Ivory", "#FFFFF0");
        colorsMap.put("Khaki", "#F0E68C");
        colorsMap.put("Lavender", "#E6E6FA");
        colorsMap.put("LavenderBlush", "#FFF0F5");
        colorsMap.put("LawnGreen", "#7CFC00");
        colorsMap.put("LemonChiffon", "#FFFACD");
        colorsMap.put("LightBlue", "#ADD8E6");
        colorsMap.put("LightCoral", "#F08080");
        colorsMap.put("LightCyan", "#E0FFFF");
        colorsMap.put("LightGoldenRodYellow", "#FAFAD2");
        colorsMap.put("LightGray", "#D3D3D3");
        colorsMap.put("LightGrey", "#D3D3D3");
        colorsMap.put("LightGreen", "#90EE90");
        colorsMap.put("LightPink", "#FFB6C1");
        colorsMap.put("LightSalmon", "#FFA07A");
        colorsMap.put("LightSeaGreen", "#20B2AA");
        colorsMap.put("LightSkyBlue", "#87CEFA");
        colorsMap.put("LightSlateGray", "#778899");
        colorsMap.put("LightSlateGrey", "#778899");
        colorsMap.put("LightSteelBlue", "#B0C4DE");
        colorsMap.put("LightYellow", "#FFFFE0");
        colorsMap.put("Lime", "#00FF00");
        colorsMap.put("LimeGreen", "#32CD32");
        colorsMap.put("Linen", "#FAF0E6");
        colorsMap.put("Magenta", "#FF00FF");
        colorsMap.put("Maroon", "#800000");
        colorsMap.put("MediumAquaMarine", "#66CDAA");
        colorsMap.put("MediumBlue", "#0000CD");
        colorsMap.put("MediumOrchid", "#BA55D3");
        colorsMap.put("MediumPurple", "#9370DB");
        colorsMap.put("MediumSeaGreen", "#3CB371");
        colorsMap.put("MediumSlateBlue", "#7B68EE");
        colorsMap.put("MediumSpringGreen", "#00FA9A");
        colorsMap.put("MediumTurquoise", "#48D1CC");
        colorsMap.put("MediumVioletRed", "#C71585");
        colorsMap.put("MidnightBlue", "#191970");
        colorsMap.put("MintCream", "#F5FFFA");
        colorsMap.put("MistyRose", "#FFE4E1");
        colorsMap.put("Moccasin", "#FFE4B5");
        colorsMap.put("NavajoWhite", "#FFDEAD");
        colorsMap.put("Navy", "#000080");
        colorsMap.put("OldLace", "#FDF5E6");
        colorsMap.put("Olive", "#808000");
        colorsMap.put("OliveDrab", "#6B8E23");
        colorsMap.put("Orange", "#FFA500");
        colorsMap.put("OrangeRed", "#FF4500");
        colorsMap.put("Orchid", "#DA70D6");
        colorsMap.put("PaleGoldenRod", "#EEE8AA");
        colorsMap.put("PaleGreen", "#98FB98");
        colorsMap.put("PaleTurquoise", "#AFEEEE");
        colorsMap.put("PaleVioletRed", "#DB7093");
        colorsMap.put("PapayaWhip", "#FFEFD5");
        colorsMap.put("PeachPuff", "#FFDAB9");
        colorsMap.put("Peru", "#CD853F");
        colorsMap.put("Pink", "#FFC0CB");
        colorsMap.put("Plum", "#DDA0DD");
        colorsMap.put("PowderBlue", "#B0E0E6");
        colorsMap.put("Purple", "#800080");
        colorsMap.put("Red", "#FF0000");
        colorsMap.put("RosyBrown", "#BC8F8F");
        colorsMap.put("RoyalBlue", "#4169E1");
        colorsMap.put("SaddleBrown", "#8B4513");
        colorsMap.put("Salmon", "#FA8072");
        colorsMap.put("SandyBrown", "#F4A460");
        colorsMap.put("SeaGreen", "#2E8B57");
        colorsMap.put("SeaShell", "#FFF5EE");
        colorsMap.put("Sienna", "#A0522D");
        colorsMap.put("Silver", "#C0C0C0");
        colorsMap.put("SkyBlue", "#87CEEB");
        colorsMap.put("SlateBlue", "#6A5ACD");
        colorsMap.put("SlateGray", "#708090");
        colorsMap.put("SlateGrey", "#708090");
        colorsMap.put("Snow", "#FFFAFA");
        colorsMap.put("SpringGreen", "#00FF7F");
        colorsMap.put("SteelBlue", "#4682B4");
        colorsMap.put("Tan", "#D2B48C");
        colorsMap.put("Teal", "#008080");
        colorsMap.put("Thistle", "#D8BFD8");
        colorsMap.put("Tomato", "#FF6347");
        colorsMap.put("Turquoise", "#40E0D0");
        colorsMap.put("Violet", "#EE82EE");
        colorsMap.put("Wheat", "#F5DEB3");
        colorsMap.put("White", "#FFFFFF");
        colorsMap.put("WhiteSmoke", "#F5F5F5");
        colorsMap.put("Yellow", "#FFFF00");
        colorsMap.put("YellowGreen", "#9ACD32");
    }

    /**
     * Returns the color code in hexadecimal
     * @param color the color
     * @return the hex value
     */
    public String getHex(String color) {
        return colorsMap.get(color);
    }

    /**
     * returns all the colors available
     * @return the colors
     */
    public ObservableList<String> getAllColors() {
        return FXCollections.observableArrayList(colorsMap.keySet());
    }
}
