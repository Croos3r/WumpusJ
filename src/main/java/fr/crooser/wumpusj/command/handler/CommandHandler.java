package fr.crooser.wumpusj.command.handler;

import fr.crooser.wumpusj.Bot;
import fr.crooser.wumpusj.command.Command;
import fr.crooser.wumpusj.command.Command.Result;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class CommandHandler {

    private final Bot bot;

    private final String prefix;
    private final List<Command> commands;


    private final Consumer<Message> insufficientPermissions;
    private final Consumer<Message> syntaxError;
    private final Consumer<Message> onAdminError;
    private final Consumer<Message> onYourselfError;
    private final Consumer<Message> unknownCommand;

    public CommandHandler(@NotNull Bot bot, @Nullable CommandHandlerCommons handlerCommons) {

        this.prefix = bot.getPrefix();
        this.commands = bot.getCommands();
        this.bot = bot;
        if (handlerCommons == null) {

            this.insufficientPermissions = this.syntaxError = this.onAdminError = this.onYourselfError = this.unknownCommand = null;
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

        if (commands != null) {

            commands.forEach(command -> {

                List<String> aliases = command.getAliases();
                aliases.add(command.getLabel());

                if (aliases.contains(label)) {

                    Result result = command.execute(event.getMember(), label, event.getChannel(), args, event.getJDA());

                    switch (result) {

                        case PASSING:
                            break;
                        case INSUFFICIENT_PERMISSIONS:
                            if (insufficientPermissions == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", vous n'avez pas les permissions requises ! ⚠️").queue();
                            else insufficientPermissions.accept(eventMessage);
                            break;
                        case SYNTAX_ERROR:
                            if (syntaxError == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", " + command.getUsage() + " ⚠").queue();
                            else syntaxError.accept(eventMessage);
                            break;
                        case ON_ADMIN_ERROR:
                            if (onAdminError == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", vous ne pouvez pas effectuer cette commande sur un administrateur ! ⚠").queue();
                            else onAdminError.accept(eventMessage);
                            break;
                        case ON_YOURSELF_ERROR:
                            if (onYourselfError == null)
                                eventMessage.getChannel().sendMessage(eventMessage.getAuthor().getAsMention() + ", vous ne pouvez pas effectuer cette commande sur vous même ! ⚠").queue();
                            else onYourselfError.accept(eventMessage);
                            break;
                    }
                }
                else {

                    if (unknownCommand == null) eventMessage.addReaction("❓").queue();
                    else unknownCommand.accept(eventMessage);
                }
            });
        }
    }

    public Bot getBot() {
        return bot;
    }
}
