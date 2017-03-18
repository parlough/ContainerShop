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

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ShopSign {

    private static final UUID ADMIN = UUID.fromString("025ad4eb-6db7-4a14-b044-189b4c52a4e7");

    private boolean adminShop;

    private ItemStack item;
    private UUID owner;
    private Set<UUID> accessors = new HashSet<>();

    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private Location<World> location;

    private long lastAccessed;

    public ShopSign(UUID owner, boolean admin, double buy, double sell, ItemStack item) {
        if (admin) {
            this.owner = ADMIN;
        } else {
            this.owner = owner;
        }
        this.adminShop = admin;
        this.buyPrice = BigDecimal.valueOf(buy);
        this.sellPrice = BigDecimal.valueOf(sell);
        this.item = item;
        this.lastAccessed = System.currentTimeMillis();
    }

    public UUID getOwner() {
        this.renewTime();
        return this.owner;
    }

    public void addAccessor(UUID accessor) {
        this.renewTime();
        this.accessors.add(accessor);
    }

    public long getLastAccessed() {
        this.renewTime();
        return this.lastAccessed;
    }

    public boolean isAdminShop() {
        this.renewTime();
        return this.adminShop;
    }

    public Optional<BigDecimal> getBuyPrice() {
        this.renewTime();
        return Optional.ofNullable(this.buyPrice);
    }

    public Optional<BigDecimal> getSellPrice() {
        this.renewTime();
        return Optional.ofNullable(this.sellPrice);
    }

    public boolean isAccessor(UUID uuid) {
        this.renewTime();
        return uuid.equals(this.owner) || this.accessors.contains(uuid);
    }

    public ImmutableSet<UUID> getAccessors() {
        return ImmutableSet.copyOf(this.accessors);
    }

    public ItemStack getItem() {
        this.renewTime();
        return this.item;
    }

    public void setLocation(Location<World> location) {
        this.renewTime();
        this.location = location;
    }

    public Location<World> getLocation() {
        this.renewTime();
        return this.location;
    }

    public void renewTime() {
        this.lastAccessed = System.currentTimeMillis();
    }

}
