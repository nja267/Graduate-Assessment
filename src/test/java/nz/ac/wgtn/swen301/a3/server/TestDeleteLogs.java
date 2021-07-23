package nz.ac.wgtn.swen301.a3.server;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test class containing the test cases for testing the doDelete() of the LogsServlet.
 */
public class TestDeleteLogs {

    @Test
    public void test_Deleting() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748f0851",
                "application started",
                "12-12-2021 10:20:30",
                "main",
                "com.example.Foo",
                "WARN",
                "string");

        //manually adding for testing purposes
        Persistency.DB.add(log);

        //asserting that there is only one log in DB
        assertEquals(1, Persistency.DB.size());

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        LogsServlet lServlet = new LogsServlet();
        lServlet.doDelete(mockReq, mockResp);

        //asserting that after doDelete() there are no more logs in DB
        assertEquals(0, Persistency.DB.size());
    }

    @Test
    public void test_AddingDeletingAdding() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log1 = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748f0851",
                "application started",
                "12-01-2021 10:20:30",
                "main",
                "com.example.Foo",
                "WARN",
                "string");
        //manually adding for testing purposes
        Persistency.DB.add(log1);

        //asserting that there is only 1 log in DB
        assertEquals(1, Persistency.DB.size());

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        LogsServlet lServlet = new LogsServlet();
        lServlet.doDelete(mockReq, mockResp);

        //asserting that after deleting there are no more logs in DB
        assertEquals(0, Persistency.DB.size());

        LogEvents log2 = new LogEvents("d290f1ee-6c54-4b01-90e6-d701748",
                "application started",
                "13-09-2000 10:20:30",
                "main",
                "com.example.Foo",
                "ERROR",
                "string");
        //manually adding for testing purposes
        Persistency.DB.add(log2);

        //asserting that there is 1 log in DB after adding again
        assertEquals(1, Persistency.DB.size());
    }

    @Test
    public void test_CheckStatus() throws IOException {
        //clearing the DB to make sure there are no logs from previous test
        Persistency.DB.clear();

        LogEvents log = new LogEvents("d290f1ee-6c54-4b01-90e6",
                "application started",
                "02-03-2021 10:20:30",
                "main",
                "com.example.Foo",
                "TRACE",
                "string");
        //manually adding for testing purposes
        Persistency.DB.add(log);

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        LogsServlet lServlet = new LogsServlet();
        lServlet.doDelete(mockReq, mockResp);

        //asserting that there are no logs in Db after deleting and checking the correct status
        assertEquals(0, Persistency.DB.size());
        assertEquals(200, mockResp.getStatus());
    }
}
