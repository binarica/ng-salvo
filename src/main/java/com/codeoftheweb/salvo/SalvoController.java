package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
				.map(Game::toDto)
				.collect(toList());
	}

	@RequestMapping("/game_view/{gamePlayerId}")
	public Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
		GamePlayer gamePlayer =  gamePlayerRepository.findById(gamePlayerId).orElse(null);
		Game game = gamePlayer.getGame();
		List<Salvo> salvoes = game.getGamePlayers()
				.stream()
				.flatMap(gp -> gp.getSalvoes().stream())
				.collect(toList());

		Map<String, Object> dto = game.toDto();

		dto.put("ships", gamePlayer.getShips()
				.stream()
				.map(Ship::toDto)
				.collect(toList()));

		dto.put("salvoes", salvoes
				.stream()
				.map(Salvo::toDto)
				.collect(toList()));

		return dto;
	}
}