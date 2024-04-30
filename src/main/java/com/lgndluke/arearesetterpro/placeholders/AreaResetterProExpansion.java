package com.lgndluke.arearesetterpro.placeholders;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AreaResetterProExpansion extends PlaceholderExpansion {

    //Attributes

    private final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final String identifier = areaPlugin.getPluginMeta().getName();
    private final String author = areaPlugin.getPluginMeta().getAuthors().get(0);
    private final String version = areaPlugin.getPluginMeta().getVersion();

    //Methods

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }
}
