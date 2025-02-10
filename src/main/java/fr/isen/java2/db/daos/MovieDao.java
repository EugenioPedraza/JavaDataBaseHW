package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Movie;
import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class MovieDao {


	// Retrieves all the movies
	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();

		// Establish database connection
		try (Connection connection = getDataSource().getConnection()) {
			// Create a statement for executing the query
			try (Statement statement = connection.createStatement()) {
				// Execute query to fetch movies and join them with their genres
				try (ResultSet resultSet = statement.executeQuery("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre")) {
					// Iterate through the result set and populate the movie list
					while (resultSet.next()) {
						// Create Genre object using retrieved data
						Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));

						// Create Movie object using retrieved data
						Movie movie = new Movie(
								resultSet.getInt("idmovie"),
								resultSet.getString("title"),
								resultSet.getDate("release_date").toLocalDate(),
								genre,
								resultSet.getInt("duration"),
								resultSet.getString("director"),
								resultSet.getString("summary"));

						// Add the movie to the list
						movies.add(movie);
					}
				}
			}
		} catch (SQLException e) {
			// Handle SQL exceptions and print error details
			e.printStackTrace();
		}

		// Return the list of movies
		return movies;
	}

	// Retrieves the list of movies by genre
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();

		// Establish database connection
		try (Connection connection = getDataSource().getConnection()) {
			// Prepare SQL query to filter movies by genre
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name=?")) {
				statement.setString(1, genreName); // Set genre name in the query

				// Execute the query and retrieve results
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						// Create Genre object using retrieved data
						Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));

						// Create Movie object using retrieved data
						Movie movie = new Movie(
								resultSet.getInt("idmovie"),
								resultSet.getString("title"),
								resultSet.getDate("release_date").toLocalDate(),
								genre,
								resultSet.getInt("duration"),
								resultSet.getString("director"),
								resultSet.getString("summary"));

						// Add the movie to the list
						movies.add(movie);
					}
				}
			}
		} catch (SQLException e) {
			// Handle SQL exceptions and print error details
			e.printStackTrace();
		}

		// Return the list of movies for the specified genre
		return movies;
	}

	// Adds a new movie to the database have as a parameter the class Movie
	public Movie addMovie(Movie movie) {
		// Establish database connection
		try (Connection connection = getDataSource().getConnection()) {
			// SQL query to insert a new movie
			String sql = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES(?,?,?,?,?,?)";

			// Prepare the statement to insert a new movie and retrieve its generated ID
			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				Genre genre = movie.getGenre();

				// Set parameters for the prepared statement
				statement.setString(1, movie.getTitle());
				statement.setDate(2, java.sql.Date.valueOf(movie.getReleaseDate()));
				statement.setInt(3, genre.getId());
				statement.setInt(4, movie.getDuration());
				statement.setString(5, movie.getDirector());
				statement.setString(6, movie.getSummary());

				// Execute the insertion
				statement.executeUpdate();

				// Retrieve the generated ID for the new movie
				try (ResultSet rs = statement.getGeneratedKeys()) {
					if (rs.next()) {
						movie.setId(rs.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			// Handle SQL exceptions and print error details
			e.printStackTrace();
		}

		// Return the added movie with its assigned ID
		return movie;
	}
}