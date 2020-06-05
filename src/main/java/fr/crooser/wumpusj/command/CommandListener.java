package fr.crooser.wumpusj.command;

import fr.crooser.wumpusj.Bot;
import fr.crooser.wumpusj.command.handler.CommandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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

        final Member member = event.getMember();
        final Message message = event.getMessage();
        final Guild guild = event.getGuild();
        final TextChannel channel = event.getChannel();

        final String content = message.getContentRaw();

        if (content.startsWith(bot.getPrefix())) this.commandHandler.handle(content, event);
    }
}
