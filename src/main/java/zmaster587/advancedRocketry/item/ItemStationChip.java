package zmaster587.advancedRocketry.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.Vector3F;

import java.util.List;

/**
 * MetaData corresponds to the id
 */
public class ItemStationChip extends ItemIdWithName {
	
	private static final String uuidIdentifier = "UUID";
	
	public ItemStationChip() {
		//setMaxDamage(Integer.MAX_VALUE);
		setHasSubtypes(true);
	}

	public void setTakeoffCoords(ItemStack stack, Vector3F<Float> pos, int dimid) {
		setTakeoffCoords(stack, pos.x, pos.y, pos.z, dimid);
	}

	public void setTakeoffCoords(ItemStack stack, float x, float y, float z, int dimid) {
		NBTTagCompound nbt;

		if(stack.hasTagCompound()) 
			nbt = stack.getTagCompound();
		else 
			nbt = new NBTTagCompound();

		NBTTagCompound nbtEntry;
		
		if(nbt.hasKey("dimid" + dimid)) 
			nbtEntry = nbt.getCompoundTag("dimid" + dimid);
		else
			nbtEntry = new NBTTagCompound();
		
		nbtEntry.setFloat("x", x);
		nbtEntry.setFloat("y", y);
		nbtEntry.setFloat("z", z);
		
		nbt.setTag("dimid" + dimid, nbtEntry);

		stack.setTagCompound(nbt);
	}

	/**
	 * @param stack
	 * @return Vector3F containing the takeoff coords or null if there is none
	 */
	public Vector3F<Float> getTakeoffCoords(ItemStack stack, int dimid) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("dimid" + dimid)) {
				nbt = nbt.getCompoundTag("dimid" + dimid);
				return new Vector3F<Float>(nbt.getFloat("x"), nbt.getFloat("y"),nbt.getFloat("z"));
			}
		}
		return null;
	}
	
	public static int getUUID(ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getInteger(uuidIdentifier);
		return 0;
	}

	public static void setUUID(ItemStack stack, int uuid) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else
			nbt = new NBTTagCompound();

		nbt.setInteger(uuidIdentifier,uuid);
		stack.setTagCompound(nbt);
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List list,
			ITooltipFlag bool) {
		if(getUUID(stack) == 0)
			list.add(ChatFormatting.GRAY + LibVulpes.proxy.getLocalizedString("msg.unprogrammed"));
		else {
			list.add(ChatFormatting.GREEN + LibVulpes.proxy.getLocalizedString("msg.stationchip.sation") + getUUID(stack));
			super.addInformation(stack, player, list, bool);
			if(player.provider.getDimension() == Configuration.spaceDimId) {
	            Entity p = Minecraft.getMinecraft().player;
				ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(p.getPosition());
				
				if(obj != null) {
					Vector3F<Float> vec = getTakeoffCoords(stack, obj.getOrbitingPlanetId());
					
					if(vec != null) {
						list.add("X: " + vec.x);
						list.add("Z: " + vec.z);
					}
					else {
						list.add("X: N/A");
						list.add("Z: N/A");
					}
				}
			}
			
		}
	}
}
