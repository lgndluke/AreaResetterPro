package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * This Class handles operations with the 'messages.yml' file.
 * @author lgndluke
 **/
public class MessageHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static File messageFile;
    private static FileConfiguration messageFileConf;

    //Static Methods
    /**
     * Initializes the 'messages.yml' file on server startup.
     **/
    public static void initialize() {

        if(!areaPlugin.getDataFolder().exists()) {
            boolean folderCreated = areaPlugin.getDataFolder().mkdir();
            if(folderCreated) {
                areaPlugin.getLogger().log(Level.INFO, "Successfully created the Plugins folder.");
            }
        }

        messageFile = new File(areaPlugin.getDataFolder().getAbsolutePath(), "messages.yml");

        if(!messageFile.exists()) {
            try {
                Files.copy(areaPlugin.getResource("messages.yml"), messageFile.toPath());
                areaPlugin.getLogger().log(Level.INFO, "Successfully created 'messages.yml' file.");
            } catch (IOException io) {
                areaPlugin.getLogger().log(Level.SEVERE, "Copying defaults into 'messages.yml' failed!", io);
                return;
            }
        }

        messageFileConf = YamlConfiguration.loadConfiguration(messageFile);
        messageFileConf.options().copyDefaults(true);
        save();

    }

    /**
     * Reloads the 'messages.yml' file.
     * @throws IOException, if 'messages.yml' file doesn't exist or can't be accessed.
     **/
    public static void reload() throws IOException {
        messageFileConf = YamlConfiguration.loadConfiguration(messageFile);
        messageFileConf.options().copyDefaults(true);
        save();
    }

    /**
     * Saves the 'messages.yml' file.
     **/
    public static void save() {
        try {
            messageFileConf.save(messageFile);
        } catch (IOException io) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't save data to 'messages.yml'", io);
        }
    }

    /**
     * Getter for String value from 'messages.yml' file.
     * @param value holding the message requested from 'message.yml' file.
     * @return requested message value as String.
     **/
    public static String getMessageAsString(String value) {
        return PlainTextComponentSerializer.plainText().serialize(getMessageAsComponent(value));
    }

    /**
     * Getter for String value from 'messages.yml' file as Component.
     * @param value holding the message requested from 'message.yml' file.
     * @return requested message value as Component.
     **/
    public static Component getMessageAsComponent(String value) {
        return MiniMessage.miniMessage().deserialize(Objects.requireNonNull(messageFileConf.getString(value)));
    }

    /**
     * Getter for String values from 'messages.yml' file that are stored as array list.
     * @param value holding the message requested from 'message.yml' file.
     * @return requested message value as Component array list.
     **/
    public static List<Component> getMessagesAsComponentList(String value) {
        ArrayList<Component> results = new ArrayList<>();
        for(String listVal : messageFileConf.getStringList(value)) {
            results.add(MiniMessage.miniMessage().deserialize(listVal));
        }
        return results;
    }

}
