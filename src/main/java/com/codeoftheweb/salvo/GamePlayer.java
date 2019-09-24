package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "game_id")
	private Game game;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "player_id")
	private Player player;

	@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
	Set<Ship> ships = new HashSet<>();

	@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
	Set<Salvo> salvoes = new HashSet<>();

	public GamePlayer() { }

	public GamePlayer(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	public long getId() {
		return id;
	}

	public Game getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public Set<Ship> getShips() {
		return ships;
	}

	public void addShip(Ship ship) {
		ship.setGamePlayer(this);
		ships.add(ship);
	}

	public Set<Salvo> getSalvoes() {
		return salvoes;
	}

	public void addSalvo(Salvo salvo) {
		salvo.setGamePlayer(this);
		salvoes.add(salvo);
	}

	public Map<String, Object> toDto() {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("id", id);
		dto.put("player", player.toDto());

		return dto;
	}
}