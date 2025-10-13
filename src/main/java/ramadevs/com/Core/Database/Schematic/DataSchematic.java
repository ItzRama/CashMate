package ramadevs.com.Core.Database.Schematic;

public class DataSchematic {
    public String id;
    public String UID, GrowID;
    public int balance, money;

    public DataSchematic(String id,String UID, String GrowID, int balance, int money) {
        this.id = id;
        this.UID = UID;
        this.GrowID = GrowID;
        this.balance = balance;
        this.money = money;
    }
}
