package icn.proludic.misc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Author:  Bradley Wilson
 * Date: 28/07/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class AgeManager {

    public static int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        return age;
    }

    public static String dateOfBirthToString(int year, int month, int dayOfMonth) {
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, dayOfMonth);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return df.format(dob.getTime());
    }
}
