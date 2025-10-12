package ramadevs.com.Core.Database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Database.Schematic.StatsSchematic;
import ramadevs.com.Core.Init;

public class MongoDB {

    Init init;

    public MongoClient mongoClient;
    public MongoDatabase database;
    public MongoCollection<Document> Data;
    public MongoCollection<Document> Stock;
    public MongoCollection<Document> Stats;

    public MongoDB(Init init) {this.init = init;}

    public void initializeDatabase() {
        try {
            this.mongoClient = MongoClients.create("mongodb+srv://LuckyNetwork:armadiyanes196969@bolt.baf3wfn.mongodb.net/?retryWrites=true&w=majority"); // Need to be configurated free
            this.database = this.mongoClient.getDatabase("CashMate");
            this.Data = database.getCollection("User");
            this.Stats = database.getCollection("Stats");
            this.Stock = database.getCollection("Stock");

            boolean found = false;
            for (String data : database.listCollectionNames()) {
                if (data.equals("Stats")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Document docs = new Document("Online", false).append("World", "").append("Owner", "");
                Stats.insertOne(docs);
            }


        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public boolean isExist(User user) {
        return Data.find(new Document("ID", user.getId())).first() != null;
    }

    public boolean isGrowIDExist(String GrowID) {
        return Data.find(new Document("GrowID", GrowID)).first() != null;
    }

    public boolean createUser(User user, String GrowID) {
        try {
            if (!isExist(user)) {
                Document data = new Document("ID", user.getId()).append("GrowID", GrowID).append("Balance", 0).append("Money", 0);
                Data.insertOne(data);
                return true;
            } else {
                return false;
            }
        } catch (MongoException e) {
            e.printStackTrace();
            return false;
        }
    }

    public DataSchematic getUserByUser(User user) {
        if (isExist(user)) {
            Document data = Data.find(new Document("ID", user.getId())).first();
            return new DataSchematic(data.getString("ID"), data.getString("GrowID"), data.getInteger("Balance"), data.getInteger("Money"));
        }
        return null;
    }

    public StatsSchematic getStats() {
        Document data = Stats.find().first();
        return new StatsSchematic(data.getBoolean("Online"), data.getString("World"), data.getString("Owner"));
    }

    public void setOnline(String mode) {
        if (mode.equalsIgnoreCase("online")) {
            Stats.updateOne(new Document(), new Document("$set", new Document("Online", true)));
        } else {
            Stats.updateOne(new Document(), new Document("$set", new Document("Online", false)));
        }
    }

    public DataSchematic getUserByGrowID(String GrowID) {
        if (isGrowIDExist(GrowID)) {
            Document data = Data.find(new Document("GrowID", GrowID)).first();
            return new DataSchematic(data.getString("ID"), data.getString("GrowID"), data.getInteger("Balance"), data.getInteger("Money"));
        }
        return null;
    }

    public void setWorld(String world, String owner) {
        Stats.updateOne(new Document(), new Document("$set", new Document("World", world).append("Owner", owner)));
    }



    public boolean addBalance(String GrowID, int amount) {
        try {
            if (isGrowIDExist(GrowID)) {
                DataSchematic schem = getUserByGrowID(GrowID);
                if (init.config.getBoolean("Growtopia.Currency.Convert")) {
                    int money = schem.money;
                    this.init.db.Data.updateOne(new Document("ID", schem.id), new Document("$set", new Document("Money", money + (amount * init.config.getInt("Growtopia.Currency.Rate")))));
                } else {
                    int balance = schem.balance;
                    this.init.db.Data.updateOne(new Document("ID", schem.id), new Document("$set", new Document("Balance", balance + amount)));
                }
                return true;
            }
        } catch (MongoException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }



}
