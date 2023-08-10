package com.atom.watchremotefile.strategy;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Atom
 */
@Component
public class CreateEventStrategy implements FileEventStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateEventStrategy.class);

    @Override
    public void handleEvent(Session session, String remoteFilePath) {
        try {
            Map<String, String> fileAttributes = getFileAttributes(session, remoteFilePath);
            // 文件元数据信息
            for (Map.Entry<String, String> entry : fileAttributes.entrySet()) {
                LOGGER.info("[{}] 属性信息 [{}]:[{}]", remoteFilePath, entry.getKey(), entry.getValue());
            }

            String currentContent = getFileContent(session, remoteFilePath);
            LOGGER.info("[{}] File Content:\n{}", remoteFilePath, currentContent);

        } catch (JSchException | IOException e) {
            LOGGER.warn("handle create file event exception ", e);
        }
    }


    private static Map<String, String> getFileAttributes(Session session, String remoteFilePath) throws JSchException, IOException {
        Map<String, String> fileAttributes = new HashMap<>();

        String command = "ls -l --time-style=long-iso " + remoteFilePath;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            channel.connect();

            // 读取文件刘
            String line = reader.readLine();
            if (line != null) {
                // 正则表达式"\s+"表示一个或多个连续的空格字符（包括空格、制表符、换行符）
                String[] parts = line.split("\\s+");
                if (parts.length >= 8) {
                    fileAttributes.put("Permissions", parts[0]);
                    fileAttributes.put("Owner", parts[2]);
                    fileAttributes.put("Group", parts[3]);
                    fileAttributes.put("Size", parts[4]);
                    fileAttributes.put("Last Modified Time", parts[5] + " " + parts[6]);
                }
            }

            channel.disconnect();
        }

        return fileAttributes;
    }


    private static String getFileContent(Session session, String remoteFilePath) throws JSchException, IOException {
        StringBuilder fileContent = new StringBuilder();

        String command = "cat " + remoteFilePath;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            channel.disconnect();
        }

        return fileContent.toString();
    }
}