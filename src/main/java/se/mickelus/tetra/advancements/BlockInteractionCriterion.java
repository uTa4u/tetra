package se.mickelus.tetra.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.PropertyMatcher;
import se.mickelus.tetra.capabilities.Capability;

public class BlockInteractionCriterion extends AbstractCriterionInstance {
    private final PropertyMatcher after = null;
    private final Capability capability = null;
    private final int capabilityLevel = -1;

    public static final GenericTrigger<BlockInteractionCriterion> TRIGGER = new GenericTrigger<>("tetra:block_interaction", BlockInteractionCriterion::deserialize);

    public BlockInteractionCriterion() {
        super(TRIGGER.getId());
    }

    public static void trigger(EntityPlayerMP player, IBlockState state, Capability usedCapability, int usedCapabilityLevel) {
        TRIGGER.fulfillCriterion(player.getAdvancements(), criterion -> criterion.test(state, usedCapability, usedCapabilityLevel));
    }

    // TODO: wtf is this
    public boolean test(IBlockState state, Capability usedCapability, int usedCapabilityLevel) {
        if (after != null && !after.test(state)) {
            return false;
        }

        if (this.capability != null && !this.capability.equals(usedCapability)) {
            return false;
        }

        return this.capabilityLevel == -1 || this.capabilityLevel == usedCapabilityLevel;
    }

    private static BlockInteractionCriterion deserialize(JsonObject json) {
        return TetraMod.dataHandler.gson.fromJson(json, BlockInteractionCriterion.class);
    }
}
