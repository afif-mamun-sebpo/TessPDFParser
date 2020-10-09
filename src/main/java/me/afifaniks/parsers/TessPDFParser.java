package me.afifaniks.parsers;

/***
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Afif Al Mamun
 * Web: https://afifaniks.me
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import me.afifaniks.downloader.Downloader;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TessPDFParser {
    private String pdfPath;
    private int DPI;
    private ArrayList<Integer> pdfPages = new ArrayList<>();
    private ImageType imageType = ImageType.BINARY;
    private final Tesseract tesseract;
    private String outputFileName;
    private boolean toFile;
    private PrintWriter out;

    public TessPDFParser() {
        // Turning Logging Off
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for ( Logger logger : loggers ) {
            logger.setLevel(Level.OFF);
        }

        // Loading from resource folder
        String tessDataPath = "tessdata";
        tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
    }

    public TessPDFParser(String tessDataPath) {
        Logger.getRootLogger().setLevel(Level.OFF);
        tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
    }

    private void setArgs (String pdfPath, HashMap<String, Object> arguments) {
        this.pdfPath = pdfPath;
        this.pdfPages = arguments.get("pages") != null ?
                ArgParser.pageParser(arguments.get("pages").toString()) :
                new ArrayList<Integer>(Collections.singletonList(-111));
        this.DPI = arguments.get("dpi") != null ? (int) arguments.get("dpi") : 150;
        this.toFile = arguments.get("toFile") != null ? (boolean) arguments.get("toFile") : false;

        outputFileName = Paths.get(pdfPath)
                .getFileName()
                .toString()
                .toLowerCase()
                .replace(".pdf", ".txt");

        if (arguments.get("outputDir") != null) {
            String dirPath = (String) arguments.get("outputDir");

            File directory = new File(dirPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            outputFileName = dirPath + "/" + outputFileName;
        }

        if (arguments.get("imageMode") != null) {
            String mode = (String) arguments.get("imageMode");

            if (mode.toLowerCase().equals("rgb")) {
                imageType = ImageType.RGB;
            } else if (mode.toLowerCase().equals("argb")) {
                imageType = ImageType.ARGB;
            } else if (mode.toLowerCase().equals("gray")) {
                imageType = ImageType.GRAY;
            } else if (mode.toLowerCase().equals("binary")) {
                imageType = ImageType.BINARY;
            }
        }

        if (toFile) {
            try {
                out = new PrintWriter(outputFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convert PDF page(s) to text via Tesseract OCR
     * @param pdfPath: The path of the PDF
     * @param args: [Optional Params HashMap]
     *
     *            key: "dpi" -> val: integer, default -> 150
     *            key: "pages" -> val: PDF page index / -1 for all, default -> -1
     *            key: "imageMode" -> val: "rgb"/"argb"/"binary"/"gray", default -> binary
     *            key: "toFile" -> val: true -> saves output to text, default -> false
     *            key: "outputDir" -> val: Path to save output file, default -> working directory
     *
     * @return PDF Text String
     */
    public String convert(String pdfPath, HashMap<String, Object> args) {
        setArgs(pdfPath, args);

        System.out.println("[PARSER STATUS]:" + "\t" +
                "Parsing PDF: " + pdfPath +
                "\tDPI: " + DPI +
                "\tColor Mode: " + imageType.name());

        PDDocument doc = null;

        try {
            if (pdfPath.substring(0, 4).equalsIgnoreCase("http") ||
                    pdfPath.substring(0, 3).equalsIgnoreCase("www")) {
                doc = PDDocument.load(Downloader.downloadUsingStream(pdfPath));
            } else {
                doc = PDDocument.load(new File(pdfPath));
            }
            int totalPages = doc.getNumberOfPages();

            PDFRenderer pdfRenderer = new PDFRenderer(doc);
            String pdfText = "";

            if (pdfPages.get(0) == -111) {
                for (int i = 0; i < totalPages; i++) {
                    pdfText += convertPage(pdfRenderer, i);
                    System.out.println("[PARSER STATUS]:\tParsing page " +
                            (i + 1) + " of " + totalPages);
                }
            } else {
                for (Integer i : pdfPages) {
                    if (i <= totalPages) {
                        pdfText += convertPage(pdfRenderer, i - 1);
                        System.out.println("[PARSER STATUS]:\tParsing page " +
                                i + " of " + totalPages);
                    } else {
                        throw new IOException("[PARSER ERROR]:\tInvalid index. " + "\t" +
                                "Total Pages: " + totalPages + "\t" +
                                "Requested Index: " + i);
                    }
                }
            }

            doc.close();

            if (toFile)
                System.out.println("[PARSER SUCCESS]: Text saved to " + outputFileName);
            return pdfText;

        } catch (IOException | TesseractException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String convertPage(PDFRenderer pdfRenderer, int pageNo) throws IOException, TesseractException {
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNo, DPI, imageType);
        String text = tesseract.doOCR(bufferedImage);
        // Saving file
        if (toFile) {
            out.append(text + "\n\n");
            out.flush();
        }

        return text;
    }
}
