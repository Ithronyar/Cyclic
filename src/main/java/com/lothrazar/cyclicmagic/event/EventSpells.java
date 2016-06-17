package com.lothrazar.cyclicmagic.event;import com.lothrazar.cyclicmagic.ModMain;import com.lothrazar.cyclicmagic.gui.wand.InventoryWand;import com.lothrazar.cyclicmagic.item.ItemCyclicWand;import com.lothrazar.cyclicmagic.net.PacketSpellShiftLeft;import com.lothrazar.cyclicmagic.net.PacketSpellShiftRight;import com.lothrazar.cyclicmagic.registry.CapabilityRegistry;import com.lothrazar.cyclicmagic.registry.SoundRegistry;import com.lothrazar.cyclicmagic.registry.SpellRegistry;import com.lothrazar.cyclicmagic.registry.CapabilityRegistry.IPlayerExtendedProperties;import com.lothrazar.cyclicmagic.spell.ISpell;import com.lothrazar.cyclicmagic.util.UtilSound;import com.lothrazar.cyclicmagic.util.UtilSpellCaster;import com.lothrazar.cyclicmagic.util.UtilTextureRender;import net.minecraft.client.Minecraft;import net.minecraft.client.gui.ScaledResolution;import net.minecraft.client.renderer.GlStateManager;import net.minecraft.client.renderer.RenderHelper;import net.minecraft.client.renderer.RenderItem;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.item.ItemStack;import net.minecraftforge.client.event.MouseEvent;import net.minecraftforge.client.event.RenderGameOverlayEvent;import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;import net.minecraftforge.fml.common.eventhandler.EventPriority;import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;import net.minecraftforge.fml.relauncher.Side;import net.minecraftforge.fml.relauncher.SideOnly;public class EventSpells{	public static SpellHud spellHud;	public EventSpells() {		spellHud = new SpellHud();	}	@SideOnly(Side.CLIENT)	@SubscribeEvent	public void onMouseInput(MouseEvent event) { 		// DO NOT use InputEvent.MouseInputEvent		EntityPlayer player = Minecraft.getMinecraft().thePlayer;		if(!player.isSneaking() || event.getDwheel() == 0){			return;		}		ItemStack wand = UtilSpellCaster.getPlayerWandIfHeld(player);		// special new case: no hud for this type		if (wand == null) { 			return;		} 		if(SpellRegistry.getSpellbook(wand) == null || SpellRegistry.getSpellbook(wand).size() <= 1){			return;//if theres only one spell, do nothing		} 		if (event.getDwheel() < 0) {			ModMain.network.sendToServer(new PacketSpellShiftRight());			event.setCanceled(true);			UtilSound.playSound(player, player.getPosition(), SoundRegistry.bip);		} else if (event.getDwheel() > 0) {			ModMain.network.sendToServer(new PacketSpellShiftLeft());			event.setCanceled(true);			UtilSound.playSound(player, player.getPosition(), SoundRegistry.bip);		}	}	@SideOnly(Side.CLIENT)	@SubscribeEvent	public void onRenderTextOverlay(RenderGameOverlayEvent.Text event) {		IPlayerExtendedProperties props = CapabilityRegistry.getPlayerProperties(Minecraft.getMinecraft().thePlayer);		if(props.getTODO() != null && props.getTODO().length() > 0){			event.getRight().add(props.getTODO());		}				ItemStack wand = UtilSpellCaster.getPlayerWandIfHeld(Minecraft.getMinecraft().thePlayer);		// special new case: no hud for this type		if (wand != null) {			spellHud.drawSpellWheel(wand);		}	}	@SideOnly(Side.CLIENT)	@SubscribeEvent(priority = EventPriority.LOWEST)	public void onRender(RenderGameOverlayEvent.Post event) {		if (event.isCanceled() || event.getType() != ElementType.EXPERIENCE){			return;		}		EntityPlayer effectivePlayer = Minecraft.getMinecraft().thePlayer;		ItemStack heldWand = UtilSpellCaster.getPlayerWandIfHeld(effectivePlayer);		if (heldWand == null) { return; }		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();				GlStateManager.color(1, 1, 1, 1);		RenderHelper.enableStandardItemLighting();		RenderHelper.enableGUIStandardItemLighting();				int itemSlot = ItemCyclicWand.BuildType.getSlot(heldWand);		ItemStack current = InventoryWand.getFromSlot(heldWand, itemSlot);		if(current != null){			itemRender.renderItemAndEffectIntoGUI(current, 					SpellHud.xoffset-1, SpellHud.ymain + SpellHud.spellSize*2);		}				RenderHelper.disableStandardItemLighting();	}	private class SpellHud {		private static final int xoffset = 14;//was 30 if manabar is showing		private static final int ymain = 6;		private static final int spellSize = 16;		private int xmain;				@SideOnly(Side.CLIENT)		public void drawSpellWheel(ItemStack wand) {			if (SpellRegistry.renderOnLeft) {				xmain = xoffset;			} else {				ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());				// NOT Minecraft.getMinecraft().displayWidth				xmain = res.getScaledWidth() - xoffset;			}					EntityPlayer player = Minecraft.getMinecraft().thePlayer;			if (player.capabilities.isCreativeMode == false) {				drawManabar(player);			}						ISpell spellCurrent = UtilSpellCaster.getPlayerCurrentISpell(player);			if(SpellRegistry.getSpellbook(wand) == null || SpellRegistry.getSpellbook(wand).size() <= 1){				return;//if theres only one spell, do not do the rest eh			}						drawCurrentSpell(player, spellCurrent);			drawNextSpells(player, spellCurrent);			drawPrevSpells(player, spellCurrent);		}				private void drawManabar(EntityPlayer player) {//			ItemStack wand = UtilSpellCaster.getPlayerWandIfHeld(player);////			double MAX = ItemCyclicWand.Energy.getMaximum(wand);//			double largest = ItemCyclicWand.Energy.getMaximumLargest();////			double ratio = MAX / largest;////			double hFull = manaCtrHeight * ratio;////			// draw the outer container//			UtilTextureRender.drawTextureSimple(mana_container, xHud, yHud, manaCtrWidth,//					MathHelper.floor_double(hFull));////			double current = ItemCyclicWand.Energy.getCurrent(wand);//			double manaPercent = current / MAX;// not using MAX anymore!!!////			double hEmpty = (hFull - 2) * manaPercent;////			// draw the filling inside//			UtilTextureRender.drawTextureSimple(mana, xHud + 1, yHud + 1, manaWidth, MathHelper.floor_double(hEmpty));		}		private void drawCurrentSpell(EntityPlayer player, ISpell spellCurrent) {			if (spellCurrent.getIconDisplay() != null) {				UtilTextureRender.drawTextureSquare(spellCurrent.getIconDisplay(), xmain, ymain, spellSize);			}		}		private void drawPrevSpells(EntityPlayer player, ISpell spellCurrent) {			ItemStack wand = UtilSpellCaster.getPlayerWandIfHeld(player);			ISpell prev = SpellRegistry.prev(wand, spellCurrent);						if (prev != null) {				int x = xmain + 9;				int y = ymain + spellSize;				int dim = spellSize / 2;				UtilTextureRender.drawTextureSquare(prev.getIconDisplay(), x, y, dim);				prev = SpellRegistry.prev(wand, prev);				if (prev != null) {					x += 5;					y += 14;					dim -= 2;					UtilTextureRender.drawTextureSquare(prev.getIconDisplay(), x, y, dim);					prev = SpellRegistry.prev(wand, prev);										if (prev != null) {						x += 3;						y += 10;						dim -= 2;						UtilTextureRender.drawTextureSquare(prev.getIconDisplay(), x, y, dim);						prev = SpellRegistry.prev(wand, prev);											if (prev != null) {							x += 2;							y += 10;							dim -= 1;							UtilTextureRender.drawTextureSquare(prev.getIconDisplay(), x, y, dim);						}					}				}			}		}		private void drawNextSpells(EntityPlayer player, ISpell spellCurrent) {			ItemStack wand = UtilSpellCaster.getPlayerWandIfHeld(player);			ISpell next = SpellRegistry.next(wand, spellCurrent);					if (next != null) {							int x = xmain - 5;				int y = ymain + spellSize;				int dim = spellSize / 2;				UtilTextureRender.drawTextureSquare(next.getIconDisplay(), x, y, dim);				ISpell next2 = SpellRegistry.next(wand, next);							if (next2 != null) {									x -= 2;					y += 14;					dim -= 2;					UtilTextureRender.drawTextureSquare(next2.getIconDisplay(), x, y, dim);					ISpell next3 = SpellRegistry.next(wand, next2);										if (next3 != null) {											x -= 2;						y += 10;						dim -= 2;						UtilTextureRender.drawTextureSquare(next3.getIconDisplay(), x, y, dim);						ISpell next4 = SpellRegistry.next(wand, next3);											if (next4 != null) {							x -= 2;							y += 10;							dim -= 1;							UtilTextureRender.drawTextureSquare(next4.getIconDisplay(), x, y, dim);						}					}				}			}		}	}}