package mishindmitriy.timetable.TolgasModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmitriy on 21.05.15.
 */
public class TolgasModel {
    private static final String TAG = "TolgasModel";
    public static final String URL = "http://www.tolgas.ru/services/raspisanie/";
    public static final String formatDate = "dd.MM.yyyy";
    public static final byte TODAY = 0;
    public static final byte TOMORROW = 1;
    public static final byte SEVEN_DAYS = 2;
    public static final byte THIS_WEEK = 3;
    public static final byte NEXT_WEEK = 4;
    public static final byte THIS_MONTH = 5;
    public static final byte NEXT_MONTH = 6;
    public static final byte CASE_GROUP = 7;

    public static final String GROUPS = "groups";
    public static final String CLASSROOMS = "classrooms";
    public static final String PREDODS = "prepods";

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


    private static TagNode groupsPostQuery() throws IOException {
        Log.i(TAG, "postQuery");
        TagNode rootNode = null;
        String htmlCode = null;
        URL url = new URL(TolgasModel.URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        Log.i(TAG, "start connection");
        connection.connect();
        Log.i(TAG, "connection done");
        Log.i(TAG, "start read");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));
        Log.i(TAG, "reader done");
        StringBuilder buf = new StringBuilder();
        String line = "";

        Pattern pattern = Pattern.compile(".*[Ii][Dd](\\s*)=(\\s*)['\"][Vv][Rr]['\"\\s>].*");
        Matcher matcher = pattern.matcher(line);

        while (!matcher.matches())
        {
            line=reader.readLine();
            matcher = pattern.matcher(line);
        }
        pattern=Pattern.compile(".*</[Ss][Ee][Ll][Ee][Cc][Tt]>.*");
        matcher = pattern.matcher(line);
        while (!matcher.matches())
        {
            buf.append(line);
            line =reader.readLine();
            matcher = pattern.matcher(line);
        }

        reader.close();
        Log.i(TAG, "html readed");
        htmlCode = buf.toString();

        int status = connection.getResponseCode();
        if (status != 200) {
            throw new IOException("Post failed with error code " + status);
        }
        connection.disconnect();
        Log.i(TAG, "postQuery done");
        Log.i(TAG, "start clean");
        rootNode = new HtmlCleaner().clean(htmlCode);
        Log.i(TAG, "clean done");

