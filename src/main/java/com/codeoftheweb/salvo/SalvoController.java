package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@RequestMapping(path = "/games", method = RequestMethod.GET)
	public Map<String, Object> getGames() {
		Player player = getCurrentPlayer();

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

	@RequestMapping(path = "/games", method = RequestMethod.POST)
	public ResponseEntity<Object> createGame() {
		// gets the current user
		Player player = getCurrentPlayer();

		// if there is none, it should send an Unauthorized response
		if (player == null) {
			return new ResponseEntity<>("Player not found", HttpStatus.UNAUTHORIZED);
		}

		// creates and saves a new game
		Game game = gameRepository.save(new Game(new Date()));

		// creates and saves a new game player for this game and the current user
		GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));

		// send a Created response, with JSON containing the new game player ID, e.g., { "gpid": 32 }
		return new ResponseEntity<>(makeMap("gamePlayerId", gamePlayer.getId()), HttpStatus.CREATED);
	}

	@RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
	public ResponseEntity<Object> joinGame(@PathVariable Long gameId) {
		Player player = getCurrentPlayer();
		if (player == null) {
			return new ResponseEntity<>("You must be logged in first", HttpStatus.UNAUTHORIZED);
		}

		Game game = gameRepository.findById(gameId).orElse(null);
		if (game == null) {
			return new ResponseEntity<>(String.format("Game %d does not exist", gameId), HttpStatus.NOT_FOUND);
		}

		Set<GamePlayer> gamePlayers = game.getGamePlayers();
		if (gamePlayers.size() > 1) {
			return new ResponseEntity<>("Game is full", HttpStatus.FORBIDDEN);
		}

		Long playerId = player.getId();
		if (gamePlayers.stream().anyMatch(gp -> playerId.equals(gp.getPlayer().getId()))) {
			return new ResponseEntity<>("You can't play against yourself!", HttpStatus.FORBIDDEN);
		}

		GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));
		return new ResponseEntity<>(makeMap("gamePlayerId", newGamePlayer.getId()), HttpStatus.CREATED);
	}

	@RequestMapping("/game_view/{gamePlayerId}")
	public ResponseEntity<Object> getGameView(@PathVariable Long gamePlayerId) {
		GamePlayer gamePlayer =  gamePlayerRepository.findById(gamePlayerId).orElse(null);
		if (gamePlayer == null) {
			return new ResponseEntity<>("GamePlayer not found", HttpStatus.NOT_FOUND);
		}

		Player player = getCurrentPlayer();
		if (player != null && !player.getId().equals(gamePlayer.getPlayer().getId())) {
			return new ResponseEntity<>(String.format("Current user is not the game player referenced by the ID %d", gamePlayerId), HttpStatus.UNAUTHORIZED);
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
	public ResponseEntity<Object> createPlayer(@RequestParam String username, @RequestParam String password) {
		if (username.isEmpty()) {
			return new ResponseEntity<>("No name", HttpStatus.FORBIDDEN);
		}
		Player player = playerRepository.findByUserName(username);
		if (player != null) {
			return new ResponseEntity<>("Name in use", HttpStatus.CONFLICT);
		}
		Player newPlayer = playerRepository.save(new Player(username, passwordEncoder.encode(password)));
		return new ResponseEntity<>(newPlayer.toDto(), HttpStatus.CREATED);
	}

	private Map<String, Object> makeMap(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	private Player getCurrentPlayer() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken)
				? playerRepository.findByUserName(authentication.getName())
				: null;
	}
}
