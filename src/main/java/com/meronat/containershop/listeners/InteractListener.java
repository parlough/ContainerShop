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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Collection;
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

        Optional<ShopSign> optionalShop = ContainerShop.getSignCollection().getSign(location.getBlockPosition());

        if (!optionalShop.isPresent()) {

            return;

        }

        ShopSign shopSign = optionalShop.get();

        ItemStack itemStack = shopSign.getItem();

        if (player.get(Keys.IS_SNEAKING).isPresent() && player.get(Keys.IS_SNEAKING).get()) {

            final String buy;

            if (shopSign.getBuyPrice().isPresent()) {

                buy = shopSign.getBuyPrice().get().toPlainString();

            } else {

                buy = "not available";

            }

            final String sell;

            if (shopSign.getSellPrice().isPresent()) {

                sell = shopSign.getSellPrice().get().toPlainString();

            } else {

                sell = "not available";

            }

            Text info = Text.builder()
                .append(Text.of(TextColors.DARK_GREEN, "Id:", TextColors.GRAY, itemStack.getItem().getId()), Text.NEW_LINE)
                .append(Text.of(TextColors.DARK_GREEN, "Name: ", TextColors.GRAY,  itemStack.getItem().getName()), Text.NEW_LINE)
                .append(Text.of(TextColors.DARK_GREEN, "Amount: ", TextColors.GRAY, itemStack.getQuantity(), Text.NEW_LINE))
                .append(Text.of(TextColors.DARK_GREEN, "Buy price: ", TextColors.GRAY, buy, Text.NEW_LINE))
                .append(Text.of(TextColors.DARK_GREEN, "Sell price: ", TextColors.GRAY, sell, Text.NEW_LINE))
                .append(Text.of(TextColors.DARK_GREEN, "Enchantments: ", TextColors.GRAY, Util.getEnchantments(itemStack), Text.NEW_LINE))
                .build();

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "You are buying ", TextColors.LIGHT_PURPLE, itemStack.getQuantity() + " " +
                    itemStack.getItem().getName(), TextColors.GRAY, " - Hover for more information.").toBuilder()
                .onHover(TextActions.showText(info))
                .onClick(TextActions.suggestCommand("/cs help"))
                .build());

        } else {

            if (player.get(Keys.GAME_MODE).isPresent() && player.get(Keys.GAME_MODE).get().equals(GameModes.CREATIVE)) {

                player.sendMessage(Text.of(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You cannot be in creative mode and use shops.")));

                return;

            }

            if (player.getUniqueId().equals(shopSign.getOwner())) {

                player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You cannot buy from your own shop."));

                return;

            }

            Optional<BigDecimal> optionalBuyPrice = shopSign.getBuyPrice();

            if (!optionalBuyPrice.isPresent()) {

                player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You cannot buy from this shop."));

                return;

            }

            //noinspection ConstantConditions
            PluginContainer pluginContainer = Sponge.getPluginManager().getPlugin("containershop").get();

            EconomyService economyService = ContainerShop.getEconomyService();

            //noinspection ConstantConditions
            UniqueAccount account = economyService.getOrCreateAccount(player.getUniqueId()).get();

            final ResultType econResult;

            if (shopSign.isAdminShop()) {

                econResult = account.withdraw(economyService.getDefaultCurrency(), optionalBuyPrice.get(), Cause.source(pluginContainer).build()).getResult();

            } else {

                //noinspection ConstantConditions
                econResult = account.transfer(
                    economyService.getOrCreateAccount(shopSign.getOwner()).get(),
                    economyService.getDefaultCurrency(),
                    optionalBuyPrice.get(),
                    Cause.source(pluginContainer).build()).getResult();

            }

            if (!econResult.equals(ResultType.SUCCESS)) {

                if (econResult.equals(ResultType.ACCOUNT_NO_FUNDS)) {

                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You do not have enough available funds."));

                } else {

                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "There was a problem transferring funds."));

                }

                return;

            }

            ItemStack stack = ItemStack.of(ItemTypes.NONE, 0);

            if (shopSign.isAdminShop()) {

                stack = shopSign.getItem().copy();

            } else {

                for (Container c : Util.getConnectedContainers(location, shopSign)) {

                    if (!c.contains(itemStack)) {

                        continue;

                    }

                    Optional<ItemStack> optionalStack = c.query(itemStack).poll(itemStack.getQuantity());

                    if (!optionalStack.isPresent()) {

                        continue;

                    }

                    if (!(optionalStack.get().getQuantity() == itemStack.getQuantity())) {

                        continue;

                    }

                    stack = optionalStack.get();

                    break;

                }

            }

            if (stack.getItem().equals(ItemTypes.NONE)) {

                player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "This shop does not have enough stock."));

                return;

            }

            InventoryTransactionResult result = player.getInventory().offer(stack);

            player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.DARK_GREEN, "Successfully purchased items."));

            Collection<ItemStackSnapshot> rejectedItems = result.getRejectedItems();

            if (rejectedItems.isEmpty()) {

                return;

            }

            Location<World> playerLocation = player.getLocation();

            World world = playerLocation.getExtent();

            for (ItemStackSnapshot i : rejectedItems) {

                Item rejected = (Item) world.createEntity(EntityTypes.ITEM, playerLocation.getPosition());

                rejected.offer(Keys.REPRESENTED_ITEM, i);

                world.spawnEntity(rejected,
                    Cause.source(EntitySpawnCause.builder()
                        .entity(rejected)
                        .type(SpawnTypes.PLUGIN)
                        .build()).owner(pluginContainer).build());

            }

            player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "Some items you bought were placed on the ground."));

        }

    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary event, @Root Player player) {

        BlockSnapshot bs = event.getTargetBlock();

        if (!bs.getState().getType().equals(BlockTypes.WALL_SIGN)) {

            return;

        }

        Optional<Location<World>> optionalLocation = bs.getLocation();

        if (!optionalLocation.isPresent()) {

            return;

        }

        Location<World> location = optionalLocation.get();

        Optional<ShopSign> optionalShop = ContainerShop.getSignCollection().getSign(location.getBlockPosition());

        if (!optionalShop.isPresent()) {

            return;

        }

        ShopSign shopSign = optionalShop.get();

        if (player.get(Keys.GAME_MODE).isPresent() && player.get(Keys.GAME_MODE).get().equals(GameModes.CREATIVE)) {

            player.sendMessage(Text.of(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You cannot be in creative mode and use shops.")));

            return;

        }

        if (player.getUniqueId().equals(shopSign.getOwner())) {

            player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You cannot sell to your own shop."));

            return;

        }

        ItemStack itemStack = shopSign.getItem();

        Optional<ItemStack> optionalTempStack = player.getInventory().query(itemStack).peek();

        if (!optionalTempStack.isPresent() || optionalTempStack.get().getQuantity() < itemStack.getQuantity()) {

            player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, "You do not have enough of this item to sell."));

            return;

        }

        for (Container c : Util.getConnectedContainers(location, shopSign)) {

            // TODO Try to see if there is space in container for items, if not don't buy
            // If it is full, inform the owner of the sign that their is no where to put the items

        }

        // TODO Try to do the economy stuff, if it doesn't work tell them

        // TODO Attempt to move item from player's inventory to one of the connected containers

    }

}
