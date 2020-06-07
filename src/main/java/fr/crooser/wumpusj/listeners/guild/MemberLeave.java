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

    private final Bot bot;
    private final Consumer<Guild> memberLeave;

    public MemberLeave(Bot bot, @Nullable Consumer<Guild> memberLeave) {

        this.bot = bot;
        this.memberLeave = memberLeave;
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {

        if (this.memberLeave != null) this.memberLeave.accept(event.getGuild());
    }
}
