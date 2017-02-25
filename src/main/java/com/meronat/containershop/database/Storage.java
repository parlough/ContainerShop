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

package com.meronat.containershop.database;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.meronat.containershop.ContainerShop;
import com.meronat.containershop.Util;
import com.meronat.containershop.entities.ShopSign;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class Storage {

    private SqlService sql;

    public Storage() throws SQLException {

        createTables();

    }

    private Connection getConnection() throws SQLException {

        if (sql == null) {

            Optional<SqlService> optionalSql = Sponge.getServiceManager().provide(SqlService.class);

            if (optionalSql.isPresent()) {

                sql = optionalSql.get();

            } else {

                throw new SQLException("SQL service is missing.");

            }

        }

        return sql.getDataSource("jdbc:h2:" + ContainerShop.getFolder().toAbsolutePath().toString()
                + "/storage.db").getConnection();

    }

    private void createTables() {

        try (

                Connection conn = getConnection();
                PreparedStatement mainPS = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS SIGNS(" +
                                "OWNER CHARACTER(36) NOT NULL, " +
                                "WORLD CHARACTER(36) NOT NULL, " +
                                "X INTEGER NOT NULL, " +
                                "Y INTEGER NOT NULL, " +
                                "Z INTEGER NOT NULL, " +
                                "INFINITE BOOLEAN NOT NULL, " +
                                "BUY DOUBLE NOT NULL, " +
                                "SELL DOUBLE NOT NULL, " +
                                "ITEM VARCHAR(2048) NOT NULL, " +
                                "PRIMARY KEY(X, Y, Z))");

                PreparedStatement accessPS = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS ACCESSORS(" +
                                "WORLD CHARACTER(36) NOT NULL, " +
                                "X INTEGER NOT NULL, " +
                                "Y INTEGER NOT NULL, " +
                                "Z INTEGER NOT NULL, " +
                                "USER_ID CHARACTER(36) NOT NULL, " +
                                "FOREIGN KEY(X, Y, Z) REFERENCES SIGNS(X, Y, Z))")

        ) {

            mainPS.execute();
            accessPS.execute();

        } catch (SQLException e) {

            ContainerShop.getLogger().error("There was a problem creating the SQL tables:");
            e.printStackTrace();

        }

    }

    public Optional<ShopSign> getSign(Location<World> location) {

        ShopSign shopSign = null;

        try (

                Connection conn = getConnection();

                PreparedStatement sign = conn.prepareStatement("SELECT OWNER, INFINITE, BUY, SELL, ITEM FROM SIGNS WHERE X = ? AND Y = ? AND Z = ? AND WORLD = ?");

                PreparedStatement additional = conn.prepareStatement("SELECT USER_ID FROM ACCESSORS WHERE X = ? AND Y = ? AND Z = ? AND WORLD = ?")

        ) {

            Vector3i pos = location.getBlockPosition();

            sign.setInt(1, pos.getX());
            sign.setInt(2, pos.getY());
            sign.setInt(3, pos.getZ());
            sign.setString(4, location.getExtent().getUniqueId().toString());

            additional.setInt(1, pos.getX());
            additional.setInt(2, pos.getY());
            additional.setInt(3, pos.getZ());
            additional.setString(4, location.getExtent().getUniqueId().toString());

            try (

                    ResultSet tempSign = sign.executeQuery();
                    ResultSet tempAdditional = additional.executeQuery()

            ) {

                // Should only have sign per location.
                if (tempSign.next()) {

                    ItemStack item = null;

                    try {
                        item = Util.deserializeItemStack(tempSign.getString("ITEM"));
                    } catch (ObjectMappingException e) {
                        ContainerShop.getLogger().error("Failed to deserialize item stack.");
                        e.printStackTrace();
                    } catch (IOException e) {
                        ContainerShop.getLogger().error("Failed to read item stack.");
                        e.printStackTrace();
                    }

                    shopSign = new ShopSign(
                            UUID.fromString(tempSign.getString("OWNER")),
                            tempSign.getBoolean("INFINITE"),
                            tempSign.getDouble("BUY"),
                            tempSign.getDouble("SELL"),
                            item
                    );

                    while (tempAdditional.next()) {

                        shopSign.addAccessor(UUID.fromString(tempAdditional.getString("USER_ID")));

                    }

                }

            }

        } catch (SQLException e) {

            ContainerShop.getLogger().error("There was a problem getting a sign:");
            e.printStackTrace();

        }

        return Optional.ofNullable(shopSign);

    }

    public void createSign(ShopSign sign) {

        try (

                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO SIGNS(OWNER, WORLD, X, Y, Z, INFINITE, BUY, SELL, ITEM) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")

        ) {

            String item = null;

            Vector3i pos = sign.getLocation().getBlockPosition();

            try {

                item = Util.serializeItemStack(sign.getItem());

            } catch (ObjectMappingException | IOException e) {

                ContainerShop.getLogger().error("Failed to serialize the ItemStack snapshot of the shop at: " + pos.toString());
                e.printStackTrace();

            }

            ps.setString(1, sign.getOwner().toString());
            ps.setString(2, sign.getLocation().getExtent().getUniqueId().toString());
            ps.setInt(3, pos.getX());
            ps.setInt(4, pos.getY());
            ps.setInt(5, pos.getZ());
            ps.setBoolean(6, sign.isAdminShop());
            ps.setBigDecimal(7, sign.getBuyPrice().orElseGet(null));
            ps.setBigDecimal(8, sign.getSellPrice().orElseGet(null));
            ps.setString(9, item);

            ps.execute();

        } catch (SQLException e) {

            ContainerShop.getLogger().error("Failed to create a sign in the SQL database:");
            e.printStackTrace();

        }

    }

    public void deleteSign(Location<World> location) {

        try (

                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM SIGNS WHERE X = ? AND Y = ? AND Z = ? AND WORLD = ?")

        ) {

            Vector3i pos = location.getBlockPosition();

            ps.setInt(1, pos.getX());
            ps.setInt(2, pos.getY());
            ps.setInt(3, pos.getZ());
            ps.setString(4, location.getExtent().getUniqueId().toString());

            ps.execute();

        } catch (SQLException e) {

            ContainerShop.getLogger().error("Failed to create a sign in the SQL database:");
            e.printStackTrace();

        }

    }

    public void updateSign(ShopSign sign) {

        try (

                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE SIGNS SET BUY = ?, SELL = ? WHERE X = ? AND Y = ? AND Z = ? and WORLD = ?")

        ) {

            Vector3i pos = sign.getLocation().getBlockPosition();

            ps.setBigDecimal(1, sign.getBuyPrice().orElseGet(null));
            ps.setBigDecimal(2, sign.getSellPrice().orElseGet(null));
            ps.setInt(3, pos.getX());
            ps.setInt(4, pos.getY());
            ps.setInt(5, pos.getZ());

            ps.execute();

        } catch (SQLException e) {

            ContainerShop.getLogger().error("Failed to update a shop at " + sign.getLocation().getBlockPosition().toString() + " in the SQL database.");
            e.printStackTrace();

        }

    }

    public void addAccessor(Location<World> location, UUID accessor) {

        try (

                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO ACCESSORS(WORLD, X, Y, Z, USER_ID) VALUES(?, ?, ?, ?, ?)")

        ) {

            executeAccessorUpdate(ps, location, accessor);

        } catch (SQLException e) {

            ContainerShop.getLogger().error("Failed to add " + accessor.toString() + " to shop at: " + location.toString());
            e.printStackTrace();

        }

    }

    public void removeAccessor(Location<World> location, UUID accessor) {

        try (

                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCESSORS WHERE WORLD = ? AND X = ? AND Y = ? AND Z = ? AND USER_ID = ?")

        ) {

            executeAccessorUpdate(ps, location, accessor);

        } catch (SQLException e) {

            ContainerShop.getLogger().error("Failed to remove " + accessor.toString() + " from a shop at: " + location.getBlockPosition().toString());
            e.printStackTrace();

        }

    }

    private void executeAccessorUpdate(PreparedStatement ps, Location<World> location, UUID accessor) throws SQLException {

        Vector3i pos = location.getBlockPosition();

        ps.setString(1, location.getExtent().getUniqueId().toString());
        ps.setInt(2, pos.getX());
        ps.setInt(3, pos.getY());
        ps.setInt(4, pos.getZ());
        ps.setString(5, accessor.toString());

        ps.execute();

    }

}
