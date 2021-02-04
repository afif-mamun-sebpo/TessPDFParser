package me.afifaniks.downloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Downloader {
    public static InputStream downloadUsingStream(String urlStr) throws IOException {
        System.out.println("[PARSER STATUS]:\tDownloading PDF...");
        BufferedInputStream inputStream = new BufferedInputStream(new URL(urlStr).openStream());

        return inputStream;
    }
}
