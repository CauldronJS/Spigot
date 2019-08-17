package me.conji.cauldron.utils;

import me.conji.cauldron.Cauldron;
import org.graalvm.polyglot.HostAccess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class ScriptHelper {

    @HostAccess.Export
    public static String readFile(String path) throws IOException {
        try {
            String localFile = PathHelper.getPath(path);
            FileInputStream fis = new FileInputStream(localFile);
            return readFromStream(fis);
        } catch (FileNotFoundException ex) {
            InputStream resxStream = Cauldron.getInstance().getResource(path);
            if (resxStream == null) throw new IOException("Resource doesn't exist: " + path);
            return readFromStream(resxStream);
        }
    }

    @HostAccess.Export
    public static String readFromStream(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader buffer = new BufferedReader(reader);
        String line;
        String result = "";
        while ((line = buffer.readLine()) != null) {
            result += line + System.lineSeparator();
        }
        return result;
    }
}
