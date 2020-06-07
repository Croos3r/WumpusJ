package fr.crooser.wumpusj.listeners.guild;

import fr.crooser.wumpusj.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class MemberJoin extends ListenerAdapter {

    private final Bot bot;
    private final Consumer<Member> memberJoin;

    public MemberJoin(Bot bot, Consumer<Member> memberJoin) {

        this.bot = bot;
        this.memberJoin = memberJoin;

        this.bot.getLogger().info("join listener");
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {

        if (this.memberJoin != null) this.memberJoin.accept(event.getMember());
    }
}
