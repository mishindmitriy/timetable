package mishindmitriy.timetable.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mishindmitriy.timetable.model.data.DateFormatter;
import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.Thing;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.model.data.ThingTypeConverter;

/**
 * Created by dmitriy on 21.05.15.
 */
public class ParseHelper {
    public static final String URL = "http://www.tolgas.ru/services/raspisanie/";
    public static final String URLprepods = "http://www.tolgas.ru/services/raspisanie/?id=1";
    public static final String URLclassrooms = "http://www.tolgas.ru/services/raspisanie/?id=2";
    public static final String formatDate = "dd.MM.yyyy";
    public static final CharSequence firstPairStart = "09.00";
    public static final CharSequence firstPairEnd = "10.35";
    public static final CharSequence secondPairStart = "10.45";
    public static final CharSequence secondPairEnd = "12.20";
    public static final CharSequence thirdPairStart = "13.00";
    public static final CharSequence thirdPairEnd = "14.35";
    public static final CharSequence fourthPairStart = "14.45";
    public static final CharSequence fourthPairEnd = "16.20";
    public static final CharSequence fifthPairStart = "16.30";
    public static final CharSequence fifthPairEnd = "18.05";
    public static final CharSequence sixthPairStart = "18.15";
    public static final CharSequence sixthPairEnd = "19.50";
    public static final CharSequence seventhPairStart = "20.00";
    public static final CharSequence seventhPairEnd = "21.35";
    private static final String TAG = "TolgasModel";

    private static TagNode someQuery(String inpupUrl, @Nullable Map<String, String> valuesPairs, String patternStart, String patternEnd) throws IOException {
        Log.i(TAG, "postQuery");
        TagNode rootNode = null;
        String htmlCode = null;
        URL url = new URL(inpupUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (valuesPairs != null) //значит делаем POST запрос
        {
            Log.i(TAG, "start post");
            connection.setDoOutput(true);
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            boolean first = true; //чтобы & не поставить перед переменными
            for (Map.Entry<String, String> pair : valuesPairs.entrySet()) {
                if (first) first = false;
                else out.print('&');
                String name = pair.getKey();
                String value = pair.getValue();
                out.print(name);
                out.print('=');
                out.print(URLEncoder.encode(value, "windows-1251"));
            }
            out.close();
            Log.i(TAG, "post done");
        }
        Log.i(TAG, "start connection");
        connection.connect();
        Log.i(TAG, "connection done");
        Log.i(TAG, "start read");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));
        Log.i(TAG, "reader done");
        StringBuilder buf = new StringBuilder();
        String line = "";

        int status = connection.getResponseCode();
        if (status != 200) {
            throw new IOException("query failed with error code " + status);
        }

        Pattern pattern = Pattern.compile(patternStart);
        Matcher matcher = pattern.matcher(line);

        while (!matcher.matches()) {
            line = reader.readLine();
            if (line == null) throw new IOException();
            matcher = pattern.matcher(line);
        }
        pattern = Pattern.compile(patternEnd);
        matcher = pattern.matcher(line);
        while (!matcher.matches()) {
            buf.append(line);
            line = reader.readLine();
            if (line == null) throw new IOException();
            matcher = pattern.matcher(line);
        }

        reader.close();
        htmlCode = buf.toString();

        connection.disconnect();
        rootNode = new HtmlCleaner().clean(htmlCode);

