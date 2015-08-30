package mishindmitriy.timetable;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
    public static final String URL="http://www.tolgas.ru/services/raspisanie/";

    private static TagNode PostQuery(Map<String, String> valuesPairs) throws IOException
    {
        TagNode rootNode=null;

            URL url = new URL(TolgasModel.URL);
            URLConnection connection = url.openConnection();
            if (valuesPairs !=null) //значит делаем POST запрос
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
            rootNode=cleaner.clean(connection.getInputStream(),"windows-1251");

        return rootNode;
    }

    public static String getStringDate(Calendar c)
    {
        int day=c.get(Calendar.DAY_OF_MONTH);
        int month=c.get(Calendar.MONTH)+1;
        String str="";
        if (day<10) str="0";
        str=str+ Integer.toString(day)+ "." ;
        if (month<10) str=str+"0";
        str=str+ month + "." + Integer.toString(c.get(Calendar.YEAR));
        return str;
    }

    private static TagNode sendPostToGetTodayShedule(String prepodId, String classId, String groupId) throws IOException
    {
        final Map<String, String> valuesPairs = new HashMap<>();

        valuesPairs.put("rel", "0");//0 - запрос групп, 1 - запрос преподавателей, 2 - запрос по аудиториям
        valuesPairs.put("prep", prepodId);//id преподавателя
        valuesPairs.put("audi", classId);//id аудитории
        valuesPairs.put("vr", groupId);//id группы

        Calendar c = Calendar.getInstance();
        int offset=7;
        String str=getStringDate(c);
        valuesPairs.put("from", str);
        if ((c.getMaximum(Calendar.DAY_OF_MONTH)-c.get(Calendar.DAY_OF_MONTH))<=offset)
        {
            c.roll(Calendar.DAY_OF_MONTH, offset);
            c.roll(Calendar.MONTH, 1);
        }
        else c.roll(Calendar.DAY_OF_MONTH, offset);
        if (c.get(Calendar.MONTH)==0) c.roll(Calendar.YEAR,1);

        str=getStringDate(c);
        valuesPairs.put("to", str);
        valuesPairs.put("submit_button", "%CF%CE%CA%C0%C7%C0%D2%DC");

        //Загружаем html код сайта
        TagNode rootNode= PostQuery(valuesPairs);

        if (rootNode==null)
        {
            return null;
        }
        return rootNode.findElementByAttValue("id", "send", true, true);
    }

    private static List<DayPairs> parseTodayShedule(TagNode output)
    {
        List<String> out=new ArrayList<>();
        TagNode[] outputTd=output.getElementsByAttValue("class", "hours", true, true);

        List<DayPairs> arrayDayPair=new ArrayList<>();
        final int len=outputTd.length;

        if (len==1) return null;

        String classroom=new String();
        String pairNumber=new String();
        String prepod=new String();
        String typePair=new String();
        String subject=new String();

        String line="";
        int i=1;
        int day=-1;
        for(int n=0; n!=len; n++) {
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
                if (n!=len) n++;
                arrayDayPair.get(day).addPair(new Pair(classroom, pairNumber, prepod, typePair, subject));
            }
        }
        i=1;
        day=0;
        for(int n=0; n!=len; n++)
        {


            try
            {
                outputTd[n].getAttributeByName("colspan").toString();
                String s= outputTd[n].getText().toString();



                out.add("      " + s);
                SimpleDateFormat d=new SimpleDateFormat(s);
                //arrayDayPair.add(new DayPairs(d.getCalendar(), null));
                if (n!=0) day++;
            }
            catch(Exception e)
            {
                switch (i)
                {
                    case 1:
                        line=line+outputTd[n].getText().toString()+", ";
                        classroom=outputTd[n].getText().toString();
                        break;
                    case 2:
                        line=line+outputTd[n].getText().toString()+" пара, ";
                        pairNumber=outputTd[n].getText().toString();
                        break;
                    case 3:
                        line=line+outputTd[n].getText().toString()+", ";
                        prepod=outputTd[n].getText().toString();
                        break;
                    case 4:
                        line=line+outputTd[n].getText().toString()+", ";
                        typePair=outputTd[n].getText().toString();
                        break;
                    case 5:
                        line=line +outputTd[n].getText().toString();
                        subject=outputTd[n].getText().toString();
                        break;
                    case 7:
                        out.add(line);
                        line="";
                        i=0;
                        //arrayDayPair.get(day).addPair(new Pair(classroom,pairNumber,prepod,typePair,subject));

                        break;
                    default:
                        break;
                }
                i++;
            }
        }
        //return out;
        return arrayDayPair;
    }

    public static List<DayPairs> getTodaySheduleByIdGroup(String groupId) throws IOException{
        TagNode node=sendPostToGetTodayShedule("0","0",groupId);
        if (node==null) return null;
        return parseTodayShedule(node);
    }

//    public static List<String> getTomorrowSheduleByIdGroup(String groupId) {
//        return parseTodayShedule(sendPostToGetTodayShedule("0","0",groupId));
//    }


    public static List<Group> getGroupsList() throws IOException
    {
        TagNode rootNode= PostQuery(null);
        if (rootNode==null) return null;
        rootNode = rootNode.findElementByAttValue("name","vr",true,true);
        List<TagNode> links=rootNode.getChildTagList();
        //парсинг списка групп
        List<Group> groups=new ArrayList<>();
        Iterator<TagNode> iterator = links.iterator();
        while (iterator.hasNext()) {
            TagNode divElement = iterator.next();
            groups.add(new Group(divElement.getAttributeByName("value"),divElement.getText().toString()));
        }
        return groups;
    }
}
