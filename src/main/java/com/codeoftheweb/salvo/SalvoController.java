package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

	@Autowired
	private PlayerRepository playerRepository;

	@RequestMapping("/games")
	public Map<String, Object> getGames() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Player player = getLoggedPlayer(authentication);

		Map<String, Object> dto = new LinkedHashMap<>();

		if (player != null) {
			dto.put("player", player.toDto());
		}

		dto.put("games", gameRepository.findAll()
				.stream()
				.map(Game::toDto)
				.collect(toList()));

		return dto;
	}

	@RequestMapping("/game_view/{gamePlayerId}")
	public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId) {
		GamePlayer gamePlayer =  gamePlayerRepository.findById(gamePlayerId).orElse(null);
		if (gamePlayer == null) {
			return new ResponseEntity<>(makeMap("error", "GamePlayer not found"), HttpStatus.NOT_FOUND);
		}

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

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@RequestMapping(path = "/players", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, @RequestParam String password) {
		if (username.isEmpty()) {
			return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
		}
		Player player = playerRepository.findByUserName(username);
		if (player != null) {
			return new ResponseEntity<>(makeMap("error", "Name in use"), HttpStatus.CONFLICT);
		}
		Player newPlayer = playerRepository.save(new Player(username, password));
		return new ResponseEntity<>(makeMap("username", newPlayer.getUserName()), HttpStatus.CREATED);
	}

	private Map<String, Object> makeMap(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	private Player getLoggedPlayer(Authentication authentication) {
		return !(authentication instanceof AnonymousAuthenticationToken)
				? playerRepository.findByUserName(authentication.getName())
				: null;
	}
}