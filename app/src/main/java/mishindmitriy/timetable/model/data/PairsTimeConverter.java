package mishindmitriy.timetable.model.data;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 15.11.2015.
 */
public class PairsTimeConverter {
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
    private static HashMap<Byte, CharSequence> pairStartTimeNumber = new HashMap<>();
    private static HashMap<Byte, CharSequence> pairEndTimeNumber = new HashMap<>();
    private static HashMap<Byte, CharSequence> pairStartTimeNumberSaturday = new HashMap<>();
    private static HashMap<Byte, CharSequence> pairEndTimeNumberSaturday = new HashMap<>();

    static {
        pairEndTimeNumber.put((byte) 1, firstPairEnd);
        pairEndTimeNumber.put((byte) 2, secondPairEnd);
        pairEndTimeNumber.put((byte) 3, thirdPairEnd);
        pairEndTimeNumber.put((byte) 4, fourthPairEnd);
        pairEndTimeNumber.put((byte) 5, fifthPairEnd);
        pairEndTimeNumber.put((byte) 6, sixthPairEnd);
        pairEndTimeNumber.put((byte) 7, seventhPairEnd);

        pairStartTimeNumber.put((byte) 1, firstPairStart);
        pairStartTimeNumber.put((byte) 2, secondPairStart);
        pairStartTimeNumber.put((byte) 3, thirdPairStart);
        pairStartTimeNumber.put((byte) 4, fourthPairStart);
        pairStartTimeNumber.put((byte) 5, fifthPairStart);
        pairStartTimeNumber.put((byte) 6, sixthPairStart);
        pairStartTimeNumber.put((byte) 7, seventhPairStart);

        pairEndTimeNumberSaturday.put((byte) 1, Saturday.firstPairEnd);
        pairEndTimeNumberSaturday.put((byte) 2, Saturday.secondPairEnd);
        pairEndTimeNumberSaturday.put((byte) 3, Saturday.thirdPairEnd);
        pairEndTimeNumberSaturday.put((byte) 4, Saturday.fourthPairEnd);
        pairEndTimeNumberSaturday.put((byte) 5, Saturday.fifthPairEnd);

        pairStartTimeNumberSaturday.put((byte) 1, Saturday.firstPairStart);
        pairStartTimeNumberSaturday.put((byte) 2, Saturday.secondPairStart);
        pairStartTimeNumberSaturday.put((byte) 3, Saturday.thirdPairStart);
        pairStartTimeNumberSaturday.put((byte) 4, Saturday.fourthPairStart);
        pairStartTimeNumberSaturday.put((byte) 5, Saturday.fifthPairStart);
    }

    public static CharSequence getPairStartTime(Byte pairNumber, boolean saturday) {
        if (saturday) {
            return pairStartTimeNumberSaturday.get(pairNumber);
        } else {
            return pairStartTimeNumber.get(pairNumber);
        }
    }

    public static CharSequence getPairEndTime(Byte pairNumber, boolean saturday) {
        if (saturday) {
            return pairEndTimeNumberSaturday.get(pairNumber);
        } else {
            return pairEndTimeNumber.get(pairNumber);
        }
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
