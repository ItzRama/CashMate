package ramadevs.com.Core.Database.Schematic;

import java.util.ArrayList;

public class ItemSchematic {
    public String ID;
    public String Display_Name;
    public String Type;
    public int Price;
    public int Locks;
    public String Image;
    public ArrayList<String> Stock;;

    public ItemSchematic(String id, String Display_Name,String Type ,int prices, int locks, String image, ArrayList<String> stock) {
        this.ID = id;
        this.Display_Name = Display_Name;
        this.Type = Type;
        this.Price = prices;
        this.Locks = locks;
        this.Image = image;
        this.Stock = stock;
    }
}
