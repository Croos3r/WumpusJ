package fr.crooser.wumpusj;

import fr.crooser.wumpusj.command.Command;
import fr.crooser.wumpusj.command.CommandListener;
import fr.crooser.wumpusj.command.handler.CommandHandler;
import fr.crooser.wumpusj.command.handler.CommandHandlerCommons;
import fr.crooser.wumpusj.listeners.guild.MemberJoin;
import fr.crooser.wumpusj.listeners.guild.MemberLeave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Bot extends ListenerAdapter {

    private static Bot instance;

    private final String name;
    private Boolean jdaLog;
    private Boolean debug;
    private final List<Command> commands;
    private String prefix;
    private JDA jda;
    private final Logger logger;

    public Bot(
            @NotNull String name,
            @Nullable String prefix,
            @NotNull Boolean debug,
            @NotNull Boolean jdaLog,
            @Nullable Activity activity,
            @NotNull JDABuilder jdaBuilder,
            @Nullable List<Command> commands,
            @Nullable CommandHandlerCommons handlerCommons,
            @Nullable Consumer<Member> memberJoin,
            @Nullable Consumer<Guild> memberLeave
    ) {

        Bot.instance = this;
        this.name = name;

        Thread.currentThread().setName("WumpusJ - " + this.name);

        this.debug = debug;
        this.jdaLog = jdaLog;
        this.commands = commands == null ? Collections.emptyList() : commands;
        this.prefix = prefix == null ? "!" : prefix;
        this.logger = LoggerFactory.getLogger(this.name);

        if (handlerCommons == null) handlerCommons = new CommandHandlerCommons().setInsufficientPermissions(null).setOnAdminError(null).setOnYourselfError(null).setSyntaxError(null).setUnknownCommand(null);
        if (!this.commands.isEmpty()) jdaBuilder.addEventListeners(new CommandListener(new CommandHandler(this, handlerCommons)));
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
            sendResumeLog(this.name, this.prefix, this.debug, this.jdaLog, this.commands, activity, memberJoin, memberLeave);
        } catch (LoginException e) {

            this.logger.error("Bot's token is invalid.");
            System.exit(1);
        } catch (InterruptedException e) {

            System.exit(-1);
        }
    }

    public String getName() {

        return this.name;
    }

    public JDA getJda() {

        return jda;
    }

    public List<Command> getCommands() {

        return commands;
    }

    public String getPrefix() {

        return prefix;
    }

    public void addCommands(Command... commands) {

        for (Command command : commands)
            if (!this.commands.contains(command))
                this.commands.add(command);
    }

    public void removeCommands(String... labels) {

        List<String> commandLabels = new LinkedList<>(Arrays.asList(labels));
        this.commands.removeIf(command -> commandLabels.contains(command.getLabel()));
    }

    public void setDebug(Boolean debug) {

        this.debug = debug;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    public Boolean getJdaLog() {
        return jdaLog;
    }

    public void setJdaLog(Boolean jdaLog) {
        this.jdaLog = jdaLog;
    }

    public void debug(String s) {

        if (this.debug) this.logger.debug(s);
    }

    public static Bot get() {

        return instance;
    }

    private void sendResumeLog(@NotNull String name,
                               @Nullable String prefix,
                               @NotNull Boolean debug,
                               @NotNull Boolean jdaLog,
                               @NotNull List<Command> commands,
                               @Nullable Activity activity,
                               @Nullable Consumer<Member> joinAction,
                               @Nullable Consumer<Guild> leaveAction) {

        this.debug("Bot created:");
        this.debug("  name -> " + name);
        this.debug("  logs -> " + (debug ? "debug" : "") + (debug && jdaLog ? " | " : "") + (jdaLog ? "JDA" : ""));
        this.debug("  prefix -> " + prefix);
        this.debug("  commands -> " + commands.size());
        this.debug("  activity -> " + (activity != null ? activity.getName() : "false"));
        this.debug("  join action -> " + (joinAction != null));
        this.debug("  leave action -> " + (leaveAction != null));
    }
}
