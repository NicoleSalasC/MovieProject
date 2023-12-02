package moo;

import java.util.List;

public class Movie {

    private String originalTitle;
    private String overview;
    private String posterPath;
    private String titulo;

    public Movie(String titulo, String originalTitle, String overview, String posterPath) {
        this.titulo = titulo;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
    }
    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the originalTitle
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     * @param originalTitle the originalTitle to set
     */
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    /**
     * @return the overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * @param overview the overview to set
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * @return the posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * @param posterPath the posterPath to set
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    private String formatOverview(String overview) {
        int maxLineLength = 80;

        StringBuilder formattedOverview = new StringBuilder();
        String[] words = overview.split("\\s+");
        int currentLineLength = 0;

        for (String word : words) {
            if (currentLineLength + word.length() + 1 <= maxLineLength) {
                formattedOverview.append(word).append(" ");
                currentLineLength += word.length() + 1;
            } else {
                formattedOverview.append("\n    ").append(word).append(" ");
                currentLineLength = word.length() + 5;
            }
        }

        return formattedOverview.toString().trim();
    }

    @Override
    public String toString() {
        return "Pelicula{" + "titulo=" + titulo + ", originalTitle="
                + originalTitle + ", overview=" + formatOverview(overview) + ","
                + " posterPath=" + posterPath + '}';
    }
}
