package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class GenreDao {

	// Retrieves list of genres
	public List<Genre> listGenres() {
		// Create a list to store genres retrieved from the database
		List<Genre> listOfGenres = new ArrayList<>();

		// With the connection class to open the database connection
		try (Connection connection = getDataSource().getConnection()) {
			// We create the SQL statement class
			try (Statement statement = connection.createStatement()) {
				// Using the statement class we insert the SQL query and save it in a ResultSet class to allow
				// the use of pointer .next()
				try (ResultSet results = statement.executeQuery("SELECT * FROM genre")) {
					// While there is a column we continue adding each column to the list of genres
					while (results.next()) {
						// Using the class genre to save each object obtained from the SQL query,
						// in which we get the current columnIndex and genreName
						Genre genre = new Genre(results.getInt("idgenre"),
								results.getString("name"));
						listOfGenres.add(genre);
					}
				}
			}
			// As always catch the exceptions
		} catch (SQLException e ) {
			e.printStackTrace();
		}
		// Return the list of genres
		return listOfGenres;
	}

	// Gets a genre by a name
	public Genre getGenre(String name) {
		// The list will store the query response...
		List<Genre> listOfGenres = new ArrayList<>();

		// Establishing connection...
		try (Connection connection = getDataSource().getConnection()) {
			// Class that runs the SQL query...
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name=?")){
				// Setting the parameter name into the query
				statement.setString(1,name);
				// We execute the query with the now inserted parameter
				try (ResultSet results = statement.executeQuery()){
					if (results.next()){
						// Return the result of the genre
						return new Genre(results.getInt("idgenre"), results.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// If no genre is found return null
		return null;
	}

	// Adds a new genre
	public void addGenre(String name) {
		// Open connection to database
		try (Connection connection = getDataSource().getConnection()) {
				// Create variable with the SQL query
				String sql = "INSERT INTO genre(name) VALUES(?)";
				// Run the query
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					// Setting parameters...
					statement.setString(1,name);
					// Executing the query...
					statement.executeUpdate();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
