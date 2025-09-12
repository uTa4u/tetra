package se.mickelus.tetra.blocks.hammer;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.EnumUtils;
import se.mickelus.tetra.items.cell.ItemCellMagmatic;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;

public class TileEntityHammerBase extends TileEntity {
    private static final String SLOTS_KEY = "slots";
    private static final String INDEX_KEY = "slot";
    private final ItemStack[] slots;

    private boolean hasPlateWest = true;
    private boolean hasPlateEast = true;

    private HammerConfig configEast = HammerConfig.A;
    private HammerConfig configWest = HammerConfig.A;

    public TileEntityHammerBase() {
        slots = new ItemStack[2];
    }

    public boolean hasEffect(HammerEffect effect) {
        if (effect.requiresBoth) {
            return effect.equals(HammerEffect.fromConfig(configEast, getWorld().getSeed()))
                    && effect.equals(HammerEffect.fromConfig(configWest, getWorld().getSeed()));
        }
        return effect.equals(HammerEffect.fromConfig(configEast, getWorld().getSeed()))
                || effect.equals(HammerEffect.fromConfig(configWest, getWorld().getSeed()));
    }

    public int getHammerLevel() {
        return hasEffect(HammerEffect.OVERCHARGED) ? 5 : 4;
    }

    public boolean isFueled() {
        for (int i = 0; i < slots.length; i++) {
            if (getCellFuel(i) <= 0) {
                return false;
            }
        }
        return true;
    }

    public void consumeFuel() {
        int fuelUsage = fuelUsage();

        for (int i = 0; i < slots.length; i++) {
            consumeFuel(i, fuelUsage);
        }

        applyConsumeEffect();
    }

    public void consumeFuel(int index, int amount) {
        if (index >= 0 && index < slots.length && slots[index] != null && slots[index].getItem() instanceof ItemCellMagmatic) {
            ItemCellMagmatic item = (ItemCellMagmatic) slots[index].getItem();
            item.drainCharge(slots[index], amount);
        }
    }

    private void applyConsumeEffect() {
        EnumFacing facing = getWorld().getBlockState(getPos()).getValue(BlockHammerBase.FACING);
        Vec3d pos = new Vec3d(getPos());
        pos = pos.add(0.5, 0.5, 0.5);

        if (!world.isRemote && hasEffect(HammerEffect.LEAKY)) {
            int countCell0 = world.rand.nextInt(Math.min(16, getCellFuel(0)));
            int countCell1 = world.rand.nextInt(Math.min(16, getCellFuel(1)));
            consumeFuel(0, countCell0);
            consumeFuel(1, countCell1);

            if (countCell0 > 0 || countCell1 > 0) {

                // particles cell 1
                Vec3d posCell0 = pos.add(new Vec3d(facing.getDirectionVec()).scale(0.55));
                spawnParticle(EnumParticleTypes.LAVA, posCell0, countCell0 * 2, 0.06f);
                spawnParticle(EnumParticleTypes.SMOKE_LARGE, posCell0, 2, 0f);

                // particles cell 2
                Vec3d posCell1 = pos.add(new Vec3d(facing.getOpposite().getDirectionVec()).scale(0.55));
                spawnParticle(EnumParticleTypes.LAVA, posCell1, countCell1 * 2, 0.06f);
                spawnParticle(EnumParticleTypes.SMOKE_LARGE, posCell1, 2, 0f);

                // gather flammable blocks
                LinkedList<BlockPos> flammableBlocks = new LinkedList<>();
                for (int x = -3; x < 3; x++) {
                    for (int y = -3; y < 2; y++) {
                        for (int z = -3; z < 3; z++) {
                            BlockPos firePos = getPos().add(x, y, z);
                            if (world.isAirBlock(firePos)) {
                                flammableBlocks.add(firePos);
                            }
                        }
                    }
                }

                // set blocks on fire
                Collections.shuffle(flammableBlocks);
                flammableBlocks.stream()
                        .limit(countCell0 + countCell1)
                        .forEach(blockPos -> world.setBlockState(blockPos, Blocks.FIRE.getDefaultState(), 11));
            }
        }
    }

    private int fuelUsage() {
        int usage = 5;
        if (!hasPlateEast) {
            usage += 2;
        }
        if (!hasPlateWest) {
            usage += 2;
        }

        if (hasEffect(HammerEffect.OVERCHARGED)) {
            usage += 4;
        }

        if (hasEffect(HammerEffect.EFFICIENT)) {
            usage -= 3;
        }

        return Math.max(usage, 1);
    }

    public boolean hasCellInSlot(int index) {
        return index >= 0 && index < slots.length && slots[index] != null;
    }

    public int getCellFuel(int index) {
        if (index >= 0 && index < slots.length && slots[index] != null) {
            if (slots[index].getItem() instanceof ItemCellMagmatic) {
                ItemCellMagmatic item = (ItemCellMagmatic) slots[index].getItem();
                return item.getCharge(slots[index]);
            }
        }
        return -1;
    }

