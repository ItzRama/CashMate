package ramadevs.com.Core.Discord.Embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Database.Schematic.StatsSchematic;
import ramadevs.com.Core.Discord.Bot;

import java.awt.*;
import java.time.Instant;

public class TransactionEmbed {

    static Bot bot;

    public TransactionEmbed(Bot bot) {
        this.bot = bot;
    }

    public static MessageEmbed addBalanceEmbed(DataSchematic schem, int amount) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0x57F287)) // Discord green
                .setAuthor("🎉 Transaction Successful", null, null)
                .setTitle("✅ Balance Updated")
                .setDescription("An amount of **" + amount + "** has been added to your account.")
                .setTimestamp(Instant.now())
                .setFooter("💡 Use /help to explore more commands");

        StringBuilder text = new StringBuilder();

        if (!bot.init.getConfig.config.getBoolean("Growtopia.Currency.Convert")) {
            int balance = schem.balance;

            int bgl = balance / 10000;
            if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

            int dl = (balance % 10000) / 100;
            if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

            int wl = (balance % 100);
            if (wl >= 0) text.append("🔒 **").append(wl).append("** World Lock");

            embed.addField("👤 GrowID", "`" + schem.GrowID + "`", true);
            embed.addField("🆔 User ID", "`" + schem.id + "`", true);
            embed.addField("💰 You Donated", text.toString(), false);

        } else {
            int bgl = amount / 10000;
            if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

            int dl = (amount % 10000) / 100;
            if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

            int wl = (amount % 100);
            if (wl > 0) text.append("🔒 **").append(wl).append("** World Lock");

            int rate = bot.init.getConfig.config.getInt("Growtopia.Currency.Rate");
            float finalMoney = (amount / 100f) * rate;

            embed.addField("👤 GrowID", "`" + schem.GrowID + "`", true);
            embed.addField("🆔 User ID", "`" + schem.id + "`", true);
            embed.addField("💰 You Donated", text.toString(), false);
            embed.addField("⚙️ Auto Convert", "Enabled ✅", true);
            embed.addField("💱 Rate", "`" + rate + "` per Diamond Lock", true);
            embed.addField("💵 Final Convert", "Rp. **" + finalMoney + "**", false);
            embed.addField("💵 Final Money", "Rp. **" + schem.money + "**", true);
        }

        return embed.build();
    }

    public static MessageEmbed addMoneyEmbed(DataSchematic schem, int amount) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0x57F287)) // Discord green
                .setAuthor("🎉 Transaction Successful", null, null)
                .setTitle("✅ Balance Updated")
                .setDescription("An amount of Rp. **" + amount + "** has been added to your account.")
                .setTimestamp(Instant.now())
                .setFooter("💡 Use /help to explore more commands");


        embed.addField("👤 GrowID", "`" + schem.GrowID + "`", true);
        embed.addField("🆔 User ID", "`" + schem.id + "`", true);
        embed.addField("💰 You Donated", "Rp. **"+ amount + "**", false);
        embed.addField("💵 Final Money", "Rp. **" + schem.money + "**", true);

        return embed.build();
    }


    public static MessageEmbed checkBalance(DataSchematic schem) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0x3498DB)) // Blue for info
                .setAuthor("📊 Balance Information", null, null)
                .setTitle("💎 Wallet Overview")
                .setDescription("Here’s what you currently own:")
                .setTimestamp(Instant.now())
                .setFooter("💡 Use /help to explore more commands");

        int balance = schem.balance;
        StringBuilder text = new StringBuilder();

        int bgl = balance / 10000;
        if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

        int dl = (balance % 10000) / 100;
        if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

        int wl = (balance % 100);
        if (wl >= 0) text.append("🔒 **").append(wl).append("** World Lock");

        embed.addField("👤 GrowID", "`" + schem.GrowID + "`", true);
        embed.addField("🆔 User ID", "`" + schem.id + "`", true);
        embed.addField("💰 Balance", text.toString(), false);
        embed.addField("💵 Money", "Rp. **" + schem.money + "**", false);

        return embed.build();
    }

    public static MessageEmbed depositBalance(StatsSchematic schem) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(schem.online ? new Color(0x57F287) : new Color(0xED4245)) // Green if online, red if offline
                .setTitle("💸 Deposit Information")
                .setTimestamp(Instant.now());

        if (schem.online && (!schem.world.isEmpty() && !schem.owner.isEmpty())) {
            embed.setDescription("✅ The bot is currently **online** and ready to accept your deposits!\n\n" +
                            "You can safely deposit your **Blue Gem Locks**, **Diamond Locks**, and **World Locks** into your wallet.")
                    .addField("🌍 Deposit World", "||" + schem.world + "||", true)
                    .addField("👑 World Owner", "||" + schem.owner + "||", true)
                    .setFooter("⚠️ Make sure you're in the correct world before depositing.");
        } else {
            embed.setDescription("⚠️ The bot is currently **offline**, so deposits are temporarily unavailable.\n\n" +
                            "Please wait until it's back online to proceed.")
                    .addField("🌍 Deposit World", "Unavailable", true)
                    .addField("👑 World Owner", "Unavailable", true)
                    .setFooter("💡 Try again later when the bot is online.");
        }

        return embed.build();
    }

    public static MessageEmbed donateLog(DataSchematic schem, int amount, String mode) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0xF1C40F)) // Gold untuk log
                .setTitle("📜 Donation Log")
                .setTimestamp(Instant.now());

        int rate = bot.init.getConfig.config.getInt("Growtopia.Currency.Rate");
        boolean gtEnabled = bot.init.getConfig.config.getBoolean("Growtopia.Support");
        boolean autoConv = bot.init.getConfig.config.getBoolean("Growtopia.Currency.Convert");

        User user = bot.jda.getUserById(schem.id);

        embed.setDescription("🎁 A donation has been received!");
        embed.addField("🙍 User", user != null ? user.getAsMention() : "`Unknown`", true);
        embed.addField("🆔 User ID", "`" + schem.id + "`", true);

        if (mode.equalsIgnoreCase("growtopia")) {
            // Format locks
            StringBuilder text = new StringBuilder();
            int bgl = amount / 10000;
            if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

            int dl = (amount % 10000) / 100;
            if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

            int wl = (amount % 100);
            if (wl > 0) text.append("🔒 **").append(wl).append("** World Lock");

            float finalMoney = (amount / 100f) * rate;

            embed.addField("💰 Donated (Locks)", text.toString(), false);

            if (autoConv) {
                embed.addField("⚙️ Auto Convert", "Enabled ✅", true);
                embed.addField("💱 Rate", "`" + rate + "` per Diamond Lock", true);
                embed.addField("💵 Final Convert", "Rp. **" + finalMoney + "**", false);
            }

        } else if (mode.equalsIgnoreCase("realmoney")) {
            embed.addField("💵 Money Donated", "Rp. **" + amount + "**", false);

            if (gtEnabled) {
                // Hitung konversi ke locks
                float bal = (float) amount / rate;   // force float division
                int locks = Math.round(bal * 100);   // convert to WLs

                StringBuilder text = new StringBuilder();

                int bgl = locks / 10000;
                if (bgl > 0) text.append("🔷 **").append(bgl).append("** Blue Gem Lock\n");

                int dl = (locks % 10000) / 100;
                if (dl > 0) text.append("💠 **").append(dl).append("** Diamond Lock\n");

                int wl = (locks % 100);
                if (wl > 0) text.append("🔒 **").append(wl).append("** World Lock");

                embed.addField("💎 Equals As", text.toString(), false);
            }
        }

        return embed.build();
    }
}