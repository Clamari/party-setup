package cl.clamari.partysetup.controller.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Service
@Scope("singleton")
public class RuntimeData {
	private List<Player> players = new ArrayList<>();
	private List<Player> tanks = new ArrayList<>();
	private List<Player> healers = new ArrayList<>();
	private List<Player> melees = new ArrayList<>();
	private List<Player> rangeds = new ArrayList<>();

	public void addPlayer(Player player) {
		players.add(player);
		if (player.getRole() == 1)
			tanks.add(player);
		else if (player.getRole() == 2)
			healers.add(player);
		else if (player.getRole() == 3)
			melees.add(player);
		else if (player.getRole() == 4)
			rangeds.add(player);
	}

	public void reset() {
		players = new ArrayList<>();
		tanks = new ArrayList<>();
		healers = new ArrayList<>();
		melees = new ArrayList<>();
		rangeds = new ArrayList<>();
	}

	public List<Group> getGroups() {
		var groups = new ArrayList<Group>();
		if (players.size() == 0)
			return groups;
		int qGroups = players.size() / 5;
		int modPlayers = players.size() % 5;
		if (modPlayers > 0)
			qGroups++;

		int tanksPerGroup = tanks.size() / qGroups;
		int aloneTanks = tanks.size() % qGroups;
		int healsPerGroup = healers.size() / qGroups;
		int aloneHeals = healers.size() % qGroups;
		int meleesPerGroup = melees.size() / qGroups;
		int aloneMelees = melees.size() % qGroups;
		int rangedsPerGroup = rangeds.size() / qGroups;
		int aloneRangeds = rangeds.size() % qGroups;

		List<Player> auxTanks = new ArrayList<Player>(tanks);
		List<Player> auxHealers = new ArrayList<Player>(healers);
		List<Player> auxMelees = new ArrayList<Player>(melees);
		List<Player> auxRangeds = new ArrayList<Player>(rangeds);

		// create empty groups
		for (int i = 1; i <= qGroups; i++) {
			var group = new Group();
			group.setName("Group " + i);
			group.setPlayers(new ArrayList<>());
			groups.add(group);
		}

		// ideal fill
		if (healsPerGroup > 0 || tanksPerGroup > 0 || aloneTanks == 0 || aloneHeals == 0) {
			normalFill(groups, tanksPerGroup, healsPerGroup, meleesPerGroup, rangedsPerGroup, auxTanks, auxHealers,
					auxMelees, auxRangeds);
			// not enough healers or tanks. Prioritize healers for tanks
		} else {
			while (meleesPerGroup > 0) {
				meleesPerGroup--;
				aloneMelees += qGroups;
			}
			while (rangedsPerGroup > 0) {
				rangedsPerGroup--;
				aloneRangeds += qGroups;
			}
			for (Group group : groups) {
				int additions = 0;
				while (additions < 2) {
					int initialValue = additions;
					if (aloneTanks > 0) {
						group.getPlayers().add(auxTanks.get(0));
						auxTanks.remove(0);
						aloneTanks--;
						additions++;
					}
					if (aloneHeals > 0) {
						group.getPlayers().add(auxHealers.get(0));
						auxHealers.remove(0);
						aloneHeals--;
						additions++;
					}
					if (aloneMelees >= aloneRangeds && additions < 2) {
						group.getPlayers().add(auxMelees.get(0));
						auxMelees.remove(0);
						aloneMelees--;
						additions++;
					} else if (additions < 2) {
						group.getPlayers().add(auxRangeds.get(0));
						auxRangeds.remove(0);
						aloneRangeds--;
						additions++;
					}
					// loop protection
					if (initialValue == additions)
						break;
				}
			}
		}

		// add leftovers
		boolean sobras = aloneTanks > 0 || aloneHeals > 0 || aloneMelees > 0 || aloneRangeds > 0;
		while (sobras) {
			for (Group group : groups) {
				if (group.getPlayers().size() < 4) {
					if (aloneMelees > 0) {
						group.getPlayers().add(auxMelees.get(0));
						auxMelees.remove(0);
						aloneMelees--;
					} else if (aloneRangeds > 0) {
						group.getPlayers().add(auxRangeds.get(0));
						auxRangeds.remove(0);
						aloneRangeds--;
					} else if (aloneTanks > 0) {
						group.getPlayers().add(auxTanks.get(0));
						auxTanks.remove(0);
						aloneTanks--;
					} else if (aloneHeals > 0) {
						group.getPlayers().add(auxHealers.get(0));
						auxHealers.remove(0);
						aloneHeals--;
					}
				} else if (group.getPlayers().size() < 5) {
					if (aloneTanks > 0) {
						group.getPlayers().add(auxTanks.get(0));
						auxTanks.remove(0);
						aloneTanks--;
					} else if (aloneHeals > 0) {
						group.getPlayers().add(auxHealers.get(0));
						auxHealers.remove(0);
						aloneHeals--;
					} else if (aloneMelees > 0) {
						group.getPlayers().add(auxMelees.get(0));
						auxMelees.remove(0);
						aloneMelees--;
					} else if (aloneRangeds > 0) {
						group.getPlayers().add(auxRangeds.get(0));
						auxRangeds.remove(0);
						aloneRangeds--;
					}
				}
				sobras = aloneTanks > 0 || aloneHeals > 0 || aloneMelees > 0 || aloneRangeds > 0;
				if (!sobras)
					break;
			}
		}

		return groups;
	}

	private void normalFill(ArrayList<Group> groups, int tanksPerGroup, int healsPerGroup, int meleesPerGroup,
			int rangedsPerGroup, List<Player> auxTanks, List<Player> auxHealers, List<Player> auxMelees,
			List<Player> auxRangeds) {
		for (Group group : groups) {
			for (int j = 0; j < tanksPerGroup; j++) {
				group.getPlayers().add(auxTanks.get(0));
				auxTanks.remove(0);
			}
			for (int j = 0; j < healsPerGroup; j++) {
				group.getPlayers().add(auxHealers.get(0));
				auxHealers.remove(0);
			}
			for (int j = 0; j < meleesPerGroup; j++) {
				group.getPlayers().add(auxMelees.get(0));
				auxMelees.remove(0);
			}
			for (int j = 0; j < rangedsPerGroup; j++) {
				group.getPlayers().add(auxRangeds.get(0));
				auxRangeds.remove(0);
			}
		}
	}
}
