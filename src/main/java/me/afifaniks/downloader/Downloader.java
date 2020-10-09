package me.afifaniks.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Downloader {
    public static InputStream downloadUsingStream(String urlStr) throws IOException {
        System.out.println("[PARSER STATUS]:\tDownloading PDF...");
        InputStream inputStream = new URL(urlStr).openStream();

        return inputStream;
    }
}
