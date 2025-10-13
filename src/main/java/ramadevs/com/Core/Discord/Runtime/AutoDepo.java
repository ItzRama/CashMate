package ramadevs.com.Core.Discord.Runtime;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Discord.Bot;

import java.util.List;

import static ramadevs.com.Core.Discord.Embed.TransactionEmbed.addBalanceEmbed;
import static ramadevs.com.Core.Discord.Embed.TransactionEmbed.addMoneyEmbed;

public class AutoDepo {
    private final Bot bot;

    public AutoDepo(Bot bot) {
        this.bot = bot;
    }

    // Runnable task for scheduler
    public Runnable autoBal() {
        return () -> {
            try {

                TextChannel channel = bot.jda.getTextChannelById("1427107500200689708");
                if (channel == null) {
                    log("Channel not found!");
                    return;
                }

                List<Message> retrieved = channel.getHistory().retrievePast(100).complete();

                for (Message message : retrieved) {
                    processMessage1(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public Runnable autoMoney() {
        return () -> {
            try {
                TextChannel channel = bot.jda.getTextChannelById("1427123645716631622");
                if (channel == null) {
                    log("Channel not found!");
                    return;
                }

                List<Message> retrieved = channel.getHistory().retrievePast(100).complete();

                for (Message message : retrieved) {
                    try {
                        List<MessageEmbed> embeds = message.getEmbeds();
                        for (MessageEmbed fMsg : embeds) {
                            String jumlahDonasi = fMsg.getTitle();
                            String from = fMsg.getDescription();

                            if (jumlahDonasi == null || from == null) {
                                log("Embed missing title/description, skipping");
                                continue;
                            }

                            jumlahDonasi = jumlahDonasi.replace("IDR", "").replace(",", "").trim();
                            int amount = Integer.parseInt(jumlahDonasi);

                            if (bot.init.db.addMoney(from, amount)) {
                                DataSchematic schem;
                                if (bot.init.db.isUIDExist(from)) {
                                    schem = bot.init.db.getUserByUID(from);
                                } else if (bot.init.db.isGrowIDExist(from)) {
                                    schem = bot.init.db.getUserByGrowID(from);
                                } else {
                                    schem = null;
                                }

                                if (schem != null) {
                                    bot.jda.openPrivateChannelById(schem.id)
                                            .queue(ch -> ch.sendMessageEmbeds(addMoneyEmbed(schem, amount)).queue());
                                    deleteMessage(message);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log("Error processing message: " + e.getMessage());
                        // continue with next message
                    }
                }
            } catch (Exception e) {
                log("Fatal error in autoMoney: " + e.getMessage());
                // swallow exception so scheduler keeps running
            }
        };
    }

    private void processMessage1(Message message) {
        System.out.println(message.getContentRaw());
        String[] args = message.getContentRaw().split("\\|");
        if (args.length < 2) {
            if (message.getContentRaw().equals("Offline")) {

            }
        }

        String growID = args[0].trim();
        int amount;
        try {
            amount = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            log("Invalid amount: " + args[1]);
            deleteMessage(message);
            return;
        }

        if (bot.init.db.addBalance(growID, amount)) {
            DataSchematic schem = bot.init.db.getUserByGrowID(growID);
            if (schem != null) {
                bot.jda.openPrivateChannelById(schem.id)
                        .queue(ch -> ch.sendMessageEmbeds(addBalanceEmbed(schem, amount)).queue());
            }
            deleteMessage(message);
        }
    }

    private void deleteMessage(Message message) {
        message.delete().queue(
                success -> log("Deleted processed message."),
                error -> log("Failed to delete message: " + error.getMessage())
        );
    }

    private void log(String msg) {
        System.out.println("[AutoDepo] " + msg);
        // Replace with proper logger in production
    }
}