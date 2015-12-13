package mishindmitriy.timetable.utils;

import android.support.annotation.Nullable;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mishindmitriy.timetable.model.data.DateFormatter;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.model.data.ThingTypeConverter;
import mishindmitriy.timetable.model.data.entity.Pair;
import mishindmitriy.timetable.model.data.entity.Thing;

/**
 * Created by dmitriy on 21.05.15.
 */
public class ParseHelper {
    public static final String URL = "http://www.tolgas.ru/services/raspisanie/";

    private static String doQuery(String inputUrl, @Nullable Map<String, String> valuesPairs) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        httpClient.setReadTimeout(5, TimeUnit.SECONDS);
        Request request;
        if (valuesPairs != null) //значит делаем POST запрос
        {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (Map.Entry<String, String> pair : valuesPairs.entrySet()) {
                builder.addEncoded(pair.getKey(), pair.getValue());
            }
            RequestBody requestBody = builder.build();
            request = new Request.Builder()
                    .url(inputUrl)
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(inputUrl)
                    .build();
        }

        Response response = httpClient.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException("query failed with error code " + response.code());
        }
        String html = response.body().string();
        validateHtml(html);
        return html;
    }

    private static List<Pair> mappingListPairs(String html, Thing thing) {
        List<Pair> pairs = new ArrayList<>();
        if (html == null || html.length() == 0) return pairs;
        Document doc = Jsoup.parse(html);
        if (doc == null) return pairs;
        Element table = doc.select("table[class=table][id=send]").first();
        if (table == null) return pairs;
        Iterator<Element> iterator = table.select("td[class=hours]").iterator();
        if (iterator == null) return pairs;
        Date date = null;
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.attr("colspan").equals("6") && element.text().equals("По данному запросу ничего не найдено!")) {
                break;
            }
            if (element.attr("colspan").equals("7") && validateParseDate(element.text())) {
                date = DateFormatter.parseDate(element.text());
            } else {
                Pair p = parsePair(element, iterator, date);
                p.setThing(thing);
                pairs.add(p);
            }
        }
        return pairs;
    }

    private static Pair parsePair(Element element, Iterator<Element> iterator, Date date) {
        Pair pair = new Pair();
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
        CharSequence uniqueString = "Вопросы по телефону: 22-13-97 – отдел организации учебного процесса";
        if (!html.contains(uniqueString))
            throw new IOException("html code not contains tolgas schedule");
        uniqueString = "Выберите диапазон даты для отображения расписания";
        if (!html.contains(uniqueString))
            throw new IOException("html code not contains tolgas schedule");
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

    private static boolean validateParseDate(String s) {
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
        return mappingListPairs(html, thing);
    }

    private static List<Thing> mappingListThings(String html, ThingType type) {
        Document doc = Jsoup.parse(html);
        Element spinner = doc.select("select[id=vr][name=vr]").first();
        Elements elements = spinner.select("option");
        List<Thing> things = new ArrayList<>();
        for (Element element : elements) {
            things.add(new Thing(element.attr("value"), element.text(), type));
        }
        return things;
    }

    public static List<Thing> getSomeThing(ThingType thingType) throws IOException {
        String url = URL + "?id=" + ThingTypeConverter.getPositionByPeriod(thingType);
        return mappingListThings(doQuery(url, null), thingType);
    }
}
