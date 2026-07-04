import java.util.Calendar;
public class Test {
    public static void main(String[] args) {
        int jy = 1405;
        int jm = 4;
        int jd = 10;
        
        int jy2 = jy + 1595;
        int days = -355668 + (365 * jy2) + (15 * (jy2 / 33)) + (jy2 % 33 + 3) / 4 + jd + (jm < 7 ? (jm - 1) * 31 : (jm - 7) * 30 + 186);
        
        int gy = 400 * (days / 146097);
        days %= 146097;
        if (days > 36524) {
            gy += 100 * (--days / 36524);
            days %= 36524;
            if (days >= 365) days++;
        }
        gy += 4 * (days / 1461);
        days %= 1461;
        if (days > 365) {
            gy += (days - 1) / 365;
            days = (days - 1) % 365;
        }
        int gd = days + 1;
        int[] sal_a = new int[]{0, 31, ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int gm = 0;
        for (int i = 1; i <= 12; i++) {
            if (gd <= sal_a[i]) {
                gm = i;
                break;
            }
            gd -= sal_a[i];
        }
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, gy);
        cal.set(Calendar.MONTH, gm - 1);
        cal.set(Calendar.DAY_OF_MONTH, gd);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("Time: " + cal.getTime());
        System.out.println("Time in millis: " + cal.getTimeInMillis());
        System.out.println("Current time in millis: " + System.currentTimeMillis());
    }
}
