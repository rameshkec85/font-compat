package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.ReflectionUtils;

import android.annotation.TargetApi;
import android.graphics.FontFamily;
import android.graphics.FontListParser;
import android.graphics.Typeface;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jhansche
 * @since 3/18/16
 */
@TargetApi(24)
public class FontManagerImpl24 extends FontManagerImpl21 {

    private static final String TAG = FontManagerImpl24.class.getSimpleName();

    public static void init(FontListParser.Config config) {
        Map<String, Typeface> systemFonts = ReflectionUtils.getSystemFontsMap();

        for (int i = 0; i < config.families.size(); i++) {
            FontListParser.Family f = config.families.get(i);

            try {
                FontFamily family = makeFamilyFromParsed(f);
                Typeface typeface = ReflectionUtils.createTypefaceFromFamily(family);
                if (systemFonts != null) systemFonts.put(f.name, typeface);
            } catch (Exception e) {
                Log.e(TAG, "TODO", e);
            }
        }
    }

    private static FontFamily makeFamilyFromParsed(FontListParser.Family family) throws NoSuchMethodError {
        final FontFamily fontFamily = new FontFamily(family.lang, family.variant);
        final int ttcIndex = 0;
        final List<FontListParser.Axis> axes = Collections.emptyList();

        for (FontListParser.Font font : family.fonts) {
            Log.v(TAG, "makeFamilyFromParsed: " + font.fontName + ", " + font.weight + ",  " + font.isItalic);

            try {
                FileInputStream fis = new FileInputStream(font.fontName);
                FileChannel channel = fis.getChannel();
                ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                boolean result = fontFamily.addFontWeightStyle(buffer, ttcIndex, axes, font.weight, font.isItalic);
                Log.v(TAG, "addFontWeightStyle: result=" + result);
            } catch (FileNotFoundException e) {
                Log.w(TAG, "TODO", e);
            } catch (IOException e) {
                Log.w(TAG, "TODO", e);
            }
        }
        return fontFamily;
    }
}