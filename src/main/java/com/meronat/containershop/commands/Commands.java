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

package com.meronat.containershop.commands;

import com.google.common.collect.Lists;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Commands {

    public void register() {
        final CommandFlags.Builder flagBuilder = GenericArguments.flags();

        Map<List<String>, CommandSpec> children = new HashMap<>();

        children.put(Lists.newArrayList("create", "new"), CommandSpec.builder()
            .description(Text.of("Creates a new ContainerShop based on the item in your hand."))
            .permission("containershop.normal.create")
            .arguments(GenericArguments.optionalWeak(
                flagBuilder
                    .valueFlag(GenericArguments.doubleNum(Text.of("sell")), "s", "-sell")
                    .valueFlag(GenericArguments.doubleNum(Text.of("buy")),"b", "-buy")
                    .permissionFlag("containershops.admin.unlimitedshops", "a")
                    .buildWith(GenericArguments.none())))
            .executor(new CreateCommand())
            .build());

        Sponge.getCommandManager().register(Sponge.getPluginManager().getPlugin("containershop").get(), CommandSpec.builder()
            .description(Text.of("The base ContainerShop command."))
            .permission("containershop.normal.use")
            .children(children)
            .build(), "cs", "shop");
    }

    private static Commands ourInstance = new Commands();

    public static Commands getCommands() {
        return ourInstance;
    }

    private Commands() {}

}
