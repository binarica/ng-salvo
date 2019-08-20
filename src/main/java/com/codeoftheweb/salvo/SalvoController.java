package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	private GameRepository repo;

	@RequestMapping("/games")
	public List<Object> getGames() {
		return repo.findAll()
				.stream()
				.map(this::mapGame)
				.collect(toList());
	}

	private Map<String, Object> mapGame(Game game) {
		Map<String, Object> map = new HashMap<>();

		map.put("id", game.getId());
		map.put("created", game.getDate());
		map.put("gamePlayers", game.gamePlayers.stream()
				.map(this::mapGamePlayer)
				.collect(toList()));

		return map;
	}

	private Map<String, Object> mapGamePlayer(GamePlayer gamePlayer) {
		Map<String, Object> map = new HashMap<>();

		map.put("id", gamePlayer.getId());
		map.put("player", mapPlayer(gamePlayer.getPlayer()));

		return map;
	}

	private Map<String, Object> mapPlayer(Player player) {
		Map<String, Object> map = new HashMap<>();

		map.put("id", player.getId());
		map.put("email", player.getUserName());

		return map;
	}
}