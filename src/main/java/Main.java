import me.afifaniks.parsers.ArgParser;
import me.afifaniks.parsers.TessPDFParser;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        // Turning Logging Off
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for ( Logger logger : loggers ) {
            logger.setLevel(Level.OFF);
        }

        String pdfPath = args[0];
        HashMap<String, Object> params = ArgParser.parseCMDArgs(args);

        TessPDFParser pdfParser;

        if (params.get("tessDataPath") != null) {
            pdfParser = new TessPDFParser(params.get("tessDataPath").toString());
        } else {
            pdfParser = new TessPDFParser();
        }

        String text = pdfParser.convert(pdfPath, params);

        if (params.get("consoleOut") != null) {
            if ((boolean) params.get("consoleOut"))
                System.out.println(text);
        } else {
            System.out.println(text);
        }
    }
}
