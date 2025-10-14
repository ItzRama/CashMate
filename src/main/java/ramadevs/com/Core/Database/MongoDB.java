package ramadevs.com.Core.Database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import ramadevs.com.Core.Database.Schematic.DataSchematic;
import ramadevs.com.Core.Database.Schematic.ItemSchematic;
import ramadevs.com.Core.Database.Schematic.StatsSchematic;
import ramadevs.com.Core.Init;

import java.util.ArrayList;

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

    public boolean isUIDExist(String UID) {
        return Data.find(new Document("UID", UID)).first() != null;
    }

    public boolean createUser(User user, String UID ,String GrowID) {
        try {
            if (!isExist(user)) {
                Document data = new Document("ID", user.getId()).append("UID", UID).append("GrowID", GrowID).append("Balance", 0).append("Money", 0);
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
            return new DataSchematic(data.getString("ID"), data.getString("UID"),data.getString("GrowID"), data.getInteger("Balance"), data.getInteger("Money"));
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
            return new DataSchematic(data.getString("ID"),data.getString("UID") ,data.getString("GrowID"), data.getInteger("Balance"), data.getInteger("Money"));
        }
        return null;
    }
    public DataSchematic getUserByUID(String UID) {
        if (isUIDExist(UID)) {
            Document data = Data.find(new Document("UID", UID)).first();
            return new DataSchematic(data.getString("ID"),data.getString("UID") ,data.getString("GrowID"), data.getInteger("Balance"), data.getInteger("Money"));
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
                if (init.getConfig.config.getBoolean("Growtopia.Currency.Convert")) {
                    amount /= 100;
                    int money = schem.money;
                    this.init.db.Data.updateOne(new Document("ID", schem.id), new Document("$set", new Document("Money", money + (amount * init.getConfig.config.getInt("Growtopia.Currency.Rate")))));
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

    public boolean addMoney(String identity, int amount) {
        if (isGrowIDExist(identity) || isUIDExist(identity)) {
            DataSchematic schem = null;
            if (isGrowIDExist(identity)) schem = getUserByGrowID(identity);
            else if (isUIDExist(identity)) schem = getUserByUID(identity);

            int curMoney = schem.money;

            this.init.db.Data.updateOne(new Document("ID", schem.id), new Document("$set", new Document("Money", curMoney + amount)));
            return true;
        } else {
            return false;
        }
    }

    public boolean ItemExist(String id) {
        return Stock.find(new Document("ID", id)).first() != null;
    }

    public ItemSchematic getItem(String id) {
        if (ItemExist(id)) {
            Document data = Stock.find(new Document("ID", id)).first();
            return new ItemSchematic(data.getString("ID"), data.getString("Display_Name"), data.getString("Type"), data.getInteger("Price"), data.getInteger("Locks"), data.getString("Image"), (ArrayList<String>) data.get("Stock"));
        }
        return null;
    }


    public boolean addItem(String id, String display,String type, int price, String image) {
        try {
            if (ItemExist(id)) return false;

            ArrayList<String> stock = new ArrayList<>();
            this.init.db.Stock.insertOne(new Document("ID", id).append("Display_Name", display).append("Type", type).append("Price", price).append("Locks", 0).append("Image", image).append("Stock", stock));
            return true;
        } catch (MongoException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addStockFromFile(String id, ArrayList<String> stock) {
        if (ItemExist(id)) {
            try {
                ItemSchematic schem = getItem(id);
                stock.addAll(schem.Stock);

                this.init.db.Stock.updateOne(new Document("ID", id), new Document("$set", new Document("Stock", stock)));
                return true;
            } catch (MongoException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean addStockOne(String id, String stock) {
        if (ItemExist(id)) {
            try {
                ItemSchematic schem = getItem(id);
                ArrayList<String> stockList = schem.Stock;
                stockList.add(stock);

                this.init.db.Stock.updateOne(new Document("ID", id), new Document("$set", new Document("Stock", stockList)));
                return true;
            } catch (MongoException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
