package nz.ac.wgtn.swen301.a3.server;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class containing the test cases for StatsCSV
 */
public class TestStatsCSV {

    @Test
    public void test_Correctness() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        //creating logEvents objects to populate DB
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
                "com.example.Fee",
                "WARN",
                "string");

        //manually populating DB for testing purposes
        Persistency.DB.add(log1);
        Persistency.DB.add(log2);
        Persistency.DB.add(log3);

        //creating the mock request and response
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        StatsCSVServlet statsCSVServlet = new StatsCSVServlet();
        statsCSVServlet.doGet(mockReq, mockResp);

        //ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
        List<Integer> fooValues = statsCSVServlet.loggerNameValues.get("com.example.Foo");
        List<Integer> fooExpected = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 0, 1));

        List<Integer> feeValues = statsCSVServlet.loggerNameValues.get("com.example.Fee");
        List<Integer> feeExpected = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0));

        //asserting that values are as expected
        assertEquals(fooExpected, fooValues);
        assertEquals(feeExpected, feeValues);
    }

    @Test
    public void test_Headers() throws IOException {
        //clearing the DB to make sure there are no logs
        Persistency.DB.clear();

        //creating the mock request and response
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        StatsCSVServlet statsCSVServlet = new StatsCSVServlet();
        statsCSVServlet.doGet(mockReq, mockResp);

        String expected = "logger\tALL\tTRACE\tDEBUG\tINFO\tWARN\tERROR\tFATAL\tOFF\t";
        String actual = mockResp.getContentAsString();

        //asserting that the values are as expected
        assertEquals(expected, actual);
    }

    @Test
    public void test_Response() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        //creating logEvents objects to populate DB
        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "ALL",
                "string");
        LogEvents log2 = new LogEvents("log-id-2",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "DEBUG",
                "string");
        LogEvents log3 = new LogEvents("log-id-3",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Fee",
                "INFO",
                "string");
        LogEvents log4 = new LogEvents("log-id-3",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Fee",
                "ERROR",
                "string");
        LogEvents log5 = new LogEvents("log-id-3",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Fee",
                "FATAL",
                "string");

        //manually populating DB for testing purposes
        Persistency.DB.add(log1);
        Persistency.DB.add(log2);
        Persistency.DB.add(log3);
        Persistency.DB.add(log4);
        Persistency.DB.add(log5);

        //creating the mock request and response
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        StatsCSVServlet statsCSVServlet = new StatsCSVServlet();
        statsCSVServlet.doGet(mockReq, mockResp);

        //ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
        List<Integer> fooValues = statsCSVServlet.loggerNameValues.get("com.example.Foo");
        List<Integer> fooExpected = new ArrayList<>(Arrays.asList(1, 0, 1, 0, 0, 0, 0, 0));

        List<Integer> feeValues = statsCSVServlet.loggerNameValues.get("com.example.Fee");
        List<Integer> feeExpected = new ArrayList<>(Arrays.asList(0, 0, 0, 1, 0, 1, 1, 0));

        //asserting that the values are expected and that the status is correct
        assertEquals(fooExpected, fooValues);
        assertEquals(feeExpected, feeValues);
        assertEquals(200, mockResp.getStatus());
    }
}
