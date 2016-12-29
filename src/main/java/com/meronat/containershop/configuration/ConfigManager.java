/*
 * This file is part of ContainerShop, licensed under the MIT License (MIT).
 *
 * Copyright (c) Meronat <http://www.meronat.com>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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