package com.unusualbread.zodiacbot;

public class ZodiacUtils {

    public static String getSignName(int day, int month) {
        if (month == 3 && day >= 21 || month == 4 && day <= 20) return "Овен";

        if (month == 4 || month == 5 && day <= 20) return "Телец";

        if (month == 5 || month == 6 && day <= 21) return "Близнецы";

        if (month == 6 || month == 7 && day <= 22) return "Рак";

        if (month == 7 || month == 8 && day <= 22) return "Лев";

        if (month == 8 || month == 9 && day <= 23) return "Дева";

        if (month == 9 || month == 10 && day <= 23) return "Весы";

        if (month == 10 || month == 11 && day <= 22) return "Скорпион";

        if (month == 11 || month == 12 && day <= 21) return "Стрелец";

        if (month == 12 || month == 1 && day <= 20) return "Козерог";

        if (month == 1 || month == 2 && day <= 18) return "Водолей";

        if (month == 2 || month == 3) return "Рыбы";

        throw new UnsupportedOperationException();
    }
}
