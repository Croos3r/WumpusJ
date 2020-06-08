package fr.crooser.wumpusj.listeners.guild;

import fr.crooser.wumpusj.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class MemberLeave extends ListenerAdapter {

    private final Consumer<Guild> memberLeave;
    private final Bot bot;

    public MemberLeave(Bot bot, @Nullable Consumer<Guild> memberLeave) {

        this.memberLeave = memberLeave;
        this.bot = bot;
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {

        if (this.memberLeave != null) {

            this.bot.debug("Member left, executing consumer");
            this.memberLeave.accept(event.getGuild());
        }
    }
}
