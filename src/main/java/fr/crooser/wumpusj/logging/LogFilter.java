package fr.crooser.wumpusj.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.crooser.wumpusj.Bot;

public class LogFilter extends Filter<ILoggingEvent> {

    private final Bot bot = Bot.get();

    @Override
    public FilterReply decide(ILoggingEvent event) {

        if (!this.bot.getJdaLog()) {

            if (!event.getLoggerName().equals(this.bot.getName()) && !event.getThreadName().contains(this.bot.getName())) return FilterReply.ACCEPT;
            else if (event.getLevel().equals(Level.ERROR)) return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
