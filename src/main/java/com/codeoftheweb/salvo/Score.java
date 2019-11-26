package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Score {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	private GamePlayer gamePlayer;

	private double score;

	public Score() {}

	public Score(GamePlayer gamePlayer, double score) {
		this.gamePlayer = gamePlayer;
		this.score = score;
	}

	public double getScore() {
		return score;
	}
}