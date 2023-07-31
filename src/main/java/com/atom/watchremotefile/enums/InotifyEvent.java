package com.atom.watchremotefile.enums;


/**
 * @author Atom
 */
public enum InotifyEvent {
    CREATE, DELETE, MODIFY, MOVE;

    /**
     * @param eventString inotifywait 监控事件返回字符串
     * @return
     */
    public static InotifyEvent fromString(String eventString) {
        if (eventString == null) {
            return null;
        }
        switch (eventString) {
            case "CREATE":
                return CREATE;
            case "DELETE":
                return DELETE;
            case "MODIFY":
                return MODIFY;
            case "MOVED_FROM":
            case "MOVED_TO":
                return MOVE;
            default:
                return null;
        }
    }
}