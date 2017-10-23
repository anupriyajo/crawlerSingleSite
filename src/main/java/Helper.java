import java.net.URI;

public class Helper {

    public static String extractDomain(String url) {
        URI uri;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
        }
        return null;
    }

    public static String sanitizeUrl(String url) {
       if (url.endsWith("/")) {
           url = url.substring(0, url.length()-1);
       }
        return url;
    }

}
