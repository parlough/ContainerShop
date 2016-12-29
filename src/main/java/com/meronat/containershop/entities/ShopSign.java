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

package com.meronat.containershop.entities;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class ShopSign {

    private boolean adminShop;

    private ItemStackSnapshot item;
    private UUID owner;
    private Set<UUID> accessors;

    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private int amount;

    private long lastAccessed;

    public ShopSign(UUID owner, boolean admin, double buy, double sell, int amount, ItemStackSnapshot item) {

        this.owner = owner;
        this.adminShop = admin;
        this.buyPrice = BigDecimal.valueOf(buy);
        this.sellPrice = BigDecimal.valueOf(sell);
        this.amount = amount;
        this.item = item;

    }

    public UUID getOwner() {

        return this.owner;

    }

    public void addAccessor(UUID accessor) {

        this.accessors.add(accessor);

    }

    public long getLastAccessed() {

        return this.lastAccessed;

    }

    public boolean isAdminShop() {

        return this.adminShop;

    }

    public BigDecimal getBuyPrice() {

        return this.buyPrice;

    }

    public BigDecimal getSellPrice() {

        return this.sellPrice;

    }

    public int getAmount() {

        return this.amount;

    }

    public boolean isAccessor(UUID uuid) {

        return uuid.equals(owner) || accessors.contains(uuid);

    }

    public ItemStackSnapshot getItem() {

        return this.item;

    }

}
