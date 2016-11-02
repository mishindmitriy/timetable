package mishindmitriy.timetable.utils;

import android.support.annotation.Nullable;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.model.ScheduleSubjectType;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by dmitriy on 21.05.15.
 */
public class DataHelper {
    private static final String URL = "http://www.tolgas.ru/services/raspisanie/";

    private static String doQuery(String inputUrl, @Nullable RequestBody requestBody) throws IOException {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request;
        if (requestBody != null) //значит делаем POST запрос
        {
            request = new Request.Builder()
                    .url(inputUrl)
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(inputUrl)
                    .get()
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

    private static List<Pair> mappingListPairs(String html, ScheduleSubject scheduleSubject) {
        List<Pair> pairs = new ArrayList<>();
        if (html == null || html.length() == 0) return pairs;
        Document doc = Jsoup.parse(html);
        if (doc == null) return pairs;
        Element table = doc.select("table[class=table][id=send]").first();
        if (table == null) return pairs;
        Iterator<Element> iterator = table.select("td[class=hours]").iterator();
        if (iterator == null) return pairs;
        LocalDate date = null;
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.attr("colspan").equals("6")
                    && element.text().equals("По данному запросу ничего не найдено!")) {
                break;
            }
            if (element.attr("colspan").equals("7") && validateParseDate(element.text())) {
                String s = null;
                try {
                    s = element.text();
                    date = LocalDate.parse(s, new DateTimeFormatterBuilder()
                            .appendPattern("dd.MM.yyyy")
                            .toFormatter());
                } catch (IllegalArgumentException e) {
                    s.hashCode();
                }
            } else {
                Pair p = parsePair(element, iterator, date);
                p.setScheduleSubject(scheduleSubject);
                pairs.add(p);
            }
        }
        return pairs;
    }

    private static Pair parsePair(Element element, Iterator<Element> iterator, LocalDate date) {
        Pair pair = new Pair();
        pair.setClassroom(element.text());
        pair.setNumber(Byte.parseByte(iterator.next().text()));
        pair.setTeacher(iterator.next().text());
        pair.setType(iterator.next().text());
        pair.setSubject(iterator.next().text());
        pair.setGroup(iterator.next().text());
        pair.setNote(iterator.next().text());
        pair.setDate(date);
        pair.setId();
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
        RequestBody requestBody = new FormBody.Builder()
                .add("rel", thingTypeId)
                .add("vr", thingId)
                .add("from", fromDate)
                .add("to", toDate)
                .add("submit_button", "ПОКАЗАТЬ")//без этого вроде не возвращалась страница
                .build();
        return doQuery(URL, requestBody);
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

    public static List<Pair> getShedule(ScheduleSubject scheduleSubject, LocalDate from, LocalDate to) throws IOException {
        String html = sendPostToGetShedule(
                String.valueOf(ScheduleSubjectType.getPositionByPeriod(scheduleSubject.getEnumType())),
                scheduleSubject.getServerId(),
                from.toString("dd.MM.yyyy"),
                to.toString("dd.MM.yyyy"));
        return mappingListPairs(html, scheduleSubject);
    }

    private static List<ScheduleSubject> mappingListThings(String html, ScheduleSubjectType type) {
        Document doc = Jsoup.parse(html);
        Element spinner = doc.select("select[id=vr][name=vr]").first();
        Elements elements = spinner.select("option");
        List<ScheduleSubject> scheduleSubjects = new ArrayList<>();
        for (Element element : elements) {
            String serverId = element.attr("value");
            String name = element.text();
            scheduleSubjects.add(new ScheduleSubject()
                    .setServerId(serverId)
                    .setName(name)
                    .setEnumType(type)
                    .setId());
        }
        return scheduleSubjects;
    }

    public static List<ScheduleSubject> loadThing(ScheduleSubjectType scheduleSubjectType) throws IOException {
        String url = URL + "?id=" + ScheduleSubjectType.getPositionByPeriod(scheduleSubjectType);
        return mappingListThings(doQuery(url, null), scheduleSubjectType);
    }

    public static void loadSchedule(final Runnable afterLoadRunnable, final LocalDate startDate) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                long id = Prefs.get().getSelectedThingId();
                if (id == 0) return;
                ScheduleSubject scheduleSubject = realm.where(ScheduleSubject.class)
                        .equalTo("id", id)
                        .findFirst();
                try {
                    LocalDate date = startDate;
                    if (date == null) date = LocalDate.now();
                    List<Pair> pairs = DataHelper.getShedule(scheduleSubject,
                            date.minusDays(100),
                            date.plusDays(100));
                    for (Pair p : pairs) {
                        Pair existPair = realm.where(Pair.class)
                                .equalTo("id", p.getId())
                                .findFirst();
                        if (existPair != null && existPair.isNotified()) {
                            p.setNotified();
                        }
                    }
                    realm.copyToRealmOrUpdate(pairs);
                    // TODO: 19.09.16 add remove old pairs
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                if (afterLoadRunnable != null) {
                    afterLoadRunnable.run();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (BuildConfig.DEBUG) {
                    try {
                        throw new Exception(error);
                    } catch (Exception ignored) {

                    }
                }
                realm.close();
                if (afterLoadRunnable != null) {
                    afterLoadRunnable.run();
                }
            }
        });
    }
}
