package org.h2.tools;

import java.util.ArrayList;
import java.util.List;

public class ServerTest {

    public static void main(String[] args) throws Throwable {
        System.setProperty("h2.lobInDatabase", "false");
        System.setProperty("h2.lobClientMaxSizeMemory", "1024");
        System.setProperty("java.io.tmpdir", "/tmp");
        System.setProperty("h2.baseDir", "/Volumes/files/h2");
        List<String> list = new ArrayList<>();
        list.add("-tcp");
        org.h2.tools.Server.main(list.toArray(new String[list.size()]));
    }

}
