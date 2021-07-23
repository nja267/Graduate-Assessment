package nz.ac.wgtn.swen301.a3.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * StatsHTML Servlet that gets the LogEvents from DB and prints it out onto the site.
 */
public class StatsServlet extends HttpServlet {

    //map that stores the names of the loggers and the list of the number of times different Level loggers show up in DB
    public Map<String, List<Integer>> loggerNameValues = new HashMap<>();

    //final List of strings for the header of the columns on the site
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
        //method call to populate the map with values
        populateValues();

        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();

        //creating the borders and the html code
        printWriter.print("<html>");
        printWriter.print("<body>");
        printWriter.print("<table border>");

        //iterating through the headers and printing them out
        for(int i = 0; i < this.headerCols.size() - 1; i++){
            printWriter.print("<td>" + this.headerCols.get(i) + "</td>");
        }
        printWriter.println("<td>" + this.headerCols.get(this.headerCols.size() - 1) + "</td>");

        //getting the keys and the values and printing them out
        for(String logName : this.loggerNameValues.keySet()){
            printWriter.print("<tr><td>");
            printWriter.print(logName + " ");

            List<Integer> logValues = this.loggerNameValues.get(logName);
            for(Integer value : logValues){
                printWriter.print("</td><td>");
                printWriter.print(value);
            }
            printWriter.print("</td></tr>");
        }

        printWriter.print("</table>");
        printWriter.print("</body>");
        printWriter.print("</html>");

        resp.setStatus(HttpServletResponse.SC_OK);
        printWriter.close();
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
