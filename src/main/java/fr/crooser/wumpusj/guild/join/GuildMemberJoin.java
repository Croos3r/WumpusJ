package fr.crooser.wumpusj.guild.join;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class GuildMemberJoin {

    private final Member member;
    private final Guild guild;
    private final JDA jda;

    public GuildMemberJoin(Member member, Guild guild, JDA jda) {


        this.member = member;
        this.guild = guild;
        this.jda = jda;
    }

    public Member getMember() {
        return member;
    }

    public Guild getGuild() {
        return guild;
    }

    public JDA getJda() {
        return jda;
    }
}
