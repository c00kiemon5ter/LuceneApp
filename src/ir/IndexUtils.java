package ir;

import core.Doc;
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

public class IndexUtils {

	private Directory index;
	private SimpleAnalyzer simpleAnalyzer;
	private Collection<Document> documents;

	public IndexUtils(Collection<Document> documents) {
		this.index = new RAMDirectory();
		this.simpleAnalyzer = new SimpleAnalyzer();
		this.documents = documents;
	}

	public Directory createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriter idxwriter = new IndexWriter(index, simpleAnalyzer, true, IndexWriter.MaxFieldLength.LIMITED);
		for (Document document : documents) {
			idxwriter.addDocument(document);
		}
		idxwriter.optimize();
		idxwriter.close();
		return index;
	}

	public void printIndexInfo() throws CorruptIndexException, IOException {
		printIndexInfo(index);
	}

	public void printIndexInfo(Directory index) throws CorruptIndexException, IOException {
		IndexReader idxreader = IndexReader.open(index, true);
		System.out.println("Index is" + (idxreader.isCurrent() ? "" : " not") + " current");
		System.out.println("Index version is " + idxreader.getVersion());
		System.out.println("Index" + (idxreader.isOptimized() ? " is" : " is not") + " optimized");
		System.out.println("Index is segmented into " + idxreader.getIndexCommit().getFileNames().size() + " file");
		System.out.println("Index files are " + idxreader.getIndexCommit().getFileNames().toString());
		System.out.println("Index currently operates on " + idxreader.getIndexCommit().getSegmentsFileName());
		System.out.println("Index generation is " + idxreader.getIndexCommit().getGeneration());
		System.out.println("Index holds " + idxreader.numDocs() + " documents");
		System.out.println("Index has modified " + idxreader.numDeletedDocs() + " documents");
		System.out.println("Index" + (idxreader.hasDeletions() ? " has" : " doesn't have any") + " deletions");
	}

	public Directory getIndex() {
		return index;
	}

	public SimpleAnalyzer getSimpleAnalyzer() {
		return simpleAnalyzer;
	}

	public void printIndex() throws CorruptIndexException, IOException {
		IndexReader idxreader = IndexReader.open(index, true);
		TermEnum termEnum = idxreader.terms();
		TermDocs termDocs = idxreader.termDocs();
		System.out.println("DocId\tDF\tTF\tIDF\tTF-IDF\tTerm");
		while (termEnum.next()) {
			termDocs.seek(termEnum);
			while (termDocs.next()) {
				int docsnum = idxreader.numDocs();
				int tf = termDocs.freq();
				int df = termEnum.docFreq();
				float idf = Similarity.getDefault().idf(df, docsnum);
				float tfidf = idf * tf;
				System.out.printf("%d\t%d\t%d\t%f\t%f\t%s\n",
						  termDocs.doc(), df, tf, idf, tfidf,
						  termEnum.term().text().toString());
			}
		}
	}

	public void printDocumentVectors() throws CorruptIndexException, IOException {
		IndexReader idxreader = IndexReader.open(index, true);
		TermEnum termEnum = idxreader.terms();
		TermDocs termDocs = idxreader.termDocs();
		while (termEnum.next()) {
			termDocs.seek(termEnum);
			System.out.format("%s\tTerm: %s\n", Doc.Fields.DOCID, termEnum.term().text());
			while (termDocs.next()) {
				int docsnum = idxreader.numDocs();
				int tf = termDocs.freq();
				int df = termEnum.docFreq();
				float idf = Similarity.getDefault().idf(df, docsnum);
				float tfidf = idf * tf;
				System.out.format("%-" + Doc.Fields.DOCID.length() + "d\t%f\n", termDocs.doc(), tfidf);
			}
		}
	}
}
