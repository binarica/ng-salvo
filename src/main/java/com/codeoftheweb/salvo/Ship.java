package com.codeoftheweb.salvo;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {
	
	public static final Map<String, Integer> shipTypeMap = new HashMap<>();
	
	static {
		shipTypeMap.put("Aircraft Carrier", 5);
		shipTypeMap.put("Battleship", 4);
		shipTypeMap.put("Submarine", 3);
		shipTypeMap.put("Destroyer", 3);
		shipTypeMap.put("Patrol Boat", 2);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
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
	
	public String getType() {
		return type;
	}
	
	public List<String> getLocations() {
		return locations;
	}
	
	public Boolean isOutOfRange() {
		
		for (String cell : locations) {
			if (cell == null || cell.length() < 2) {
				return true;
			}
			char y = cell.substring(0, 1).charAt(0);
			int x;
			
			try {
				x = Integer.parseInt(cell.substring(1));
			} catch (NumberFormatException e) {
				x = 99;
			}
			
			if (x < 1 || x > 10 || y < 'A' || y > 'J') {
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean isNotConsecutive() {
		
		for (int i = 0; i < locations.size(); i ++){
			
			if (i < locations.size() - 1) {
				if (isVertical()) {
					char yChar = locations.get(i).substring(0, 1).charAt(0);
					char compareChar = locations.get(i + 1).substring(0, 1).charAt(0);
					if (compareChar - yChar != 1) {
						return true;
					}
				} else {
					Integer xInt = Integer.parseInt(locations.get(i).substring(1));
					Integer compareInt = Integer.parseInt(locations.get(i + 1).substring(1));
					
					if (compareInt - xInt != 1) {
						return true;
					}
				}
			}
			
			for (int j = i + 1; j < locations.size(); j ++) {
				if (isVertical()) {
					if (!locations.get(i).substring(1).equals(locations.get(j).substring(1))) {
						return true;
					}
				} else {
					if (!locations.get(i).substring(0,1).equals(locations.get(j).substring(0, 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public Map<String, Object> toDto() {
		Map<String, Object> dto = new LinkedHashMap<>();

		dto.put("type", type);
		dto.put("locations", locations);

		return dto;
	}
	
	private Boolean isVertical() {
		return locations.get(0).charAt(0) != locations.get(1).charAt(0);
	}
}