    public ItemStack removeCellFromSlot(int index) {
        if (index >= 0 && index < slots.length && slots[index] != null) {
            ItemStack itemStack = slots[index];
            slots[index] = null;
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public boolean putCellInSlot(ItemStack itemStack, int index) {
        if (itemStack.getItem() instanceof ItemCellMagmatic
                && index >= 0 && index < slots.length && slots[index] == null) {
            slots[index] = itemStack;
            return true;
        }
        return false;
    }

    public void removePlate(HammerPlate plate) {
        switch (plate) {
            case EAST:
                hasPlateEast = false;
                break;
            case WEST:
                hasPlateWest = false;
                break;
        }
        markDirty();
    }

    public void attachPlate(HammerPlate plate) {
        switch (plate) {
            case EAST:
                hasPlateEast = true;
                break;
            case WEST:
                hasPlateWest = true;
                break;
        }
        markDirty();
    }

    public boolean hasPlate(HammerPlate plate) {
        switch (plate) {
            case EAST:
                return hasPlateEast;
            case WEST:
                return hasPlateWest;
        }
        return false;
    }

    public void reconfigure(EnumFacing side) {
        if (EnumFacing.EAST.equals(side)) {
            configEast = HammerConfig.getNextConfiguration(configEast);
            applyReconfigurationEffect(HammerEffect.fromConfig(configEast, world.getSeed()));
        } else if (EnumFacing.WEST.equals(side)) {
            configWest = HammerConfig.getNextConfiguration(configWest);
            applyReconfigurationEffect(HammerEffect.fromConfig(configWest, world.getSeed()));
        }
        markDirty();
    }

    private void applyReconfigurationEffect(HammerEffect effect) {
        EnumFacing facing = getWorld().getBlockState(getPos()).getValue(BlockHammerBase.FACING);
        Vec3d pos = new Vec3d(getPos());
        pos = pos.add(0.5, 0.5, 0.5);

        if (HammerEffect.OVERCHARGED.equals(effect)) {
            if (!hasCellInSlot(0)) {
                Vec3d rotPos = pos.add(new Vec3d(facing.getDirectionVec()).scale(0.55));
                spawnParticle(EnumParticleTypes.SMOKE_NORMAL, rotPos, 15, 0.02f);
            }

            if (!hasCellInSlot(1)) {
                Vec3d rotPos = pos.add(new Vec3d(facing.getOpposite().getDirectionVec()).scale(0.55));
                spawnParticle(EnumParticleTypes.SMOKE_NORMAL, rotPos, 15, 0.02f);
            }
        }

        if (HammerEffect.LEAKY.equals(effect)) {
            if (getCellFuel(0) > 0) {
                Vec3d rotPos = pos.add(new Vec3d(facing.getDirectionVec()).scale(0.55));
                spawnParticle(EnumParticleTypes.LAVA, rotPos, 3, 0.06f);
                spawnParticle(EnumParticleTypes.SMOKE_LARGE, rotPos, 3, 0f);
            }

            if (getCellFuel(1) > 0) {
                Vec3d rotPos = pos.add(new Vec3d(facing.getOpposite().getDirectionVec()).scale(0.55));
                spawnParticle(EnumParticleTypes.LAVA, rotPos, 3, 0.06f);
                spawnParticle(EnumParticleTypes.SMOKE_LARGE, rotPos, 3, 0f);
            }
        }
    }

    public HammerConfig getConfiguration(EnumFacing side) {
        if (EnumFacing.EAST.equals(side)) {
            return configEast;
        } else if (EnumFacing.WEST.equals(side)) {
            return configWest;
        }

        return HammerConfig.A;
    }

    /**
     * Utility for spawning particles from the server
     */
    private void spawnParticle(EnumParticleTypes particle, Vec3d pos, int count, float speed) {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(particle, pos.x, pos.y, pos.z, count, 0, 0, 0, speed);
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(SLOTS_KEY)) {
            NBTTagList tagList = compound.getTagList(SLOTS_KEY, 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
                int slot = nbttagcompound.getByte(INDEX_KEY) & 255;

                if (slot < this.slots.length) {
                    this.slots[slot] = new ItemStack(nbttagcompound);
                }
            }
        }

        hasPlateEast = compound.getBoolean(HammerPlate.EAST.key);
        hasPlateWest = compound.getBoolean(HammerPlate.WEST.key);

        if (compound.hasKey(HammerConfig.propE.getName())) {
            String enumName = compound.getString(HammerConfig.propE.getName());
            if (EnumUtils.isValidEnum(HammerConfig.class, enumName)) {
                configEast = HammerConfig.valueOf(enumName);
            }
        }

        if (compound.hasKey(HammerConfig.propW.getName())) {
            String enumName = compound.getString(HammerConfig.propW.getName());
            if (EnumUtils.isValidEnum(HammerConfig.class, enumName)) {
                configWest = HammerConfig.valueOf(enumName);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        writeCells(compound, slots);

        writePlate(compound, HammerPlate.EAST, hasPlateEast);
        writePlate(compound, HammerPlate.WEST, hasPlateWest);

        writeConfig(compound, configEast, configWest);

        return compound;
    }

    public static void writeCells(NBTTagCompound compound, ItemStack... cells) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setByte(INDEX_KEY, (byte) i);
                cells[i].writeToNBT(nbttagcompound);

                nbttaglist.appendTag(nbttagcompound);
            }
        }
        compound.setTag(SLOTS_KEY, nbttaglist);
    }

    public static void writePlate(NBTTagCompound compound, HammerPlate plate, boolean hasPlate) {
        compound.setBoolean(plate.key, hasPlate);
    }

    public static void writeConfig(NBTTagCompound compound, HammerConfig configEast, HammerConfig configWest) {
        compound.setString(HammerConfig.propE.getName(), configEast.toString());
        compound.setString(HammerConfig.propW.getName(), configWest.toString());
    }

}
