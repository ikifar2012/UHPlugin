/**
 *  Plugin UltraHardcore Reloaded (UHPlugin)
 *  Copyright (C) 2013 azenet
 *  Copyright (C) 2014-2015 Amaury Carrade
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package me.azenet.UHPlugin.commands.commands;

import me.azenet.UHPlugin.UHPlugin;
import me.azenet.UHPlugin.commands.core.AbstractCommand;
import me.azenet.UHPlugin.commands.core.annotations.Command;
import me.azenet.UHPlugin.commands.core.exceptions.CannotExecuteCommandException;
import me.azenet.UHPlugin.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * This command, /t <message>, is used to send a team-message.
 */
@Command(name = "t", noPermission = true)
public class TeamMessageCommand extends AbstractCommand {

	UHPlugin p;
	I18n i;

	public TeamMessageCommand(UHPlugin p) {
		this.p = p;
		this.i = p.getI18n();
	}

	/**
	 * Runs the command.
	 *
	 * @param sender The sender of the command.
	 * @param args   The arguments passed to the command.
	 * @throws me.azenet.UHPlugin.commands.core.exceptions.CannotExecuteCommandException If the command cannot be executed.
	 */
	@Override
	public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
		if(!(sender instanceof Player)) {
			throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER);
		}

		if(args.length == 0) { // /t
			throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
		}

		String message = "";
		for (String arg : args) {
			message += arg + " ";
		}

		p.getTeamChatManager().sendTeamMessage((Player) sender, message);
	}

	/**
	 * Tab-completes this command.
	 *
	 * @param sender The sender.
	 * @param args   The arguments passed to the command.
	 * @return A list of suggestions.
	 */
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}


	@Override
	public List<String> help(CommandSender sender) {
		return Arrays.asList(i.t("team.message.usage", "t"));
	}

	@Override
	public List<String> onListHelp(CommandSender sender) {
		return null;
	}
}