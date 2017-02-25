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

package com.meronat.containershop.listeners;

import com.meronat.containershop.ContainerShop;
import com.meronat.containershop.Util;
import com.meronat.containershop.database.Storage;
import com.meronat.containershop.entities.ShopSign;
import com.meronat.containershop.entities.ShopSignCollection;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.awt.Container;
import java.util.Optional;
import java.util.UUID;

public class BlockBreakListener {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {

        for (Transaction<BlockSnapshot> t: event.getTransactions()) {

            BlockSnapshot block = t.getOriginal();

            Optional<Location<World>> optionalLocation = block.getLocation();

            if (!optionalLocation.isPresent()) {

                return;

            }

            Location<World> location = optionalLocation.get();

            Optional<ShopSign> optionalSign = Optional.empty();

            ShopSign sign;

            if (block.getExtendedState().getType().equals(BlockTypes.WALL_SIGN)) {

                optionalSign = ContainerShop.getSignCollection().getSign(location);


            } else if (ContainerShop.getConfig().getContainers().contains(block.getExtendedState().getId())) {

                optionalSign = Util.getAttachedSign(block);

            }

            if (optionalSign.isPresent()) {

                sign = optionalSign.get();

            } else {

                return;

            }

            if (event.getCause().root() instanceof Player) {

                Player player = (Player) event.getCause().root();

                if (player.getUniqueId().equals(sign.getOwner()) || ContainerShop.isBypassing(player.getUniqueId())) {

                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You have destroyed your shop."));

                    ContainerShop.getSignCollection().remove(sign.getLocation());

                    Storage storage = ContainerShop.getStorage();

                    for (UUID uuid : sign.getAccessors()) {

                        storage.removeAccessor(sign.getLocation(), uuid);

                    }

                    ContainerShop.getStorage().deleteSign(sign.getLocation());

                    return;

                } else {

                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.DARK_RED, "You cannot destroy someone else's shop."));

                }

            }

            event.setCancelled(true);

        }

    }

    @Listener
    public void onExplosionEvent(ExplosionEvent.Post event) {

        ShopSignCollection signCollection = ContainerShop.getSignCollection();

        for (Transaction<BlockSnapshot> t : event.getTransactions()) {

            if (t.isValid() && t.getOriginal().getLocation().isPresent()) {

                Location<World> location = t.getOriginal().getLocation().get();

                if (signCollection.getSign(location).isPresent()) {

                    t.setValid(false);

                } else if (Util.getAttachedSign(t.getOriginal()).isPresent()) {

                    t.setValid(false);

                }

            }

        }

    }

    // TODO Protect from pistons pushing if containers are ever broken by that

}
