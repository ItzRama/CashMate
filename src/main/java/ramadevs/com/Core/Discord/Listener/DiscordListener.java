package ramadevs.com.Core.Discord.Listener;

import com.mongodb.MongoException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Database.Schematic.StatsSchematic;
import ramadevs.com.Core.Discord.Bot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ramadevs.com.Core.Discord.Embed.EmbedRegister.*;
import static ramadevs.com.Core.Discord.Embed.TransactionEmbed.*;
import static ramadevs.com.Core.Discord.Modal.CreateModalItem.createItem;
import static ramadevs.com.Core.Discord.Modal.CreateModalRegister.createRegister;

public class DiscordListener extends ListenerAdapter {
    Bot bot;
    public DiscordListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!bot.init.db.isExist(event.getUser()) && !event.getName().toLowerCase().equals("register")) {
            event.replyEmbeds(notRegistered(event.getUser())).setEphemeral(true).queue();
            return;
        }
        switch(event.getName().toLowerCase()) {
            case "register" -> {
                if (bot.init.db.isExist(event.getUser())) {
                    event.replyEmbeds(accountExistEmbed(event.getUser())).setEphemeral(true).queue();
                } else {
                    event.replyModal(createRegister()).queue();
                }
            }
            case "balance" -> {
                DataSchematic dataSchematic = bot.init.db.getUserByUser(event.getUser());
                event.replyEmbeds(checkBalance(dataSchematic)).setEphemeral(true).queue();
            }
            case "deposit" -> {
                StatsSchematic stats = bot.init.db.getStats();
                event.replyEmbeds(depositBalance(stats)).setEphemeral(true).queue();
            }
            case "additem" -> {
                event.replyModal(createItem()).queue();
            }
            case "addstock" -> {
                String id = event.getOption("id").getAsString();

                if (!bot.init.db.ItemExist(id)) {
                    event.reply("❌ Item dengan ID `" + id + "` tidak ditemukan.").setEphemeral(true).queue();
                    return;
                }

                OptionMapping file = event.getOption("file");
                OptionMapping stock = event.getOption("stock");

                if (file != null) {
                    Message.Attachment attachment = file.getAsAttachment();

                    if (!attachment.getFileName().endsWith(".txt")) {
                        event.reply("❌ File harus berupa `.txt`.").setEphemeral(true).queue();
                        return;
                    }

                    event.deferReply().setEphemeral(true).queue(); // optional, if processing takes time

                    attachment.getProxy().download().thenAccept(inputStream -> {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                            List<String> stockList = new ArrayList<>();
                            String line;

                            while ((line = reader.readLine()) != null) {
                                if (!line.trim().isEmpty()) {
                                    stockList.add(line.trim());
                                }
                            }

                            // Simpan ke database atau lakukan sesuatu dengan stockList
                            bot.init.db.addStockFromFile(id, (ArrayList<String>) stockList);


                            event.getHook().sendMessage("✅ Stock berhasil ditambahkan ke item `" + id + "`.\nJumlah: **" + stockList.size() + "**").queue();

                        } catch (Exception e) {
                            e.printStackTrace();
                            event.getHook().sendMessage("❌ Gagal membaca file.").queue();
                        }
                    });
                    return;
                }

                if (stock != null) {
                    try {
                        String stocks = stock.getAsString();
                        event.deferReply().setEphemeral(true).queue();
                        if (bot.init.db.addStockOne(id, stocks)) {
                            event.getHook().sendMessage("✅ Stock berhasil ditambahkan ke item `" + id + "`.\nJumlah: **1**").queue();
                        } else {
                            event.getHook().sendMessage("❌ Gagal menambahkan stock ke item `" + id + "`.").queue();
                        }
                    } catch (MongoException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        switch(event.getModalId().toLowerCase()) {
            case "register_menu" -> {
                String UID = event.getValue("uid").getAsString();
                String GrowID = event.getValue("growid").getAsString();

                if (Arrays.stream(UID.split(" ")).toArray().length > 1) {
                    event.replyEmbeds(WhitespaceDetect(event.getUser())).setEphemeral(true).queue();
                    return;
                }

                if (bot.init.db.isUIDExist(UID)) {
                    event.replyEmbeds(UIDExistEmbed(event.getUser())).setEphemeral(true).queue();
                    return;
                }

                if (bot.init.db.isGrowIDExist(GrowID)) {
                    event.replyEmbeds(GrowIDExistEmbed(event.getUser())).setEphemeral(true).queue();
                    return;
                }

                boolean state = bot.init.db.createUser(event.getUser(),UID ,GrowID);
                if (state) {
                    event.replyEmbeds(accountCreatedEmbed(event.getUser())).setEphemeral(true).queue();
                } else {
                    event.replyEmbeds(accountExistEmbed(event.getUser())).setEphemeral(true).queue();
                }
            }
            case "item_create_menu" -> {
                String id = event.getValue("id").getAsString(),
                        displayname = event.getValue("display_name").getAsString(),
                        type = event.getValue("type").getAsString(),
                        image = event.getValue("image").getAsString();
                int price = Integer.parseInt(event.getValue("price").getAsString());
                if (this.bot.init.db.addItem(id, displayname,type, price, image)) {
                    event.replyEmbeds(itemCreated(bot.init.db.getItem(id))).setEphemeral(true).queue();
                } else {
                    event.replyEmbeds(itemCreateFailed()).setEphemeral(true).queue();
                }
            }
        }
    }
}
