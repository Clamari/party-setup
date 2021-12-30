package cl.clamari.partysetup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import cl.clamari.partysetup.controller.model.Player;
import cl.clamari.partysetup.controller.model.RuntimeData;

@Controller
public class RaidController {
	
	@Autowired
	private RuntimeData data;

	@GetMapping("/register")
	public String getRegister(Model model) {
		model.addAttribute("player", new Player());
		return "register";
	}

	@PostMapping("/register")
	public RedirectView postRegister(@ModelAttribute Player player, Model model) {
		data.addPlayer(player);
		return new RedirectView("list");
	}

	@GetMapping("/list")
	public String listPlayers(Model model) {
		model.addAttribute("groups", data.getGroups());
		return "list";
	}

	@GetMapping("/reset")
	public RedirectView resetPlayers(Model model) {
		data.reset();
		return new RedirectView("list");
	}
}
