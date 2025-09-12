package se.mickelus.tetra.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.capabilities.Capability;

public class ImprovementCraftCriterion extends AbstractCriterionInstance {
    public static final GenericTrigger<ImprovementCraftCriterion> TRIGGER = new GenericTrigger<>("tetra:craft_improvement", ImprovementCraftCriterion::deserialize);

    private final ItemPredicate before = null;
    private final ItemPredicate after = null;

    private final String schema = null;

    private final String slot = null;
    private final String improvement = null;
    private final int improvementLevel = -1;

    private final Capability capability = null;
    private final int capabilityLevel = -1;

    public ImprovementCraftCriterion() {
        super(TRIGGER.getId());
    }

    public static void trigger(EntityPlayerMP player, ItemStack before, ItemStack after, String schema, String slot, String improvement,
                               int improvementLevel, Capability capability, int capabilityLevel) {
        TRIGGER.fulfillCriterion(player.getAdvancements(), criterion -> criterion.test(before, after, schema, slot, improvement, improvementLevel,
                capability, capabilityLevel));
    }

    // TODO: wtf is this
    public boolean test(ItemStack before, ItemStack after, String schema, String slot, String improvement, int improvementLevel,
                        Capability capability, int capabilityLevel) {

        if (this.before != null && !this.before.test(before)) {
            return false;
        }

        if (this.after != null && !this.after.test(after)) {
            return false;
        }

        if (this.schema != null && !this.schema.equals(schema)) {
            return false;
        }

        if (this.slot != null && !this.slot.equals(slot)) {
            return false;
        }

        if (this.improvement != null && !this.improvement.equals(improvement)) {
            return false;
        }

        if (this.improvementLevel != -1 && this.improvementLevel != improvementLevel) {
            return false;
        }

        if (this.capability != null && !this.capability.equals(capability)) {
            return false;
        }

        return this.capabilityLevel == -1 || this.capabilityLevel == capabilityLevel;
    }

    private static ImprovementCraftCriterion deserialize(JsonObject json) {
        return TetraMod.dataHandler.gson.fromJson(json, ImprovementCraftCriterion.class);
    }
}
