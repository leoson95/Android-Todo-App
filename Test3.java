import java.util.Calendar;
public class Test3 {
    public static void main(String[] args) {
        int jy = 1405;
        int jm = 4;
        int jd = 10;
        
        int gy = (jy <= 979) ? 621 : 1600;
        jy -= (jy <= 979) ? 0 : 979;
        
        int days = (365 * jy) + ((int) (jy / 33)) * 8 + ((int) (((jy % 33) + 3) / 4))
                + 78 + jd + ((jm < 7) ? (jm - 1) * 31 : ((jm - 7) * 30 + 186));
        
        gy += 400 * ((int) (days / 146097));
        days %= 146097;
        
        if (days > 36524) {
            gy += 100 * ((int) (--days / 36524));
            days %= 36524;
            if (days >= 365) days++;
        }
        
        gy += 4 * ((int) (days / 1461));
        days %= 1461;
        
        if (days > 365) {
            gy += (int) ((days - 1) / 365);
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
        System.out.println(gy + "-" + gm + "-" + gd);
    }
}
