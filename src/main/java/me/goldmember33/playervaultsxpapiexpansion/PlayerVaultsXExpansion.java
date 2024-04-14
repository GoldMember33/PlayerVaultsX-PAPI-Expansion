package me.goldmember33.playervaultsxpapiexpansion;

import com.drtshock.playervaults.PlayerVaults;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
import com.drtshock.playervaults.vaultmanagement.VaultOperations;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerVaultsXExpansion extends PlaceholderExpansion {

    private PlayerVaults playerVaults;

    @Override
    public @NotNull String getIdentifier() {
        return "playervaultsx";
    }

    @Override
    public @NotNull String getName() {
        return "playervaultsx";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GoldMember33";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return "PlayerVaults";
    }

    @Override
    public boolean canRegister() {
        if (Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) == null) return false;
        playerVaults = PlayerVaults.getInstance();
        return playerVaults != null;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        if (player == null || !player.isOnline()) {
            return "";
        }

        String playerUUIDStr = player.getUniqueId().toString();
        int used_slots_amount = 0;

        if (params.startsWith("amount_opened")) {
            // Values range: Min: 0, Max: 99
            // Return the amount of used/opened vaults of the player.
            return VaultManager.getInstance().getPlayerVaultFile(playerUUIDStr, false) != null
                    ? String.valueOf(VaultManager.getInstance().getPlayerVaultFile(playerUUIDStr, false).getValues(false).size())
                    : String.valueOf(0);
        }

        if (params.startsWith("amount_available")) {
            // Values range: Min: 0, Max: 99
            // Return the amount of vaults determined by permission on the player.
            return String.valueOf(getAvailableVaultsAmountWithPermission(player));
        }

        if (params.startsWith("size")) {
            // Values range: Min: 9, Max: 54
            // Return the amount of inventory slot per vault for the player.
            return String.valueOf(VaultOperations.getMaxVaultSize(Bukkit.getOfflinePlayer(player.getUniqueId())));
        }

        if (params.startsWith("rows")) {
            // Values range: Min: 1, Max: 6
            // Return the amount of inventory slot rows per vault for the player.
            return String.valueOf((VaultOperations.getMaxVaultSize(Bukkit.getOfflinePlayer(player.getUniqueId()))) / 9);
        }

        if (params.startsWith("global_max_vaults")) {
            // Value: 99 (PlayerVaultX plugin hard set).
            // Return the maximum amount of vaults a player can have.
            return String.valueOf(playerVaults.getMaxVaultAmountPermTest());
        }

        if (params.toLowerCase().startsWith("free_slots_")) {
            // Values range: Min: 0, Max: 54
            // Return the amount of free (empty) slots of a specific vault.
            String iden_new = params.split("(?i)free_slots_")[1];
            String vaultNumberStr = iden_new.trim();

            int vaultNumber;

            try {
                vaultNumber = Integer.parseInt(vaultNumberStr);
            } catch (NumberFormatException ignored) {
                return "";
            }

            if (VaultManager.getInstance().vaultExists(playerUUIDStr, vaultNumber)) {

                int free_slots_amount = 0;

                for (ItemStack item : VaultManager.getInstance().getVault(playerUUIDStr, vaultNumber).getContents()) {
                    if (item == null) free_slots_amount++;
                }

                return String.valueOf(free_slots_amount);

            } else {
                return "";
            }
        }

        if (params.toLowerCase().startsWith("used_slots_")) {
            // Values range: Min: 0, Max: 54
            // Return the amount of filled (with items) slots of a specific vault.
            String iden_new = params.split("(?i)used_slots_")[1];
            String vaultNumberStr = iden_new.trim();

            int vaultNumber;

            try {
                vaultNumber = Integer.parseInt(vaultNumberStr);
            } catch (NumberFormatException ignored) {
                return "";
            }

            if (VaultManager.getInstance().vaultExists(playerUUIDStr, vaultNumber)) {

                for (ItemStack item : VaultManager.getInstance().getVault(playerUUIDStr, vaultNumber).getContents()) {
                    if (item != null) used_slots_amount++;
                }

                return String.valueOf(used_slots_amount);

            } else {
                return "";
            }
        }

        if (params.startsWith("locked_status_")) {
            // Values: true or false
            // true indicates that specified vault is locked.
            // false indicates an accessible vault status.
            // Return whether a specific vault is accessible or not by the player.
            String iden_new = params.split("(?i)status_")[1];
            String vaultNumberStr = iden_new.trim();

            int vaultNumber;

            try {
                vaultNumber = Integer.parseInt(vaultNumberStr);
            } catch (NumberFormatException ignored) {
                return "";
            }

            return vaultNumber < 1 || vaultNumber > playerVaults.getMaxVaultAmountPermTest()
                   ? String.valueOf(true)
                   : String.valueOf(getVaultStatus(player, vaultNumber));
        }

        return null;
    }

    public int getAvailableVaultsAmountWithPermission(Player player) {
        if (player.hasPermission("playervaults.*")) {
            return playerVaults.getMaxVaultAmountPermTest();

        } else {

            List<Integer> tempList = new ArrayList<>();
            for (int i = 0; i < playerVaults.getMaxVaultAmountPermTest(); i++) {
                if (player.hasPermission("playervaults.amount." + (i + 1))) {
                    tempList.add(i + 1);
                }
            }

            Optional<Integer> value = tempList.stream().max(Integer::compareTo);
            return value.orElse(0);

        }
    }

    public boolean getVaultStatus(Player player, int vaultNumber) {
        return !player.hasPermission("playervaults.amount." + vaultNumber);
    }
}
