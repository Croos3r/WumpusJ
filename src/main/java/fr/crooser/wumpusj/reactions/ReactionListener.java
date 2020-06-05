package fr.crooser.wumpusj.reactions;

import fr.crooser.wumpusj.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactionListener extends ListenerAdapter {

    private final List<ReactionTrigger> triggers;

    public ReactionListener(Bot bot) {

        this.triggers = bot.getReactionTriggers();
    }

    private Message message;

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {

        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Emote emote = event.getReactionEmote().getEmote();

        if (channel.hasLatestMessage()) {

            channel.retrieveMessageById(channel.getLatestMessageId()).queue(message -> this.message = message);

            if (triggers != null) {

                triggers.forEach(trigger -> {

                    if (channel == trigger.getChannel() && trigger.getEmotes().contains(emote)) trigger.trigger(member, message, emote);
                });

                super.onGuildMessageReactionAdd(event);
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {

        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Emote emote = event.getReactionEmote().getEmote();

        if (channel.hasLatestMessage()) {

            channel.retrieveMessageById(channel.getLatestMessageId()).queue(message -> this.message = message);

            if (triggers != null) {

                triggers.forEach(trigger -> {

                    if (channel == trigger.getChannel() && trigger.getEmotes().contains(emote)) trigger.untrigger(member, message, emote);
                });

                super.onGuildMessageReactionRemove(event);
            }
        }
    }
}
