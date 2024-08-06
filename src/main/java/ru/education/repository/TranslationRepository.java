package ru.education.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class TranslationRepository {
    @Value("${database.url}")
    private String URL;
    @Value("${database.username}")
    private String USERNAME;
    @Value("${database.password}")
    private String PASSWORD;

    public void insert(String userAddress, String text, String translatedText) throws SQLException {
        try (
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into translation_requests (user_address, text, translated_text) values (?, ?, ?)"
            )
        ) {
            preparedStatement.setString(1, userAddress);
            preparedStatement.setString(2, text);
            preparedStatement.setString(3, translatedText);

            preparedStatement.executeUpdate();
        }
    }
}
