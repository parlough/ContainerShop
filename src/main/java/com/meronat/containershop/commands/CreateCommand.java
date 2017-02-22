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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
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

        boolean admin = args.getOne("admin").isPresent();

        if (admin && !player.hasPermission("containershops.admin.unlimitedshop")) {

            throw new CommandException(Text.of(TextColors.RED, "You do not have permission to create admin shops."));

        }

        Optional<Integer> optionalAmount = args.getOne("amount");

        if (!optionalAmount.isPresent()) {

            throw new CommandException(Text.of(TextColors.RED, "You must specify an amount to sell."));

        }

        Optional<Double> optionalSellPrice = args.getOne("sell");

        Optional<Double> optionalBuyPrice = args.getOne("buy");

        if (!optionalSellPrice.isPresent() && !optionalBuyPrice.isPresent()) {

            throw new CommandException(Text.of(TextColors.RED, "You must specify a sell price, a buy price, or both."));

        }

        // TODO Send message to verify information

        if (optionalAmount.get() <= 0) {

            throw new CommandException(Text.of(TextColors.RED, "The amount specified must be greater than 0"));

        }

        ContainerShop.getPlacing().put(player.getUniqueId(), new ShopSign(
                player.getUniqueId(),
                admin, optionalBuyPrice.orElseGet(null),
                optionalSellPrice.orElseGet(null),
                optionalAmount.get(),
                itemStack));

        player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click the sign attached to the container you want to create a shop for."));

        return CommandResult.success();

    }

}
