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

package com.meronat.containershop;

import com.flowpowered.math.vector.Vector3i;
import com.meronat.containershop.entities.ShopSign;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class Util {

    public static Optional<ShopSign> getAttachedSign(BlockSnapshot block) {

        Optional<Set<Direction>> optionalDirections = block.get(Keys.CONNECTED_DIRECTIONS);

        ShopSign sign = null;

        if (optionalDirections.isPresent()) {

            Set<Direction> directions = optionalDirections.get();

            for (Direction d : directions) {

                Vector3i possible = block.getPosition().add(d.asBlockOffset());

                Optional<ShopSign> optionalSign = ContainerShop.getSignCollection().getSign(possible);

                if (optionalSign.isPresent()) {

                    sign = optionalSign.get();

                }

            }

        }

        return Optional.ofNullable(sign);

    }

    public static int getLimit(Subject subject, String key) {

        Optional<String> optionalLimit = subject.getOption(key);

        return optionalLimit.map(Integer::parseInt).orElse(0);

    }

    public static Set<Container> getConnectedContainers(Location<World> location, ShopSign sign) {

        return new HashSet<>();

    }

    public static String getEnchantments(ItemStack stack) {

        Optional<EnchantmentData> optionalEnchantmentData = stack.get(EnchantmentData.class);

        if (optionalEnchantmentData.isPresent()) {

            List<ItemEnchantment> enchantments = optionalEnchantmentData.get().enchantments().get();

            List<String> names = new ArrayList<>();

            for (ItemEnchantment e : enchantments) {

                names.add(e.getEnchantment().getName() + " " + e.getLevel());

            }

            return String.join(", ", names);

        }

        return "";

    }

}
