package fr.partipirate.discord.bots.congressus.commands.radio;

import fr.partipirate.discord.bots.congressus.CongressusBot;
import fr.partipirate.discord.bots.congressus.commands.ICommand;
import fr.partipirate.discord.bots.congressus.commands.dj.ADJCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class NumberOfTracksCommand extends ADJCommand implements ICommand {

	private CongressusBot bot;

	public NumberOfTracksCommand(CongressusBot bot) {
		this.bot = bot;
	}
	
	@Override
	public String getKeyWord() {
		return "nbTracks";
	}

	@Override
	public boolean canDoCommand(User user, Guild guild) {
		return isDJ(getMember(user, guild));
	}

	@Override
	public void doCommand(User user, MessageChannel channel, Guild guild, String[] commandParts) {
		if (channel instanceof PrivateChannel) {
        	channel.sendMessage("*Commande à faire publiquement*").complete();
		}
		else if (!isDJ(getMember(user, guild))) {
        	channel.sendMessage("*Vous n'avez pas les droits suffisants*").complete();
		}
		else {
			int numberOfTracks = bot.getNumberOfTracks((TextChannel) channel);

			channel.sendMessage("*Il y a "+numberOfTracks+" piste(s) dans la pile*").complete();
		}
	}
	
	@Override
	public String getCommandHelp() {
		return "Donne le nombre de piste dans la pile.";
	}
}
