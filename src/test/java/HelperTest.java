import org.junit.Test;

import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void testExtractDomain() throws Exception {
        assertEquals(Helper.extractDomain("http://www.google.com"), "google.com");
        assertEquals(Helper.extractDomain("http://youtube.com"), "youtube.com");
    }

    @Test
    public void testSanitizeUrl() throws Exception {
        assertEquals(Helper.sanitizeUrl("http://www.google.com/"), "http://www.google.com");
        assertEquals(Helper.sanitizeUrl("http://www.google.com"), "http://www.google.com");
    }
}