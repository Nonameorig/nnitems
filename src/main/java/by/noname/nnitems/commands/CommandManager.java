package by.noname.nnitems.commands;

import by.noname.nnitems.NNItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor {
    private final NNItems plugin;
    private final GetDefenderSwordCommand defenderCommand;

    public CommandManager(NNItems plugin) {
        this.plugin = plugin;
        this.defenderCommand = new GetDefenderSwordCommand(plugin);

        plugin.getCommand("defender").setExecutor(this);
        plugin.getCommand("nnitems").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("defender")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }

            if (!sender.hasPermission("nnitems.defender")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            defenderCommand.execute((Player) sender);
            return true;
        }

        if (command.getName().equalsIgnoreCase("nnitems")) {
            if (!sender.hasPermission("nnitems.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                plugin.getConfigManager().loadConfig();
                sender.sendMessage("§aКонфиг перезагружен!");
                return true;
            }

            sender.sendMessage("§6NNItems v1.0.0");
            sender.sendMessage("§7/nnitems reload §8- Перезагрузить конфиг");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("nnitems")) {
            return Arrays.asList("reload");
        }
        return List.of();
    }
}