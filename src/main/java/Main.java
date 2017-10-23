import java.io.*;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {

        Crawler crawler = new Crawler("http://www.wiprodigital.com");
        Map<String, Set<String>> result = crawler.crawl();

        File outputFile = new File("out.txt");
        FileOutputStream fos = new FileOutputStream(outputFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos);

        result.forEach((key, value) ->
                value.forEach(element -> {
                    try {
                        osw.write(element);
                        osw.write("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
        osw.close();
        System.out.println("completed");
    }
}
