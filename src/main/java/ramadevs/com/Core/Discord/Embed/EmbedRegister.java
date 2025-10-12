package ramadevs.com.Core.Discord.Embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Discord.Bot;

import java.awt.*;
import java.time.Instant;

public class EmbedRegister {

    static Bot bot;
    public EmbedRegister(Bot bot) {
        this.bot = bot;
    }

    public static MessageEmbed accountCreatedEmbed(User user) {
        EmbedBuilder embed = new EmbedBuilder();
        DataSchematic schem = bot.init.db.getUserByUser(user);
        embed.setTitle("✅ Akun Berhasil Dibuat!");
        embed.setDescription("Welcome to CashMate, " + user.getAsMention() + "! Your profile is being created.");
        embed.setColor(new Color(0x57F287)); // Discord Green
        embed.setThumbnail(user.getEffectiveAvatarUrl());

        embed.addField("ID", schem.id, true);
        embed.addField("GrowID", schem.GrowID, true);
        embed.addField("Balance ", String.valueOf(schem.balance), true);

        embed.setFooter("Start your adventure with /help");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }
    public static MessageEmbed accountExistEmbed(User user) {
        EmbedBuilder embed = new EmbedBuilder();
        DataSchematic schem = bot.init.db.getUserByUser(user);

        embed.setTitle("❌ You already registered!");
        embed.setDescription("You already have an account with us. If you began to confuse, do /help.");
        embed.setColor(Color.RED); // Discord RED
        embed.setThumbnail(user.getEffectiveAvatarUrl());

        embed.addField("ID", schem.id, true);
        embed.addField("GrowID", schem.GrowID, true);
        embed.addField("Balance ", String.valueOf(schem.balance), true);

        embed.setFooter("Start your adventure with /help");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed notRegistered(User user) {
        EmbedBuilder embed = new EmbedBuilder();
        DataSchematic schem = bot.init.db.getUserByUser(user);

        embed.setTitle("❌ Please Register Before Using The Bot!");
        embed.setDescription("Start Register By Using The Command /register");
        embed.setColor(Color.RED); // Discord RED
        embed.setThumbnail(user.getEffectiveAvatarUrl());
        embed.setFooter("Start your adventure with /help");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }
}
