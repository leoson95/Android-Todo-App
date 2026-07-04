import java.util.Calendar

fun main() {
    var jy = 1405
    var jm = 4
    var jd = 10
    
    var outYear: Int
    var outMonth: Int
    var outDay: Int
    
    val jy2 = jy + 1595
    var days = -355668 + (365 * jy2) + (15 * (jy2 / 33)) + (jy2 % 33 + 3) / 4 + jd + if (jm < 7) (jm - 1) * 31 else (jm - 7) * 30 + 186
    
    var gy = 400 * (days / 146097)
    days %= 146097
    if (days > 36524) {
        gy += 100 * (--days / 36524)
        days %= 36524
        if (days >= 365) days++
    }
    gy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        gy += (days - 1) / 365
        days = (days - 1) % 365
    }
    var gd = days + 1
    var sal_a = intArrayOf(0, 31, if ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var gm = 0
    for (i in 1..12) {
        if (gd <= sal_a[i]) {
            gm = i
            break
        }
        gd -= sal_a[i]
    }
    outYear = gy
    outMonth = gm
    outDay = gd
    
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, outYear)
    cal.set(Calendar.MONTH, outMonth - 1)
    cal.set(Calendar.DAY_OF_MONTH, outDay)
    cal.set(Calendar.HOUR_OF_DAY, 15)
    cal.set(Calendar.MINUTE, 30)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    println("Time: ${cal.time}")
    println("Time in millis: ${cal.timeInMillis}")
    println("Current time in millis: ${System.currentTimeMillis()}")
}
