package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private String type;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "game_player_id")
	private GamePlayer gamePlayer;

	@ElementCollection
	@Column(name = "location")
	private List<String> locations = new ArrayList<>();

	public Ship() { }

	public Ship(GamePlayer gamePlayer, String type, List<String> locations) {
		this.gamePlayer = gamePlayer;
		this.type = type;
		this.locations = locations;
	}

	public Map<String, Object> toDto() {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("type", type);
		dto.put("locations", locations);

		return dto;
	}
}