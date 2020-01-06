package com.mercury.platform.core.utils;

import com.mercury.platform.core.utils.interceptor.*;
import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageFileHandler implements AsSubscriber {
    private static final String dateRGPattern = "^\\n?[0-9]{4}\\/(0[1-9]|1[0-2])\\/(0[1-9]|[1-2][0-9]|3[0-1])\\s([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
    private final Logger logger = LogManager.getLogger(MessageFileHandler.class);
    private Date lastMessageDate = new Date();
    private Pattern datePattern;

    private List<MessageInterceptor> interceptors = new ArrayList<>();

    public MessageFileHandler() {
        this.datePattern = Pattern.compile(dateRGPattern);

        this.interceptors.add(new TradeIncMessagesInterceptor());
        this.interceptors.add(new TradeOutMessagesInterceptor());
        this.interceptors.add(new PlainMessageInterceptor());
        this.interceptors.add(new PlayerJoinInterceptor());
        this.interceptors.add(new PlayerLeftInterceptor());
        this.interceptors.add(new PlayerInaccessibleInterceptor());

        this.subscribe();
    }

    public void parse(File logFile) {
        List<String> stubMessages = new ArrayList<>();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(logFile, "r");
            int lines = 0;
            StringBuilder builder = new StringBuilder();
            long length = logFile.length();
            length--;
            randomAccessFile.seek(length);
            for (long seek = length; seek >= 0; --seek) {
                randomAccessFile.seek(seek);
                char c = (char) randomAccessFile.read();
                builder.append(c);
                if (c == '\n') {
                    builder = builder.reverse();
                    String str = builder.toString();
                    String utf8 = new String(str.getBytes("ISO-8859-1"), "UTF-8");
                    stubMessages.add(utf8);
                    lines++;
                    builder = new StringBuilder();
                    if (lines == 30) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in MessageFileHandler: ", e);
        }
        List<String> filteredMessages = stubMessages.stream().filter(message ->
                message != null && !message.equals("\n"))
                .collect(Collectors.toList());

        List<String> resultMessages = filteredMessages.stream().filter(message -> {
            if (message.contains("2019") || message.contains("2020") || message.contains("2021")) { //todo
                Date date = new Date(StringUtils.substring(message, 0, 20));
                return date.after(lastMessageDate);
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        Collections.reverse(resultMessages);
        this.interceptors.forEach(interceptor -> {
            resultMessages.forEach(message -> {
                if (interceptor.match(message)) {
                    this.lastMessageDate = new Date(StringUtils.substring(message, 0, 20));
                }
            });
        });
    }

    @Override
    public void subscribe() {
        MercuryStoreCore.addInterceptorSubject.subscribe(interceptor -> {
            this.interceptors.add(interceptor);
            this.lastMessageDate = new Date();
        });
        MercuryStoreCore.removeInterceptorSubject.subscribe(interceptor -> {
            this.interceptors.remove(interceptor);
        });
    }
}
