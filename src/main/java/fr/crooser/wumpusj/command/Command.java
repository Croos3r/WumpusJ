package fr.crooser.wumpusj.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Command {

    private final String label;
    private final String usage;
    private final List<String> aliases;

    public Command(String label, String usage, String... aliases) {

        this.label = label;
        this.usage = usage;
        this.aliases = new LinkedList<>(Arrays.asList(aliases));
    }

    public String getLabel() {
        return label;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public abstract Result execute(Member member, String label, TextChannel channel, List<String> args, JDA jda);

    public enum Result {

        INSUFFICIENT_PERMISSIONS,
        SYNTAX_ERROR,
        ON_ADMIN_ERROR,
        ON_YOURSELF_ERROR,
        PASSING
    }
}
