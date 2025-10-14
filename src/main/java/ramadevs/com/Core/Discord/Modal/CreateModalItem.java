package ramadevs.com.Core.Discord.Modal;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CreateModalItem {
    public static Modal createItem() {
        try {
            return Modal.create("item_create_menu", "🛠️ Create New Item")
                    .addActionRow(
                            TextInput.create("id", "🔑 Item ID", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. sword_001")
                                    .setMinLength(3)
                                    .setMaxLength(32)
                                    .setRequired(true)
                                    .build()
                    )
                    .addActionRow(
                            TextInput.create("display_name", "📛 Display Name", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. Flaming Sword")
                                    .setMaxLength(32)
                                    .setRequired(false)
                                    .build()
                    )
                    .addActionRow(
                            TextInput.create("type", "📦 Item Type", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. File, Text")
                                    .setMaxLength(32)
                                    .setRequired(false)
                                    .build()
                    )
                    .addActionRow(
                            TextInput.create("price", "💰 Price (in rupiah)", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. 1500")
                                    .setMaxLength(16)
                                    .setRequired(false)
                                    .build()
                    )
                    .addActionRow(
                            TextInput.create("image", "💰 Image link of Product", TextInputStyle.SHORT)
                                    .setPlaceholder("e.g. https://example.com/image.png")
                                    .setMaxLength(128)
                                    .setRequired(false)
                                    .build()
                    )
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}