package ramadevs.com.Core.Discord.Embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Database.Schematic.StatsSchematic;

import java.awt.*;
import java.time.Instant;

public class TransactionEmbed {

    public static MessageEmbed addBalanceEmbed(DataSchematic schem, int amount) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0x57F287)); // Discord Green

        embed.setAuthor("Transaction Successful 🎉", null, null);
        embed.setTitle("✅ Balance Updated");
        embed.setDescription("An amount of **" + amount + "** has been added to your account.");

        // Format balance into BGL / DL / WL
        int balance = schem.balance;
        StringBuilder text = new StringBuilder();

        int bgl = balance / 10000;
        if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

        int dl = (balance % 10000) / 100;
        if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

        int wl = (balance % 100);
        if (wl >= 0) text.append("🔒 **").append(wl).append("** World Lock");

        embed.addField("👤 GrowID", schem.GrowID, true);
        embed.addField("🆔 User ID", schem.id, true);
        embed.addField("💰 New Balance", text.toString(), false);

        embed.setFooter("Start your adventure with /help");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed checkBalance(DataSchematic schem) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0x57F287)); // Discord Green

        embed.setAuthor("Balance Information 📊", null, null);
        embed.setTitle("💎 Wallet Overview");

        int balance = schem.balance;
        StringBuilder text = new StringBuilder();

        int bgl = balance / 10000;
        if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

        int dl = (balance % 10000) / 100;
        if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

        int wl = (balance % 100);
        if (wl >= 0) text.append("🔒 **").append(wl).append("** World Lock");

        embed.setDescription("Here’s what you currently own:");
        embed.addField("👤 GrowID", schem.GrowID, true);
        embed.addField("🆔 User ID", schem.id, true);
        embed.addField("💰 Balance", text.toString(), false);

        embed.setFooter("Start your adventure with /help");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed depositBalance(StatsSchematic schem) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0x57F287)); // Discord green

        embed.setTitle("💸 Deposit Information");

        if (schem.online && (!schem.world.isEmpty() && !schem.owner.isEmpty())) {
            embed.setDescription("""
            The bot is currently **online** and ready to accept your deposits!
            You can safely deposit your **Blue Gem Locks**, **Diamond Locks**, and **World Locks** into your wallet.
            """);

            embed.addField("🌍 Deposit World", "||" + schem.world + "||", true);
            embed.addField("👑 World Owner", "||" + schem.owner + "||", true);
            embed.setFooter("Make sure you're in the correct world before depositing.", null);
        } else {
            embed.setDescription("""
            ⚠️ The bot is currently **offline**, so deposits are temporarily unavailable.
            Please wait until it's back online to proceed.
            """);

            embed.addField("🌍 Deposit World", "Unavailable", true);
            embed.addField("👑 World Owner", "Unavailable", true);
            embed.setFooter("Try again later when the bot is online, or use real cash instead!", null);
        }

        return embed.build();
    }
}