package org.mwangi.desktop.download.zip;

import org.mwangi.desktop.download.DownloadProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mwangi.desktop.io.ExtendedIO.getSystemPath;

public class ZipManager {
    public  static void androidZipper(DownloadProperties downloadProperties, Map<String, Map<String, String>> translations) throws IOException {
        String absolutePath=getSystemPath()+downloadProperties.filePath();
        Files.createDirectories(Path.of(absolutePath));
        Path zipPath = Path.of("%s/%s.zip".formatted(absolutePath, downloadProperties.fileName()));
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            for (Map.Entry<String, Map<String, String>> langEntry : translations.entrySet()) {
                StringBuilder androidXml = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
                for (Map.Entry<String, String> entry : langEntry.getValue().entrySet()) {
                    androidXml.append(String.format("    <string name=\"%s\">%s</string>\n",
                            entry.getKey(), entry.getValue()));
                }
                androidXml.append("</resources>");
                String androidPath = "res/values-%s/strings.xml".formatted(langEntry.getKey());
                zout.putNextEntry(new ZipEntry(androidPath));
                zout.write(androidXml.toString().getBytes(StandardCharsets.UTF_8));
                zout.closeEntry();
            }
        }
    }
    public static void iosZipper(DownloadProperties downloadProperties, Map<String, Map<String, String>> translations) throws IOException {
        String absolutePath=getSystemPath()+downloadProperties.filePath();
        Files.createDirectories(Path.of(absolutePath));
        Path zipPath = Path.of("%s/%s.zip".formatted(absolutePath, downloadProperties.fileName()));
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            for (Map.Entry<String, Map<String, String>> langEntry : translations.entrySet()) {
                StringBuilder iosStrings = new StringBuilder();
                for (Map.Entry<String, String> entry : langEntry.getValue().entrySet()) {
                    iosStrings.append(String.format("%s=%s;%n",
                            entry.getKey(), entry.getValue()));
                }
                String iosPath = "ios/%s.lproj/Localizable.strings".formatted(langEntry.getKey());
                zout.putNextEntry(new ZipEntry(iosPath));

                zout.write(iosStrings.toString().getBytes(StandardCharsets.UTF_8));
                zout.closeEntry();
            }
        }
    }
}
