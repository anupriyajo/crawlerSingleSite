import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Crawler {

    private final static int MAX_DEPTH = 2;
    private final static int MAX_URLS = 1000;

    private final String baseUrl;
    private final int maxDepth;
    private final boolean scanAllForBaseDomain;
    private final int maxUrls;

    private List<String> excludedDomains = Arrays.asList("twitter", "google");

    private String baseDomain;

    public Crawler(String baseUrl) {
        this(baseUrl, MAX_DEPTH, true, MAX_URLS);
    }

    public Crawler(String baseUrl, int maxDepth) {
        this(baseUrl, maxDepth, true, MAX_URLS);
    }

    public Crawler(String baseUrl, boolean scanAllForBaseDomain) {
        this(baseUrl, MAX_DEPTH, scanAllForBaseDomain, MAX_URLS);
    }

    public Crawler(String baseUrl, int maxDepth, boolean scanAllForBaseDomain, int maxUrls) {
        this.baseUrl = baseUrl;
        this.maxDepth = maxDepth;
        this.scanAllForBaseDomain = scanAllForBaseDomain;
        this.maxUrls = maxUrls;
        this.baseDomain = Helper.extractDomain(baseUrl);

        if (baseDomain == null) {
            throw new IllegalArgumentException("Incorrect base url");
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isScanAllForBaseDomain() {
        return scanAllForBaseDomain;
    }

    public int getMaxUrls() {
        return maxUrls;
    }

    public List<String> getExcludedDomains() {
        return excludedDomains;
    }

    public String getBaseDomain() {
        return baseDomain;
    }

    public void setExcludedDomains(List<String> excludedDomains) {
        this.excludedDomains = excludedDomains;
    }


    public Map<String, Set<String>> crawl() {

        Set<String> urls = new HashSet<>();
        Set<String> images = new HashSet<>();

        Queue<MetaUrl> queue = new LinkedList<>();

        queue.add(new MetaUrl(Helper.sanitizeUrl(baseUrl), 0));

        while (!(queue.isEmpty()) && urls.size() <= maxUrls) {

            MetaUrl metaUrl = queue.poll();
            String url = metaUrl.getUrl();
            urls.add(url);

            try {
                Connection connection = Jsoup.connect(url);
                Document document = connection.get();
                Elements elements = document.select("a[href]");
                for (Element element : elements) {
                    String thisUrl = Helper.sanitizeUrl(element.absUrl("href"));
                    String domain = Helper.extractDomain(thisUrl);
                    if (domain == null) {
                        continue;
                    }
                    if (urls.contains(thisUrl)) {
                        continue;
                    }
                    switch (extractDomainType(domain)) {
                        case IGNORE:
                            break;
                        case DOMAIN:
                            queue.add(new MetaUrl(thisUrl, metaUrl.getDepth() + 1));
                            urls.add(thisUrl);
                            break;
                        case NON_DOMAIN:
                            if (metaUrl.getDepth() < MAX_DEPTH) {
                                urls.add(thisUrl);
                                queue.add(new MetaUrl(thisUrl, metaUrl.getDepth() +1));
                            }
                            break;
                    }
                }

                elements = document.select("img[src]");
                for (Element element : elements) {
                    String src = element.absUrl("src");
                    images.add(src);
                }
            } catch (IOException e) {
            }
        }

        Map<String, Set<String>> result = new HashMap<>();
        result.put("url", urls);
        result.put("image", images);
        return result;
    }

    private DomainType extractDomainType(String domain) {
        try {
            for (String excludedDomain : excludedDomains) {
                if (excludedDomain.equalsIgnoreCase(domain.split("\\.")[0])) {
                    return DomainType.IGNORE;
                }
            }
            if (domain.equalsIgnoreCase(baseDomain)) {
                return DomainType.DOMAIN;
            }
        } catch (Exception e) {

        }
        return DomainType.NON_DOMAIN;
    }
}
