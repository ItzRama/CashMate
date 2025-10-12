package ramadevs.com.Core.Discord.Runtime;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Discord.Bot;

import java.util.List;

import static ramadevs.com.Core.Discord.Embed.TransactionEmbed.addBalanceEmbed;

public class AutoDepo {
    private final Bot bot;

    public AutoDepo(Bot bot) {
        this.bot = bot;
    }

    // Runnable task for scheduler
    public Runnable autoBal() {
        return () -> {
            TextChannel channel = bot.jda.getTextChannelById("1421704453433594059");
            if (channel == null) {
                log("Channel not found!");
                return;
            }

            List<Message> retrieved = channel.getHistory().retrievePast(100).complete();

            for (Message message : retrieved) {
                processMessage(message);
            }
        };
    }

    private void processMessage(Message message) {
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