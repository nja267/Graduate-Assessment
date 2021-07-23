package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test class containing test cases for doGet() of LogsServlet.
 */
public class TestGetLogs {

    @Test
    public void test_Response200() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        mockReq.setParameter("limit", "1");
        mockReq.setParameter("level", "DEBUG");

        LogsServlet lServlet = new LogsServlet();
        lServlet.doGet(mockReq, mockResp);

        //asserting that status is correct
        assert 200 == mockResp.getStatus();
    }

    @Test
    public void test_InvalidLimit() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        mockReq.setParameter("limit", "invalid");
        mockReq.setParameter("level", "DEBUG");

        LogsServlet lServlet = new LogsServlet();
        lServlet.doGet(mockReq, mockResp);

        //asserting that status is correct and has correct error message
        assert 400 == mockResp.getStatus();
        assert mockResp.getErrorMessage().equals("bad or missing input parameter (limit is not a positive, non-zero integer)");
    }

    @Test
    public void test_InvalidLevel() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        mockReq.setParameter("limit", "2");
        mockReq.setParameter("level", "INVALID");

        LogsServlet lServlet = new LogsServlet();
        lServlet.doGet(mockReq, mockResp);

        //asserting that status is correct and has correct error message
        assert 400 == mockResp.getStatus();
        assert mockResp.getErrorMessage().equals("bad or missing input parameter (level is not a valid level)");
    }

    @Test
    public void test_NegativeLimit() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        mockReq.setParameter("limit", "-3");
        mockReq.setParameter("level", "DEBUG");

        LogsServlet lServlet = new LogsServlet();
        lServlet.doGet(mockReq, mockResp);

        //asserting that status is correct and has correct error message
        assert 400 == mockResp.getStatus();
        assert mockResp.getErrorMessage().equals("bad or missing input parameter (limit is not a positive, non-zero integer)");
    }

    @Test
    public void test_CorrectContent() throws IOException {
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

        //setting the limit to 2 and wanting to get Logs that are >= OFF
        mockReq.setParameter("limit", "2");
        mockReq.setParameter("level", "OFF");

        //calling on doGet()
        LogsServlet lServlet = new LogsServlet();
        lServlet.doGet(mockReq, mockResp);

        //getting the content and comparing with expected result
        String result = mockResp.getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        arrayNode.add(mapper.readTree(log1.toString()));

        String expectedResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        //asserting that the result is equal to the expected result
        assertEquals(expectedResult, result);
    }

    @Test
    public void test_CompareToYear(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "OFF",
                "string");
        LogEvents log2 = new LogEvents("log-id-2",
                "application started",
                "20-05-2020 10:20:30",
                "main",
                "com.example.Foo",
                "TRACE",
                "string");

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        ///asserting that the values are correct when comparing the years
        assertEquals(1, result1);
        assertEquals(-1, result2);
    }

    @Test
    public void test_CompareToMonth(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-06-2021 10:20:30",
                "main",
                "com.example.Foo",
                "FATAL",
                "string");
        LogEvents log2 = new LogEvents("log-id-2",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "ERROR",
                "string");

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        //asserting that the values are correct when comparing the months
        assertEquals(1, result1);
        assertEquals(-1, result2);
    }

    @Test
    public void test_CompareToDay(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "25-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "INFO",
                "string");
        LogEvents log2 = new LogEvents("log-id-2",
                "application started",
                "20-05-2021 10:20:30",
                "main",
                "com.example.Foo",
                "ALL",
                "string");

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        //asserting that the values are correct when comparing the days
        assertEquals(1, result1);
        assertEquals(-1, result2);
    }

    @Test
    public void test_CompareToHour(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-05-2021 15:20:30",
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

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        //asserting the values are correct when comparing hours
        assertEquals(1, result1);
        assertEquals(-1, result2);
    }

    @Test
    public void test_CompareToMinute(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-05-2021 10:30:30",
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

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        //asserting values are correct when comparing minutes
        assertEquals(1, result1);
        assertEquals(-1, result2);
    }

    @Test
    public void test_CompareToSecond(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("log-id-1",
                "application started",
                "20-05-2021 10:20:46",
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

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        //asserting that values are correct when comapring seconds
        assertEquals(1, result1);
        assertEquals(-1, result2);
    }

    @Test
    public void test_SameTimestamp(){
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

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

        int result1 = log1.compareTo(log2);
        int result2 = log2.compareTo(log1);

        //asserting that the values are correct when logs have the same timestamp
        assertEquals(0, result1);
        assertEquals(0, result2);
    }
}
