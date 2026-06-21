package com.zycu.zydata;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

public final class ZyData implements ModInitializer {
    private static final int PAGE_SIZE = 54;

    private static final List<String> LOOT_TABLES = List.of(
            "moldomre:items/extras/big_key",
            "moldomre:items/extras/crab_claw",
            "moldomre:items/extras/emerald_amulet",
            "moldomre:items/extras/goat_horn_amethyst",
            "moldomre:items/extras/goat_horn_copper",
            "moldomre:items/extras/goat_horn_diamond",
            "moldomre:items/extras/goat_horn_emerald",
            "moldomre:items/extras/goat_horn_gold",
            "moldomre:items/extras/goat_horn_iron",
            "moldomre:items/extras/goat_horn_lapis",
            "moldomre:items/extras/goat_horn_quartz",
            "moldomre:items/extras/goat_horn_redstone",
            "moldomre:items/extras/goat_horn_resin",
            "moldomre:items/extras/locked_barrel",
            "moldomre:items/extras/locked_barrel_big",
            "moldomre:items/extras/locked_chest",
            "moldomre:items/extras/locked_chest_big",
            "moldomre:items/extras/mutated_brown_mushroom",
            "moldomre:items/extras/mutated_crimson_fungus",
            "moldomre:items/extras/mutated_red_mushroom",
            "moldomre:items/extras/mutated_warped_fungus",
            "moldomre:items/extras/small_key",
            "moldomre:items/extras/soul_healer",
            "moldomre:items/extras/vindicator_charm",
            "moldomre:items/food/apple_pie",
            "moldomre:items/food/baked_apple",
            "moldomre:items/food/banana",
            "moldomre:items/food/big_beetroot",
            "moldomre:items/food/candy_cane",
            "moldomre:items/food/chocolate_ice_cream",
            "moldomre:items/food/chocolate_milk",
            "moldomre:items/food/chocolate_pie",
            "moldomre:items/food/crazy_cake",
            "moldomre:items/food/diamond_apple",
            "moldomre:items/food/donut",
            "moldomre:items/food/donut_apple",
            "moldomre:items/food/donut_chocolate_cake",
            "moldomre:items/food/donut_chocolate_iced",
            "moldomre:items/food/donut_cream_filled",
            "moldomre:items/food/donut_glow_berry_filled",
            "moldomre:items/food/donut_sweet_berry_filled",
            "moldomre:items/food/eggnog",
            "moldomre:items/food/elven_cookie",
            "moldomre:items/food/elven_kiss",
            "moldomre:items/food/elven_surprise",
            "moldomre:items/food/emerald_apple",
            "moldomre:items/food/enchanted_diamond_apple",
            "moldomre:items/food/french_fries",
            "moldomre:items/food/fried_egg",
            "moldomre:items/food/gingerbread_man",
            "moldomre:items/food/glow_berry_ice_cream",
            "moldomre:items/food/glow_berry_pie",
            "moldomre:items/food/green_apple",
            "moldomre:items/food/gumbo",
            "moldomre:items/food/herring_pie",
            "moldomre:items/food/honey_candy",
            "moldomre:items/food/hot_chocolate",
            "moldomre:items/food/hot_dog",
            "moldomre:items/food/kelp_roll",
            "moldomre:items/food/mead",
            "moldomre:items/food/milk_bottles",
            "moldomre:items/food/mint",
            "moldomre:items/food/mushroom_omelet",
            "moldomre:items/food/noble_pursuit",
            "moldomre:items/food/oreo",
            "moldomre:items/food/poptorch",
            "moldomre:items/food/pumpkin_soup",
            "moldomre:items/food/rancid_egg",
            "moldomre:items/food/root_beer",
            "moldomre:items/food/salmon_meuniere",
            "moldomre:items/food/shroom_shake",
            "moldomre:items/food/smoked_beef",
            "moldomre:items/food/smoked_chicken",
            "moldomre:items/food/smoked_cod",
            "moldomre:items/food/smoked_mutton",
            "moldomre:items/food/smoked_porkchop",
            "moldomre:items/food/smoked_rabbit",
            "moldomre:items/food/smoked_salmon",
            "moldomre:items/food/steak_burger",
            "moldomre:items/food/steak_skewer",
            "moldomre:items/food/sugar_cookie",
            "moldomre:items/food/sweet_berry_coke",
            "moldomre:items/food/sweet_berry_crepe",
            "moldomre:items/food/sweet_berry_ice_cream",
            "moldomre:items/food/sweet_berry_pie",
            "moldomre:items/food/toast",
            "moldomre:items/food/vanilla_ice_cream",
            "moldomre:items/food/whacka_bump",
            "moldomre:items/gear/amethyst_axe",
            "moldomre:items/gear/amethyst_boots",
            "moldomre:items/gear/amethyst_chestplate",
            "moldomre:items/gear/amethyst_helmet",
            "moldomre:items/gear/amethyst_hoe",
            "moldomre:items/gear/amethyst_leggings",
            "moldomre:items/gear/amethyst_pickaxe",
            "moldomre:items/gear/amethyst_shovel",
            "moldomre:items/gear/amethyst_spear",
            "moldomre:items/gear/amethyst_sword",
            "moldomre:items/gear/emerald_axe",
            "moldomre:items/gear/emerald_boots",
            "moldomre:items/gear/emerald_chestplate",
            "moldomre:items/gear/emerald_helmet",
            "moldomre:items/gear/emerald_hoe",
            "moldomre:items/gear/emerald_leggings",
            "moldomre:items/gear/emerald_pickaxe",
            "moldomre:items/gear/emerald_shovel",
            "moldomre:items/gear/emerald_spear",
            "moldomre:items/gear/emerald_sword",
            "moldomre:items/gear/evocation_boots",
            "moldomre:items/gear/evocation_hat",
            "moldomre:items/gear/evocation_pants",
            "moldomre:items/gear/evocation_robe",
            "moldomre:items/gear/iron_hammer",
            "moldomre:items/gear/lapis_axe",
            "moldomre:items/gear/lapis_boots",
            "moldomre:items/gear/lapis_chestplate",
            "moldomre:items/gear/lapis_helmet",
            "moldomre:items/gear/lapis_hoe",
            "moldomre:items/gear/lapis_leggings",
            "moldomre:items/gear/lapis_pickaxe",
            "moldomre:items/gear/lapis_shovel",
            "moldomre:items/gear/lapis_sword",
            "moldomre:items/gear/nether_star_axe",
            "moldomre:items/gear/nether_star_boots",
            "moldomre:items/gear/nether_star_chestplate",
            "moldomre:items/gear/nether_star_helmet",
            "moldomre:items/gear/nether_star_hoe",
            "moldomre:items/gear/nether_star_leggings",
            "moldomre:items/gear/nether_star_pickaxe",
            "moldomre:items/gear/nether_star_shovel",
            "moldomre:items/gear/nether_star_sword",
            "moldomre:items/gear/quartz_axe",
            "moldomre:items/gear/quartz_boots",
            "moldomre:items/gear/quartz_chestplate",
            "moldomre:items/gear/quartz_helmet",
            "moldomre:items/gear/quartz_hoe",
            "moldomre:items/gear/quartz_leggings",
            "moldomre:items/gear/quartz_pickaxe",
            "moldomre:items/gear/quartz_shovel",
            "moldomre:items/gear/quartz_spear",
            "moldomre:items/gear/quartz_sword",
            "moldomre:items/gear/redstone_axe",
            "moldomre:items/gear/redstone_boots",
            "moldomre:items/gear/redstone_chestplate",
            "moldomre:items/gear/redstone_helmet",
            "moldomre:items/gear/redstone_hoe",
            "moldomre:items/gear/redstone_leggings",
            "moldomre:items/gear/redstone_pickaxe",
            "moldomre:items/gear/redstone_shovel",
            "moldomre:items/gear/redstone_sword",
            "moldomre:items/gear/resin_axe",
            "moldomre:items/gear/resin_boots",
            "moldomre:items/gear/resin_chestplate",
            "moldomre:items/gear/resin_helmet",
            "moldomre:items/gear/resin_hoe",
            "moldomre:items/gear/resin_leggings",
            "moldomre:items/gear/resin_pickaxe",
            "moldomre:items/gear/resin_shovel",
            "moldomre:items/gear/resin_spear",
            "moldomre:items/gear/resin_sword",
            "moldomre:items/gear/turtle_boots",
            "moldomre:items/gear/turtle_carapace",
            "moldomre:items/gear/turtle_kneepads",
            "moldomre:items/hats/armorer_mask",
            "moldomre:items/hats/cartographer_monocle",
            "moldomre:items/hats/elf_hat",
            "moldomre:items/hats/farmer_hat",
            "moldomre:items/hats/link_hat_awakening",
            "moldomre:items/hats/link_hat_hero",
            "moldomre:items/hats/link_hat_time",
            "moldomre:items/hats/party_hat",
            "moldomre:items/hats/santa_hat",
            "moldomre:items/hats/toad_hat_blue",
            "moldomre:items/hats/toad_hat_green",
            "moldomre:items/hats/toad_hat_purple",
            "moldomre:items/hats/toad_hat_red",
            "moldomre:items/hats/toad_hat_yellow",
            "moldomre:items/hats/top_hat_red",
            "moldomre:items/hats/top_hat_white",
            "moldomre:items/hats/winter_hat",
            "moldomre:items/hats/yoshi_hat_black",
            "moldomre:items/hats/yoshi_hat_blue",
            "moldomre:items/hats/yoshi_hat_green",
            "moldomre:items/hats/yoshi_hat_light_blue",
            "moldomre:items/hats/yoshi_hat_orange",
            "moldomre:items/hats/yoshi_hat_pink",
            "moldomre:items/hats/yoshi_hat_purple",
            "moldomre:items/hats/yoshi_hat_red",
            "moldomre:items/hats/yoshi_hat_yellow"
    );

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("zydata")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("open")
                    .executes(context -> open(context.getSource().getPlayerOrException(), 1))
                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> open(
                            context.getSource().getPlayerOrException(),
                            IntegerArgumentType.getInteger(context, "page")
                        ))
                    )
                )
            )
        );
    }

    private static int open(ServerPlayer player, int requestedPage) {
        List<ItemStack> stacks = buildCatalog(player);
        int pages = Math.max(1, (stacks.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.min(requestedPage, pages);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, stacks.size());

        CatalogContainer container = new CatalogContainer(PAGE_SIZE);
        for (int slot = 0; start + slot < end; slot++) {
            container.seed(slot, stacks.get(start + slot));
        }

        Component title = Component.literal("ZyData " + page + "/" + pages);
        player.openMenu(new SimpleMenuProvider(
            (containerId, inventory, ignored) -> ChestMenu.sixRows(containerId, inventory, container),
            title
        ));
        player.sendSystemMessage(Component.literal(
            "ZyData page " + page + " of " + pages + ". Use /zydata open <page> for another page."
        ));
        return 1;
    }

    private static List<ItemStack> buildCatalog(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        List<ItemStack> result = new ArrayList<>();

        LootParams params = new LootParams.Builder(player.serverLevel())
            .withParameter(LootContextParams.ORIGIN, player.position())
            .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
            .create(LootContextParamSets.CHEST);

        for (String id : LOOT_TABLES) {
            try {
                ResourceLocation location = ResourceLocation.parse(id);
                ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, location);
                LootTable table = server.reloadableRegistries().getLootTable(key);
                List<ItemStack> generated = table.getRandomItems(params);
                if (!generated.isEmpty()) {
                    ItemStack stack = generated.getFirst().copy();
                    stack.setCount(1);
                    result.add(stack);
                }
            } catch (Exception exception) {
                System.err.println("[ZyData] Could not load " + id + ": " + exception.getMessage());
            }
        }
        return result;
    }

    private static final class CatalogContainer extends SimpleContainer {
        CatalogContainer(int size) {
            super(size);
        }

        void seed(int slot, ItemStack stack) {
            super.setItem(slot, stack.copy());
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack shown = getItem(slot);
            if (shown.isEmpty()) return ItemStack.EMPTY;
            ItemStack copy = shown.copy();
            copy.setCount(Math.min(amount, shown.getMaxStackSize()));
            return copy;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack shown = getItem(slot);
            return shown.isEmpty() ? ItemStack.EMPTY : shown.copy();
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            // The catalog is read-only. Items are copied out, never moved in.
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }
}
