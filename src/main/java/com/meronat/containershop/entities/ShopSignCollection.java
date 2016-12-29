package com.meronat.containershop.entities;

import com.flowpowered.math.vector.Vector3i;
import com.meronat.containershop.ContainerShop;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ShopSignCollection extends ConcurrentHashMap<Vector3i, ShopSign> {

    public Optional<ShopSign> getSign(Vector3i location) {

        if (!this.containsKey(location)) {

            ContainerShop.getStorage().getSign(location).ifPresent(s -> super.put(location, s));

        }

        return Optional.ofNullable(super.get(location));

    }

}
