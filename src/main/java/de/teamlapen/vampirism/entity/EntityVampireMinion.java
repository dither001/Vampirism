package de.teamlapen.vampirism.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendLord;
import de.teamlapen.vampirism.entity.ai.EntityAIFollowBoss;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class EntityVampireMinion extends DefaultVampire implements IMinion, ISyncable {

	private final static int MAX_SEARCH_TIME = 100;
	private UUID lordId = null;
	protected IMinionLord lord;
	private int lookForLordTimer = 0;
	/**
	 * Used for the visual transition from normal vampire to players minion
	 */
	private int oldVampireTexture=-1;

	public int getOldVampireTexture() {
		return oldVampireTexture;
	}

	public void setOldVampireTexture(int oldVampireTexture) {
		this.oldVampireTexture = oldVampireTexture;
	}

	public EntityVampireMinion(World world) {
		super(world);

		this.tasks.addTask(4, new EntityAIFollowBoss(this, 1.0D));
		this.tasks.addTask(5, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
		this.targetTasks.addTask(2, new EntityAIDefendLord(this));

		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (lord != null && lord.getRepresentingEntity().equals(entity)) {
					return false;
				}
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= BALANCE.VAMPIRE_FRIENDLY_LEVEL || VampirePlayer.get((EntityPlayer) entity).isVampireLord();
				}
				return false;
			}

		}));
		// Search for villagers
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityVillager) {
					return !VampireMob.get((EntityVillager) entity).isVampire();
				}
				return false;
			}

		}));

		this.targetTasks.addTask(8, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MAX_HEALTH / 1.5D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_ATTACK_DAMAGE / 1.5D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MOVEMENT_SPEED / 1.2D);
	}

	@Override
	public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_) {
		float i = 0.5F - this.worldObj.getLightBrightness(p_70783_1_, p_70783_2_, p_70783_3_);
		if (i > 0)
			return i;
		return 0.01F;
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		if (lord != null) {
			nbt.setInteger("eid", lord.getRepresentingEntity().getEntityId());
		}
		nbt.setInteger("oldvampire", oldVampireTexture);
	}

	@Override
	public IMinionLord getLord() {
		return lord;
	}

	@Override
	public EntityCreature getRepresentingEntity() {
		return this;
	}

	@Override
	public boolean isChild() {
		return true;
	}

	/**
	 * Vampire minions have no right to complain about sun damage ;)
	 */
	@Override
	public boolean isValidLightLevel() {
		return true;
	}

	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("eid")) {
			Entity e = worldObj.getEntityByID(nbt.getInteger("eid"));
			if (e instanceof EntityPlayer) {
				this.lord = VampirePlayer.get((EntityPlayer) e);
			} else if (e instanceof IMinionLord) {
				this.lord = (IMinionLord) e;
			} else {
				Logger.w("EntityVampireMinion", "PartialUpdate: The given id(" + nbt.getInteger("eid") + ")[" + e + "] is no Minion Lord");
				return;
			}
			this.lordId = this.lord.getThePersistentID();
		}
		if(nbt.hasKey("oldvampire")){
			this.oldVampireTexture=nbt.getInteger("oldvampire");
		}

	}

	protected void lookForLord() {
		List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(15, 10, 15));
		for (EntityLivingBase e : list) {
			if (e.getPersistentID().equals(lordId)) {
				if (e instanceof IMinionLord) {
					this.setLord((IMinionLord) e);
					lookForLordTimer = 0;
				} else if (e instanceof EntityPlayer) {
					this.setLord(VampirePlayer.get((EntityPlayer) e));
					lookForLordTimer = 0;
				} else {
					Logger.w("VampireMinion", "Found lord with UUID " + lordId + " but it isn't a Minion Lord");
					lordId = null;
					lord = null;
					lookForLordTimer = 0;
				}

				break;
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			if (lord == null) {
				if (lordId != null) {
					lookForLord();
				}

				if (lord == null) {
					lookForLordTimer++;
				}
				if (lookForLordTimer > MAX_SEARCH_TIME) {
					this.attackEntityFrom(DamageSource.generic, 5);
				}
			} else if (!lord.isTheEntityAlive()) {
				lord = null;
				lordId = null;
			} else if (lord.getTheDistanceSquared(this) > 1000) {
				if (this.rand.nextInt(80) == 0) {
					this.attackEntityFrom(DamageSource.generic, 3);
				}
			}

		}
		if(oldVampireTexture!=-1&&this.ticksExisted>50){
			oldVampireTexture=-1;
		}
		if(oldVampireTexture!=-1&&worldObj.isRemote){
			Helper.spawnParticlesAroundEntity(this, "witchMagic", 1.0F, 3);
		}
		super.onLivingUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("BossUUIDMost")) {
			this.lordId = new UUID(nbt.getLong("BossUUIDMost"), nbt.getLong("BossUUIDLeast"));
		}
	}

	private void setLordId(UUID id) {
		if (!id.equals(lordId)) {
			lordId = id;
			lord = null;
			lookForLordTimer = 0;
		}
	}

	@Override
	public void setLord(IMinionLord b) {
		if (!b.equals(lord)) {
			this.setLordId(b.getThePersistentID());
			b.getMinionHandler().registerMinion(this, true);
			lord = b;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.lordId != null) {
			nbt.setLong("BossUUIDMost", this.lordId.getMostSignificantBits());
			nbt.setLong("BossUUIDLeast", this.lordId.getLeastSignificantBits());
		}

	}

}