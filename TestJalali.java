import java.util.Calendar;
public class TestJalali {
    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int gy = cal.get(Calendar.YEAR);
        int gm = cal.get(Calendar.MONTH) + 1;
        int gd = cal.get(Calendar.DAY_OF_MONTH);

        int[] g_d_m = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int gy2 = (gm > 2) ? (gy + 1) : gy;
        int days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + g_d_m[gm - 1];

        int jy = -1595 + (33 * (days / 12053));
        days %= 12053;

        jy += 4 * (days / 1461);
        days %= 1461;

        if (days > 365) {
            jy += (days - 1) / 365;
            days = (days - 1) % 365;
        }

        int outYear = jy;
        int outMonth = (days < 186) ? 1 + (days / 31) : 7 + ((days - 186) / 30);
        int outDay = 1 + ((days < 186) ? (days % 31) : ((days - 186) % 30));

        System.out.println(outYear + "-" + outMonth + "-" + outDay);
    }
}
