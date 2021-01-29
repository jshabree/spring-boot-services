package com.example.uploadingfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import com.example.Document;
import com.example.DocumentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UploadingFilesApplicationTests {

	@Autowired

	private DocumentRepository repo;

	@Autowired

	private TestEntityManager entityManager;

	@Test
	@Rollback(false)
	void testInsertDocument() throws IOException {
		File file = new File("C:\\Users\\juver\\OneDrive\\Desktop\\upload_documents\\Mehveen-Faria-Resume.pdf");
		
		Document document = new Document();
		document.setName(file.getName());

		byte[] bytes =  Files.readAllBytes(file.toPath());
		document.setContent(bytes);
		long fileSize = bytes.length;
		document.setSize(fileSize);

		document.setUploadTime(new Date());
		


		Document saveDocument = repo.save(document);

		Document existDocument= entityManager.find(Document.class, saveDocument.getId());

		assertEquals(fileSize, existDocument.getSize());
	}

}
