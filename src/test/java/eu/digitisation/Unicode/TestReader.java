package eu.digitisation.Unicode;

import eu.digitisation.Unicode.Reader;
import junit.framework.TestCase;
import org.junit.Test;
/**
 *
 * @author rafa
 */
public class TestReader extends TestCase {

    public void testUnicodeReader() {
        String input = "día, mes y año";
        String ref = "[100, 237, 97, 44, 32, 109, 101, 115, 32, 121, 32, 97, 241, 111]";

        String output =
                java.util.Arrays.toString(Reader.toCodepoints(input));
        System.out.println(output);
        assertEquals(ref, output);
    }
}