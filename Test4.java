import java.util.Calendar;
public class Test4 {
    public static void main(String[] args) {
        int gy = 2026;
        int gm = 7;
        int gd = 1;
        
        int gy2 = (gm > 2) ? (gy + 1) : gy;
        int[] g_d_m = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + g_d_m[gm - 1];
        
        int jy = 33 * (days / 12053);
        days %= 12053;
        jy += 4 * (days / 1461);
        days %= 1461;
        if (days > 365) {
            jy += (days - 1) / 365;
            days = (days - 1) % 365;
        }
        
        int outYear = jy - 1096;
        int outMonth = (days < 186) ? (days / 31) + 1 : ((days - 186) / 30) + 7;
        int outDay = 1 + ((days < 186) ? (days % 31) : ((days - 186) % 30));
        System.out.println(outYear + "-" + outMonth + "-" + outDay);
    }
}
