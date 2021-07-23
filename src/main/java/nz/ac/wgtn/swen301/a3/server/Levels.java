package nz.ac.wgtn.swen301.a3.server;

/**
 * Enum of the different Levels that can be found in the logs being stored, each with their own priority levels.
 */
public enum Levels {

    //all the enums have different priorities, with 8 being the highest prio and 1 being the lowest
    OFF(8), FATAL(7), ERROR(6), WARN(5), INFO(4), DEBUG(3), TRACE(2), ALL(1);

    //priority level of the enum
    private final int priority;

    Levels(int prio){
        this.priority = prio;
    }

    /**
     * Returns the priority of a Level.
     * @return the priority number of the Level
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Gives the Level representation of a given string.
     * @param lStr  String that will be turned into a Level (if valid)
     * @return a Level enum when given a valid string, null otherwise
     */
    public static Levels stringToLevels(String lStr){
        Levels level = null;
        switch(lStr){
            case "OFF":
                level = OFF;
                break;
            case "FATAL":
                level = FATAL;
                break;
            case "ERROR":
                level = ERROR;
                break;
            case "WARN":
                level = WARN;
                break;
            case "INFO":
                level = INFO;
                break;
            case "DEBUG":
                level = DEBUG;
                break;
            case "TRACE":
                level = TRACE;
                break;
            case "ALL":
                level = ALL;
                break;
        }
        return level;
    }
}
