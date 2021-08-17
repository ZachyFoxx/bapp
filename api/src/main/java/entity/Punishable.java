package entity;

import sh.foxboy.bapp.api.punishment.Punishment;
import sh.foxboy.bapp.api.punishment.PunishmentResponse;

public interface Punishable {

	public PunishmentResponse ban(String reason);

	public PunishmentResponse mute(String reason);

	public PunishmentResponse warn(String reason);

	public PunishmentResponse kick(String reason);


}

