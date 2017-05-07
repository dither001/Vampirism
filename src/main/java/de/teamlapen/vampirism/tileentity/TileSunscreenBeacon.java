package de.teamlapen.vampirism.tileentity;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class TileSunscreenBeacon extends TileEntity implements ITickable {

    private BlockPos oldPos;
    private Predicate<EntityPlayer> selector;

    @Override
    public void update() {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
            this.updateBeacon();
        }
    }

    private void updateBeacon() {

        if (this.worldObj != null && !this.worldObj.isRemote) {
            //Position check is probably not necessary, but not sure
            if (oldPos == null || selector == null || !oldPos.equals(this.pos)) {
                oldPos = this.pos;
                final BlockPos center = new BlockPos(this.pos.getX(), 0, this.pos.getZ());
                final int distSq = Configs.sunscreen_beacon_distance * Configs.sunscreen_beacon_distance;
                selector = new Predicate<EntityPlayer>() {
                    @Override
                    public boolean apply(@Nullable EntityPlayer input) {
                        if (input == null) return false;
                        BlockPos player = new BlockPos(input.posX, 0, input.posZ);
                        return player.distanceSq(center) < distSq;
                    }
                };
            }

            List<EntityPlayer> list = this.worldObj.getPlayers(EntityPlayer.class, selector);

            for (EntityPlayer entityplayer : list) {
                if (VampirePlayer.get(entityplayer).getLevel() > 0) {
                    entityplayer.addPotionEffect(new PotionEffect(ModPotions.sunscreen, 160, 5, true, false));
                }
            }
        }
    }
}