package se.mickelus.tetra.module.schema;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.capabilities.Capability;
import se.mickelus.tetra.module.data.GlyphData;

import javax.annotation.Nullable;
import java.util.Collection;

public interface UpgradeSchema {

    String getKey();

    String getName();

    String getDescription(@Nullable ItemStack itemStack);

    int getNumMaterialSlots();

    String getSlotName(final ItemStack itemStack, int index);

    int getRequiredQuantity(final ItemStack itemStack, int index, ItemStack materialStack);

    /**
     * Returns true if the provided materialStack is accepted in the slot at the given index, for the given upgrade
     * target.
     * A slot may accept a material without a craft actually being possible using the material.
     *
     * @param itemStack     the itemstack that is to be upgraded
     * @param index         the index of the slot
     * @param materialStack the upgrade material
     * @return
     */
    boolean acceptsMaterial(ItemStack itemStack, int index, ItemStack materialStack);

    /**
     * Returns true if all material slot contain valid material of enough quantity. Craft is possible using the provided
     * materials.
     *
     * @param itemStack the itemstack that is to be upgraded
     * @param materials the materials to be used for the upgrade
     * @return
     */
    boolean isMaterialsValid(ItemStack itemStack, ItemStack[] materials);

    /**
     * Returns true if this upgrade can be applied to the given item.
     *
     * @param itemStack the itemstack that is to be upgraded
     * @return
     */
    boolean isApplicableForItem(ItemStack itemStack);

    /**
     * Returns true if this upgrade can be applied to the given slot on the given item.
     *
     * @param slot      the slot on which the schema will be applied
     * @param itemStack the itemstack that is to be upgraded
     * @return
     */
    boolean isApplicableForSlot(String slot, ItemStack itemStack);

    /**
     * This is a final check if the player should be able to see the schema in schema listings, based on the player or it's surroundings.
     *
     * @param player      The player
     * @param targetStack The target itemstack for the schema
     * @return true if it should be visible, otherwise false
     */
    default boolean isVisibleForPlayer(EntityPlayer player, ItemStack targetStack) {
        return true;
    }

    /**
     * Returns true if all criterias are met (e.g. correct materials & tools) and the upgrade can be performed.
     *
     * @param player                the player performing the upgrade
     * @param itemStack             the itemstack that is to be upgraded
     * @param materials             the materials to be used for the upgrade
     * @param slot                  the slot on which the schema will be applied
     * @param availableCapabilities
     * @return
     */
    boolean canApplyUpgrade(EntityPlayer player, ItemStack itemStack, ItemStack[] materials, String slot, int[] availableCapabilities);

    boolean isIntegrityViolation(EntityPlayer player, ItemStack itemStack, ItemStack[] materials, String slot);

    ItemStack applyUpgrade(ItemStack itemStack, ItemStack[] materials, boolean consumeMaterials, String slot, EntityPlayer player);

    boolean checkCapabilities(final ItemStack targetStack, final ItemStack[] materials, int[] availableCapabilities);

    Collection<Capability> getRequiredCapabilities(final ItemStack targetStack, final ItemStack[] materials);

    int getRequiredCapabilityLevel(final ItemStack targetStack, final ItemStack[] materials, Capability capability);

    default int getExperienceCost(final ItemStack targetStack, final ItemStack[] materials, String slot) {
        return 0;
    }

    /**
     * Returns true if this is a honing schema and should have it's usage regulated by honing rules.
     *
     * @return
     */
    default boolean isHoning() {
        return false;
    }

    SchemaType getType();

    default SchemaRarity getRarity() {
        return SchemaRarity.BASIC;
    }

    GlyphData getGlyph();

    OutcomePreview[] getPreviews(ItemStack targetStack, String slot);
}
