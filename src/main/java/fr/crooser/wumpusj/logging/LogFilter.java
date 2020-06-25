package fr.crooser.wumpusj.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.crooser.wumpusj.Bot;

public class LogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {

        Bot bot = Bot.get();

        if (!bot.getJdaLog()) {

            if (event.getLoggerName().equals(bot.getName()) || event.getThreadName().contains(bot.getName()))
                return FilterReply.ACCEPT;
            else if (event.getLevel().equals(Level.ERROR))
                return FilterReply.ACCEPT;
            else
                return FilterReply.DENY;
        }
        else return FilterReply.ACCEPT;
    }
}
