package nl.imine.command;

import nl.imine.service.EditingService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Optional;

public class JsonTeleportDiscardCommand implements CommandExecutor {

    private final EditingService editingService;

    public JsonTeleportDiscardCommand(EditingService editingService) {
        this.editingService = editingService;
    }

    @Override
    @NonnullByDefault
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if(commandSource instanceof Player) {
            editingService.discardEdit((Player) commandSource);
        }
        return CommandResult.builder()
                .build();
    }

    public static CommandSpec commandSpec(EditingService editingService) {
        return CommandSpec.builder()
                .arguments(GenericArguments.none())
                .executor(new JsonTeleportDiscardCommand(editingService))
                .description(Text.of("Stop your current edit"))
                .extendedDescription(Text.of("This will remove all changes to the currently edited Teleport without saving"))
                .build();
    }
}
