package fr.crooser.wumpusj;

import fr.crooser.wumpusj.command.Command;
import fr.crooser.wumpusj.command.CommandListener;
import fr.crooser.wumpusj.command.handler.CommandHandler;
import fr.crooser.wumpusj.command.handler.CommandHandlerCommons;
import fr.crooser.wumpusj.listeners.guild.MemberJoin;
import fr.crooser.wumpusj.listeners.guild.MemberLeave;
import fr.crooser.wumpusj.listeners.reaction.ReactionListener;
import fr.crooser.wumpusj.reaction.ReactionTrigger;
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
    private String prefix = "!";
    private Boolean debug = true;
    private Boolean jdaLog = false;
    private List<Command> commands;
    private List<ReactionTrigger> reactionTriggers;
    private JDA jda;
    private final Logger logger;
    private Activity activity;
    private Consumer<Member> memberJoinAction;
    private Consumer<Guild> memberLeaveAction;
    private CommandHandlerCommons commandHandlerCommons;

    public Bot(
            @NotNull JDABuilder builder,
            @NotNull String name,
            @Nullable String prefix,
            @NotNull Boolean debug,
            @NotNull Boolean jdaLog,
            @Nullable Activity activity,
            @Nullable List<Command> commands,
            @Nullable List<ReactionTrigger> reactionTriggers,
            @Nullable CommandHandlerCommons commandHandlerCommons,
            @Nullable Consumer<Member> memberJoin,
            @Nullable Consumer<Guild> memberLeave,
            @Nullable List<GatewayIntent> intents
    ) {

        Bot.instance = this;
        this.name = name;

        Thread.currentThread().setName("WumpusJ BotThread");
        this.logger = LoggerFactory.getLogger(this.name);

        this.setDebug(debug);
        this.setPrefix(prefix);
        this.setJdaLog(jdaLog);
        this.setActivity(activity);
        this.setCommands(new LinkedList<>(commands != null ? commands : Collections.emptyList()));
        this.setReactionTriggers(new LinkedList<>(reactionTriggers != null ? reactionTriggers : Collections.emptyList()));
        this.setCommandHandlerCommons(commandHandlerCommons);
        this.initCommandHandler(builder);

        builder.addEventListeners(new ReactionListener(this));

        builder.enableIntents(intents != null ? intents : Collections.emptyList());

        try {

            this.jda = builder.build().awaitReady();
        }
        catch (LoginException e) {

            this.logger.error("Bot's token is invalid.");
            System.exit(1);
            return;
        }
        catch (InterruptedException e) {

            System.exit(-1);
            return;
        }

        this.setMemberJoinAction(memberJoin);
        this.setMemberLeaveAction(memberLeave);

        this.sendResumeLog();
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

    public List<ReactionTrigger> getReactionTriggers() {

        return this.reactionTriggers;
    }

    public String getPrefix() {

        return prefix;
    }

    public Boolean getJdaLog() {
        return jdaLog;
    }

    private void setCommands(List<Command> commands) {

        this.commands = commands != null ? commands : Collections.emptyList();
    }

    public void addCommands(@NotNull Command... commands) {

        for (Command command : commands) if (!this.commands.contains(command))
            this.commands.add(command);
    }

    public void removeCommands(@NotNull String... labels) {

        List<String> commandLabels = new LinkedList<>(Arrays.asList(labels));
        this.commands.removeIf(command -> commandLabels.contains(command.getLabel()));
    }

    private void setReactionTriggers(List<ReactionTrigger> reactionTriggers) {

        this.reactionTriggers = reactionTriggers != null ? reactionTriggers : Collections.emptyList();
    }

    public void addReactionTriggers(@NotNull ReactionTrigger... reactionTriggers) {

        for (ReactionTrigger trigger : reactionTriggers) if (!this.reactionTriggers.contains(trigger))
            this.reactionTriggers.add(trigger);
    }

    public void removeReactionTriggers(@NotNull String... ids) {

        List<String> triggersIDs = new LinkedList<>(Arrays.asList(ids));
        this.reactionTriggers.removeIf(trigger -> triggersIDs.contains(trigger.getMessage().getId()));
    }

    public void setDebug(@NotNull Boolean debug) {

        if (this.debug != debug) this.debug = debug;
    }

    public void setPrefix(@Nullable String prefix) {

        if (!this.prefix.equals(prefix)) this.prefix = prefix;
    }

    public void setJdaLog(@NotNull Boolean jdaLog) {

        if (this.jdaLog != jdaLog) this.jdaLog = jdaLog;
    }

    public void setActivity(@Nullable Activity activity) {

        if (activity != this.activity && activity != null) {

            this.activity = activity;
            this.jda.getPresence().setActivity(this.activity);
        }
    }

    public void setMemberJoinAction(@Nullable Consumer<Member> memberJoinAction) {

        if (this.memberJoinAction != null) this.jda.removeEventListener(new MemberJoin(this, this.memberJoinAction));


        if (this.memberJoinAction != memberJoinAction) {

            this.memberJoinAction = memberJoinAction;
            if (this.memberJoinAction != null)
                this.jda.addEventListener(new MemberJoin(this, this.memberJoinAction));
        }
    }

    public void setMemberLeaveAction(@Nullable Consumer<Guild> memberLeaveAction) {

        if (this.memberLeaveAction != null) this.jda.removeEventListener(new MemberLeave(this, this.memberLeaveAction));

        if (this.memberLeaveAction != memberLeaveAction) {

            this.memberLeaveAction = memberLeaveAction;
            if (this.memberLeaveAction != null)
                this.jda.addEventListener(new MemberLeave(this, this.memberLeaveAction));
        }
    }

    public void setCommandHandlerCommons(@Nullable CommandHandlerCommons commandHandlerCommons) {

        if (commandHandlerCommons == null) this.commandHandlerCommons = new CommandHandlerCommons()
                .setUnknownCommand(null)
                .setSyntaxError(null)
                .setOnYourselfError(null)
                .setOnAdminError(null)
                .setInsufficientPermissions(null);
        else this.commandHandlerCommons = commandHandlerCommons;
    }

    private void initCommandHandler(JDABuilder builder) {

        builder.addEventListeners(new CommandListener(new CommandHandler(this, this.commandHandlerCommons)));
    }

    public void debug(String s) {

        if (this.debug) this.logger.debug(s);
    }

    public static Bot get() {

        return instance;
    }

    private void sendResumeLog() {

        this.logger.debug("Invite the bot to your server: " + "https://discordapp.com/oauth2/authorize?client_id=" + this.jda.getSelfUser().getId() + "&scope=bot&permissions=2146958839");
        this.logger.debug("Bot created:");
        this.logger.debug("  name -> " + this.name);
        this.logger.debug("  log(s) -> " + (this.debug ? "debug" : "") + (this.debug && this.jdaLog ? " | " : "") + (this.jdaLog ? "JDA" : "") + (!(this.debug || this.jdaLog) ? "false" : ""));
        this.logger.debug("  prefix -> " + this.prefix);
        this.logger.debug("  command(s) -> " + this.commands.size());
        this.logger.debug("  reactions trigger(s) -> " + this.reactionTriggers.size());
        this.logger.debug("  activity -> " + (this.activity != null ? this.activity.getName() : "false"));
        this.logger.debug("  join action -> " + (this.memberJoinAction != null));
        this.logger.debug("  leave action -> " + (this.memberLeaveAction != null));
    }
}
