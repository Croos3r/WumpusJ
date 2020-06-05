package fr.crooser.wumpusj.reactions;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public abstract class ReactionTrigger {

    private final TextChannel channel;
    private final List<Emote> emotes;

    public ReactionTrigger(TextChannel channel, List<Emote> emotes) {

        this.channel = channel;
        this.emotes = emotes;
    }

    private void init() {

        if (this.channel.hasLatestMessage()) this.channel.retrieveMessageById(this.channel.getLatestMessageId()).queue( message -> emotes.forEach(emote -> message.addReaction(emote).queue()));
    }

    public TextChannel getChannel() {
        return channel;
    }

    public List<Emote> getEmotes() {
        return emotes;
    }

    public abstract void trigger(Member member, Message message, Emote emote);

    public abstract void untrigger(Member member, Message message, Emote emote);
}
