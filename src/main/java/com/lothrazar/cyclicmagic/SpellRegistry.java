package com.lothrazar.cyclicmagic;

import java.util.ArrayList;
import com.lothrazar.cyclicmagic.spell.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

public class SpellRegistry {

	private static ArrayList<ISpell> spellbook;
	
	static SpellScreenRender screen;
	public static SpellCaster caster;

	private static void registerSpell(ISpell spell){
		spellbook.add(spell);
		//System.out.println("spell "+spell.getID()+ " registered "+spell.getName());
	}
	
	public static ISpell getDefaultSpell() {
		return SpellRegistry.getSpellbook().get(0);
	}

	public static boolean spellsEnabled(EntityPlayer player) {
		ItemStack held = player.getHeldItem();
		return held != null && held.getItem() == ItemRegistry.master_wand;
	}
	 
	public static void register() {
		screen = new SpellScreenRender();
		caster = new SpellCaster();
		spellbook = new ArrayList<ISpell>();

		int potionDuration = Const.TICKS_PER_SEC * 20;

		int spellId = -1;//the smallest spell gets id zero

		// used to be public statics
		BaseSpell ghost;
		BaseSpell phase;
		SpellExpPotion waterwalk;
		SpellExpPotion nightvision;//TODO: replace with night vision
		BaseSpell rotate;
		BaseSpell push;
		SpellThrowTorch torch;
		SpellThrowFishing fishing;
		BaseSpell carbon;

		ghost = new SpellGhost(++spellId,"ghost");
		registerSpell(ghost);
		
		phase = new SpellPhasing(++spellId,"phasing");
		registerSpell(phase);

		waterwalk = new SpellExpPotion(++spellId,"waterwalk");
		waterwalk.setPotion(PotionRegistry.waterwalk.id, potionDuration, PotionRegistry.I);
		registerSpell(waterwalk);

		nightvision = new SpellExpPotion(++spellId,"nightvision");
		nightvision.setPotion(Potion.nightVision.id, potionDuration, PotionRegistry.I);
		registerSpell(nightvision);

		rotate = new SpellRotate(++spellId,"rotate"); 
		registerSpell(rotate);

		push = new SpellPush(++spellId,"push");
		registerSpell(push);

		SpellPull pull = new SpellPull(++spellId,"pull");
		registerSpell(pull);

		torch = new SpellThrowTorch(++spellId,"torch");
		registerSpell(torch);

		fishing = new SpellThrowFishing(++spellId,"fishing");
		registerSpell(fishing);

		SpellThrowExplosion explode = new SpellThrowExplosion(++spellId,"explode");
		registerSpell(explode);

		SpellThrowFire fire = new SpellThrowFire(++spellId,"fire");
		registerSpell(fire);

		SpellThrowIce ice = new SpellThrowIce(++spellId,"ice");
		registerSpell(ice);

		SpellThrowLightning lightning = new SpellThrowLightning(++spellId,"lightning");
		registerSpell(lightning);

		SpellThrowShear shear = new SpellThrowShear(++spellId,"shear");
		registerSpell(shear);

		SpellThrowWater water = new SpellThrowWater(++spellId,"water");
		registerSpell(water);

		SpellScaffolding scaffold = new SpellScaffolding(++spellId,"scaffold");
		registerSpell(scaffold);

		SpellChestSack chestsack = new SpellChestSack(++spellId,"chestsack");
		registerSpell(chestsack);

		SpellThrowSpawnEgg spawnegg = new SpellThrowSpawnEgg(++spellId,"spawnegg");
		registerSpell(spawnegg);
		
		carbon = new SpellCarbonPaper(++spellId,"carbon");
		registerSpell(carbon);
		
		SpellLaunch launch = new SpellLaunch(++spellId,"launch");
		registerSpell(launch);
		
		SpellThrowHarvest harvest = new SpellThrowHarvest(++spellId,"harvest");
		registerSpell(harvest);
		
		SpellLinkingPortal waypoint = new SpellLinkingPortal(++spellId,"waypoint");
		registerSpell(waypoint);
		
		SpellBuilder builder = new SpellBuilder(++spellId,"builder");
		registerSpell(builder);
	}

	public static ISpell getSpellFromID(int id) {
		
		if(id >= spellbook.size()){
			return null;//this should avoid all OOB exceptoins
		}
		
		try{
			return spellbook.get(id);
		}
		catch(IndexOutOfBoundsException  e){
			System.out.println(id+" SPELL OOB fix yo stuff k");
			return null;
		}
	}

	public static ArrayList<ISpell> getSpellbook() {
		return spellbook;
	}
}
