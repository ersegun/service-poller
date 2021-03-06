package servicepoller.helper;

import org.apache.commons.validator.routines.UrlValidator;

public class UrlHelper {

    public static boolean IsValidUrl(String url)
    {
        UrlValidator urlValidator = new UrlValidator();

        if (urlValidator.isValid(url)) {
            return true;
        } else {
            return false;
        }
    }
}
