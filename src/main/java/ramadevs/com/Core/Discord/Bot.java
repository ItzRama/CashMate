package ramadevs.com.Core.Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ramadevs.com.Core.Discord.Embed.EmbedRegister;
import ramadevs.com.Core.Discord.Embed.TransactionEmbed;
import ramadevs.com.Core.Discord.Listener.DiscordListener;
import ramadevs.com.Core.Discord.Runtime.AutoDepo;
import ramadevs.com.Core.Init;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {
    public EmbedRegister embedRegister = new EmbedRegister(this);
    public TransactionEmbed transactionalEmbed = new TransactionEmbed((this));
    public JDA jda;
    public Init init;
    public ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
    public ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
    public AutoDepo autoDepo = new AutoDepo(this);

    public Bot(Init init) {
        this.init = init;
    }

    public void StartBot(String token) throws InterruptedException {
        jda = JDABuilder.createLight(token,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new DiscordListener(this))
                .setStatus(OnlineStatus.ONLINE)
                //Status Related
                .setActivity(Activity.competing("CashMate"))
                .build().awaitReady();
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                // Perintah untuk melihat profil dan statistik pengguna.
                Commands.slash("register", "👤 Register for the bot."),
                Commands.slash("balance", "💰 Check your balance."),
                Commands.slash("deposit", "📥 Deposit your world locks."),
                Commands.slash("setworld", "🔧 Set deposit world")
                );
        commands.queue();

        System.out.println("Growtopia.Support: " + init.config.getBoolean("Growtopia.Support"));

        if (init.config.getBoolean("Growtopia.Support")) {
            System.out.println("Enabling Donation Thread...");
            scheduler1.scheduleAtFixedRate(autoDepo.autoBal(), 1, 5, TimeUnit.SECONDS);
        }
        System.out.println("Enabling Money Thread...");
        scheduler2.scheduleAtFixedRate(autoDepo.autoMoney(), 1, 5, TimeUnit.SECONDS);
    }
}
