package nz.ac.wgtn.swen301.a3.server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class containing test cases for StatsHTMl
 */
public class TestStatsHTML {

    @Test
    public void test_NoLogs() throws IOException {
        //clearing the DB to make sure there are no logs
        Persistency.DB.clear();

        //creating the mock request and response
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        StatsServlet statsServlet = new StatsServlet();
        statsServlet.doGet(mockReq, mockResp);

        //getting the content and getting the headers
        Document document = Jsoup.parse(mockResp.getContentAsString());
        String[] headers = Jsoup.clean(document.select("td").toString(), Whitelist.none()).split(" ");

        List<String> headerCols = new ArrayList<>(Arrays.asList("logger",
                                                                "ALL",
                                                                "TRACE",
                                                                "DEBUG",
                                                                "INFO",
                                                                "WARN",
                                                                "ERROR",
                                                                "FATAL",
                                                                "OFF"));

        //asserting that with and empty DB, the headers are correct and there
        for(int i = 0; i < headerCols.size(); i++){
            assertEquals(headerCols.get(i), headers[i]);
        }
    }

    @Test
    public void test_CorrectContent() throws IOException {
        //clearing DB
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
                "com.example.Foo",
                "WARN",
                "string");

        //manually populating DB for testing purposes
        Persistency.DB.add(log1);
        Persistency.DB.add(log2);
        Persistency.DB.add(log3);

        //creating the mock request and response
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        StatsServlet statsServlet = new StatsServlet();
        statsServlet.doGet(mockReq, mockResp);

        //ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
        List<Integer> actual = statsServlet.loggerNameValues.get("com.example.Foo");
        List<Integer> expected = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 1, 0, 0, 1));

        //getting the information and parsing using Jsoup
        Document document = Jsoup.parse(mockResp.getContentAsString());
        String docBody = document.body().wholeText();
        String[] output = docBody.split("\n");
        String[] headerValue = output[1].split(" ");

        //asserting that values are as expected
        assertEquals(expected, actual);

        //asserting that the logger name is correct and that the values are correct
        assertEquals("com.example.Foo", headerValue[0]);
        assertEquals("01001001", headerValue[1]);
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
        LogEvents log4 = new LogEvents("log-id-4",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Fee",
                "ERROR",
                "string");
        LogEvents log5 = new LogEvents("log-id-5",
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

        //getting the information and parsing using Jsoup
        Document document = Jsoup.parse(mockResp.getContentAsString());
        String docBody = document.body().wholeText();
        String[] output = docBody.split("\n");
        String[] headerValue1 = output[1].split("\t");
        String[] headerValue2 = output[2].split("\t");

        String values1 = "";
        String values2 = "";

        for(int i = 1; i < headerValue1.length; i++){
            values1 = values1 + headerValue1[i];
            values2 = values2 + headerValue2[i];
        }

        //asserting that the values are as expected
        assertEquals(fooExpected, fooValues);
        assertEquals(feeExpected, feeValues);

        //asserting that the logger names and associated values are also correct
        assertEquals("com.example.Foo", headerValue1[0]);
        assertEquals("com.example.Fee", headerValue2[0]);

        assertEquals("10100000", values1);
        assertEquals("00010110", values2);

        //asserting that the status is correct
        assertEquals(200, mockResp.getStatus());
    }
}