        return rootNode;
    }

    private static TagNode sendPostToGetShedule(String thingTypeId, String thingId,
                                                String fromDate, String toDate)
            throws IOException {
        Log.i(TAG, "sendPost from " + fromDate + " to " + toDate);
        final Map<String, String> valuesPairs = new HashMap<>();

        valuesPairs.put("rel", thingTypeId);
        valuesPairs.put("vr", thingId);
        valuesPairs.put("from", fromDate);
        valuesPairs.put("to", toDate);
        valuesPairs.put("submit_button", "ПОКАЗАТЬ");//без этого вроде не возвращалась страница

        //Загружаем html код сайта
        String pattertStart = ".*[Ii][Dd](\\s*)=(\\s*)['\"][Ss][Ee][Nn][Dd]['\"\\s>].*";
        String patternEnd = ".*</[Tt][Aa][Bb][Ll][Ee]>.*";
        TagNode rootNode = someQuery(URL, valuesPairs, pattertStart, patternEnd);
        if (rootNode == null) {
            return null;
        }
        return rootNode;//в html у таблицы с данными id=send
    }

    private static List<Pair> mapPairList(TagNode output, Thing thing) {
        //парсит html код с расписанием
        output = output.findElementByAttValue("id", "send", true, true);
        TagNode[] outputTd = output.getElementsByAttValue("class", "hours", true, true);

        List<Pair> pairs= new ArrayList<>();
        int len = outputTd.length;
        if (len == 1) len = 0;

        String date = null;
        for (int n = 0; n < len; n++) {
            String s = outputTd[n].getText().toString();
            Pattern pattern = Pattern.compile("\\d\\d.\\d\\d.\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                date=s;
            } else {
                Pair pair=new Pair();
                pair.setClassroom(outputTd[n++].getText().toString());
                pair.setPairNumber(Integer.parseInt(outputTd[n++].getText().toString()));
                pair.setPrepod(outputTd[n++].getText().toString());
                pair.setTypePair(outputTd[n++].getText().toString());
                pair.setSubject(outputTd[n++].getText().toString());
                pair.setGroups(outputTd[n].getText().toString());
                pair.setDate(DateFormatter.parseDate(date));
                pairs.add(pair);
                if (n != len) n++;
            }
        }
        return pairs;
    }

    private static String parseLastUpdateServer(TagNode output) {
        String lastUpdate = null;
        TagNode[] outputTd = output.getElementsByAttValue("class", "last_mod", true, true);
        lastUpdate = outputTd[0].getText().toString();

        return lastUpdate;
    }

    public static List<Pair> getShedule(Thing thing, Date from, Date to)
            throws IOException {
        TagNode node = sendPostToGetShedule(String.valueOf(ThingTypeConverter.getPositionByPeriod(thing.getWhatThing()))
                , thing.getThingID(), DateFormatter.DateToString(from), DateFormatter.DateToString(to));
        if (node == null) return null;
        return mapPairList(node,thing);
    }

    public static List<Thing> getSomeThing(String url) throws IOException {
        String pattertStart = ".*[Ii][Dd](\\s*)=(\\s*)['\"][Vv][Rr]['\"\\s>].*";
        String patternEnd = ".*</[Ss][Ee][Ll][Ee][Cc][Tt]>.*";
        ThingType thing = null;
        switch (url) {
            case URL:
                thing = ThingType.GROUP;
                break;
            case URLprepods:
                thing = ThingType.TEACHER;
                break;
            case URLclassrooms:
                thing = ThingType.CLASSROOM;
                break;
        }
        TagNode rootNode = someQuery(url, null, pattertStart, patternEnd);
        if (rootNode == null) return null;
        rootNode = rootNode.findElementByAttValue("name", "vr", true, true);
        List<TagNode> links = rootNode.getChildTagList();
        //парсинг списка групп
        List<Thing> things = new ArrayList<>();
        for (TagNode divElement : links) {
            String groupId = divElement.getAttributeByName("value");
            String groupNumber = divElement.getText().toString();
            things.add(new Thing(groupId, groupNumber, thing));
        }
        return things;
    }

    public static List<Thing> getGroupsList() throws IOException {
        return getSomeThing(URL);
    }

    public static List<Thing> getTeachersList() throws IOException {
        return getSomeThing(URLprepods);
    }

    public static List<Thing> getClassroomsList() throws IOException {
        return getSomeThing(URLclassrooms);
    }

    public static class Saturday {
        public static final CharSequence firstPairStart = "08.30";
        public static final CharSequence firstPairEnd = "10.05";
        public static final CharSequence secondPairStart = "10.15";
        public static final CharSequence secondPairEnd = "11.50";
        public static final CharSequence thirdPairStart = "12.35";
        public static final CharSequence thirdPairEnd = "14.10";
        public static final CharSequence fourthPairStart = "14.20";
        public static final CharSequence fourthPairEnd = "15.55";
        public static final CharSequence fifthPairStart = "16.05";
        public static final CharSequence fifthPairEnd = "17.35";
    }

}
