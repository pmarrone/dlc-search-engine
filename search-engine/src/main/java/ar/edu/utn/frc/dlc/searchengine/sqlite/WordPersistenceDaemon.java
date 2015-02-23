package ar.edu.utn.frc.dlc.searchengine.sqlite;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import ar.edu.utn.frc.dlc.searchengine.indexer.Dictionary;

public class WordPersistenceDaemon implements Runnable {
  private boolean oneMoreTime = true;
  private Dictionary dictionary;
  private DAL dal;
  private int iteration = 1;
  public WordPersistenceDaemon(Dictionary dictionary, DAL dal) {
    super();
    this.dictionary = dictionary;
    this.dal = dal;
  }

  public void run() {
    while (oneMoreTime) {
      oneMoreTime = !dictionary.isFinished();
      System.out.println("Word Persistence daemon iteration : " + iteration++);
      Collection<Word> words = dictionary.getWords();
      int i = 0;
      for (Word word : words) { 
        try {
          if (word.hasUpdates()) {
            i++;
            List<PostingEntry> postingEntries = word.flushEntries();
            this.dal.flushPostings(word, postingEntries);
          };
          if (i % 300 == 0) {
            dal.commit();
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      try {
        dal.commit();
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    }
    System.out.println("Persistence Daemon done!");
  }
}
