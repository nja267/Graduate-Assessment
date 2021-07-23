package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Class that represents the LogEvents that are stored in Persistency.DB.
 */
public class LogEvents implements Comparable<LogEvents>{

    //the different properties of a LogEvents object
    private String id;
    private String message;
    private String timestamp;
    private String thread;
    private String logger;
    private String level;
    private String errorDetails;

    public LogEvents(String id, String message, String timestamp, String thread, String logger, String level, String errorDetails){
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.thread = thread;
        this.logger = logger;
        this.level = level;
        this.errorDetails = errorDetails;
    }

    /**
     * Gets the id of the LogEvent.
     * @return id of the LogEvent as a String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the message of the LogEvent.
     * @return message of the LogEvent as a String
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the timestamp of the LogEvent.
     * @return timestamp creation of the LogEvent as a String
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    /**
     * Gets the thread of the LogEvent.
     * @return thread of the LogEvent as a String
     */
    public String getThread() {
        return this.thread;
    }

    /**
     * Gets the logger of the LogEvent.
     * @return logger of the LogEvent as a String
     */
    public String getLogger() {
        return this.logger;
    }

    /**
     * Gets the level of the LogEvent.
     * @return level of the LogEvent as a String
     */
    public String getLevel() {
        return this.level;
    }

    /**
     * Gets the error details of the LogEvent.
     * @return error details of the LogEvent as a String
     */
    public String getErrorDetails() {
        return this.errorDetails;
    }

    @Override
    public int compareTo(LogEvents o) {
//        A positive integer, if the current object is greater than the parameter object
//        A negative integer, if the current object is less than the parameter object
//        Zero, if the current object is equal to the parameter object

        //getting the timestamp from the other object and this object
        String otherTimestamp = o.getTimestamp();
        String thisTimestamp = this.getTimestamp();

        //we want to compare year first, month, day, then hour, minute, second
        String[] otherStamp = otherTimestamp.split(" "); //28-05-2021 16:01:24
        String[] otherDate = otherStamp[0].split("-"); //day-month-year
        String[] otherTime = otherStamp[1].split(":"); //hour:minute:second

        int otherYear = Integer.parseInt(otherDate[2]);
        int otherMonth = Integer.parseInt(otherDate[1]);
        int otherDay = Integer.parseInt(otherDate[0]);
        int otherHour = Integer.parseInt(otherTime[0]);
        int otherMinute = Integer.parseInt(otherTime[1]);
        int otherSecond = Integer.parseInt(otherTime[2]);

        String[] thisStamp = thisTimestamp.split(" "); //28-05-2021 16:01:24
        String[] thisDate = thisStamp[0].split("-"); //day-month-year
        String[] thisTime = thisStamp[1].split(":"); //hour:minute:second

        int thisYear = Integer.parseInt(thisDate[2]);
        int thisMonth = Integer.parseInt(thisDate[1]);
        int thisDay = Integer.parseInt(thisDate[0]);
        int thisHour = Integer.parseInt(thisTime[0]);
        int thisMinute = Integer.parseInt(thisTime[1]);
        int thisSecond = Integer.parseInt(thisTime[2]);

        if(thisYear > otherYear){
            return 1;
        } else if(thisYear < otherYear) {
            return -1;
        }

        if(thisYear == otherYear){
            if(thisMonth > otherMonth){
                return 1;
            } else if(thisMonth < otherMonth){
                return -1;
            }
        }

        if(thisYear == otherYear && thisMonth == otherMonth){
            if(thisDay > otherDay){
                return 1;
            } else if(thisDay < otherDay){
                return -1;
            }
        }

        if(thisYear == otherYear && thisMonth == otherMonth && thisDay == otherDay){
            if(thisHour > otherHour){
                return 1;
            } else if(thisHour < otherHour){
                return -1;
            }
        }

        if(thisYear == otherYear && thisMonth == otherMonth && thisDay == otherDay && thisHour == otherHour){
            if(thisMinute > otherMinute){
                return 1;
            } else if(thisMinute < otherMinute){
                return -1;
            }
        }

        if(thisYear == otherYear && thisMonth == otherMonth && thisDay == otherDay && thisHour == otherHour
            && thisMinute == otherMinute){
            if(thisSecond > otherSecond){
                return 1;
            } else if(thisSecond < otherSecond){
                return -1;
            }
        }

        return 0;
    }

    @Override
    public String toString(){
        //creating new ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        //changing the LogEvent into an ObjectNode
        ObjectNode eventNode = logEventToJSON();
        String str = null;
        try {
            //converting the ObjectNode into JSON string
            str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(eventNode);
        } catch (JsonProcessingException e) {
            //
        }
        return str;
    }

    /**
     * Turns this LogEvent into an ObjectNode.
     * @return ObjectNode representing this LogEvent
     */
    private ObjectNode logEventToJSON(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objNode = mapper.createObjectNode();

        objNode.put("id", this.id);
        objNode.put("message", this.message);
        objNode.put("timestamp", this.timestamp);
        objNode.put("thread", this.thread);
        objNode.put("logger", this.logger);
        objNode.put("level", this.level);
        objNode.put("errorDetails", this.errorDetails);

        return objNode;
    }
}
