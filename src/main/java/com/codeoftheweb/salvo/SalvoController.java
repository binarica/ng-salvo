package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashSet;
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
	private ShipRepository shipRepository;

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
			return new ResponseEntity<>(ErrorMessage.NOT_LOGGED_IN, HttpStatus.UNAUTHORIZED);
		}

		// creates and saves a new game
		Game game = gameRepository.save(new Game(new Date()));

		// creates and saves a new game player for this game and the current user
		GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));

		// send a Created response, with JSON containing the new game player ID
		return new ResponseEntity<>(gamePlayer.getId(), HttpStatus.CREATED);
	}
	
	@RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.GET)
	public ResponseEntity<Object> getPlayers(@PathVariable Long gameId) {
		// JSON list of players in game nn
		Game game =  gameRepository.findById(gameId).orElse(null);
		if (game == null) {
			return new ResponseEntity<>(String.format(ErrorMessage.NOT_FOUND, "Game"), HttpStatus.NOT_FOUND);
		}
		
		List<Map<String, Object>> players = game.getPlayers()
				.stream()
				.map(Player::toDto)
				.collect(toList());
		
		return new ResponseEntity<>(players, HttpStatus.OK);
	}

	@RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
	public ResponseEntity<Object> joinGame(@PathVariable Long gameId) {
		/*
			success: 201 created;
			content: { gpid: mm }
			
			failure responses:
			401 unauthorized if not logged in
			403 forbidden if no such game, game full, already member
		 */
		
		Player player = getCurrentPlayer();
		if (player == null) {
			return new ResponseEntity<>(ErrorMessage.NOT_LOGGED_IN, HttpStatus.UNAUTHORIZED);
		}

		Game game = gameRepository.findById(gameId).orElse(null);
		if (game == null) {
			return new ResponseEntity<>(ErrorMessage.NO_SUCH_GAME, HttpStatus.FORBIDDEN);
		}

		Set<GamePlayer> gamePlayers = game.getGamePlayers();
		if (gamePlayers.size() > 1) {
			return new ResponseEntity<>(ErrorMessage.GAME_FULL, HttpStatus.FORBIDDEN);
		}

		Long playerId = player.getId();
		if (gamePlayers.stream().anyMatch(gp -> playerId.equals(gp.getPlayer().getId()))) {
			return new ResponseEntity<>(ErrorMessage.ALREADY_MEMBER, HttpStatus.FORBIDDEN);
		}

		GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));
		return new ResponseEntity<>(newGamePlayer.getId(), HttpStatus.CREATED);
	}
	
	@RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.GET)
	public ResponseEntity<Object> getShips(@PathVariable long gamePlayerId) {
		// JSON list of current ships for game player nn
		GamePlayer gamePlayer =  gamePlayerRepository.findById(gamePlayerId).orElse(null);
		if (gamePlayer == null) {
			return new ResponseEntity<>(String.format(ErrorMessage.NOT_FOUND, "GamePlayer"), HttpStatus.NOT_FOUND);
		}
		
		List<Map<String, Object>> ships = gamePlayer.getShips()
				.stream()
				.map(Ship::toDto)
				.collect(toList());
		
		return new ResponseEntity<>(ships, HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
	public ResponseEntity<Object> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships) {
		/*
			success: 201 created
			
			failure responses:
			401 unauthorized if not logged in, not game player nn
			403 forbidden: no such game player, ships already placed, too many ships
		 */
		
		ResponseEntity<Object> response;
		
		Player player = getCurrentPlayer();
		if (player == null) {
			return new ResponseEntity<>(ErrorMessage.NOT_LOGGED_IN, HttpStatus.UNAUTHORIZED);
		}

		else {

			GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

			if (gamePlayer == null) {
				return new ResponseEntity<>(String.format(ErrorMessage.NOT_FOUND, "GamePlayer"), HttpStatus.NOT_FOUND);
			}

			if (!player.getId().equals(gamePlayer.getPlayer().getId())) {
				return new ResponseEntity<>(ErrorMessage.ANOTHER_PLAYER, HttpStatus.UNAUTHORIZED);
			}

			if (!CollectionUtils.isEmpty(gamePlayer.getShips())) {
				return new ResponseEntity<>(ErrorMessage.SHIPS_ALREADY_PLACED, HttpStatus.FORBIDDEN);
			}

			if (ships == null || ships.size() != 5) {
				return new ResponseEntity<>(ErrorMessage.SHIPS_NOT_ALLOWED, HttpStatus.FORBIDDEN);
			}
			else {
				if (ships.stream().anyMatch(Ship::isOutOfRange)) {
					response = new ResponseEntity<>(ErrorMessage.SHIPS_OUT_OF_RANGE, HttpStatus.FORBIDDEN);
				}
				else if (ships.stream().anyMatch(Ship::isNotConsecutive)) {
					response = new ResponseEntity<>(ErrorMessage.SHIPS_NOT_CONSECUTIVE, HttpStatus.FORBIDDEN);
				}
				else if (this.areOverlapped(ships)) {
					response = new ResponseEntity<>(ErrorMessage.SHIPS_OVERLAPPED, HttpStatus.FORBIDDEN);
				}
				else {
					ships.forEach(ship -> shipRepository.save(new Ship(gamePlayer, ship.getType(), ship.getLocations())));
					response = new ResponseEntity<>(HttpStatus.CREATED);
				}
			}
		}
		
		return response;
	}
	
	private boolean areOverlapped(List<Ship> ships) {
		List<String> allLocations = ships
				.stream()
				.flatMap(ship -> ship.getLocations().stream())
				.collect(toList());
		
		Set<String> cells = new HashSet<>();
		for (String location : allLocations) {
			if (!cells.add(location)) {
				return true;
			}
		}
		
		return false;
	}

	@RequestMapping("/game_view/{gamePlayerId}")
	public ResponseEntity<Object> getGameView(@PathVariable Long gamePlayerId) {
		GamePlayer gamePlayer =  gamePlayerRepository.findById(gamePlayerId).orElse(null);
		if (gamePlayer == null) {
			return new ResponseEntity<>(String.format(ErrorMessage.NOT_FOUND, "GamePlayer"), HttpStatus.NOT_FOUND);
		}

		Player player = getCurrentPlayer();
		if (player != null && !player.getId().equals(gamePlayer.getPlayer().getId())) {
			return new ResponseEntity<>(ErrorMessage.ANOTHER_PLAYER, HttpStatus.UNAUTHORIZED);
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
			return new ResponseEntity<>(ErrorMessage.NO_NAME, HttpStatus.FORBIDDEN);
		}
		Player player = playerRepository.findByUserName(username);
		if (player != null) {
			return new ResponseEntity<>(ErrorMessage.NAME_ALREADY_USED, HttpStatus.CONFLICT);
		}
		Player newPlayer = playerRepository.save(new Player(username, passwordEncoder.encode(password)));
		return new ResponseEntity<>(newPlayer.toDto(), HttpStatus.CREATED);
	}

	private Player getCurrentPlayer() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken)
				? playerRepository.findByUserName(authentication.getName())
				: null;
	}
}
