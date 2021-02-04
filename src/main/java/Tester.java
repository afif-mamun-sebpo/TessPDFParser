import me.afifaniks.parsers.TessPDFParser;

import java.util.HashMap;

/**
 * Project: TessPDFParser
 * Author: AFIF AL MAMUN
 * Created in 2/4/21
 * Web: https://afifaniks.me
 */
public class Tester {
    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("dpi", 150);
        map.put("cache", true);
        map.put("pages", "1,2,8-15,28");

        // Keeping the tessdata directory in the classpath
        TessPDFParser pdfParser = new TessPDFParser();

        // Custom tessdata path
        // TessPDFParser pdfParser = new TessPDFParser("/PATH_TO_TESSDATA/tessdata");

        String text = pdfParser.convert("http://tdc-www.harvard.edu/Python.pdf", map);
        System.out.println(text);
    }
}
