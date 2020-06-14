package fr.crooser.wumpusj.listeners.reaction;

import fr.crooser.wumpusj.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter {

    private final Bot bot;

    public ReactionListener(@NotNull Bot bot) {

        this.bot = bot;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {

        final Member member = event.getMember();
        final JDA jda = event.getJDA();
        final MessageReaction reaction = event.getReaction();
        final String id = event.getMessageId();

        this.bot.getReactionTriggers().forEach(trigger -> {

            if (trigger.getMessage().getId().equals(id) && !member.getUser().equals(bot.getJda().getSelfUser())) trigger.trigger(reaction, member, jda);
        });
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {

        final Member member = event.getMember();
        final JDA jda = event.getJDA();
        final MessageReaction reaction = event.getReaction();
        final String id = event.getMessageId();


        this.bot.getReactionTriggers().forEach(trigger -> {

            if (trigger.getMessage().getId().equals(id) && !member.getUser().equals(bot.getJda().getSelfUser())) trigger.untrigger(reaction, member, jda);
        });
    }
}
