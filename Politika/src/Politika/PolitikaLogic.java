package Politika;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public class PolitikaLogic {
    private Process p;
    String url = "jdbc:sqlite:articleDb.db";


    public PolitikaLogic() {
    }

    public Object[] analyseText(String inputText){
        Double[] results = new Double[2];
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File("jwrite"), true);
            fos.write(inputText.getBytes(), 0,  inputText.length());
            fos.close();
            p = Runtime.getRuntime().exec("/venv/Scripts/python.exe Predictor.py");
            while(p.isAlive()){
                ;
            }
            System.out.println("Program Executed");
            File f = new File("pwrite");
            String encodedString;
            String scriptOutput;
            BufferedReader br = new BufferedReader(new FileReader(f));
            encodedString = br.readLine();
            String[] encodedStringArray = encodedString.split(",");
            ArrayList<String> encodedList = new ArrayList<>();
            for(String s: encodedStringArray){
                if(s.equals("")) continue;
                encodedList.add(s);
            }
            scriptOutput = br.readLine();
            String[] outputArray = scriptOutput.split(",");
            results[0] = Double.parseDouble(outputArray[0]);
            results[1] = Double.parseDouble(outputArray[1]);
            Object[] retArr = new Object[2];
            retArr[0] = results;
            retArr[1] = encodedList;
            br.close();
            f.delete();
            return retArr;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void saveToDB(String inputText, String articleName, String author, String[] predictions,
                         String polParty, String dateCreated) {

        String partyCountSQL = "SELECT COUNT(partyID) FROM Party WHERE partyName = ?";
        String partyInsertSQL = "INSERT INTO Party(partyName) VALUES(?)";
        String partySelectSQL = "SELECT partyID FROM Party WHERE partyName = ?";
        String articleInsertSQL = "INSERT INTO Article(articleName, articleDate, articleContents, conValPred, labValPred, partyID) " +
                "VALUES(?,?,?,?,?,?)";
        String articleSelectSQL = "SELECT articleID FROM Article WHERE articleName = ? AND articleDate = ? AND " +
                "articleContents = ? AND conValPred = ? AND labValPred = ? ANd partyID = ?";
        String authorCountSQL = "SELECT COUNT(AuthorID) FROM Author WHERE lastName=? and foreName=?";
        String authorSelectSQL = "SELECT authorID FROM Author WHERE lastName=? and foreName=?";
        String authorInsertSQL = "INSERT INTO Author(lastName, foreName) VALUES(?,?)";
        String articleAuthorInsertSQL = "INSERT INTO ArticleAuthor(articleID, authorID) VALUES(?,?)";
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url);

            PreparedStatement partyCountStatement = conn.prepareStatement(partyCountSQL);
            partyCountStatement.setString(1, polParty);
            if(partyCountStatement.executeQuery().getInt(1) < 1){
                PreparedStatement partyInsertStatement = conn.prepareStatement(partyInsertSQL);
                partyInsertStatement.setString(1, polParty);
                partyInsertStatement.executeUpdate();
            }

            int partyID;
            PreparedStatement partySelectStatement = conn.prepareStatement(partySelectSQL);
            partySelectStatement.setString(1, polParty);
            partyID = partySelectStatement.executeQuery().getInt(1);

            PreparedStatement articleInsertStatement = conn.prepareStatement(articleInsertSQL);
            articleInsertStatement.setString(1, articleName);
            articleInsertStatement.setString(2, dateCreated);
            articleInsertStatement.setString(3, inputText);
            articleInsertStatement.setDouble(4, Double.parseDouble(predictions[0]));
            articleInsertStatement.setDouble(5, Double.parseDouble(predictions[1]));
            articleInsertStatement.setInt(6, partyID);
            articleInsertStatement.executeUpdate();

            PreparedStatement articleSelectStatement = conn.prepareStatement(articleSelectSQL);
            articleSelectStatement.setString(1, articleName);
            articleSelectStatement.setString(2, dateCreated);
            articleSelectStatement.setString(3, inputText);
            articleSelectStatement.setDouble(4, Double.parseDouble(predictions[0]));
            articleSelectStatement.setDouble(5, Double.parseDouble(predictions[1]));
            articleSelectStatement.setInt(6, partyID);
            int articleID = articleSelectStatement.executeQuery().getInt(1);

            if(!(author.contains("; "))) author = author + "; ";
            String[] authorArray = author.split("; ");
            for(String authorName: authorArray){
                String[] authorNameArray = authorName.split(", ");
                System.out.println(authorNameArray[0] + " " + authorNameArray[1]);
                PreparedStatement authorCountStatement = conn.prepareStatement(authorCountSQL);
                authorCountStatement.setString(1, authorNameArray[0]);
                authorCountStatement.setString(2, authorNameArray[1]);
                ResultSet authorCountResults = authorCountStatement.executeQuery();
                System.out.println(authorCountResults.getInt(1));
                if(authorCountResults.getInt(1) < 1){
                    PreparedStatement authorInsertStatement = conn.prepareStatement(authorInsertSQL);
                    authorInsertStatement.setString(1, authorNameArray[0]);
                    authorInsertStatement.setString(2, authorNameArray[1]);
                    authorInsertStatement.executeUpdate();
                }

                PreparedStatement authorSelectStatement = conn.prepareStatement(authorSelectSQL);
                authorSelectStatement.setString(1, authorNameArray[0]);
                authorSelectStatement.setString(2, authorNameArray[1]);
                int authorID = authorSelectStatement.executeQuery().getInt(1);

                PreparedStatement articleAuthorInsertStatement = conn.prepareStatement(articleAuthorInsertSQL);
                articleAuthorInsertStatement.setInt(1, articleID);
                articleAuthorInsertStatement.setInt(2, authorID);
                articleAuthorInsertStatement.executeUpdate();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public ResultSet retrieveArticles(String searchTerm, String colDiscrim) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        PreparedStatement articleSearchStatement = conn.prepareStatement(
                "SELECT Article.articleID, Article.articleName, Article.articleDate, Article.articleContents, " +
                        "Article.ArticleContents, Article.conValPred, Article.labValPred, Party.partyName " +
                        "FROM Article INNER JOIN Party ON Party.partyID=Article.partyID " +
                        "WHERE " + colDiscrim +" = ?");
        articleSearchStatement.setString(1, searchTerm);
        return articleSearchStatement.executeQuery();
    }

    public ArrayList<ResultSet> retrieveArticlesFromAuthors(String searchInput) throws SQLException{
        ArrayList<ResultSet> resultList = new ArrayList<>();
        String[] authorArray = searchInput.split("; ");
        Connection conn = DriverManager.getConnection(url);
        for(String author: authorArray) {
            String[] authorNameArray = author.split(", ");
            PreparedStatement authorSearchStatement = conn.prepareStatement(
                    "SELECT ArticleAuthor.articleID FROM Author INNER JOIN ArticleAuthor ON ArticleAuthor.authorID = Author.authorID " +
                            "WHERE Author.lastName = ? AND Author.foreName = ?");
            authorSearchStatement.setString(1, authorNameArray[0]);
            authorSearchStatement.setString(2, authorNameArray[1]);
            //ResultSet authorSearchResults =
        }
        return resultList;
    }

    public String retrieveAuthorsFromArticle(int articleID) throws SQLException {
        String authors = "";
        Connection conn = DriverManager.getConnection(url);
        PreparedStatement authorFromArticleStatement = conn.prepareStatement(
                "SELECT Author.lastName, Author.foreName FROM ArticleAuthor " +
                        "INNER JOIN Author ON ArticleAuthor.authorID=Author.authorID " +
                        "WHERE articleID = ?");
        authorFromArticleStatement.setInt(1, articleID);
        ResultSet authorIDResults = authorFromArticleStatement.executeQuery();
        while(authorIDResults.next()){
            String lastName = authorIDResults.getString(1);
            String foreName = authorIDResults.getString(2);
            authors = authors + lastName + ", " + foreName + "; ";
        }
        return authors;
    }

    //public ResultSet searchByAuthorName(String searchTerm){
    //
    //}

    public void search(String[] searchInfo) throws SQLException{
        String searchTerm = searchInfo[0];
        String searchInput = searchInfo[1];
        ResultSet articleResults;
        switch(searchTerm){
            case "Name":
                articleResults = retrieveArticles(searchInput, "Article.articleName"); break;
            case "Date":
                articleResults = retrieveArticles(searchInput, "Article.articleDate"); break;
            case "Author":

                break;
            case "Party":
                articleResults = retrieveArticles(searchInput, "Party.PartyName"); break;
        }
        //while(articleResults.next()){
        //    int articleID = articleResults.getInt(1);
        //    String articleName = articleResults.getString(2);
        //    String articleDate = articleResults.getString(3);
        //    String articleContents = articleResults.getString(4);
        //    int conValPred = articleResults.getInt(5);
        //    int LabValPred = articleResults.getInt(6);
        //    int partyID = articleResults.getInt(7);
        //    String authors = retrieveAuthorsFromArticle(articleID);

        //}
    }

}
