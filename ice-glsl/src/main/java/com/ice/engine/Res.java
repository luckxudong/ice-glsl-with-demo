package com.ice.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: Jason
 * Date: 13-2-22
 */
public class Res {
    private static Context context;

    public static void build(Context context) {
        Res.context = context;
    }

    public static void release() {
        context = null;
    }

    public static String assetSting(String assetFile) {

        try {
            return streamString(context.getAssets().open(assetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String streamString(InputStream is) {
        BufferedReader reader = null;

        try {
            StringBuilder sb = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();

            while (line != null) {
                sb.append(line).append('\n');
                line = reader.readLine();
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static Bitmap bitmap(int drawableRes) {
        return BitmapFactory.decodeResource(context.getResources(), drawableRes);
    }

    public static InputStream openRaw(int rawRes) {
        return context.getResources().openRawResource(rawRes);
    }

    public static String rawShader(int rawRes) {
        return streamString(openRaw(rawRes));
    }

}
