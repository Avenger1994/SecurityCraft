package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityKeypadChest extends TileEntityChest implements IPasswordProtected, IOwnable {
	
	private String passcode;
	private Owner owner = new Owner();

	public TileEntityKeypadChest adjacentChestZNeg;
    public TileEntityKeypadChest adjacentChestXPos;
    public TileEntityKeypadChest adjacentChestXNeg;
    public TileEntityKeypadChest adjacentChestZPos;

    public Owner getOwner(){
    	return owner;
    }
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.passcode != null && !this.passcode.isEmpty()){
        	par1NBTTagCompound.setString("passcode", this.passcode);
        }
        
        if(this.owner != null){
        	par1NBTTagCompound.setString("owner", this.getOwner().getName());
        	par1NBTTagCompound.setString("ownerUUID", this.getOwner().getUUID());
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("passcode"))
        {
        	if(par1NBTTagCompound.getInteger("passcode") != 0){
        		this.passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
        	}else{
        		this.passcode = par1NBTTagCompound.getString("passcode");
        	}
        }
        
        if (par1NBTTagCompound.hasKey("owner"))
        {
            this.getOwner().setOwnerName(par1NBTTagCompound.getString("owner"));
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.getOwner().setOwnerUUID(par1NBTTagCompound.getString("ownerUUID"));
        }
    }
    
    /**
     * Returns the name of the inventory
     */
    public String getName()
    {
        return "Protected chest";
    }
    
    public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadChest){
    		BlockKeypadChest.activate(worldObj, pos, player);
    	}
	}
    
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null) {
			player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
		}
		else {
			player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
		}		
	}
	
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled) {
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypadChest.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
		}
		else {
			activate(player);
			return true;
		}
		
		return false;
	}

    public String getPassword() {
		return (this.passcode != null && !this.passcode.isEmpty()) ? this.passcode : null;
	}
    
	public void setPassword(String password) {
		passcode = password;
	}

	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

}
