package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	@RequestMapping("/games")
	public List<Object> getGames() {
		return gameRepository.findAll()
				.stream()
				.map(this::makeGameDTO)
				.collect(toList());
	}

	@RequestMapping("/game_view/{gamePlayerId}")
	public Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
		GamePlayer gamePlayer =  gamePlayerRepository.findById(gamePlayerId).orElse(null);

		Map<String, Object> dto = makeGameDTO(gamePlayer.getGame());

		dto.put("ships", gamePlayer.getShips()
				.stream()
				.map(this::makeShipDTO)
				.collect(toList()));

		return dto;
	}

	private Map<String, Object> makeGameDTO(Game game) {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("id", game.getId());
		dto.put("created", game.getDate());
		dto.put("gamePlayers", game.getGamePlayers()
				.stream()
				.map(this::makeGamePlayerDTO)
				.collect(toList()));

		return dto;
	}

	private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("id", gamePlayer.getId());
		dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));

		return dto;
	}

	private Map<String, Object> makePlayerDTO(Player player) {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("id", player.getId());
		dto.put("email", player.getUserName());

		return dto;
	}

	private Map<String, Object> makeShipDTO(Ship ship) {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("type", ship.getType());
		dto.put("locations", ship.getLocations());

		return dto;
	}
}