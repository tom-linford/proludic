package icn.proludic.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static icn.proludic.misc.Constants.EMAIL;
import static icn.proludic.misc.Constants.NAME;
import static icn.proludic.misc.Constants.PASSWORD;
import static icn.proludic.misc.Constants.USERNAME_INPUT;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class Validate {
    public static boolean isValid(String type, String inputToValidate) {
        String PATTERN = "";
        Pattern pattern;
        switch (type) {
            case NAME:
                PATTERN = "^[\\p{L} .'-]+$";
                pattern = Pattern.compile(PATTERN);
                break;
            case USERNAME_INPUT:
                PATTERN = "^[a-zA-Z0-9]+$";
                pattern = Pattern.compile(PATTERN);
                break;
            case EMAIL:
                PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                pattern = Pattern.compile(PATTERN);
                break;
            case PASSWORD:
                PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{6,45}$";
                pattern = Pattern.compile(PATTERN);
                break;
            default:
                PATTERN = "";
                pattern = Pattern.compile(PATTERN);
                break;
        }
        Matcher matcher = pattern.matcher(inputToValidate);
        return matcher.matches();
    }
}
