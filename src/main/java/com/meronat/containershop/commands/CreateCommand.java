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

import com.meronat.containershop.ContainerShop;
import com.meronat.containershop.Util;
import com.meronat.containershop.entities.ShopSign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CreateCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        if (!(source instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "You must be a player to use this command."));
        }

        Player player = (Player) source;

        // TODO Check if there is a limit and if they have reached it if so

        Optional<ItemStack> optionalItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (!optionalItem.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, "You must have an item in your hand to sell."));
        }

        ItemStack itemStack = optionalItem.get();

        if (itemStack.getItem().equals(ItemTypes.AIR) || itemStack.getItem().equals(ItemTypes.NONE)) {
            throw new CommandException(Text.of(TextColors.RED, "You must have a proper item in your hand to sell."));
        }

        if (itemStack.getQuantity() <= 0) {
            throw new CommandException(Text.of(TextColors.RED, "You must have at least one of the item."));
        }

        if (itemStack.getQuantity() > itemStack.getMaxStackQuantity()) {
            throw new CommandException(Text.of(TextColors.RED, "You must have a legal amount of the item."));
        }

        Optional<DurabilityData> optionalDurability = itemStack.get(DurabilityData.class);

        if (optionalDurability.isPresent()) {
            MutableBoundedValue<Integer> durability = optionalDurability.get().durability();

            ContainerShop.getLogger().info("" + durability.get() + durability.getMaxValue());

            if (durability.get() < durability.getMaxValue()) {
                throw new CommandException(Text.of(TextColors.DARK_GREEN, "The item you are selecting must have maximum durability."));
            }
        }

        boolean admin = args.hasAny("a");

        if (admin && !player.hasPermission("containershops.admin.unlimitedshops")) {
            throw new CommandException(Text.of(TextColors.RED, "You do not have permission to create admin shops."));
        }

        Optional<Double> optionalSellPrice = args.getOne("sell");

        Optional<Double> optionalBuyPrice = args.getOne("buy");

        if (!optionalSellPrice.isPresent() && !optionalBuyPrice.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, "You must specify a sell price, a buy price, or both."));
        }

        final String  buy;

        buy = optionalBuyPrice.map(aDouble -> "" + aDouble).orElse("not available");

        final String sell;

        sell = optionalSellPrice.map(aDouble -> "" + aDouble).orElse("not available");

        Text info = Text.builder()
            .append(Text.of(TextColors.DARK_GREEN, "Id: ", TextColors.GRAY, itemStack.getItem().getId()), Text.NEW_LINE)
            .append(Text.of(TextColors.DARK_GREEN, "Name: ", TextColors.GRAY,  itemStack.getItem().getTranslation().get(player.getLocale())), Text.NEW_LINE)
            .append(Text.of(TextColors.DARK_GREEN, "Amount: ", TextColors.GRAY, itemStack.getQuantity(), Text.NEW_LINE))
            .append(Text.of(TextColors.DARK_GREEN, "Buy price: ", TextColors.GRAY, buy, Text.NEW_LINE))
            .append(Text.of(TextColors.DARK_GREEN, "Sell price: ", TextColors.GRAY, sell, Text.NEW_LINE))
            .append(Text.of(TextColors.DARK_GREEN, "Enchantments: ", TextColors.GRAY, Util.getEnchantments(itemStack)))
            .build();

        // TODO Look into using the `showItem` functionality

        player.sendMessage(Text.of(TextColors.DARK_GREEN, "You are creating a shop with ", TextColors.LIGHT_PURPLE, itemStack.getQuantity() + " " +
            itemStack.getItem().getTranslation().get(player.getLocale()), TextColors.GRAY, " - Hover for more information.").toBuilder()
            .onHover(TextActions.showText(info))
            .onClick(TextActions.suggestCommand("/cs help"))
            .build());

        ShopSign shopSign = new ShopSign(
            player.getUniqueId(),
            admin,
            optionalBuyPrice.orElseGet(null),
            optionalSellPrice.orElseGet(null),
            itemStack);

        Text yes = Text.builder("YES")
            .color(TextColors.GREEN)
            .onHover(TextActions.showText(Text.of(TextColors.DARK_GREEN, "Click to create the shop")))
            .onClick(TextActions.executeCallback(x -> yes(player, shopSign)))
            .build();

        Text no = Text.builder("NO")
            .color(TextColors.DARK_RED)
            .onHover(TextActions.showText(Text.of(TextColors.DARK_GREEN, "Click to cancel creation of this shop")))
            .onClick(TextActions.executeCallback(x -> no(player)))
            .build();

        player.sendMessage(Text.of(TextColors.DARK_GREEN, "Is that your desired shop? ").concat(yes).concat(Text.of("  ")).concat(no));

        return CommandResult.success();
    }

    private void yes(Player player, ShopSign shopSign) {
        ContainerShop.getPlacing().put(player.getUniqueId(), shopSign);

        player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place a sign onto the side of a container you want to create your shop for."));
    }

    private void no(Player player) {
        player.sendMessage(Text.of(TextColors.DARK_GREEN, "You have cancelled the creation of this shop."));
    }

}
