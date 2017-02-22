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
import com.meronat.containershop.entities.ShopSign;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class InteractListener {

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event, @Root Player player) {

        BlockSnapshot bs = event.getTargetBlock();

        if (!bs.getState().getType().equals(BlockTypes.WALL_SIGN)) {

            return;

        }

        Optional<Location<World>> optionalLocation = bs.getLocation();

        if (!optionalLocation.isPresent()) {

            return;

        }

        Location<World> location = optionalLocation.get();

        Optional<ShopSign> optionalShopSign = ContainerShop.getSignCollection().getSign(location.getBlockPosition());

        if (!optionalShopSign.isPresent()) {

            return;

        }

        ShopSign shopSign = optionalShopSign.get();

        if (player.get(Keys.IS_SNEAKING).isPresent() && player.get(Keys.IS_SNEAKING).get()) {

            // TODO Send message with information about item

        } else {

            // TODO Finish checking if they have enough money
            //ContainerShop.getEconomyService().getOrCreateAccount(player.getUniqueId()).get().getBalance(ContainerShop.getEconomyService().get)

            for (Container c : Util.getConnectedContainers(location, shopSign)) {

                // TODO Check if empty and config option is set to remove shop

                if (!c.containsAny(shopSign.getItem())) {

                    continue;

                }

                // TODO Iterate through slots, remove item if possible

            }

        }

    }

}
