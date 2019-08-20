package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private Date date;

	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
	Set<GamePlayer> gamePlayers;

	public Game() { }

	public Game(Date date) {
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void addGamePlayer(GamePlayer gamePlayer) {
		gamePlayer.setGame(this);
		gamePlayers.add(gamePlayer);
	}

	@JsonIgnore
	public List<Player> getPlayers() {
		return gamePlayers.stream()
				.map(gp -> gp.getPlayer())
				.collect(toList());
	}
}