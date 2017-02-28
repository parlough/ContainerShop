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

    @Setting(value = "owner-editing", comment = "Should users be able to edit their own shop signs?")
    private boolean editing = true;

    @Setting(value = "send-owner-if-out-of-stock", comment = "Should a message be sent if their shop is out of stock?")
    private boolean outOfStock = false;

    @Setting(value = "send-owner-transactiions", comment = "Should the owner be notified of transactions?")
    private boolean transactionNotify = true;

    @Setting(value = "protect", comment = "Should we protect the signs and containers?")
    private boolean protect = true;

    @Setting(value = "limit-shops", comment = "Should we limit the amount of shops a player can create?")
    private boolean limitShops = false;

    @Setting(value = "protect-from-explosions", comment = "Should we protect them from explosions?")
    private boolean protectExplosions = true;

    @Setting(value = "protect-from-hoppers", comment = "Should we protect them from hoppers?")
    private boolean protectHoppers = true;

    @Setting(value = "containers", comment = "Which containers should be allowed to be shops?")
    private List<String> containers = Lists.newArrayList("minecraft:chest", "minecraft:trapped_chest");

    public boolean allowCreative() {
        return this.creative;
    }

    public boolean canEdit() {
        return this.editing;
    }

    public boolean messageOutOfStock() {
        return this.outOfStock;
    }

    public boolean notifyTransactions() {
        return this.transactionNotify;
    }

    public boolean protect() {
        return this.protect;
    }

    public boolean limitShops() {
        return this.limitShops;
    }

    public boolean protectExplosions() {
        return this.protectExplosions;
    }

    public boolean protectHoppers() {
        return this.protectHoppers;
    }

    public List<String> getContainers() {
        return this.containers;
    }

}
