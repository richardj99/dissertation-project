package Politika;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public class PolitikaLogic {
    private final Runtime rt = Runtime.getRuntime();
    String url = "jdbc:sqlite:articleDb.db";
    RowSetFactory rsf;

    public PolitikaLogic() {
        try{
            rsf = RowSetProvider.newFactory();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> retreiveNNSpecs(){
        try{
            ArrayList<String> nnSpecs = new ArrayList<>();
            Process p = rt.exec("python nnSpecs.py");
            while(p.isAlive()){

            }
            File f = new File("nnConfig");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while((line = br.readLine()) != null){
               nnSpecs.add(line);
            }
            return nnSpecs;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public Object[] analyseText(String inputText){
        inputText = inputText.replace(",", "");
        Double[] results = new Double[2];
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File("jwrite"), true);
            fos.write(inputText.getBytes("UTF-8"), 0,  inputText.length());
            fos.close();
            Process p = rt.exec("python Predictor.py");
            while(p.isAlive()){}
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

    public Object[] analyseBySentence(String inputText, PolitikaInterface i_uiInstance){
        String[] sentences = inputText.split("\\. ");

        int currentIndex = 0;

        double[][] resultsArray= new double[sentences.length][2];
        ArrayList<String> encodedText = new ArrayList<>();

        int sentenceIndex = 0;
        for(String s : sentences){
            Object[] retArr = analyseText(s);
            Double[] results = (Double[]) retArr[0];
            encodedText.addAll((ArrayList<String>) retArr[1]);

            currentIndex = inputText.indexOf(s, currentIndex);
            Color highlightColor;
            if(results[0] > results[1]) highlightColor = Color.BLUE;
            else highlightColor = Color.RED;
            i_uiInstance.highlightText(highlightColor, currentIndex, currentIndex+s.length());
            i_uiInstance.setEncodedTextPanel((ArrayList<String>) retArr[1]);
            resultsArray[sentenceIndex][0] = results[0]; resultsArray[sentenceIndex][1] = results[1];
            sentenceIndex++;
        }
        Double[] finalResults = new Double[2];
        double leftTotal = 0; double rightTotal = 0;
        for(double[] results : resultsArray){
            rightTotal += results[0];
            leftTotal += results[1];
        }
        finalResults[0] = rightTotal/(sentences.length); finalResults[1] = leftTotal/(sentences.length);
        Object[] retArr = new Object[2];
        retArr[0] = finalResults;
        retArr[1] = encodedText;
        return retArr;
    }

    public String calculateFinalBiasResult(Double[] results){
        String finalResult = null;
        if((results[0] > 0.7) & (results[1] < 0.4)) finalResult = "Right-Leaning";
        else if((results[1] > 0.7) & (results[0] < 0.4)) finalResult = "Left-Leaning";
        else finalResult = "Inconclusive";
        return finalResult;
    }

    public void saveToDB(String inputText, String articleName, String author, String[] predictions,
                         String polParty, String dateCreated, PolitikaInterface uiInstance) {

        String partyInsertSQL = "INSERT INTO Party(partyName) VALUES(?)";
        String partySelectSQL = "SELECT partyID FROM Party WHERE partyName = ?";
        String articleInsertSQL = "INSERT INTO Article(articleName, articleDate, articleContents, conValPred, labValPred, partyID) " +
                "VALUES(?,?,?,?,?,?)";
        String articleSelectSQL = "SELECT articleID FROM Article WHERE articleName = ? AND articleDate = ? AND " +
                "articleContents = ? AND conValPred = ? AND labValPred = ? ANd partyID = ?";
        String authorSelectSQL = "SELECT authorID FROM Author WHERE lastName=? and foreName=?";
        String authorInsertSQL = "INSERT INTO Author(lastName, foreName) VALUES(?,?)";
        String articleAuthorInsertSQL = "INSERT INTO ArticleAuthor(articleID, authorID) VALUES(?,?)";
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url);

            PreparedStatement partySelectStatement = conn.prepareStatement(partySelectSQL);
            partySelectStatement.setString(1, polParty);

            CachedRowSet partyResults = rsf.createCachedRowSet();
            partyResults.populate(partySelectStatement.executeQuery());
            int rowNum = getNumberOfRows(partyResults);
            int partyID;
            if(rowNum > 0){
                partyResults.next();
                partyID = partyResults.getInt(1);
            } else{
                PreparedStatement partyInsertStatement = conn.prepareStatement(partyInsertSQL);
                partyInsertStatement.setString(1, polParty);
                partyInsertStatement.executeUpdate();

                ResultSet partyResultsPostInsert = partySelectStatement.executeQuery();
                partyResultsPostInsert.next();
                partyID = partyResultsPostInsert.getInt(1);
            }

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

                PreparedStatement authorSelectStatement = conn.prepareStatement(authorSelectSQL);
                authorSelectStatement.setString(1, authorNameArray[0]);
                authorSelectStatement.setString(2, authorNameArray[1]);
                CachedRowSet authorSelectResults = rsf.createCachedRowSet();
                authorSelectResults.populate(authorSelectStatement.executeQuery());
                rowNum = getNumberOfRows(authorSelectResults);
                if(rowNum == 0){
                    PreparedStatement authorInsertStatement = conn.prepareStatement(authorInsertSQL);
                    authorInsertStatement.setString(1, authorNameArray[0]);
                    authorInsertStatement.setString(2, authorNameArray[1]);
                    authorInsertStatement.executeUpdate();
                    authorSelectResults.populate(authorSelectStatement.executeQuery());
                    rowNum = getNumberOfRows(authorSelectResults);
                } else if(rowNum > 1){

                }
                authorSelectResults.next();
                int authorID = authorSelectResults.getInt(1);

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

    public ArrayList<ArrayList<String>> retrieveArticleResults(String searchTerm, String colDiscriminant) throws SQLException {
        ArrayList<ArrayList<String>> rowList = new ArrayList<>();
        Connection conn = DriverManager.getConnection(url);
        PreparedStatement articleSearchStatement = conn.prepareStatement(
                "SELECT Article.articleID, Article.articleName, Article.articleDate, " +
                        "Article.ArticleContents, Article.conValPred, Article.labValPred, Party.partyName " +
                        "FROM Article INNER JOIN Party ON Party.partyID=Article.partyID " +
                        "WHERE "  + colDiscriminant + " LIKE ?");
        if(colDiscriminant.equals("Article.articleID")){
            int articleID = Integer.parseInt(searchTerm);
            articleSearchStatement.setInt(1, articleID);
        }
        else articleSearchStatement.setString(1, "%"+searchTerm+"%");
        CachedRowSet articleResults = rsf.createCachedRowSet();
        articleResults.populate(articleSearchStatement.executeQuery());
        int rowNum = getNumberOfRows(articleResults);
        if(rowNum > 0) {
            while(articleResults.next()) {
                ArrayList<String> row = new ArrayList<>();
                String articleID = Integer.toString(articleResults.getInt(1));
                row.add(articleID);
                row.add(articleResults.getString(2));
                row.add(retrieveAuthors(articleID, conn));
                row.add(articleResults.getString(3));
                row.add(articleResults.getString(4));
                row.add(Double.toString(articleResults.getDouble(5)));
                row.add(Double.toString(articleResults.getDouble(6)));
                row.add(articleResults.getString(7));
                rowList.add(row);
            }
        }
        return rowList;
    }

    public ArrayList<ArrayList<String>> retrieveAuthorResults(String searchTerm) throws SQLException {
        String articleSQL;
        Connection conn = DriverManager.getConnection(url);
        PreparedStatement authorIDStatement;
        if(searchTerm.contains(", ")){
            String[] name = searchTerm.split(", ");
            articleSQL = "SELECT AA.articleID FROM Author " +
                    "INNER JOIN ArticleAuthor AA on Author.authorID = AA.authorID " +
                    "WHERE Author.lastName = ? AND Author.foreName = ?";
            authorIDStatement = conn.prepareStatement(articleSQL);
            authorIDStatement.setString(1, "%"+name[0]+"%");
            authorIDStatement.setString(2, "%"+name[1]+"%");
        } else{
            articleSQL = "SELECT AA.articleID FROM Author " +
                    "INNER JOIN ArticleAuthor AA on Author.authorID = AA.authorID " +
                    "WHERE AUthor.lastName LIKE ? OR Author.foreName LIKE ?";
            authorIDStatement = conn.prepareStatement(articleSQL);
            authorIDStatement.setString(1, "%"+searchTerm+"%");
            authorIDStatement.setString(2, "%"+searchTerm+"%");
        }
        CachedRowSet authorResults = rsf.createCachedRowSet();
        authorResults.populate(authorIDStatement.executeQuery());
        ArrayList<ArrayList<String>> fetchedResults = new ArrayList<ArrayList<String>>();
        if(getNumberOfRows(authorResults) != 0){
            while(authorResults.next()){
                String articleID = Integer.toString(authorResults.getInt(1));
                ArrayList<ArrayList<String>> articleResultsFromAuthor = retrieveArticleResults(articleID, "Article.articleID");
                fetchedResults.addAll(articleResultsFromAuthor);
            }
            return fetchedResults;
        } else return null;

    }

    public String retrieveAuthors(String i_articleID, Connection conn) throws SQLException {
        String authors = "";
        Integer articleID = Integer.parseInt(i_articleID);
        PreparedStatement  retAuthorsStatement = conn.prepareStatement(
                "SELECT Author.lastName, Author.foreName FROM ArticleAuthor " +
                        "INNER JOIN Author on Author.authorID = ArticleAuthor.authorID " +
                        "WHERE ArticleAuthor.articleID = ?");
        retAuthorsStatement.setInt(1, articleID);
        CachedRowSet authorResults = rsf.createCachedRowSet();
        authorResults.populate(retAuthorsStatement.executeQuery());
        int rowNum = getNumberOfRows(authorResults);
        if(rowNum > 0){
            while(authorResults.next()){
                String lastName = authorResults.getString(1);
                String foreName = authorResults.getString(2);
                authors = authors + lastName +", " + foreName + "; ";
            }
        }
        return authors;
    }

    public ArrayList<ArrayList<String>> search(String[] searchInfo) throws SQLException{
        String searchTerm = searchInfo[0];
        String searchInput = searchInfo[1];
        ArrayList<ArrayList<String>> rows;
        switch(searchTerm){
            case "Name":
                rows = retrieveArticleResults(searchInput, "Article.articleName"); break;
            case "Date":
                rows = retrieveArticleResults(searchInput, "Article.articleDate"); break;
            case "Author":
                rows = retrieveAuthorResults(searchInput); break;
            case "Party":
                rows = retrieveArticleResults(searchInput, "Party.PartyName"); break;
            default:
                return null;
        }
        return rows;
    }

    public int getNumberOfRows(CachedRowSet rowSet) throws SQLException {
        int rowNum;
        rowSet.last();
        rowNum = rowSet.getRow();
        rowSet.beforeFirst();
        return rowNum;
    }

}
