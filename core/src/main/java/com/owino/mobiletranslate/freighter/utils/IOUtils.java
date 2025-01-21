package com.owino.mobiletranslate.freighter.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    public static String readFileContents(BufferedReader bufferedReader) {
        String contents;
        StringBuilder stringBuilder = new StringBuilder();
        try (bufferedReader) {
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
            e.printStackTrace();
        } finally {
             contents = stringBuilder.toString();
        }

        return contents;
    }

    public static String readExternalFileContents(File file) throws FileNotFoundException {
        String contents = null;
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            contents = stringBuilder.toString();
        }

        return contents;
    }

    public static void writeStringToFile(File file, String contents) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file,false));
        bufferedWriter.write(contents);
        bufferedWriter.close();
    }

    public static InputStream loadResourceFileUrl(String fileName){
        return IOUtils.class.getClassLoader().getResourceAsStream("files/" + fileName);
    }
}
