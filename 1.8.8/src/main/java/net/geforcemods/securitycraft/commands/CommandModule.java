package net.geforcemods.securitycraft.commands;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class CommandModule extends CommandBase implements ICommand {

	@Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getCommandName()
	{
		return "module";
	}
    
	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return StatCollector.translateToLocal("messages.command.module.usage");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length == 1){
			if(args[0].matches("copy")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){		
					mod_SecurityCraft.instance.setSavedModule(player.getCurrentEquippedItem().getTagCompound());
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.saved"), EnumChatFormatting.GREEN);
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.notHoldingForData"), EnumChatFormatting.RED);
				}
				
				return;
			}else if(args[0].matches("paste")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());

				if(mod_SecurityCraft.instance.getSavedModule() == null){
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.nothingSaved"), EnumChatFormatting.RED);
					return;
				}
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){		
					player.getCurrentEquippedItem().setTagCompound(mod_SecurityCraft.instance.getSavedModule());
					mod_SecurityCraft.instance.setSavedModule(null);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.saved"), EnumChatFormatting.GREEN);
				}
				
				return;
			}
		}else if(args.length == 2){
			if(args[0].matches("add")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){			
					if(player.getCurrentEquippedItem().getTagCompound() == null){
						player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());				
					}
					
					for(int i = 1; i <= 10; i++){
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).matches(args[1])){
							PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.alreadyContained").replace("#", args[1]), EnumChatFormatting.RED);
							return;
						}
					}
                    
					player.getCurrentEquippedItem().getTagCompound().setString("Player" + getNextSlot(player.getCurrentEquippedItem().getTagCompound()), args[1]);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.added").replace("#", args[1]), EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.notHoldingForModify"), EnumChatFormatting.RED);
					return;
				}
			}else if(args[0].matches("remove")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){			
					if(player.getCurrentEquippedItem().getTagCompound() == null){
						player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());				
					}
					
					for(int i = 1; i <= 10; i++){
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).matches(args[1])){
							player.getCurrentEquippedItem().getTagCompound().removeTag("Player" + i);
						}
					}
					
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.removed").replace("#", args[1]), EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.notHoldingForModify"), EnumChatFormatting.RED);
					return;
				}
			}
		}
		
		throw new WrongUsageException(StatCollector.translateToLocal("messages.command.module.usage"));
	}
	
	private int getNextSlot(NBTTagCompound stackTagCompound) {
		for(int i = 1; i <= 10; i++){
			if(stackTagCompound.getString("Player" + i) != null && !stackTagCompound.getString("Player" + i).isEmpty()){
				continue;
			}else{
				return i;
			}
		}
		
		return 0;
	}
}
