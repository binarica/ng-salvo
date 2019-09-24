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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private Date creationDate;
	private Date finishDate;

	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
	Set<GamePlayer> gamePlayers;

	public Game() { }

	public Game(Date creationDate) {
		this.creationDate = creationDate;
	}

	public long getId() {
		return id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	@JsonIgnore
	public List<Player> getPlayers() {
		return gamePlayers.stream()
				.map(GamePlayer::getPlayer)
				.collect(toList());
	}

	public Set<GamePlayer> getGamePlayers() {
		return gamePlayers;
	}

	public Map<String, Object> toDto() {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("id", id);
		dto.put("created", creationDate);
		dto.put("gamePlayers", gamePlayers
				.stream()
				.map(GamePlayer::toDto)
				.collect(toList()));

		return dto;
	}
}