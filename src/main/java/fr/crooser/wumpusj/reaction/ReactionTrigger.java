package fr.crooser.wumpusj.reaction;

import fr.crooser.wumpusj.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ReactionTrigger {


    private final Message message;

    public ReactionTrigger(@NotNull Message message) {

        this.message = message;
    }

    public Message getMessage() {

        return message;
    }

    public abstract void trigger(@NotNull MessageReaction reaction, @NotNull Member member, @NotNull JDA jda);

    public abstract void untrigger(@NotNull MessageReaction reaction, @NotNull Member member, @NotNull JDA jda);

    public void init(@NotNull Emote... emotes) {

        try {

            for (Emote emote : emotes)
                this.message.addReaction(emote).queue();
        } catch (NullPointerException e) {

            Bot.get().debug("Can't init reaction trigger, message not found !");
            e.printStackTrace();
        }
    }

    public void init(@NotNull String... emotes) {

        try {

            for (String emote : emotes)
                this.message.addReaction(emote).queue();
        } catch (NullPointerException e) {

            Bot.get().debug("Can't init reaction trigger, message not found !");
            e.printStackTrace();
        }
    }
}
