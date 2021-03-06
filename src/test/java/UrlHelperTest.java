import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import servicepoller.helper.UrlHelper;

public class UrlHelperTest {

    @Test
    public void testValidUrl()
    {
        String url = "https://www.kry.se";

        var isValidUrl = UrlHelper.IsValidUrl(url);

        Assertions.assertTrue(isValidUrl);
    }

    @Test
    public void testInvalidUrl()
    {
        String url = "www.kry";

        var isValidUrl = UrlHelper.IsValidUrl(url);

        Assertions.assertFalse(isValidUrl);
    }
}
