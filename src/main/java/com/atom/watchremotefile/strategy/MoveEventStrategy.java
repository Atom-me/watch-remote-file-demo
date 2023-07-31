package com.atom.watchremotefile.strategy;

import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Atom
 */
@Component
public class MoveEventStrategy implements FileEventStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveEventStrategy.class);

    @Override
    public void handleEvent(Session session, String remoteFilePath) {
        // do sth
        LOGGER.info(" start handle move event...");


    }
}
