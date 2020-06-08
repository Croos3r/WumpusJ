package fr.crooser.wumpusj.command.handler;

import fr.crooser.wumpusj.Bot;
import fr.crooser.wumpusj.command.Command;
import fr.crooser.wumpusj.command.Command.Result;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class CommandHandler {

    private final Bot bot;

    private final String prefix;


    private final Consumer<Message> insufficientPermissions;
    private final Consumer<Message> syntaxError;
    private final Consumer<Message> onAdminError;
    private final Consumer<Message> onYourselfError;
    private final Consumer<Message> unknownCommand;

    public CommandHandler(@NotNull Bot bot, @Nullable CommandHandlerCommons handlerCommons) {

        this.prefix = bot.getPrefix();
        this.bot = bot;
        if (handlerCommons == null) {

            this.insufficientPermissions = this.syntaxError = this.onAdminError = this.onYourselfError = this.unknownCommand = null;
            this.bot.debug("Defaulted commons");
        }
        else {

            this.insufficientPermissions = handlerCommons.getInsufficientPermissions();
            this.syntaxError = handlerCommons.getSyntaxError();
            this.onAdminError = handlerCommons.getOnAdminError();
            this.onYourselfError = handlerCommons.getOnYourselfError();
            this.unknownCommand = handlerCommons.getUnknownCommand();
        }
    }

    public void handle(String message, GuildMessageReceivedEvent event) {


        Message eventMessage = event.getMessage();

        message = message.replace(this.prefix, "");
        String label = message.split(" ")[0];
        List<String> args = new LinkedList<>(Arrays.asList(message.split(" ")));
        args.remove(label + "");

        this.bot.debug("Processed message, returned label -> " + label + " and args -> " + args.toString());

        List<Command> commands = this.bot.getCommands();

        if (commands != null) {

            AtomicBoolean found = new AtomicBoolean(false);

            commands.forEach(command -> {

                List<String> aliases = command.getAliases();
                aliases.add(command.getLabel());

                if (aliases.contains(label)) {

                    this.bot.debug("Command found, waiting for result");

                    found.getAndSet(false);

                    Result result = command.execute(eventMessage, label, args, event.getJDA());

                    switch (result) {

                        case PASSING:
                            this.bot.debug("No commons error returned, passing");
                            break;
                        case INSUFFICIENT_PERMISSIONS:
                            this.bot.debug("Insufficient permission returned, sending common");
                            if (this.insufficientPermissions == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", vous n'avez pas les permissions requises ! ⚠️").queue();
                            else this.insufficientPermissions.accept(eventMessage);
                            break;
                        case SYNTAX_ERROR:
                            this.bot.debug("Syntax error returned, sending common");
                            if (this.syntaxError == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", " + command.getUsage() + " ⚠").queue();
                            else this.syntaxError.accept(eventMessage);
                            break;
                        case ON_ADMIN_ERROR:
                            this.bot.debug("On admin error returned, sending common");
                            if (this.onAdminError == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", vous ne pouvez pas effectuer cette commande sur un administrateur ! ⚠").queue();
                            else this.onAdminError.accept(eventMessage);
                            break;
                        case ON_YOURSELF_ERROR:
                            this.bot.debug("On yourself error returned, sending common");
                            if (this.onYourselfError == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", vous ne pouvez pas effectuer cette commande sur vous même ! ⚠").queue();
                            else this.onYourselfError.accept(eventMessage);
                            break;
                    }
                }
            });

            if (!found.get()) {

                this.bot.debug("Command not found, sending common");
                if (this.unknownCommand != null) this.unknownCommand.accept(eventMessage);
                else eventMessage.addReaction("❓").queue();
            }
        }
    }

    public Bot getBot() {
        return bot;
    }
}
