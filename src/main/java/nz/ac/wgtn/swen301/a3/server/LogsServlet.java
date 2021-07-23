package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Log Servlet that responds to different HTTP requests, this class works with methods that
 * gets, posts and deletes logs.
 */
public class LogsServlet extends HttpServlet {

    /**
     * Called by the server (via the service method) to allow a servlet to handle a GET request.
     * @param req Request that contains the request the client made to the servlet
     * @param resp Response that contains the response of the servlet that is returned to the client
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Persistency.logs(); //for testing purposes

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        PrintWriter printWriter = resp.getWriter();

        //creating the different error messages when given bad input request parameters
        String badReqMsgLimit = "bad or missing input parameter (limit is not a positive, non-zero integer)";
        String badReqMsgLevel = "bad or missing input parameter (level is not a valid level)";
        int limit = -1;
        String level = null;

        //making sure that the limit is >= 0 and is a number
        try{
            limit = Integer.parseInt(req.getParameter("limit"));
            if(limit < 0 || limit > Integer.MAX_VALUE){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            //sending an error and message associated with the error
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, badReqMsgLimit);
            return;
        }

        //making sure level is a valid Level (Levels enum)
        try{
            level = req.getParameter("level");
            if(!isValid(level)){
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            //sending an error and message associated with the error
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, badReqMsgLevel);
            return;
        }

        //getting the logs that are greater than or equal to the priority of the level
        List<LogEvents> logs = new ArrayList<>();
        Levels levelReq = Levels.stringToLevels(level);
        for(LogEvents lEvent : Persistency.DB){
            if(Levels.stringToLevels(lEvent.getLevel()).getPriority() >= levelReq.getPriority()){
                logs.add(lEvent);
            }
        }

        if(!Persistency.DB.isEmpty()){
            //if the logs size is less than the limit, change the limit so that it gets appropriate no. of logs
            if(logs.size() < limit){
                limit = logs.size();
            }

            //sorting the logs based on timestamp
            Collections.sort(logs);

            //adding the logs as strings into the arrayNode
            for(int i = 0; i < limit; i++){
                arrayNode.add(mapper.readTree(logs.get(logs.size() - 1).toString()));
                logs.remove(logs.size() - 1);
            }
        }

        //changing the arrayNode into a string and printing it out
        String responseStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
        printWriter.print(responseStr);

        resp.setStatus(HttpServletResponse.SC_OK);
        printWriter.close();
    }

    /**
     * Called by the server (via the service method) to allow a servlet to handle a POST request.
     * @param req Request that contains the request the client made to the servlet
     * @param resp  Response that contains the response of the servlet that is returned to the client
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();

        ObjectMapper mapper = new ObjectMapper();

        //reading the tree and creating Json node corresponding to the different properties of a log
        JsonNode reqNode = mapper.readTree(reader);

        JsonNode nodeId = reqNode.get("id");
        JsonNode nodeMsg = reqNode.get("message");
        JsonNode nodeTimestamp = reqNode.get("timestamp");
        JsonNode nodeThread = reqNode.get("thread");
        JsonNode nodeLogger = reqNode.get("logger");
        JsonNode nodeLevel = reqNode.get("level");
        JsonNode nodeErrorDetails = reqNode.get("errorDetails");

        //if the log has any null values or invalid items, send an error and return
        if(nodeId == null || nodeMsg == null || nodeTimestamp == null || nodeThread == null ||
            nodeLogger == null || nodeLevel == null || nodeErrorDetails == null ||
            nodeId.asText().equals("null") || nodeMsg.asText().equals("null") || nodeThread.asText().equals("null") ||
            nodeLogger.asText().equals("null") || nodeLevel.asText().equals("null") || nodeErrorDetails.asText().equals("null")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid input, object invalid");
            return;
        }

        //checking each log in DB and comparing to the log being posted and if it already exists send an error and return
        for(LogEvents lEvent: Persistency.DB){
            if(nodeId.asText().equals(lEvent.getId())){
                resp.sendError(HttpServletResponse.SC_CONFLICT, "a log event with this id already exists");
                return;
            }
        }

        //creating a new LogEvents object with the valid properties, then adding to DB
        LogEvents newLog = new LogEvents(nodeId.asText(), nodeMsg.asText(), nodeTimestamp.asText(), nodeThread.asText(),
                                            nodeLogger.asText(), nodeLevel.asText(), nodeErrorDetails.asText());
        Persistency.DB.add(newLog);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    /**
     * Called by the server (via the service method) to allow a servlet to handle a DELETE request.
     * @param req Request that contains the request the client made to the servlet
     * @param resp  Response that contains the response of the servlet that is returned to the client
     * @throws IOException
     */
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //clearing DB and setting the content type
        Persistency.DB.clear();
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Checking to see if string in parameter is a valid Levels enum.
     * @param levelStr String to be checked
     * @return true if the string is a valid Levels enum, false otherwise
     */
    private boolean isValid(String levelStr){
        Levels level = Levels.stringToLevels(levelStr);
        if(level == null){
            return false;
        }

        return true;
    }
}
