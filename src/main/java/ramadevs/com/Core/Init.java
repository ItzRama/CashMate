package ramadevs.com.Core;

import org.simpleyaml.configuration.file.YamlFile;
import ramadevs.com.Core.Configuration.Config;
import ramadevs.com.Core.Database.MongoDB;
import ramadevs.com.Core.Discord.Bot;

public class Init {

    public boolean DEBUG = true;
    public MongoDB db = new MongoDB(this);
    public Bot bot = new Bot(this);

    public Config getConfig = new Config(this);

    public void Initialize() throws InterruptedException {
        getConfig.initConfig();
        System.out.println("Starting.");
        db.initializeDatabase();
        bot.StartBot("Bot Token");
        if (getConfig.config.getBoolean("Growtopia.Support")) System.out.println("Growtopia Support Enabled");
    }


}
