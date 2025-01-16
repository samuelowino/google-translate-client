package org.mwangi.desktop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadNameFormatter extends Formatter {
    private String format;
    public ThreadNameFormatter(){
        this.format= LogManager.getLogManager().getProperty("org.mwangi.desktop.ThreadNameFormatter.format");
    }

    public void setFormat(String format) {
        this.format = format;
    }


    @Override
    public String format(LogRecord logRecord) {
        ZonedDateTime zdt=logRecord.getInstant().atZone(ZoneId.systemDefault());
        String source = Stream.of(Optional.ofNullable(logRecord.getSourceClassName()),Optional.ofNullable(logRecord.getSourceMethodName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(""));
        String  message=formatMessage(logRecord);
        String throwable = Optional.ofNullable(logRecord.getThrown())
                .map(_ -> {
                    StringWriter sw = new StringWriter();
                    try (PrintWriter pw = new PrintWriter(sw)) {
                        pw.println();
                        logRecord.getThrown().printStackTrace(pw);
                    }
                    return sw.toString();
                }).orElse("");
        String threadName=Thread.currentThread().getName();

        return String.format(format,
                zdt,
                source,
                logRecord.getLoggerName(),
                logRecord.getLevel().getLocalizedName(),
                message,
                throwable,
                threadName
        );
    }
}
