package fr.crooser.wumpusj.command;

import fr.crooser.wumpusj.Bot;
import fr.crooser.wumpusj.command.handler.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {

    private final Bot bot;
    private final CommandHandler commandHandler;

    public CommandListener(@NotNull CommandHandler commandHandler) {

        this.bot = commandHandler.getBot();
        this.commandHandler = commandHandler;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        Member member = event.getMember();
        Message message = event.getMessage();

        final String content = message.getContentRaw();

        if (content.startsWith(this.bot.getPrefix()) && member.getUser() != this.bot.getJda().getSelfUser() && !member.getUser().isBot()) {

            this.bot.debug("Recognized possible command, transmitting to handler");
            this.commandHandler.handle(content, event);
        }
    }
}
