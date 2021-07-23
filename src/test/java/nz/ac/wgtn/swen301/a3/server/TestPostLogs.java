package nz.ac.wgtn.swen301.a3.server;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test class containing test cases for doPost() of LogsServlet.
 */
public class TestPostLogs {

    @Test
    public void test_correct() throws IOException {
        Persistency.DB.clear(); //clearing for testing purposes

        LogEvents log = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748f0851",
                                      "application started",
                                      "29-05-2021 11:22:30",
                                      "main",
                                      "com.example.Foo",
                                      "WARN",
                                      "string");
        //changing the string into bytes
        byte[] bytes = log.toString().getBytes();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        //setting the content with the bytes
        mockReq.setContent(bytes);

        LogsServlet lServlet = new LogsServlet();
        lServlet.doPost(mockReq, mockResp);

        //asserting that the status is correct and that DB has been populated by doPost()
        assertEquals(201, mockResp.getStatus());
        assertEquals(1, Persistency.DB.size());
    }

    @Test
    public void test_NullLogInfo() throws IOException {
        Persistency.DB.clear(); //clearing for testing purposes

        LogEvents log = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748f0851",
                null,
                "21-06-2020 10:20:30",
                null,
                "com.example.Foo",
                "WARN",
                "string");

        //getting the string as bytes
        byte[] bytes = log.toString().getBytes();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        //setting the content to the bytes
        mockReq.setContent(bytes);

        LogsServlet lServlet = new LogsServlet();
        lServlet.doPost(mockReq, mockResp);

        //asserting that the status is correct and that the size is 0 as it is invalid
        assertEquals(400, mockResp.getStatus());
        assertEquals(0, Persistency.DB.size());
    }

    @Test
    public void test_SameId() throws IOException {
        Persistency.DB.clear(); //clearing for testing purposes
        LogEvents log1 = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748f0851",
                "application started",
                "12-12-2020 10:20:30",
                "main",
                "com.example.Foo",
                "WARN",
                "string");
        LogEvents log2 = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748f0851",
                "application started",
                "26-07-1999 06:09:20",
                "main",
                "com.example.Foo",
                "WARN",
                "string");

        //converting the logs into bytes
        byte[] bytes1 = log1.toString().getBytes();
        byte[] bytes2 = log2.toString().getBytes();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        //setting the content and calling on doPost()
        mockReq.setContent(bytes1);

        LogsServlet lServlet = new LogsServlet();
        lServlet.doPost(mockReq, mockResp);

        mockReq.setContent(bytes2);
        lServlet.doPost(mockReq, mockResp);

        //asserting that the status is correct and that there is only one log as second log wouldn't be added
        assertEquals(409, mockResp.getStatus());
        assertEquals(1, Persistency.DB.size());
    }
}
