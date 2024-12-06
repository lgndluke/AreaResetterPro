package com.lgndluke.lgndware.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Utility class for handling message related operations.
 * @author lgndluke
 **/
public class MessageHandler extends AbstractFileHandler {

    /**
     * @param plugin The JavaPlugin instance associated with this MessageHandler.
     **/
    public MessageHandler(JavaPlugin plugin) {
        super(plugin, "messages.yml");
    }

    /**
     * Initializes the 'messages.yml' file on server startup.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    @Override
    public boolean initialize() {
        FutureTask<Boolean> initAbstractFileHandler = new FutureTask<>(() -> {
            createFile();
            super.getFileConfig().load(super.getFile());;
            super.getFileConfig().options().copyDefaults(true);
            return save();
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initAbstractFileHandler, 10, TimeUnit.SECONDS);
    }

    /**
     * Asynchronously retrieves a message as String from the 'messages.yml' file.
     * @param value The key for the requested message.
     * @return The requested message value as String.
     **/
    public String getMessageAsString(String value) {
        RunnableFuture<String> getMsgAsString = new FutureTask<>(() -> PlainTextComponentSerializer.plainText().serialize(getMessageAsComponent(value)));
        return super.getDefaultAsyncExecutor().fetchExecutionResult(super.getPlugin().getLogger(), getMsgAsString, 10, TimeUnit.SECONDS);
    }

    /**
     * Asynchronously retrieves a message as Component from the 'messages.yml' file.
     * @param value The key for the requested message.
     * @return The requested message value as Component.
     **/
    public Component getMessageAsComponent(String value) {
        RunnableFuture<Component> getMsgAsComponent = new FutureTask<>(() -> MiniMessage.miniMessage().deserialize(Objects.requireNonNull(super.getFileConfig().getString(value))));
        return super.getDefaultAsyncExecutor().fetchExecutionResult(super.getPlugin().getLogger(), getMsgAsComponent, 10, TimeUnit.SECONDS);
    }

    /**
     * Asynchronously retrieves a List of messages as Components from the 'messages.yml' file.
     * @param value The key for the requested list of messages.
     * @return The requested list of messages.
     **/
    public List<Component> getMessagesAsComponentList(String value) {
        RunnableFuture<List<Component>> getMsgAsComponentList = new FutureTask<>(() -> {
            List<Component> results = new ArrayList<>();
            for(String listVal : super.getFileConfig().getStringList(value)) {
                results.add(MiniMessage.miniMessage().deserialize(listVal));
            }
            return results;
        });
        return super.getDefaultAsyncExecutor().fetchExecutionResultAsList(super.getPlugin().getLogger(), getMsgAsComponentList, 10, TimeUnit.SECONDS);
    }

    /**
     * Creates the 'messages.yml' file.
     **/
    @Override
    protected void createFile() {
        if(!super.getFile().exists()) {
            try {
                Files.copy(Objects.requireNonNull(super.getPlugin().getResource("messages.yml")), super.getFile().toPath());
                super.getPlugin().getLogger().log(Level.INFO, "Successfully created " + super.getFile().getName() + " file.");
            } catch (IOException io) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Copying defaults into 'messages.yml' failed!", io);
            }
        }
    }

}
