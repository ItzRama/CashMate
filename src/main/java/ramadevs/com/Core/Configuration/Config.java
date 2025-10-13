package ramadevs.com.Core.Configuration;

import org.simpleyaml.configuration.file.YamlFile;
import ramadevs.com.Core.Init;

public class Config {

    Init init;

    public Config(Init init) {
        this.init = init;
    }

    public final YamlFile config = new YamlFile("config.yml");
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

            defaultConfig();

            // Penting: simpan perubahan ke file
            config.save();

            System.out.println("Configuration saved to " + config.getFilePath());

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    void defaultConfig() {
        config.addDefault("Debug", true);
        config.addDefault("Prefix", "[CashMate]");

        // Load Discord Bot Token
        config.addDefault("Discord.Token", "");
        config.addDefault("Discord.Donate-Log", "");

        // Growtopia Support Related Files.
        config.addDefault("Growtopia.Support", true);
        config.addDefault("Growtopia.Currency.Rate", 1700);
        config.addDefault("Growtopia.Currency.Convert", true);
    }
}
