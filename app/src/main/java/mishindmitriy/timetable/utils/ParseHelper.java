package mishindmitriy.timetable.utils;

import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final String TAG = "TolgasModel";

    private static String doQuery(String inpupUrl, @Nullable Map<String, String> valuesPairs) throws IOException{

        String html="";
        HttpURLConnection connection=null;
        try {
            URL url = new URL(inpupUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
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
                    out.print(URLEncoder.encode(value, "windows-1251"));
                }
                out.close();
                connection.getOutputStream().close();
            }
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new IOException("query failed with error code " + status);
            }

            StringBuilder buf = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }

            reader.close();
            connection.getInputStream().close();


            html = buf.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if (connection!=null) connection.disconnect();
            validateHtml(html);
        }
        return html;
    }

    private static List<Pair> mappingListPairs(String html,Thing thing)
    {
        List<Pair> pairs=new ArrayList<>();
        if (html==null || html.length()==0) return pairs;
        Document doc = Jsoup.parse(html);
        if (doc==null) return pairs;
        Element table = doc.select("table[class=table][id=send]").first();
        if (table==null) return pairs;
        Iterator<Element> iterator = table.select("td[class=hours]").iterator();
        if (iterator==null) return pairs;
        Date date=null;
        while (iterator.hasNext())
        {
            Element element =iterator.next();
            if (element.attr("colspan").equals("6") && element.text().equals("По данному запросу ничего не найдено!"))
            {
                break;
            }
            if (element.attr("colspan").equals("7")&&validateParseDate(element.text()))
            {
                date=DateFormatter.parseDate(element.text());
            }
            else {
                Pair p=parsePair(element,iterator,date);
                p.setThing(thing);
                pairs.add(p);
            }
        }
        return pairs;
    }

    private static Pair parsePair(Element element,Iterator<Element> iterator,Date date)
    {
        Pair pair=new Pair();
        pair.setClassroom(element.text());
        pair.setNumber(Byte.parseByte(iterator.next().text()));
        pair.setTeacher(iterator.next().text());
        pair.setType(iterator.next().text());
        pair.setSubject(iterator.next().text());
        pair.setGroup(iterator.next().text());
        pair.setNote(iterator.next().text());
        pair.setDate(date);
        return pair;
    }

    private static void validateHtml(String html) throws IOException {
        CharSequence uniqueString="Вопросы по телефону: 22-13-97 – отдел организации учебного процесса";
        if (!html.contains(uniqueString)) throw new IOException("html code not correct");
        uniqueString="Выберите диапазон даты для отображения расписания";
        if (!html.contains(uniqueString)) throw new IOException("html code not correct");
    }

    private static String sendPostToGetShedule(String thingTypeId, String thingId,
                                                String fromDate, String toDate)
            throws IOException {
        final Map<String, String> valuesPairs = new HashMap<>();

        valuesPairs.put("rel", thingTypeId);
        valuesPairs.put("vr", thingId);
        valuesPairs.put("from", fromDate);
        valuesPairs.put("to", toDate);
        valuesPairs.put("submit_button", "ПОКАЗАТЬ");//без этого вроде не возвращалась страница

        //Загружаем html код сайта
        return doQuery(URL, valuesPairs);
    }

    private static boolean validateParseDate(String s)
    {
        Pattern pattern = Pattern.compile("\\d\\d.\\d\\d.\\d\\d\\d\\d");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

   /* private static String parseLastUpdateServer(TagNode output) {
        String lastUpdate = null;
        TagNode[] outputTd = output.getElementsByAttValue("class", "last_mod", true, true);
        lastUpdate = outputTd[0].getText().toString();
        return lastUpdate;
    }*/

    public static List<Pair> getShedule(Thing thing, Date from, Date to) throws IOException {
        String html = sendPostToGetShedule(
                String.valueOf(ThingTypeConverter.getPositionByPeriod(thing.getType())),
                thing.getServerId(),
                DateFormatter.DateToString(from),
                DateFormatter.DateToString(to));
        return mappingListPairs(html,thing);
    }

    private static List<Thing> mappingListThings(String html,ThingType type)
    {
        Document doc = Jsoup.parse(html);
        Element spinner=doc.select("select[id=vr][name=vr]").first();
        Elements elements = spinner.select("option");
        List<Thing> things=new ArrayList<>();
        for (Element element:elements)
        {
            things.add(new Thing(element.attr("value"),element.text(),type));
        }
        return things;
    }

    public static List<Thing> getSomeThing(ThingType thingType) throws IOException {
        String url=URL+"?id="+ThingTypeConverter.getPositionByPeriod(thingType);
        return mappingListThings(doQuery(url, null),thingType);
    }
}
