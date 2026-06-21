package com.zycu.zydata;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ZyData implements ModInitializer {
    private static final int CONTENT_SLOTS = 45;
    private static final int PREVIOUS_SLOT = 45;
    private static final int INFO_SLOT = 49;
    private static final int NEXT_SLOT = 53;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("zydata")
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
        List<ItemStack> stacks = discoverDatapackItems(player);
        int pages = Math.max(1, (stacks.size() + CONTENT_SLOTS - 1) / CONTENT_SLOTS);
        int page = Math.max(1, Math.min(requestedPage, pages));

        CatalogContainer container = new CatalogContainer(54);
        int start = (page - 1) * CONTENT_SLOTS;
        int end = Math.min(start + CONTENT_SLOTS, stacks.size());

        for (int slot = 0; start + slot < end; slot++) {
            container.seed(slot, stacks.get(start + slot));
        }

        if (page > 1) {
            container.seed(PREVIOUS_SLOT, named(new ItemStack(Items.ARROW), "Previous Page"));
        }

        container.seed(INFO_SLOT, named(
            new ItemStack(Items.PAPER),
            "Page " + page + " of " + pages + " • " + stacks.size() + " items"
        ));

        if (page < pages) {
            container.seed(NEXT_SLOT, named(new ItemStack(Items.ARROW), "Next Page"));
        }

        Component title = Component.literal("ZyData " + page + "/" + pages);
        player.openMenu(new SimpleMenuProvider(
            (containerId, inventory, ignored) -> new CatalogMenu(
                containerId,
                inventory,
                container,
                player,
                page,
                pages
            ),
            title
        ));

        return 1;
    }

    private static ItemStack named(ItemStack stack, String name) {
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }

    private static List<ItemStack> discoverDatapackItems(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        ServerLevel level = (ServerLevel) player.level();
        LootParams params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, player.position())
            .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
            .create(LootContextParamSets.CHEST);

        Map<String, ItemStack> unique = new LinkedHashMap<>();

        Map<Identifier, Resource> resources = server.getResourceManager().listResources(
            "loot_table",
            id -> id.getPath().endsWith(".json") && looksLikeItemTable(id.getPath())
        );

        List<Identifier> sortedResources = new ArrayList<>(resources.keySet());
        sortedResources.sort(Comparator.comparing(Identifier::toString));

        for (Identifier resourceId : sortedResources) {
            try {
                String path = resourceId.getPath();
                String tablePath = path.substring("loot_table/".length(), path.length() - ".json".length());
                Identifier tableId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), tablePath);
                ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, tableId);
                LootTable table = server.reloadableRegistries().getLootTable(key);

                for (ItemStack generated : table.getRandomItems(params)) {
                    if (generated.isEmpty()) {
                        continue;
                    }

                    ItemStack stack = generated.copy();
                    stack.setCount(1);
                    unique.putIfAbsent(stackKey(stack), stack);
                }
            } catch (Exception exception) {
                System.err.println("[ZyData] Skipped loot table " + resourceId + ": " + exception.getMessage());
            }
        }

        return new ArrayList<>(unique.values());
    }

    private static boolean looksLikeItemTable(String path) {
        String lower = path.toLowerCase();
        return lower.contains("/items/")
            || lower.contains("/item/")
            || lower.contains("/gear/")
            || lower.contains("/food/")
            || lower.contains("/hats/")
            || lower.contains("/weapons/")
            || lower.contains("/tools/")
            || lower.contains("/armor/")
            || lower.contains("/extras/");
    }

    private static String stackKey(ItemStack stack) {
        return stack.getItem().toString() + "|" + stack.getComponents().toString();
    }

    private static final class CatalogMenu extends ChestMenu {
        private final ServerPlayer owner;
        private final int page;
        private final int pages;

        CatalogMenu(
            int containerId,
            Inventory inventory,
            CatalogContainer container,
            ServerPlayer owner,
            int page,
            int pages
        ) {
            super(MenuType.GENERIC_9x6, containerId, inventory, container, 6);
            this.owner = owner;
            this.page = page;
            this.pages = pages;
        }

        @Override
        public void clicked(int slotId, int button, ClickType clickType, Player player) {
            if (slotId == PREVIOUS_SLOT && page > 1) {
                owner.closeContainer();
                open(owner, page - 1);
                return;
            }

            if (slotId == NEXT_SLOT && page < pages) {
                owner.closeContainer();
                open(owner, page + 1);
                return;
            }

            if (slotId == INFO_SLOT) {
                return;
            }

            if (slotId >= 0 && slotId < CONTENT_SLOTS) {
                ItemStack shown = getSlot(slotId).getItem();
                if (!shown.isEmpty()) {
                    ItemStack copy = shown.copy();
                    copy.setCount(Math.max(1, copy.getMaxStackSize()));
                    if (!owner.getInventory().add(copy)) {
                        owner.drop(copy, false);
                    }
                }
                return;
            }

            super.clicked(slotId, button, clickType, player);
        }
    }

    private static final class CatalogContainer extends SimpleContainer {
        CatalogContainer(int size) {
            super(size);
        }

        void seed(int slot, ItemStack stack) {
            super.setItem(slot, stack.copy());
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }
}
