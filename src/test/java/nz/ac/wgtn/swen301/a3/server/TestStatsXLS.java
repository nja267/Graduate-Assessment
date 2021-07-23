package nz.ac.wgtn.swen301.a3.server;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class containing the test cases for StatsXLS.
 */
public class TestStatsXLS {

    @Test
    public void test_NoLogs() throws IOException {
        //clearing the DB to make sure there are no logs
        Persistency.DB.clear();

        //creating the mock request and response
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        StatsXLSServlet statsXLSServlet = new StatsXLSServlet();
        statsXLSServlet.doGet(mockReq, mockResp);

        //getting the content as bytes and making an inputstream
        byte[] contentBytes = mockResp.getContentAsByteArray();
        InputStream inputStream = new ByteArrayInputStream(contentBytes);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        Sheet statsSheet = workbook.getSheet("stats");
        Row headers = statsSheet.getRow(0);

        List<String> headerCols = new ArrayList<>(Arrays.asList("logger",
                                                                "ALL",
                                                                "TRACE",
                                                                "DEBUG",
                                                                "INFO",
                                                                "WARN",
                                                                "ERROR",
                                                                "FATAL",
                                                                "OFF"));

        //asserting that the only things in the xls file are headers
        assertEquals(1, statsSheet.getPhysicalNumberOfRows());

        //asserting that the headers are correct
        for(int i = 0; i < headerCols.size(); i++){
            assertEquals(headerCols.get(i), headers.getCell(i).getStringCellValue());
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

        StatsXLSServlet statsXLSServlet = new StatsXLSServlet();
        statsXLSServlet.doGet(mockReq, mockResp);

        //getting the content as bytes and making an inputstream
        byte[] contentBytes = mockResp.getContentAsByteArray();
        InputStream inputStream = new ByteArrayInputStream(contentBytes);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        Sheet statsSheet = workbook.getSheet("stats");

        //getting the header row and the content row
        Row headers = statsSheet.getRow(0);
        Row content = statsSheet.getRow(1);

        List<String> headerCols = new ArrayList<>(Arrays.asList("logger",
                                                                "ALL",
                                                                "TRACE",
                                                                "DEBUG",
                                                                "INFO",
                                                                "WARN",
                                                                "ERROR",
                                                                "FATAL",
                                                                "OFF"));


        assertEquals(2, statsSheet.getPhysicalNumberOfRows());

        //asserting that the headers are correct
        for(int i = 0; i < headerCols.size(); i++){
            assertEquals(headerCols.get(i), headers.getCell(i).getStringCellValue());
        }

        //asserting that the logger name is correct
        assertEquals("com.example.Foo", content.getCell(0).getStringCellValue());

        //getting the values from the cell, casting as double and comparing to the expected result
        List<Integer> expected = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 1, 0, 0, 1));
        List<Integer> actual = new ArrayList<>();
        for(int i = 1 ; i < 9; i++){
            double value = content.getCell(i).getNumericCellValue();
            actual.add((int) value);
        }

        assertEquals(expected, actual);
    }

    @Test
    public void test_Response_MultipleLogs() throws IOException {
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

        StatsXLSServlet statsXLSServlet = new StatsXLSServlet();
        statsXLSServlet.doGet(mockReq, mockResp);

        //getting the content as bytes and making an inputstream
        byte[] contentBytes = mockResp.getContentAsByteArray();
        InputStream inputStream = new ByteArrayInputStream(contentBytes);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        Sheet statsSheet = workbook.getSheet("stats");

        //getting the header row and the content row
        Row headers = statsSheet.getRow(0);
        Row content1 = statsSheet.getRow(1);
        Row content2 = statsSheet.getRow(2);

        List<String> headerCols = new ArrayList<>(Arrays.asList("logger",
                                                                "ALL",
                                                                "TRACE",
                                                                "DEBUG",
                                                                "INFO",
                                                                "WARN",
                                                                "ERROR",
                                                                "FATAL",
                                                                "OFF"));

        assertEquals(3, statsSheet.getPhysicalNumberOfRows());

        //asserting that the headers are correct
        for(int i = 0; i < headerCols.size(); i++){
            assertEquals(headerCols.get(i), headers.getCell(i).getStringCellValue());
        }

        //asserting that the logger name is correct for the first content row and the second row
        assertEquals("com.example.Foo", content1.getCell(0).getStringCellValue());
        assertEquals("com.example.Fee", content2.getCell(0).getStringCellValue());

        List<Integer> fooExpected = new ArrayList<>(Arrays.asList(1, 0, 1, 0, 0, 0, 0, 0));
        List<Integer> feeExpected = new ArrayList<>(Arrays.asList(0, 0, 0, 1, 0, 1, 1, 0));

        //getting the actual values
        List<Integer> fooActual = new ArrayList<>();
        for(int i = 1 ; i < 9; i++){
            double value = content1.getCell(i).getNumericCellValue();
            fooActual.add((int) value);
        }

        List<Integer> feeActual = new ArrayList<>();
        for(int i = 1 ; i < 9; i++){
            double value = content2.getCell(i).getNumericCellValue();
            feeActual.add((int) value);
        }

        //asserting that the values are as expected and the status is correct
        assertEquals(fooExpected, fooActual);
        assertEquals(feeExpected, feeActual);
        assertEquals(200, mockResp.getStatus());
    }
}
