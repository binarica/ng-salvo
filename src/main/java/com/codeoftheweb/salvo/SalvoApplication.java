package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
	                                  GameRepository gameRepository,
	                                  GamePlayerRepository gamePlayerRepository,
	                                  ShipRepository shipRepository,
	                                  SalvoRepository salvoRepository) {
		return (args) -> {

			Stream.of("j.bauer@ctu.gov", "c.obrian@ctu.gov", "t.almeida@ctu.gov", "d.palmer@whitehouse.gov").forEach(userName -> {
						Player player = new Player(userName);
						playerRepository.save(player);
					});

			Date date = new Date();

			Game game1 = gameRepository.save(new Game(date));
			Game game2 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600))));
			Game game3 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(7200))));

			GamePlayer gamePlayer1 = new GamePlayer(game1, playerRepository.findById(1L).orElse(null));
			GamePlayer gamePlayer2 = new GamePlayer(game1, playerRepository.findById(2L).orElse(null));
			GamePlayer gamePlayer3 = new GamePlayer(game2, playerRepository.findById(1L).orElse(null));

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);

			Ship ship1 = new Ship("Destroyer", Arrays.asList("H2", "H3", "H4"));
			Ship ship2 = new Ship("Submarine", Arrays.asList("E1", "F1", "G1"));
			Ship ship3 = new Ship("Patrol Boat", Arrays.asList("B4", "B5"));

			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer1.addShip(ship3);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);

			Salvo salvo1 = new Salvo(1L, Arrays.asList("H1", "A2"));
			Salvo salvo2 = new Salvo(1L, Arrays.asList("C5", "E6"));
			Salvo salvo3 = new Salvo(2L, Arrays.asList("B4", "D8"));
			Salvo salvo4 = new Salvo(2L, Arrays.asList("A7", "F1"));

			gamePlayer1.addSalvo(salvo1);
			gamePlayer2.addSalvo(salvo2);
			gamePlayer1.addSalvo(salvo3);
			gamePlayer2.addSalvo(salvo4);

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
		};
	}
}