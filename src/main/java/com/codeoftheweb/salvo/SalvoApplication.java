package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
		return (args) -> {

			Stream.of("j.bauer@ctu.gov", "c.obrian@ctu.gov", "t.almeida@ctu.gov", "d.palmer@whitehouse.gov").forEach(userName -> {
				Player player = new Player(userName);
				playerRepository.save(player);
			});

			Date date = new Date();

			Game game1 = gameRepository.save(new Game(date));
			Game game2 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600))));
			Game game3 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(7200))));

			gamePlayerRepository.save(new GamePlayer(game1, playerRepository.findById(1L).get()));
			gamePlayerRepository.save(new GamePlayer(game1, playerRepository.findById(2L).get()));
			gamePlayerRepository.save(new GamePlayer(game2, playerRepository.findById(1L).get()));

		};
	}
}