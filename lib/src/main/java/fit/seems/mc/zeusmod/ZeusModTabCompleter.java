package fit.seems.mc.zeusmod;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ZeusModTabCompleter implements TabCompleter {
	private static final ArrayList<String> SUB_COMMANDS = new ArrayList<>();
	static {
		SUB_COMMANDS.add("fly");
		SUB_COMMANDS.add("reset");
		SUB_COMMANDS.add("run");
		SUB_COMMANDS.add("sunny");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] rawArgs) {
		ArrayList<String> args = new ArrayList<>();

		boolean isLastArgEmpty = true;
		for (String arg : rawArgs) {
			isLastArgEmpty = arg.isEmpty();
			if (!isLastArgEmpty) {
				args.add(arg);
			}
		}

		if (isLastArgEmpty) {
			args.add("");
		}

		ArrayList<String> suggestions = new ArrayList<>();
		switch (args.size()) {
			case 1:
				addMatching(suggestions, SUB_COMMANDS, args.get(0));
				break;

			case 2:
				if (args.get(0).equals("fly") || args.get(0).equals("run")) {
					suggestions.add("<number>");
				}
				break;
		}

		return suggestions;
	}

	private static void addMatching(List<String> suggestions, Iterable<String> options, String partial) {
		for (String option : options) {
			if (option.startsWith(partial)) {
				suggestions.add(option);
			}
		}
	}
}
