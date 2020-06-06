package fr.crooser.wumpusj;

import fr.crooser.wumpusj.command.Command;
import fr.crooser.wumpusj.command.CommandListener;
import fr.crooser.wumpusj.command.handler.CommandHandler;
import fr.crooser.wumpusj.command.handler.CommandHandlerCommons;
import fr.crooser.wumpusj.guild.join.GuildMemberJoin;
import fr.crooser.wumpusj.reaction.ReactionListener;
import fr.crooser.wumpusj.reaction.ReactionTrigger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.function.Consumer;

public class Bot extends ListenerAdapter {

    private final String name;
    private final List<Command> commands;
    private final String prefix;
    private final List<ReactionTrigger> reactionTriggers;
    private final Consumer<GuildMemberJoin> memberJoin;
    private JDA jda;
    private final Logger logger;

    public Bot(
            @NotNull String name,
            @Nullable String prefix,
            @Nullable Activity activity,
            @NotNull JDABuilder jdaBuilder,
            @Nullable List<Command> commands,
            @Nullable CommandHandlerCommons handlerCommons,
            @Nullable List<ReactionTrigger> reactionTriggers,
            @Nullable Consumer<GuildMemberJoin> memberJoin
    ) {

        this.name = name;
        this.commands = commands;
        this.prefix = prefix;
        this.reactionTriggers = reactionTriggers;
        this.memberJoin = memberJoin;
        this.logger = LoggerFactory.getLogger(this.name);

        if (commands != null && !commands.isEmpty()) {

            assert handlerCommons != null;
            jdaBuilder.addEventListeners(new CommandListener(new CommandHandler(this, handlerCommons.getInsufficientPermissions(), handlerCommons.getSyntaxError(), handlerCommons.getOnAdminError(), handlerCommons.getOnYourselfError(), handlerCommons.getUnknownCommand())));
        }

        if (reactionTriggers != null && !reactionTriggers.isEmpty()) jdaBuilder.addEventListeners(new ReactionListener(this));

        jdaBuilder.addEventListeners(this);

        if (activity != null) jdaBuilder.setActivity(activity);

        try {

            this.jda = jdaBuilder.build();
        } catch (LoginException e) {

            logger.error("Bot's token is invalid.");
            System.exit(1);
        }
    }

    public JDA getJda() {

        return jda;
    }

    public List<Command> getCommands() {

        return commands;
    }

    public List<ReactionTrigger> getReactionTriggers() {

        return reactionTriggers;
    }

    public String getPrefix() {

        return prefix;
    }

    public Logger getLogger() {

        return logger;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {

        logger.info(this.name + " online and ready !");
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event)  {

        if (this.memberJoin != null) memberJoin.accept(new GuildMemberJoin(event.getMember(), event.getGuild(), event.getJDA()));

        super.onGuildMemberJoin(event);
    }
}
