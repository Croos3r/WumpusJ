package fr.crooser.wumpusj.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.crooser.wumpusj.Bot;

public class LogFilter extends Filter<ILoggingEvent> {

    private final Bot bot = Bot.get();

    @Override
    public FilterReply decide(ILoggingEvent event) {

        if (!bot.getJdaLog() && !event.getLoggerName().equals(bot.getName()) && !event.getThreadName().contains(bot.getName())) return FilterReply.DENY;
        return FilterReply.ACCEPT;
    }
}
