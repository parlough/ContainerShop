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
import com.google.common.reflect.TypeToken;
import com.meronat.containershop.entities.ShopSign;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class Util {

    public static Optional<ShopSign> getAttachedSign(BlockSnapshot block) {

        Optional<Location<World>> optionalLocation = block.getLocation();

        if (!optionalLocation.isPresent()) {
            return Optional.empty();
        }

        Location<World> location = optionalLocation.get();

        Optional<Set<Direction>> optionalDirections = block.get(Keys.CONNECTED_DIRECTIONS);

        ShopSign sign = null;

        if (optionalDirections.isPresent()) {
            Set<Direction> directions = optionalDirections.get();

            for (Direction d : directions) {
                Vector3i possible = block.getPosition().add(d.asBlockOffset());

                Optional<ShopSign> optionalSign = ContainerShop.getSignCollection().getSign(location.getBlockRelative(d));
                if (optionalSign.isPresent()) {

                    sign = optionalSign.get();
                }
            }
        }

        return Optional.ofNullable(sign);
    }

    public static int getLimit(Subject subject, String key) {
        return subject.getOption(key).map(Integer::parseInt).orElse(0);
    }

    public static List<Inventory> getConnectedContainers(Location<World> location, ShopSign sign) {
        List<Inventory> inventories = new ArrayList<>();

        BlockState state = location.getBlock();

        if (state.supports(Keys.DIRECTION)) {
            Optional<Direction> optionalDirection = state.get(Keys.DIRECTION);

            if (optionalDirection.isPresent()) {
                Location<World> conLocation = location.getRelative(optionalDirection.get().getOpposite());

                if (ContainerShop.getConfig().getContainers().contains(conLocation.getBlockType().getId())) {
                    Optional<TileEntity> optionalTileEntity = conLocation.getTileEntity();

                    if (optionalTileEntity.isPresent()) {
                        if (optionalTileEntity.get() instanceof TileEntityCarrier) {
                            inventories.add(((TileEntityCarrier) optionalTileEntity.get()).getInventory());
                        }
                    }
                }
            }
        }

        return inventories;
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

    public static String serializeItemStack(ItemStack item) throws ObjectMappingException, IOException {
        StringWriter sink = new StringWriter();
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
        ConfigurationNode node = loader.createEmptyNode();
        node.setValue(TypeToken.of(ItemStack.class), item);
        loader.save(node);

        return sink.toString();
    }

    public static ItemStack deserializeItemStack(String item) throws IOException, ObjectMappingException {
        StringReader source = new StringReader(item);
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
        ConfigurationNode node = loader.load();

        return node.getValue(TypeToken.of(ItemStack.class));
    }

}
