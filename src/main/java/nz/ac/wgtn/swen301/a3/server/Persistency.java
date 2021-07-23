package nz.ac.wgtn.swen301.a3.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulation of a database for this project, contains a List of LogEvents that represent the database.
 */
public class Persistency {

    //Database containing the LogEvents
    public static List<LogEvents> DB = new ArrayList<>();

    /**
     * Helper method for testing purposes, creating LogEvents and adding them to DB.
     */
    public static void logs(){
        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "OFF",
                "string");
        LogEvents log2 = new LogEvents("log-id-2",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "TRACE",
                "string");
        LogEvents log3 = new LogEvents("log-id-3",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "WARN",
                "string");

        DB.add(log1);
        DB.add(log2);
        DB.add(log3);
    }
}
