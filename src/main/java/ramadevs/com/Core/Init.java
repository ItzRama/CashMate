package ramadevs.com.Core;

import org.simpleyaml.configuration.file.YamlFile;
import ramadevs.com.Core.Database.MongoDB;
import ramadevs.com.Core.Discord.Bot;

public class Init {

    public boolean DEBUG = true;
    public MongoDB db = new MongoDB(this);
    public Bot bot = new Bot(this);

    public final YamlFile config = new YamlFile("config.yml");

    public void Initialize() throws InterruptedException {
        db.initializeDatabase();
        bot.StartBot("MTQyMTcxODQ0OTMzOTYyOTY2MA.GMgVnm.aS7bMh-tDUR-rwzeQFJdtWQST0RTDJgNUTyy_k");

        initConfig();
    }

    public void initConfig() {
        try {
            if (!config.exists()) {
                config.createNewFile();
                System.out.println("New file has been created: " + config.getFilePath() + "\n");
            } else {
                System.out.println(config.getFilePath() + " already exists, loading configurations...\n");
            }

            // Load file
            config.load();

            // Load Discord Bot Token
            config.addDefault("Discord.Token", "");
            config.addDefault("Discord.Donate-Log", "");

            // Growtopia Support Related Files.
            config.addDefault("Growtopia.Suppport", true);
            config.addDefault("Growtopia.Currency.Rate", 1700);
            config.addDefault("Growtopia.Currency.Convert", true);

            // Penting: simpan perubahan ke file
            config.save();

            System.out.println("Configuration saved to " + config.getFilePath());

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
