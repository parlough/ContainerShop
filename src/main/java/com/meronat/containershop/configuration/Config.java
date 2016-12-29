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

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@ConfigSerializable
public class Config {

    @Setting(value = "allow-creative-mode", comment = "Should players in creative mode be able to use shops?")
    private boolean creative = false;

    @Setting(value = "shifting-full-stack-sales", comment = "If a player is shifting should they buy/sell in full stacks?")
    private boolean shifting = true;

    @Setting(value = "owner-editing", comment = "Should users be able to edit their own shop signs?")
    private boolean editing = true;

    @Setting(value = "remove-shop-sign-when-empty", comment = "Should the shop be removed when the container is out of stock?")
    private boolean removeShopWhenEmpty = false;

    @Setting(value = "remove-container-when-empty", comment = "Should the containers also be removed?")
    private boolean removeContainerWhenEmpty = false;

    @Setting(value = "allow-creation-by-commands", comment = "Should players be able to create shops with commands?")
    private boolean withCommands = true;

    @Setting(value = "send-owner-if-out-of-stock", comment = "Should a message be sent if their shop is out of stock?")
    private boolean outOfStock = false;

    @Setting(value = "send-owner-transactiions", comment = "Should the owner be notified of transactions?")
    private boolean transactionNotify = true;

    @Setting(value = "protect", comment = "Should we protect the signs and containers?")
    private boolean protect = true;

    @Setting(value = "protect-from-explosions", comment = "Should we protect them from explosions?")
    private boolean protectExplosions = true;

    @Setting(value = "protect-from-hoppers", comment = "Should we protect them from hoppers?")
    private boolean protectHoppers = true;

    @Setting(value = "containers", comment = "Which containers should be allowed to be shops?")
    private List<String> containers = Lists.newArrayList("minecraft:chest", "minecraft:trapped_chest");

    public boolean allowCreative() {

        return creative;

    }

    public boolean shiftTrading() {

        return shifting;

    }

    public boolean canEdit() {

        return editing;

    }

    public boolean removeShopWhenEmpty() {

        return removeShopWhenEmpty;

    }

    public boolean removeContainerWhenEmpty() {

        return removeContainerWhenEmpty;

    }

    public boolean withCommands() {

        return withCommands;

    }

    public boolean messageOutOfStock() {

        return outOfStock;

    }

    public boolean notifyTransactions() {

        return transactionNotify;

    }

    public boolean protect() {

        return protect;

    }

    public boolean protectExplosions() {

        return protectExplosions;

    }

    public boolean protectHoppers() {

        return protectHoppers;

    }

    public List<String> getContainers() {

        return containers;

    }

}