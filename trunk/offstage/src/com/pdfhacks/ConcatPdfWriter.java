package com.pdfhacks;

/* -*- Mode: java; tab-width: 2; c-basic-offset: 2 -*- */
/* http://www.pdfhacks.com/concat/
  concat_pdf, version 1.0, adapted from the iText tools
  catenate input PDF files and write the results into a new PDF
  visit: www.pdfhacks.com/concat/

  This code is free software. It may only be copied or modified
  if you include the following copyright notice:

  This class by Mark Thompson. Copyright (c) 2002 Mark Thompson.

  This code is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 
	 Modified by Robert Fischer; August 8, 2007
 */

import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.*;

public class ConcatPdfWriter extends java.lang.Object {

OutputStream fout;
Document document= null;
PdfCopy writer= null;

public ConcatPdfWriter(OutputStream fout)
{
	this.fout = fout;
}

public void flush() {
	writer.flush();
}
public void writePdfDoc(InputStream fin) throws IOException, DocumentException
{
	// we create a reader for a certain document
	PdfReader reader= new PdfReader( fin );
	reader.consolidateNamedDestinations();

	// we retrieve the total number of pages
	int num_pages= reader.getNumberOfPages();
	System.out.println( "There are "+ num_pages+ 
		" pages in the PDF document");

	// Initialize the output if not yet initialized
	if (document == null) {
		// step 1: creation of a document-object
		document= new Document( reader.getPageSizeWithRotation(1) );

		// step 2: we create a writer that listens to the document
		writer= new PdfCopy( document, fout );

		// step 3: we open the document
		document.open();
	}

	// step 4: we add content
	PdfImportedPage page;
	for( int ii= 1; ii<= num_pages; ++ii) {
		page= writer.getImportedPage( reader, ii );
		writer.addPage( page );
		System.out.println( "Processed page "+ ii );
	}

	if(reader.getAcroForm() != null ) writer.copyAcroForm( reader );
}

public void close()
{
	// step 5: we close the document
	if (document != null) document.close();
}
}
