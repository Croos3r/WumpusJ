package fr.crooser.wumpusj.command.handler;

import net.dv8tion.jda.api.entities.Message;

import java.util.function.Consumer;

public class CommandHandlerCommons {

    private Consumer<Message> insufficientPermissions;
    private Consumer<Message> syntaxError;
    private Consumer<Message> onAdminError;
    private Consumer<Message> onYourselfError;
    private Consumer<Message> unknownCommand;

    public Consumer<Message> getInsufficientPermissions() {
        return insufficientPermissions;
    }

    public Consumer<Message> getSyntaxError() {
        return syntaxError;
    }

    public Consumer<Message> getOnAdminError() {
        return onAdminError;
    }

    public Consumer<Message> getOnYourselfError() {
        return onYourselfError;
    }

    public Consumer<Message> getUnknownCommand() {
        return unknownCommand;
    }

    public CommandHandlerCommons setInsufficientPermissions(Consumer<Message> insufficientPermissions) {
        this.insufficientPermissions = insufficientPermissions;
        return this;
    }

    public CommandHandlerCommons setSyntaxError(Consumer<Message> syntaxError) {
        this.syntaxError = syntaxError;
        return this;
    }

    public CommandHandlerCommons setOnAdminError(Consumer<Message> onAdminError) {
        this.onAdminError = onAdminError;
        return this;
    }

    public CommandHandlerCommons setOnYourselfError(Consumer<Message> onYourselfError) {
        this.onYourselfError = onYourselfError;
        return this;
    }

    public CommandHandlerCommons setUnknownCommand(Consumer<Message> unknownCommand) {
        this.unknownCommand = unknownCommand;
        return this;
    }
}
