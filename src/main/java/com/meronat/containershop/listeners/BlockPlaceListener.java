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
import com.meronat.containershop.entities.ShopSign;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BlockPlaceListener {

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {

        for (Transaction<BlockSnapshot> t : event.getTransactions()) {

            BlockSnapshot bs = t.getFinal();

            if (!bs.getState().getType().equals(BlockTypes.WALL_SIGN)) {

                return;

            }

            ShopSign shopSign = ContainerShop.getPlacing().get(player.getUniqueId());

            if (shopSign == null) {

                return;

            }

            Optional<Set<Direction>> optionalDirections = bs.get(Keys.CONNECTED_DIRECTIONS);

            if (!optionalDirections.isPresent()) {

                return;

            }

            Optional<Location<World>> optionalLocation = bs.getLocation();

            if (!optionalLocation.isPresent()) {

                return;

            }

            Location<World> location = optionalLocation.get();

            if (!ContainerShop.getConfig().getContainers().contains(location.getRelative(optionalDirections.get().iterator().next()).getBlockType().getId())) {

                return;

            }

            Optional<TileEntity> optionalTileEntity = location.getTileEntity();

            if (!optionalTileEntity.isPresent()) {

                player.sendMessage(Text.of(TextColors.RED, "Failed to create the shop, try again."));

                return;

            }

            final String name;

            if (shopSign.isAdminShop()) {

                name = "Admin";

            } else {

                name = player.getName();

            }

            final String buySell;

            Optional<BigDecimal> optionalBuy = shopSign.getBuyPrice();

            Optional<BigDecimal> optionalSell = shopSign.getSellPrice();

            if (optionalBuy.isPresent() && optionalSell.isPresent()) {

                buySell = "S " + optionalSell.get().toPlainString() + ":" + optionalBuy.get().toPlainString() + " B";

            } else if (optionalBuy.isPresent()) {

                buySell = "B " + optionalBuy.get().toPlainString();

            } else if (optionalSell.isPresent()) {

                buySell = "S " + optionalSell.get().toPlainString();

            } else {

                player.sendMessage(Text.of(TextColors.RED, "The shop cannot have neither a buy or sell price. You must recreate your shop."));

                ContainerShop.getPlacing().remove(player.getUniqueId());

                return;

            }

            List<Text> signLines = new ArrayList<>();

            signLines.add(Text.of(name));
            signLines.add(Text.of(shopSign.getItem().getQuantity()));
            signLines.add(Text.of(buySell));
            signLines.add(Text.of(shopSign.getItem().getItem().getName()));

            TileEntity sign = optionalTileEntity.get();

            sign.offer(Keys.SIGN_LINES, signLines);

            shopSign.setPosition(bs.getPosition());

            ContainerShop.getSignCollection().put(bs.getPosition(), shopSign);

            ContainerShop.getPlacing().remove(player.getUniqueId());

        }

    }

}
