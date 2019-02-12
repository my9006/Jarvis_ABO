package com.jarvis_abo.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.jarvis_abo.JA_application;
import com.jarvis_abo.R;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mxo on 20-May-16.
 */
public class Type {

    public static ArrayList<String> types = new ArrayList<String>() {{
        add("fa-home");
        add("fa-briefcase");
        add("fa-shopping-cart");
        add("fa-plus-square");
        add("fa-map-marker");
    }};
    public static HashMap<String, Integer> colors = new HashMap<String, Integer>(){{
        put("fa-home", R.color.home);
        put("fa-briefcase", R.color.briefcase);
        put("fa-shopping-cart", R.color.shopping_cart);
        put("fa-plus-square", R.color.plus_square);
        put("fa-map-marker", R.color.map_marker);

    }};

    public static BitmapDescriptor getCustomMarker(final String name) {
        final Icon icon = Iconify.findIconForKey(name);
        IconDrawable id = new IconDrawable(JA_application.getInstance(), name) {
            @Override
            public void draw(Canvas canvas) {
                // The TextPaint is defined in the constructor
                // but we override it here
                TextPaint paint = new TextPaint();
                paint.setTypeface( Iconify.findTypefaceOf(icon).getTypeface(JA_application.getInstance()));
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setUnderlineText(false);

                // If you need a custom color specify it here
                paint.setColor(JA_application.getInstance().getResources().getColor(Type.colors.get(name)));

                paint.setAntiAlias(true);
                paint.setTextSize(getBounds().height());
                Rect textBounds = new Rect();
                String textValue = String.valueOf(icon.character());
                paint.getTextBounds(textValue, 0, 1, textBounds);
                float textBottom = (getBounds().height() - textBounds.height()) / 2f + textBounds.height() - textBounds.bottom;
                canvas.drawText(textValue, getBounds().width() / 2f, textBottom, paint);
            }

        }.actionBarSize();
        Drawable d = id.getCurrent();
        Bitmap bm = Bitmap.createBitmap(id.getIntrinsicWidth(), id.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);

        d.draw(c);

        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    public static Bitmap getCustomMarkerBitmap(final String name) {
        final Icon icon = Iconify.findIconForKey(name);
        IconDrawable id = new IconDrawable(JA_application.getInstance(), name) {
            @Override
            public void draw(Canvas canvas) {
                // The TextPaint is defined in the constructor
                // but we override it here
                TextPaint paint = new TextPaint();
                paint.setTypeface( Iconify.findTypefaceOf(icon).getTypeface(JA_application.getInstance()));
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setUnderlineText(false);

                // If you need a custom color specify it here
                paint.setColor(JA_application.getInstance().getResources().getColor(Type.colors.get(name)));

                paint.setAntiAlias(true);
                paint.setTextSize(getBounds().height());
                Rect textBounds = new Rect();
                String textValue = String.valueOf(icon.character());
                paint.getTextBounds(textValue, 0, 1, textBounds);
                float textBottom = (getBounds().height() - textBounds.height()) / 2f + textBounds.height() - textBounds.bottom;
                canvas.drawText(textValue, getBounds().width() / 2f, textBottom, paint);
            }

        }.actionBarSize();
        Drawable d = id.getCurrent();
        Bitmap bm = Bitmap.createBitmap(id.getIntrinsicWidth(), id.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        return bm;
    }

}
