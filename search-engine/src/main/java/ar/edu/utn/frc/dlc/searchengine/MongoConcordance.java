package ar.edu.utn.frc.dlc.searchengine;

import com.mongodb.BasicDBObject;

public class MongoConcordance implements Concordance {

  private static final long CONCORDANCE_LIMIT = 50000;
  BasicDBObject concordance = new BasicDBObject();
  private long wordCount = 0;

  public void put(String word) throws ConcordanceTooLargeException {
    wordCount++;

    if (concordance.size() > CONCORDANCE_LIMIT) {
      throw new ConcordanceTooLargeException();
    }

    if (concordance.containsField(word)) {
      long incrementedCount = (Long) concordance.get(word) + 1;
      concordance.put(word, incrementedCount);
    } else {
      concordance.put(word, 1l);
    }
  }

  public long getWordCount() {
    return wordCount;
  }

  public int size() {
    return concordance.size();
  }

  public Long get(String word) {
    Object value = concordance.get(word);
    return value != null ? (Long) value : null;
  }

}