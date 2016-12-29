package com.meronat.containershop.entities;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.item.inventory.ItemStack;
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
