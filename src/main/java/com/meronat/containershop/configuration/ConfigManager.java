package com.meronat.containershop.configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.util.function.Supplier;

public class ConfigManager<T> {

    private final TypeToken<T> configTypeToken;
    private final ConfigurationLoader<?> configurationLoader;
    private final Supplier<T> newObjectProvider;

    private T config;

    public ConfigManager(TypeToken<T> configTypeToken, ConfigurationLoader<?> configurationLoader, Supplier<T> newObjectProvider)
            throws IOException, ObjectMappingException {

        this.configTypeToken = configTypeToken;
        this.configurationLoader = configurationLoader;
        this.newObjectProvider = newObjectProvider;

        save();

    }

    public void load() throws IOException, ObjectMappingException {

        config = this.configurationLoader.load().getValue(this.configTypeToken, newObjectProvider);

    }

    public void save() throws IOException, ObjectMappingException {

        if (config == null) {

            load();

        }

        this.configurationLoader.save(this.configurationLoader.createEmptyNode().setValue(this.configTypeToken, this.config));

    }

    public T getConfig() {

        return this.config;

    }

}