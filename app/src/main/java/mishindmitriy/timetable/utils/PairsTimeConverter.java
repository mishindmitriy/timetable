package mishindmitriy.timetable.utils;

import android.util.SparseArray;

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
    private static SparseArray<CharSequence> pairStartTimeNumber = new SparseArray<>();
    private static SparseArray<CharSequence> pairEndTimeNumber = new SparseArray<>();
    private static SparseArray<CharSequence> pairStartTimeNumberSaturday = new SparseArray<>();
    private static SparseArray<CharSequence> pairEndTimeNumberSaturday = new SparseArray<>();

    static {
        pairEndTimeNumber.put(1, firstPairEnd);
        pairEndTimeNumber.put(2, secondPairEnd);
        pairEndTimeNumber.put(3, thirdPairEnd);
        pairEndTimeNumber.put(4, fourthPairEnd);
        pairEndTimeNumber.put(5, fifthPairEnd);
        pairEndTimeNumber.put(6, sixthPairEnd);
        pairEndTimeNumber.put(7, seventhPairEnd);

        pairStartTimeNumber.put(1, firstPairStart);
        pairStartTimeNumber.put(2, secondPairStart);
        pairStartTimeNumber.put(3, thirdPairStart);
        pairStartTimeNumber.put(4, fourthPairStart);
        pairStartTimeNumber.put(5, fifthPairStart);
        pairStartTimeNumber.put(6, sixthPairStart);
        pairStartTimeNumber.put(7, seventhPairStart);

        pairEndTimeNumberSaturday.put(1, Saturday.firstPairEnd);
        pairEndTimeNumberSaturday.put(2, Saturday.secondPairEnd);
        pairEndTimeNumberSaturday.put(3, Saturday.thirdPairEnd);
        pairEndTimeNumberSaturday.put(4, Saturday.fourthPairEnd);
        pairEndTimeNumberSaturday.put(5, Saturday.fifthPairEnd);

        pairStartTimeNumberSaturday.put(1, Saturday.firstPairStart);
        pairStartTimeNumberSaturday.put(2, Saturday.secondPairStart);
        pairStartTimeNumberSaturday.put(3, Saturday.thirdPairStart);
        pairStartTimeNumberSaturday.put(4, Saturday.fourthPairStart);
        pairStartTimeNumberSaturday.put(5, Saturday.fifthPairStart);
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
