package apps.ftumj.ac.id.androidgeneratetest;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import apps.ftumj.ac.id.androidgeneratetest.model.Rumah;

/**
 * Created by winzaldi on 12/19/17.
 */

public class Utils {

    public static String loadJson(Resources resources, final int resId) {
        InputStream is = resources.openRawResource(resId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        return writer.toString();
    }
    public  static List<Rumah> getListRumah(String json){
         return new Gson().fromJson(json,new TypeToken<List<Rumah>>(){}.getType());
    }
}
