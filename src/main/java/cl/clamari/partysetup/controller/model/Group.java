package cl.clamari.partysetup.controller.model;

import java.util.List;

import lombok.Data;

@Data
public class Group {
	private String name;
	private List<Player> players;
}
