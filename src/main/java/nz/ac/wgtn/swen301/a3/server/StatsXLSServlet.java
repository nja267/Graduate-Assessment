package nz.ac.wgtn.swen301.a3.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * StatsXLS Servlet that gets the LogEvents from DB and puts the values into an excel spreadsheet.
 */
public class StatsXLSServlet extends HttpServlet {

    //map that stores the names of the loggers and the list of the number of times different Level loggers show up in DB
    public Map<String, List<Integer>> loggerNameValues = new HashMap<>();

    //final List of strings for the header of the columns on the excel spreadsheet
    private final List<String> headerCols = new ArrayList<>(Arrays.asList("logger",
                                                                          "ALL",
                                                                          "TRACE",
                                                                          "DEBUG",
                                                                          "INFO",
                                                                          "WARN",
                                                                          "ERROR",
                                                                          "FATAL",
                                                                          "OFF"));

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //method call to populate map with values
        populateValues();

        resp.setHeader("Content-Disposition", "attachment;filename=stats.xlsx");
        resp.setContentType("application/vnd.ms-excel");

        OutputStream outputStream = resp.getOutputStream();

        XSSFWorkbook workBook = new XSSFWorkbook();

        //creating a sheet
        XSSFSheet sheet = workBook.createSheet("stats");

        int rows = 0;
        int cols = 0;

        //creating the first row
        Row bookRows = sheet.createRow(rows++);
        Cell bookCell;

        //creating cells in the 1st row and setting them to the header
        for(String header : this.headerCols){
            bookCell = bookRows.createCell(cols++);
            bookCell.setCellValue(header);
        }

        //creating other rows and cells and setting them with the value corresponding
        for(String logName : this.loggerNameValues.keySet()){
            cols = 0;
            bookRows = sheet.createRow(rows++);
            bookCell = bookRows.createCell(cols++);
            bookCell.setCellValue(logName);

            List<Integer> logValues = this.loggerNameValues.get(logName);
            for(Integer value : logValues){
                bookCell = bookRows.createCell(cols++);
                bookCell.setCellValue(value);
            }
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        workBook.write(outputStream);
    }

    /**
     * Helper method to populate the map for testing purposes.
     */
    private void populateValues(){
        Set<String> logNames = new HashSet<>();

        //getting the names of the loggers from the LogEvents
        for(LogEvents logEvent : Persistency.DB){
            logNames.add(logEvent.getLogger());
        }

        //getting the logs assoc with the logName
        for(String logName : logNames){
            int ALL = 0;
            int TRACE = 0;
            int DEBUG = 0;
            int INFO = 0;
            int WARN = 0;
            int ERROR = 0;
            int FATAL = 0;
            int OFF = 0;
            List<Integer> assocValues = new ArrayList<>();
            List<LogEvents> assocLogs = new ArrayList<>();

            for(LogEvents logEvent : Persistency.DB){
                if(logEvent.getLogger().equals(logName)){
                    assocLogs.add(logEvent);
                }
            }

            //for all the logs with the same logger name, get their levels and increment the corresponding int variable
            for(LogEvents assoc : assocLogs){
                switch(assoc.getLevel()){
                    case "ALL":
                        ALL++;
                        break;
                    case "TRACE":
                        TRACE++;
                        break;
                    case "DEBUG":
                        DEBUG++;
                        break;
                    case "INFO":
                        INFO++;
                        break;
                    case "WARN":
                        WARN++;
                        break;
                    case "ERROR":
                        ERROR++;
                        break;
                    case "FATAL":
                        FATAL++;
                        break;
                    case "OFF":
                        OFF++;
                        break;
                }
            }

            //once all the logs have been processed for the logName, add them to the assocValues list
            assocValues.addAll(Arrays.asList(ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF));

            //finally adding the logName with the assocValues to the map
            this.loggerNameValues.put(logName, assocValues);
        }

    }
}
