package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private String userName;

	@OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
	Set<GamePlayer> gamePlayers;

	public Player() { }

	public Player(String userName) {
		this.userName = userName;
	}

	public long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public void addGamePlayer(GamePlayer gamePlayer) {
		gamePlayer.setPlayer(this);
		gamePlayers.add(gamePlayer);
	}

	@JsonIgnore
	public List<Game> getGames() {
		return gamePlayers.stream()
				.map(gp -> gp.getGame())
				.collect(toList());
	}
}