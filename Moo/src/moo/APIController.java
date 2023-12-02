package moo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal que controla la interfaz de usuario y la interacción con la 
 * API de TMDb.
 */

public class APIController extends JFrame {

    private String apiKey = "1e06b5db7677d5edf1c81d7f19f93735";
    private List<JsonObject> movies;
    private JPanel panel;
    
    /**
     * Constructor de la clase APIController que inicializa la interfaz de
     * usuario.
     */
    public APIController() {
        movies = new ArrayList<>();

        setTitle("Centro de películas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Buscar");
        JButton returnButton = new JButton("Devolver");

        searchButton.setPreferredSize(new Dimension(100, 30));
        returnButton.setPreferredSize(new Dimension(100, 30));

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(returnButton);

        panel = new JPanel(new GridLayout(0, 3, 4, 4));

        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(searchPanel, BorderLayout.NORTH);
        combinedPanel.add(panel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(combinedPanel);
        add(scrollPane);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim().toLowerCase();
                if (!searchTerm.isEmpty()) {
                    try {
                        filterMovies(searchTerm);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(APIController.class.getName()).log
        (Level.SEVERE, null, ex);
                    }
                } else {
                    loadMovies();
                }
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMovies();
            }
        });

        loadMovies();
    }
    /**
     * Carga la lista de películas populares desde la API de TMDb y actualiza la
     * interfaz.
     */
    private void loadMovies() {
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/popular?"
                    + "language=es&api_key=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.
                    openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader
        (connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).
                    getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("results");

            movies.clear();

            for (int i = 0; i < results.size(); i++) {
                JsonObject movie = results.get(i).getAsJsonObject();
                movies.add(movie);

                String posterPath = movie.get("poster_path").getAsString();

                String posterUrl = "https://image.tmdb.org/t/p/w185" + posterPath;

                ImageIcon imageIcon = new ImageIcon(new URL(posterUrl));
                Image image = imageIcon.getImage();
                Image newImage = image.getScaledInstance(150, 225, Image.
                        SCALE_SMOOTH);
                imageIcon = new ImageIcon(newImage);

                JPanel moviePanel = new JPanel();
                JLabel imageLabel = new JLabel(imageIcon);
                moviePanel.add(imageLabel);

                imageLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showMovieDetails(movie, apiKey);
                    }
                });

                panel.add(moviePanel);
            }

            revalidate();
            repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Filtra y muestra las películas que coinciden con el término de búsqueda.
     *
     * @param searchTerm Término de búsqueda para filtrar películas.
     * @throws MalformedURLException Si hay un problema con la URL de la API.
     */
    private void filterMovies(String searchTerm) throws MalformedURLException {
        List<JsonObject> filteredMovies = new ArrayList<>();
        for (JsonObject movie : movies) {
            String title = movie.get("title").getAsString().toLowerCase();
            if (title.contains(searchTerm)) {
                filteredMovies.add(movie);
            }
        }

        panel.removeAll();

        for (JsonObject movie : filteredMovies) {
            String posterPath = movie.get("poster_path").getAsString();

            String posterUrl = "https://image.tmdb.org/t/p/w185" + posterPath;

            ImageIcon imageIcon = new ImageIcon(new URL(posterUrl));
            Image image = imageIcon.getImage();
            Image newImage = image.getScaledInstance(150, 225, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newImage);

            JPanel moviePanel = new JPanel();
            JLabel imageLabel = new JLabel(imageIcon);
            moviePanel.add(imageLabel);

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showMovieDetails(movie, apiKey);
                }
            });

            panel.add(moviePanel);
        }
        revalidate();
        repaint();
    }

    /**
     * Muestra los detalles de una película, incluyendo la sinopsis y opciones 
     * para ver el tráiler y el reparto.
     *
     * @param movie   Objeto JSON que representa la película.
     * @param apiKey  Clave de API para acceder a la API de TMDb.
     */
    private void showMovieDetails(JsonObject movie, String apiKey) {
        String title = movie.get("title").getAsString();
        String overview = movie.get("overview").getAsString();
        int movieId = movie.get("id").getAsInt();

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setPreferredSize(new Dimension(400, 300));

        JTextArea overviewTextArea = new JTextArea(overview, 7, 7);
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setWrapStyleWord(true);
        overviewTextArea.setEditable(false);
        overviewTextArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));

        detailsPanel.add(new JScrollPane(overviewTextArea), BorderLayout.CENTER);

        JButton trailerButton = new JButton("Ver Tráiler");
        JButton castButton = new JButton("Ver Reparto");

        trailerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTrailer(movieId, apiKey);
            }
        });

        castButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCast(movieId, apiKey);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(trailerButton);
        buttonPanel.add(castButton);

        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, detailsPanel, title,
                JOptionPane.PLAIN_MESSAGE);
    }
    /**
     * Muestra el tráiler de una película utilizando la API de YouTube.
     *
     * @param movieId Identificador único de la película.
     * @param apiKey Clave de API para acceder a la API de TMDb.
     */
    private void showTrailer(int movieId, String apiKey) {
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId
                    + "/videos?"
                    + "api_key=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader
        (connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).
                    getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("results");

            if (results.size() > 0) {
                JsonObject video = results.get(0).getAsJsonObject();
                String key = video.get("key").getAsString();
                String trailerUrl = "https://www.youtube.com/watch?v=" + key;

                Desktop.getDesktop().browse(new URI(trailerUrl));
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró tráiler para "
                        + "esta película", "Tráiler no disponible", JOptionPane.
                                INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra el reparto de una película utilizando la información de créditos 
     * de la API de TMDb.
     *
     * @param movieId Identificador único de la película.
     * @param apiKey  Clave de API para acceder a la API de TMDb.
     */
    private void showCast(int movieId, String apiKey) {
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId
                    + "/credits?"
                    + "api_key=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader
        (connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).
                    getAsJsonObject();
            JsonArray cast = jsonObject.getAsJsonArray("cast");

            if (cast.size() > 0) {
                StringBuilder castList = new StringBuilder();
                for (int i = 0; i < Math.min(5, cast.size()); i++) {
                    JsonObject actor = cast.get(i).getAsJsonObject();
                    String actorName = actor.get("name").getAsString();
                    String character = actor.get("character").getAsString();
                    castList.append(actorName).append(" ---> interpreta a: ").
                            append(character).append("\n");
                }

                JOptionPane.showMessageDialog(null, "Reparto:\n" + castList.
                        toString(), "Reparto", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró información de"
                        + " reparto para esta película", "Reparto no disponible",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            APIController frame = new APIController();
            frame.setVisible(true);
        });
    }
}