        return rootNode;
    }

    private static TagNode PostQuery(Map<String, String> valuesPairs) throws IOException {
        Log.i(TAG, "postQuery");
        TagNode rootNode = null;
        String htmlCode = null;
        URL url = new URL(TolgasModel.URL);
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
                out.print(URLEncoder.encode(value, "UTF-8"));
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

        Pattern pattern = Pattern.compile(".*[Ii][Dd](\\s*)=(\\s*)['\"][Ss][Ee][Nn][Dd]['\"\\s>].*");
        Matcher matcher = pattern.matcher(line);

        while (!matcher.matches())
        {
            line=reader.readLine();
            matcher = pattern.matcher(line);
        }
        pattern=Pattern.compile(".*</[Tt][Aa][Bb][Ll][Ee]>.*");
        matcher = pattern.matcher(line);
        while (!matcher.matches())
        {
            buf.append(line);
            line =reader.readLine();
            matcher = pattern.matcher(line);
        }

        reader.close();
        Log.i(TAG, "html readed");
        htmlCode = buf.toString();

        int status = connection.getResponseCode();
        if (status != 200) {
            throw new IOException("Post failed with error code " + status);
        }
        connection.disconnect();
        Log.i(TAG, "postQuery done");
        Log.i(TAG, "start clean");
        rootNode = new HtmlCleaner().clean(htmlCode);
        Log.i(TAG, "clean done");

        return rootNode;
    }

    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        return sdf.parse(date);
    }

    public static String getStringDate(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        return sdf.format(c.getTime());
    }

    public static String getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        int d = cal.get(Calendar.DAY_OF_WEEK);
        String dayOfWeek = "Понедельник";
        switch (d) {
            case Calendar.TUESDAY:
                dayOfWeek = "Вторник";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Среда";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Четверг";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Пятница";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Суббота";
                break;
            case Calendar.SUNDAY:
                dayOfWeek = "Воскресенье";
                break;
        }
        return dayOfWeek;
    }

    private static TagNode sendPostToGetShedule(String prepodId, String classId, String groupId,
                                                String fromDate, String toDate)
            throws IOException {
        Log.i(TAG, "sendPost from "+fromDate+" to "+toDate);
        final Map<String, String> valuesPairs = new HashMap<>();

        valuesPairs.put("rel", "0");//0 - запрос групп, 1 - запрос преподавателей, 2 - запрос по аудиториям
        //valuesPairs.put("prep", prepodId);//id преподавателя
        //valuesPairs.put("audi", classId);//id аудитории
        valuesPairs.put("vr", groupId);//id группы
        valuesPairs.put("from", fromDate);//с даты
        valuesPairs.put("to", toDate);//по дату
        valuesPairs.put("submit_button", "ПОКАЗАТЬ");//без этого вроде не возвращалась страница

        //Загружаем html код сайта
        TagNode rootNode = PostQuery(valuesPairs);
        if (rootNode == null) {
            return null;
        }
        Log.i(TAG, "sendPost Done");
        return rootNode;//в html у таблицы с данными id=send
    }

    private static List<DayPairs> parseShedule(TagNode output) {
        //парсит html код с расписанием, выводит список дней
        Log.i(TAG, "parseShedule");
        output = output.findElementByAttValue("id", "send", true, true);
        TagNode[] outputTd = output.getElementsByAttValue("class", "hours", true, true);

        List<DayPairs> arrayDayPair = new ArrayList<>();
        final int len = outputTd.length;

        if (len == 1) return null;

        String classroom;
        String pairNumber;
        String prepod;
        String typePair;
        String subject;

        int day = -1;
        for (int n = 0; n != len; n++) {
            String s = outputTd[n].getText().toString();
            Pattern pattern = Pattern.compile("\\d\\d.\\d\\d.\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                arrayDayPair.add(new DayPairs(s, null));
                day++;
            } else {
                classroom = outputTd[n++].getText().toString();
                pairNumber = outputTd[n++].getText().toString();
                prepod = outputTd[n++].getText().toString();
                typePair = outputTd[n++].getText().toString();
                subject = outputTd[n++].getText().toString();
                if (n != len) n++;
                arrayDayPair.get(day).addPair(new Pair(classroom, pairNumber, prepod, typePair, subject));
            }
        }
        Log.i(TAG, "parseShedule done");
        return arrayDayPair;
    }

    private static String parseLastUpdateServer(TagNode output) {
        Log.i(TAG, "parseLastMod");
        String lastUpdate = null;
        TagNode[] outputTd = output.getElementsByAttValue("class", "last_mod", true, true);
        lastUpdate = outputTd[0].getText().toString();

        return lastUpdate;
    }

    public static List<DayPairs> getSheduleByGroupId(final String groupId, final byte period) throws IOException {
        //TODO сделать обьекты для всех дат, для дат где нет расписания, оставлять пустыми и записывать в кэш
        Log.i(TAG, "getSheduleByGroupId");
        TagNode node = null;
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        String today = getStringDate(c);
        switch (period) {
            case TolgasModel.TODAY:
                node = sendPostToGetShedule("0", "0", groupId, today, today);
                break;

            case TolgasModel.TOMORROW:
                c.roll(Calendar.DAY_OF_YEAR, 1);
                final String tomorrow = getStringDate(c);
                node = sendPostToGetShedule("0", "0", groupId, tomorrow, tomorrow);
                break;

            case TolgasModel.SEVEN_DAYS:
                c.roll(Calendar.DAY_OF_YEAR, 6);
                final String sevendays = getStringDate(c);//сдвинуть на 7 дней, это 6 дней + сегодня
                node = sendPostToGetShedule("0", "0", groupId, today, sevendays);
                break;

            case TolgasModel.THIS_WEEK:
                if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) c.roll(Calendar.DAY_OF_YEAR,-1);
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                final String from = getStringDate(c);
                c.roll(Calendar.DAY_OF_YEAR, 6);
                final String thisweek = getStringDate(c);
                node = sendPostToGetShedule("0", "0", groupId, from, thisweek);
                break;

            case TolgasModel.NEXT_WEEK:
                if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) c.roll(Calendar.DAY_OF_YEAR,5);
                else c.roll(Calendar.DAY_OF_YEAR, 6);
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                final String startweek = getStringDate(c);
                c.roll(Calendar.DAY_OF_YEAR, 6);
                final String endweek = getStringDate(c);
                node = sendPostToGetShedule("0", "0", groupId, startweek, endweek);
                break;

            case TolgasModel.THIS_MONTH:
                c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
                final String startMonth = getStringDate(c);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String endMonth = getStringDate(c);
                node = sendPostToGetShedule("0", "0", groupId, startMonth, endMonth);
                break;

            case TolgasModel.NEXT_MONTH:
                c.roll(Calendar.DAY_OF_YEAR, 30);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
                final String startNextMonth = getStringDate(c);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                final String endNextMonth = getStringDate(c);
                node = sendPostToGetShedule("0", "0", groupId, startNextMonth, endNextMonth);
                break;
        }

        if (node == null) return null;
        //String lastMod = parseLastUpdateServer(node);
        return parseShedule(node);
    }

    public static List<Group> getGroupsList() throws IOException {
        TagNode rootNode = groupsPostQuery();
        if (rootNode == null) return null;
        rootNode = rootNode.findElementByAttValue("name", "vr", true, true);
        List<TagNode> links = rootNode.getChildTagList();
        //парсинг списка групп
        List<Group> groups = new ArrayList<>();
        for (TagNode divElement : links) {
            String groupId = divElement.getAttributeByName("value");
            String groupNumber = divElement.getText().toString();
            groups.add(new Group(groupId, groupNumber));
        }
        return groups;
    }
}
