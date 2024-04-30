package com.app.ctatracker.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import com.app.ctatracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static double calculateLuminance(int color) {
        double r = Color.red(color) / 255.0;
        double g = Color.green(color) / 255.0;
        double b = Color.blue(color) / 255.0;

        r = (r <= 0.03928) ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
        g = (g <= 0.03928) ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
        b = (b <= 0.03928) ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    public static int getTextColorForBackground(String backgroundColorHex) {
        int color = Color.parseColor(backgroundColorHex);
        double luminance = calculateLuminance(color);

        return luminance < 0.25 ? Color.WHITE : Color.BLACK;
    }

    public static String convertTimeFormat(String originalTime) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        SimpleDateFormat targetFormat = new SimpleDateFormat("h:mm a");
        try {
            Date date = originalFormat.parse(originalTime);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        Date now = new Date();
        return sdf.format(now);
    }

    public static void dialog(Context context, String title, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(AppCompatResources.getDrawable(context, R.drawable.bus_icon))
                .setMessage(message)
                .setPositiveButton(positiveButtonText, listener)
                .setNegativeButton(negativeButtonText, listener)
                .create()
                .show();

    }
}