package org.sindria.xpipe.lib.nanoREST.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LoggerFormatter extends SimpleFormatter {

//    @Override
//    public String format(LogRecord record) {
//        return record.getThreadID()+"::"+record.getSourceClassName()+"::"
//                +record.getSourceMethodName()+"::"
//                +new Date(record.getMillis())+"::"
//                +record.getMessage()+"\n";
//    }

}
