package ramadevs.com.Core.Discord.Modal;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CreateModalRegister {
    public static Modal createRegister() {
        try {
            Modal m = Modal.create("register_menu", "Registration Menu")
                    .addActionRow(
                            TextInput.create("growid", "GrowID (GROWTOPIA IGN)", TextInputStyle.SHORT)
                                    .setMaxLength(32)
                                    .setRequired(true)
                                    .build()
                    )
                    .build();
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
