package com.atom.watchremotefile.listener;

import com.atom.watchremotefile.config.RemoteLinuxProperties;
import com.atom.watchremotefile.enums.InotifyEvent;
import com.atom.watchremotefile.strategy.CreateEventStrategy;
import com.atom.watchremotefile.strategy.FileEventStrategy;
import com.atom.watchremotefile.strategy.ModifyEventStrategy;
import com.atom.watchremotefile.strategy.MoveEventStrategy;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Atom
 */
@Component
public class RemoteDirectoryWatcherListener {

    @Autowired
    private RemoteLinuxProperties remoteLinuxProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDirectoryWatcherListener.class);
    private static final List<String> FILE_EXTENSIONS_TO_MONITOR = Arrays.asList("txt", "csv", "json");
    private static final Map<InotifyEvent, FileEventStrategy> EVENT_STRATEGY_MAP = new HashMap<>();


    @Autowired
    public RemoteDirectoryWatcherListener(ModifyEventStrategy modifyEventStrategy,
                                          CreateEventStrategy createEventStrategy,
                                          MoveEventStrategy moveEventStrategy) {
        EVENT_STRATEGY_MAP.put(InotifyEvent.MODIFY, modifyEventStrategy);
        EVENT_STRATEGY_MAP.put(InotifyEvent.CREATE, createEventStrategy);
        EVENT_STRATEGY_MAP.put(InotifyEvent.MOVE, moveEventStrategy);
    }


    @PostConstruct
    public void startWatcher() throws Exception {
        String username = remoteLinuxProperties.getUsername();
        String password = remoteLinuxProperties.getPassword();
        String hostname = remoteLinuxProperties.getHostname();
        int port = remoteLinuxProperties.getPort();
        String remoteDirectoryPath = remoteLinuxProperties.getDirectory();

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        String command = "inotifywait -r -m " + remoteDirectoryPath;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            channel.connect();

            // 读取文件刘
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("inotifywait Response line : [{}]", line);
                // Process the output and filter specific events
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    InotifyEvent event = InotifyEvent.fromString(parts[1]);
                    if (event != null) {
                        String remoteFilePath = remoteDirectoryPath + "/" + parts[2];

                        // 处理指定类型文件
                        String fileExtension = getFileExtension(remoteFilePath);
                        if (FILE_EXTENSIONS_TO_MONITOR.contains(fileExtension)) {
                            handleFileEvent(session, remoteFilePath, event);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("handle file event exception ", e);
        } finally {
            channel.disconnect();
            session.disconnect();
        }
    }

    public void handleFileEvent(Session session, String remoteFilePath, InotifyEvent event) {
        FileEventStrategy eventStrategy = EVENT_STRATEGY_MAP.get(event);
        if (eventStrategy != null) {
            eventStrategy.handleEvent(session, remoteFilePath);
        } else {
            LOGGER.warn("Unsupported event type: {}", event);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex != -1 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1) : "";
    }

}