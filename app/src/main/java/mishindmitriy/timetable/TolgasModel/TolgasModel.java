package mishindmitriy.timetable.TolgasModel;

import android.util.Log;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmitriy on 21.05.15.
 */
public class TolgasModel {
    private static final String TAG="TolgasModel";
    public static final String URL = "http://www.tolgas.ru/services/raspisanie/";
    public static final byte TODAY =0;
    public static final byte TOMORROW =1;
    public static final byte SEVEN_DAYS =2;
    public static final byte THIS_WEEK=3;
    public static final byte THIS_MONTH=4;

    private static TagNode PostQuery(Map<String, String> valuesPairs) throws IOException {
        TagNode rootNode = null;

        URL url = new URL(TolgasModel.URL);
        URLConnection connection = url.openConnection();
        if (valuesPairs != null) //значит делаем POST запрос
        {
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
        }
        //Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        //Загружаем html код сайта
        rootNode = cleaner.clean(connection.getInputStream(), "windows-1251");

        return rootNode;
    }

    public static String getStringDate(Calendar c) {
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        String str = "";
        if (day < 10) str = "0";
        str = str + Integer.toString(day) + ".";
        if (month < 10) str = str + "0";
        str = str + month + "." + Integer.toString(c.get(Calendar.YEAR));
        return str;
    }

    private static TagNode sendPostToGetShedule(String prepodId, String classId, String groupId,
                                                String fromDate, String toDate)
            throws IOException {
        final Map<String, String> valuesPairs = new HashMap<>();

        valuesPairs.put("rel", "0");//0 - запрос групп, 1 - запрос преподавателей, 2 - запрос по аудиториям
        valuesPairs.put("prep", prepodId);//id преподавателя
        valuesPairs.put("audi", classId);//id аудитории
        valuesPairs.put("vr", groupId);//id группы
        valuesPairs.put("from", fromDate);//с даты
        valuesPairs.put("to", toDate);//по дату
        valuesPairs.put("submit_button", "%CF%CE%CA%C0%C7%C0%D2%DC");//без этого вроде не возвращалась страница

        //Загружаем html код сайта
        TagNode rootNode = PostQuery(valuesPairs);
        if (rootNode == null) {
            return null;
        }
        return rootNode.findElementByAttValue("id", "send", true, true);//в html у таблицы с данными id=send
    }

    private static List<DayPairs> parseShedule(TagNode output) {
        //парсит html код с расписанием, выводит список дней
        TagNode[] outputTd = output.getElementsByAttValue("class", "hours", true, true);

        List<DayPairs> arrayDayPair = new ArrayList<>();
        final int len = outputTd.length;

        if (len == 1) return null;

        String classroom = new String();
        String pairNumber = new String();
        String prepod = new String();
        String typePair = new String();
        String subject = new String();

        String line = "";
        int i = 1;
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
        return arrayDayPair;
    }

    private static String rollOffset(Calendar c,int offset)
    {
        if ((c.getMaximum(Calendar.DAY_OF_MONTH) - c.get(Calendar.DAY_OF_MONTH)) < offset) {
            c.roll(Calendar.DAY_OF_MONTH, offset);
            c.roll(Calendar.MONTH, 1);
        } else c.roll(Calendar.DAY_OF_MONTH, offset);
        if (c.get(Calendar.MONTH) == 0) c.roll(Calendar.YEAR, 1);
        return getStringDate(c);
    }

    public static List<DayPairs> getSheduleByGroupId(String groupId, int offset) throws IOException {
        Calendar c = Calendar.getInstance();
        String from = getStringDate(c);
        Log.d(TAG,"from:"+from);

        if ((c.getMaximum(Calendar.DAY_OF_MONTH) - c.get(Calendar.DAY_OF_MONTH)) < offset) {
            c.roll(Calendar.DAY_OF_MONTH, offset);
            c.roll(Calendar.MONTH, 1);
        } else c.roll(Calendar.DAY_OF_MONTH, offset);
        if (c.get(Calendar.MONTH) == 0) c.roll(Calendar.YEAR, 1);

        String to = getStringDate(c);
        Log.d(TAG,"to:"+to);
        TagNode node = sendPostToGetShedule("0", "0", groupId, from, to);
        if (node == null) return null;
        return parseShedule(node);
    }

    public static List<DayPairs> getSheduleByGroupId(final String groupId,final byte period) throws IOException {
        TagNode node=null;
        Calendar c = Calendar.getInstance();
        String today = getStringDate(c);
        switch (period) {
            case TolgasModel.TODAY:
                node = sendPostToGetShedule("0", "0", groupId, today, today);
            break;
            case TolgasModel.TOMORROW:
                final String tomorrow=rollOffset(c,1);
                node = sendPostToGetShedule("0", "0", groupId, tomorrow, tomorrow);
                break;
            case TolgasModel.SEVEN_DAYS:
                final String sevendays=rollOffset(c,6);//сдвинуть на 7 дней, это 6 дней + сегодня
                node = sendPostToGetShedule("0", "0", groupId, today, sevendays);
                break;
            case TolgasModel.THIS_WEEK:

                final String from=getStringDate(c);

                final String thisweek=rollOffset(c,c.getMaximum(Calendar.DAY_OF_WEEK)-c.get(Calendar.DAY_OF_WEEK));//сдвинуть на 7 дней, это 6 дней + сегодня
                //node = sendPostToGetShedule("0", "0", groupId, today, thisweek);
                break;
        }

        if (node == null) return null;
        return parseShedule(node);
    }

    public static List<Group> getGroupsList() throws IOException {
        TagNode rootNode = PostQuery(null);
        if (rootNode == null) return null;
        rootNode = rootNode.findElementByAttValue("name", "vr", true, true);
        List<TagNode> links = rootNode.getChildTagList();
        //парсинг списка групп
        List<Group> groups = new ArrayList<>();
        Iterator<TagNode> iterator = links.iterator();
        while (iterator.hasNext()) {
            TagNode divElement = iterator.next();
            groups.add(new Group(divElement.getAttributeByName("value"), divElement.getText().toString()));
        }
        return groups;
    }
}
