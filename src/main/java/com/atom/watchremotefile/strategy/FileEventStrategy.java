package com.atom.watchremotefile.strategy;

import com.jcraft.jsch.Session;

/**
 * @author Atom
 */
public interface FileEventStrategy {
    /**
     * 处理文件事件（inotifywait）
     *
     * @param session
     * @param remoteFilePath
     */
    void handleEvent(Session session, String remoteFilePath);

}
