# TessPDFParser
PDF parsing using [tesseract-ocr](https://github.com/tesseract-ocr/)
Parse PDF into text even for the scanned copies!
The library can be used as a stand alone application or can be integrated into other JDK 1.8+ projects.

## Usage
- Download the `tesspdf-1.0.1.zip` from root directory
- Extract the zip file in your preferred directory

#### Using As A Stand Alone Application from Terminal
- Make sure `tesspdf-1.0.1.jar` and `tessdata` directory are in the same directory
- Open your terminal in the directory
- Run `java -jar tesspdf-1.0.1.jar "PDF_PATH/PDF_URL"`


**Optional arguments**
```
-dpi : Integer (default = 150)
-toFile : To save the output in a `.txt` file (by default the file will be saved in the same directory)
-outputDir : Directory path to save the output
-imageType : binary/rgb/argb/gray (by default it is set to binary)
-pages : Pages of the PDF to be converted for example -pages="1,2,4-8,14" (default = all)
-consoleOut : boolean (to show/hide output in the console; default = true)
-tessDataPath : Custom `tessdata` directory path

```
For example to convert the first page of [this](http://www.orimi.com/pdf-test.pdf) to a file with specified output directory and **dpi** set to 200 can be done by -
```
java -jar tesspdf-1.0.1.jar http://www.orimi.com/pdf-test.pdf -dpi=200 -toFile=true -outputDir=/home/afif/Documents -pages=1
```

#### Using in a Java/Groovy Project
- Include the `tesspdf-1.0.1.jar` file in your project dependency
- Keep the `tessdata` directory in your classpath or manually pass the absolute path of the directory through the `TessPDFParser` constructor.

Sample Java snippet -
```
import me.afifaniks.parsers.TessPDFParser;

import java.io.IOException;
import java.util.HashMap;


public class Main {
    public static void main(String[] args) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("dpi", 150);
        map.put("toFile", true);
        map.put("pages", "1,2,8-15,28");

        // Keeping the tessdata directory in the classpath
        TessPDFParser pdfParser = new TessPDFParser();

        // Custom tessdata path
        // TessPDFParser pdfParser = new TessPDFParser("/PATH_TO_TESSDATA/tessdata");

        String text = pdfParser.convert("http://tdc-www.harvard.edu/Python.pdf", map);
        System.out.println(text);
    }
}
```

Sample Groovy snippet -
```
import me.afifaniks.parsers.TessPDFParser

TessPDFParser pdfParser = new TessPDFParser()
String text = pdfParser.convert("http://tdc-www.harvard.edu/Python.pdf",
                                        [dpi: 150,
                                         imageMode: "binary",
                                         toFile: false,
                                         pages: "1,2,8-15,28"]);
println(text)
```

## Features
- Converting PDF to Text
- High performance for scanned PDF
- PDF parsing from both local and web URL
- Customizable dpi for better performance

## Notes
- JDK 1.8+ need to be installed
- If the program fails to run, execute `sudo apt install tesseract-ocr` on terminal and try again

Kudos to [ngyenq](https://github.com/nguyenq) for [tess4j](https://github.com/nguyenq/tess4j) library.


Find me here: [https://afifaniks.me](https://afifaniks.me)
