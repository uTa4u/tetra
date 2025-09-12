package se.mickelus.tetra.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.PropertyMatcher;

public class BlockUseCriterion extends AbstractCriterionInstance {
    public static final GenericTrigger<BlockUseCriterion> TRIGGER = new GenericTrigger<>("tetra:block_use", BlockUseCriterion::deserialize);

    private final PropertyMatcher before = null;
    private final PropertyMatcher after = null;

    private final ItemPredicate item = null;

    public BlockUseCriterion() {
        super(TRIGGER.getId());
    }

    public static void trigger(EntityPlayerMP player, IBlockState state, ItemStack usedItem) {
        TRIGGER.fulfillCriterion(player.getAdvancements(), criterion -> criterion.test(state, usedItem));
    }

    // TODO: wtf is this
    public boolean test(IBlockState state, ItemStack usedItem) {
        if (before != null && !before.test(state)) {
            return false;
        }

        if (after != null && !after.test(state)) {
            return false;
        }

        return item == null || item.test(usedItem);
    }

    private static BlockUseCriterion deserialize(JsonObject json) {
        return TetraMod.dataHandler.gson.fromJson(json, BlockUseCriterion.class);
    }
}
