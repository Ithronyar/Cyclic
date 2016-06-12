package com.lothrazar.cyclicmagic.event.core;

import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.registry.CapabilityRegistry;
import com.lothrazar.cyclicmagic.registry.CapabilityRegistry.IPlayerExtendedProperties;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventPlayerData{

	// send from both events to avoid NULL player; known issue due to threading race conditions
	// https://github.com/MinecraftForge/MinecraftForge/issues/1583
	@SubscribeEvent
    public void onSpawn(PlayerLoggedInEvent event){
		if(event.player instanceof EntityPlayerMP && event.player.worldObj.isRemote == false){

			EntityPlayerMP p = (EntityPlayerMP)event.player;
			if(p != null){
				CapabilityRegistry.syncServerDataToClient(p);
			}
		}
	}
	@SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof EntityPlayerMP && event.getEntity().worldObj.isRemote == false){
			EntityPlayerMP p = (EntityPlayerMP)event.getEntity();

			if(p != null){
				CapabilityRegistry.syncServerDataToClient(p);
			}
		}
    }
	
    @SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent evt)
    {
        evt.addCapability(new ResourceLocation(Const.MODID, "IModdedSleeping"), new ICapabilitySerializable<NBTTagCompound>()
        {
            IPlayerExtendedProperties inst = ModMain.CAPABILITYSTORAGE.getDefaultInstance();
            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                return capability == ModMain.CAPABILITYSTORAGE;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return capability == ModMain.CAPABILITYSTORAGE ? ModMain.CAPABILITYSTORAGE.<T>cast(inst) : null;
            }

            @Override
            public NBTTagCompound serializeNBT() {
            	try{
                return (NBTTagCompound)ModMain.CAPABILITYSTORAGE.getStorage().writeNBT(ModMain.CAPABILITYSTORAGE, inst, null);
            	}catch(java.lang.ClassCastException e){
            		return new NBTTagCompound();
            	}
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
            	ModMain.CAPABILITYSTORAGE.getStorage().readNBT(ModMain.CAPABILITYSTORAGE, inst, null, nbt);
            }
        });
    }
}