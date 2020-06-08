package fr.crooser.wumpusj;

import fr.crooser.wumpusj.command.Command;
import fr.crooser.wumpusj.command.CommandListener;
import fr.crooser.wumpusj.command.handler.CommandHandler;
import fr.crooser.wumpusj.command.handler.CommandHandlerCommons;
import fr.crooser.wumpusj.listeners.guild.MemberJoin;
import fr.crooser.wumpusj.listeners.guild.MemberLeave;
import fr.crooser.wumpusj.reaction.ReactionListener;
import fr.crooser.wumpusj.reaction.ReactionTrigger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Bot extends ListenerAdapter {

    private final String name;
    private final Boolean debug;
    private final List<Command> commands;
    private final String prefix;
    private final List<ReactionTrigger> reactionTriggers;
    private JDA jda;
    private final Logger logger;

    public Bot(
            @NotNull String name,
            @Nullable String prefix,
            @NotNull Boolean debug,
            @Nullable Activity activity,
            @NotNull JDABuilder jdaBuilder,
            @Nullable List<Command> commands,
            @Nullable CommandHandlerCommons handlerCommons,
            @Nullable List<ReactionTrigger> reactionTriggers,
            @Nullable Consumer<Member> memberJoin,
            @Nullable Consumer<Guild> memberLeave
    ) {

        this.name = name;
        this.debug = debug;
        this.commands = commands == null ? Collections.emptyList() : commands;
        this.prefix = prefix == null ? "!" : prefix;
        this.reactionTriggers = reactionTriggers == null ? Collections.emptyList() : reactionTriggers;
        this.logger = LoggerFactory.getLogger(this.name);


        if (!this.commands.isEmpty()) {

            if (handlerCommons == null) handlerCommons = new CommandHandlerCommons().setInsufficientPermissions(null).setOnAdminError(null).setOnYourselfError(null).setSyntaxError(null).setUnknownCommand(null);
            jdaBuilder.addEventListeners(new CommandListener(new CommandHandler(this, handlerCommons)));
        }

        if (!this.reactionTriggers.isEmpty()) jdaBuilder.addEventListeners(new ReactionListener(this));
        if (memberJoin != null) {

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            jdaBuilder.addEventListeners(new MemberJoin(this, memberJoin));
        }
        if (memberLeave != null) {

            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            jdaBuilder.addEventListeners(new MemberLeave(this, memberLeave));
        }

        jdaBuilder.addEventListeners(this);

        if (activity != null) jdaBuilder.setActivity(activity);

        try {

            this.jda = jdaBuilder.build().awaitReady();
            this.debug("Bot created:");
            this.debug("name -> " + this.name);
            this.debug("prefix -> " + this.prefix);
            this.debug("commands -> " + this.commands.size());
            this.debug("reaction triggers -> " + this.reactionTriggers.size());
            this.debug("activity -> " + (activity != null ? activity.getName() : "false"));
        } catch (LoginException e) {

            logger.error("Bot's token is invalid.");
            System.exit(1);
        } catch (InterruptedException e) {

            System.exit(-1);
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

    public void debug(String s) {

        if (this.debug) this.logger.debug(s);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {

        logger.info(this.name + " online and ready !");
    }
}
