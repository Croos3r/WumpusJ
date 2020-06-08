package fr.crooser.wumpusj.listeners.guild;

import fr.crooser.wumpusj.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class MemberJoin extends ListenerAdapter {

    private final Bot bot;

    private final Consumer<Member> memberJoin;

    public MemberJoin(Bot bot, @Nullable Consumer<Member> memberJoin) {

        this.memberJoin = memberJoin;
        this.bot = bot;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {

        if (this.memberJoin != null) {

            this.bot.debug("Member joined, executing consumer");
            this.memberJoin.accept(event.getMember());
        }
    }
}
