package edu.augustana.osleventsandroid;

import android.graphics.Color;

public class Theme {
    public static Theme themeInstance = new Theme();

    private static int backgroundColor = Color.rgb(255,255,255);
    private static int darkerBackground = Color.rgb(211,211,211);
    private static int textColor = Color.rgb(0,0,0);
    private static int checkboxColor = Color.BLUE;
    private static int checkboxBackground = Color.WHITE;
    private static int buttonColor = Color.GRAY;

    public static void whiteTheme() {
        backgroundColor = Color.rgb(255, 255, 255);
        darkerBackground = Color.rgb(211, 211, 211);
        textColor = Color.rgb(0, 0, 0);
        checkboxColor = Color.BLUE;
        checkboxBackground = Color.WHITE;
        buttonColor = Color.GRAY;
    }

    public static void darkTheme(){
        backgroundColor = Color.rgb(48, 48, 48);
        darkerBackground = Color.rgb(33, 33, 33);
        textColor = Color.rgb(255, 255, 255);
        checkboxColor = Color.BLACK;
        checkboxBackground = Color.WHITE;
        buttonColor = Color.LTGRAY;
    }

    public static void seaBlueTheme(){
        backgroundColor = Color.rgb(0, 105, 148);
        darkerBackground = Color.rgb(0, 89, 126);
        textColor = Color.rgb(255, 255, 255);
        checkboxColor = Color.rgb(0, 105, 148);
        checkboxBackground = Color.WHITE;
        buttonColor = Color.rgb(0, 161, 228);
    }


    public static void twilightPurpleTheme(){
        backgroundColor = Color.rgb(101, 101, 142);
        darkerBackground = Color.rgb(80, 80, 100);
        textColor = Color.rgb(255, 255, 255);
        checkboxColor = Color.rgb(101, 101, 142);
        checkboxBackground = Color.WHITE;
        buttonColor = Color.rgb(154, 129, 171);
    }

    public static int getBackgroundColor(){
        return backgroundColor;
    }
    public static int getDarkerBackground(){
        return darkerBackground;
    }
    public static int getTextColor(){
        return textColor;
    }
    public static int getCheckboxColor(){
        return checkboxColor;
    }
    public static int getCheckboxBackground(){
        return checkboxBackground;
    }
    public static int getButtonColor(){
        return buttonColor;
    }




}
