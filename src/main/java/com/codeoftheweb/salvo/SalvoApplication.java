package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
	                                  GameRepository gameRepository,
	                                  GamePlayerRepository gamePlayerRepository,
	                                  ShipRepository shipRepository,
	                                  SalvoRepository salvoRepository,
	                                  ScoreRepository scoreRepository) {
		return (args) -> {

			Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player player3 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
			Player player4 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			Date date = new Date();

			Game game1 = gameRepository.save(new Game(date));
			Game game2 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600))));
			Game game3 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(7200))));
			Game game4 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(10800))));

			GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
			GamePlayer gamePlayer3 = new GamePlayer(game2, player1);
			GamePlayer gamePlayer4 = new GamePlayer(game2, player2);
			GamePlayer gamePlayer5 = new GamePlayer(game3, player2);
			GamePlayer gamePlayer6 = new GamePlayer(game3, player3);
			GamePlayer gamePlayer7 = new GamePlayer(game4, player2);
			GamePlayer gamePlayer8 = new GamePlayer(game4, player1);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);
			gamePlayerRepository.save(gamePlayer8);

			Ship ship1 = new Ship(gamePlayer1, "Destroyer", Arrays.asList("H2", "H3", "H4"));
			Ship ship2 = new Ship(gamePlayer1, "Submarine", Arrays.asList("E1", "F1", "G1"));
			Ship ship3 = new Ship(gamePlayer1, "Patrol Boat", Arrays.asList("B4", "B5"));

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);

			Salvo salvo1 = new Salvo(gamePlayer1, 1L, Arrays.asList("H1", "A2"));
			Salvo salvo2 = new Salvo(gamePlayer2, 1L, Arrays.asList("C5", "E6"));
			Salvo salvo3 = new Salvo(gamePlayer1, 2L, Arrays.asList("B4", "D8"));
			Salvo salvo4 = new Salvo(gamePlayer2, 2L, Arrays.asList("A7", "F1"));

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);

			scoreRepository.save(new Score(gamePlayer1, 1.0));
			scoreRepository.save(new Score(gamePlayer2, 0.0));
			scoreRepository.save(new Score(gamePlayer3, 0.5));
			scoreRepository.save(new Score(gamePlayer4, 0.5));
			scoreRepository.save(new Score(gamePlayer5, 1.0));
			scoreRepository.save(new Score(gamePlayer6, 0.0));
			scoreRepository.save(new Score(gamePlayer7, 0.5));
			scoreRepository.save(new Score(gamePlayer8, 0.5));
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}

@Configuration
class GlobalAuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName -> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		}).passwordEncoder(passwordEncoder);
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/api/games", "/api/players", "/api/login").permitAll()
				.antMatchers("/api/game_view/*").hasAuthority("USER")
				.antMatchers("/rest/*").denyAll()
				.and()
				.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login")
				.and()
				.logout().logoutUrl("/api/logout");

		http.headers().frameOptions().disable();

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());

		// if login is successful, send a success response, with JSON containing the current user
		http.formLogin().successHandler(successHandler());

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler(failureHandler());

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}
	
	private AuthenticationEntryPoint authenticationEntryPoint() {
		return (httpServletRequest, httpServletResponse, exception) -> {
			httpServletResponse.getWriter().append(exception.getMessage());
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		};
	}

	private AuthenticationSuccessHandler successHandler() {
		return (httpServletRequest, httpServletResponse, authentication) -> {
			Player player = playerRepository.findByUserName(authentication.getName());
			httpServletResponse.getWriter().append(player.toJson());
			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		};
	}

	private AuthenticationFailureHandler failureHandler() {
		return (httpServletRequest, httpServletResponse, exception) -> {
			httpServletResponse.getWriter().append(exception.getMessage());
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		};
	}
}
