package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Statement;

import java.time.LocalDate;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (3,'Science Fiction')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}

	 @Test
	 public void shouldListMovies() {

			// WHEN
			MovieDao movieDao = new MovieDao();
			List<Movie> movies = movieDao.listMovies();


			// THEN
			assertThat(movies).hasSize(3);
			assertThat(movies).extracting(Movie::getTitle).containsExactly("Title 1", "My Title 2", "Third title");
			assertThat(movies).extracting(Movie::getReleaseDate)
					.containsExactly(
							LocalDate.of(2015, 11, 26),
							LocalDate.of(2015,11,14),
							LocalDate.of(2015,12,12));
			assertThat(movies).extracting(Movie::getGenre).extracting(Genre::getId).containsExactly(1,2,2);
			assertThat(movies).extracting(Movie::getDuration).containsExactly(120,114,176);
			assertThat(movies).extracting(Movie::getDirector).containsExactly("director 1", "director 2", "director 3");
			assertThat(movies).extracting(Movie::getSummary).containsExactly("summary of the first movie", "summary of the second movie", "summary of the third movie");
	 }

	 @Test
	 public void shouldListMoviesByGenre() {
		 MovieDao movieDao = new MovieDao();
		 List<Movie> dramaMovies = movieDao.listMoviesByGenre("Drama");
		 List<Movie> comedyMovies = movieDao.listMoviesByGenre("Comedy");

		 assertThat(dramaMovies).hasSize(1);
		 assertThat(comedyMovies).hasSize(2);
		 assertThat(dramaMovies).extracting(Movie::getGenre).extracting(Genre::getName).containsExactly("Drama");
		 assertThat(comedyMovies).extracting(Movie::getGenre).extracting(Genre::getName).contains("Comedy");

	 }

	@Test
	public void shouldAddMovie() throws Exception {
		MovieDao movieDao = new MovieDao();

		Genre genre = new Genre(3, "Science Fiction");

		// Add the movie
		Movie movie = new Movie(null, "Interstellar", LocalDate.of(2013, 1, 3), genre, 163, "Christopher Nolan", "A space oddyssey to save humanity...");
		movieDao.addMovie(movie);

		// Retrieve and assert
		List<Movie> movies = movieDao.listMovies();
		assertThat(movies).extracting(Movie::getTitle).contains("Interstellar");
		assertThat(movies).extracting(Movie::getReleaseDate).contains(LocalDate.of(2013, 1, 3));
		assertThat(movies).extracting(Movie::getGenre).extracting(Genre::getName).contains("Science Fiction");
		assertThat(movies).extracting(Movie::getDuration).contains(163);
		assertThat(movies).extracting(Movie::getDirector).contains("Christopher Nolan");
		assertThat(movies).extracting(Movie::getSummary).contains("A space oddyssey to save humanity...");
	}
}
