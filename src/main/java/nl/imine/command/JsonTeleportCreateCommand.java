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

public class JsonTeleportCreateCommand implements CommandExecutor {

    private static final String IS_RETURN_TELEPORT_ARGUMENT_KEY = "isReturnTeleport";

    private final EditingService editingService;

    public JsonTeleportCreateCommand(EditingService editingService) {
        this.editingService = editingService;
    }

    @Override
    @NonnullByDefault
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if(commandSource instanceof Player) {
            Optional<Boolean> isReturnTeleport = commandContext.getOne(IS_RETURN_TELEPORT_ARGUMENT_KEY);
            isReturnTeleport.ifPresent(aBoolean -> editingService.createNewEdit((Player) commandSource, aBoolean));
        }
        return CommandResult.builder()
                .build();
    }

    public static CommandSpec commandSpec(EditingService editingService) {
        return CommandSpec.builder()
                .arguments(GenericArguments.bool(Text.of("isReturnTeleport")))
                .executor(new JsonTeleportCreateCommand(editingService))
                .description(Text.of("Create a new Teleport"))
                .extendedDescription(Text.of("A new teleport will be created. You will then be set into edit mode to set up the teleport."))
                .build();
    }
}
