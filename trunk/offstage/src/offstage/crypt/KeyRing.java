/*
 * KeyRing.java
 *
 * Created on July 22, 2007, 11:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

import java.security.interfaces.*;
import javax.crypto.*;
import java.io.*;
import java.security.spec.*;
import java.security.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.mozilla.javascript.JavaAdapter;
import pubdomain.*;

/**
 *
 * @author citibob
 */
public class KeyRing {

PublicKey pubKey;
File privDir;
//PrivateKey privKeys[];		// Contains past history of private keys in decreasing time order
PrivKeyFile privKeys[];
KeyFactory keyFactory;
SecureRandom rnd;
File pubDir;

static class PrivKeyFile
{
	public java.util.Date dt;		// Date/Time identifyin the key
	public PrivateKey key;			// The actual key
}

public static final int EXTRABYTES = 8;		// Add this many random bytes to end of each message before encrypting
public static final byte[] MAGIC = new byte[]{(byte)23,(byte)19,(byte)215,(byte)154,(byte)128,(byte)146,(byte)171,(byte)94};

/** Creates a new instance of KeyRing */
public KeyRing(File pubDir, File privDir)
throws GeneralSecurityException, IOException
{
	this.pubDir = pubDir;
	rnd = SecureRandom.getInstance("SHA1PRNG");
	keyFactory = KeyFactory.getInstance("RSA");

	// Store location of private key, but don't read it yet; that happens
	// only when user inserts the USB drive containing the private keys.
	this.privDir = privDir;

	loadPubKey();
}

public void loadPubKey()
throws GeneralSecurityException, IOException
{
	// Get file names that might be our public key
	String[] files = pubDir.list();
	ArrayList<String> keyFiles = new ArrayList();
	for (int i=0; i<files.length; i++) {
		String fname = files[i];
		if (fname.startsWith("pubkey") && fname.endsWith(".txt")) {
			keyFiles.add(fname);
		}
	}
	Collections.sort(keyFiles);

	// Get most recent public key
	File keyFile = new File(pubDir, keyFiles.get(keyFiles.size()-1));

	// Read and decode public key
	byte[] bytes = pubdomain.Base64.readBase64File(keyFile);
	X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
	pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
}

/** Returns: fals if USB drive was not inserted. */
public boolean loadPrivKeys()
throws GeneralSecurityException, IOException
{
	if (!(privDir.exists() && privDir.isDirectory()) ) return false;

	String[] files = privDir.list();

	// Get file names
	ArrayList<String> keyFiles = new ArrayList();
	for (int i=0; i<files.length; i++) {
		String fname = files[i];
		if (fname.startsWith("privkey") && fname.endsWith(".txt")) {
			keyFiles.add(fname);
		}
	}
	Collections.sort(keyFiles);

	// Read each one as a key file
	ArrayList<PrivKeyFile> xPrivKeys = new ArrayList();
	for (int i=keyFiles.size()-1; i >= 0; ++i) {
		try {
			// Parse the timestamp from the filename
			String fname = keyFiles.get(i);
			int dash = fname.indexOf('-');
			int dot = fname.lastIndexOf('.');
			String sdt = fname.substring(dash+1, dot);
			java.util.Date dt = dfmt.parse(fname);

			// Read the key from the file
			File f = new File(privDir, fname);
			byte[] bytes = pubdomain.Base64.readBase64File(f);
			PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
			PrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

			// Store our (timestamp, key) pair
			PrivKeyFile keyfile = new PrivKeyFile();
				keyfile.dt = dt;
				keyfile.key = key;
			xPrivKeys.add(keyfile);
		} catch(IOException e) {
			// Something wrong with this file; skip it.
		} catch(ParseException e2) {}
	}

	// Convert to an array
	privKeys = new PrivKeyFile[xPrivKeys.size()];
	xPrivKeys.toArray(privKeys);

	return true;
}

/** Encrypts a piece of data (to be stored in a DB table). */
public String encrypt(String msg)
throws GeneralSecurityException, IOException
{
	// Use public key to encrypt a message
	Cipher ecipher = Cipher.getInstance("RSA");
	ecipher.init(Cipher.ENCRYPT_MODE, pubKey);

	// Convert text to bytes, and pad with random extra bytes
	byte[] plainText = msg.getBytes("UTF8");
	byte[] rndBytes = new byte[EXTRABYTES];
	rnd.nextBytes(rndBytes);
	byte[] padded = new byte[MAGIC.length + plainText.length + EXTRABYTES];
	int j=0;
	for (int i=0; i<MAGIC.length; ++i) padded[j++] = MAGIC[i];
	for (int i=0; i<plainText.length; ++i) padded[j++] = plainText[i];
	for (int i=0; i<plainText.length; ++i) padded[j++] = rndBytes[i];

	// Encrypt!
	byte[] cipherText = ecipher.doFinal(padded);

	// Convert to Base 64
	return pubdomain.Base64.encodeBytes(cipherText);
}

/** Decrypts a Base64-encoded, padded message */
public String decrypt(String emsg)
throws GeneralSecurityException
{
	// Decode base 64
	byte[] cipherText = pubdomain.Base64.decode(emsg);

	// Decrypt, trying each key in turn
	for (int i=0; i<privKeys.length; ++i) {
		PrivateKey key = privKeys[i].key;
		Cipher decipher = Cipher.getInstance("RSA");
		decipher.init(Cipher.DECRYPT_MODE, key);

		byte[] decodeText = decipher.doFinal(cipherText);

		// Check the magic bytes to see if we used the right key
		for (int j=0; i<MAGIC.length; ++j) {
			if (decodeText[j] != MAGIC[j]) continue;
		}

		// Decode, removing the extra bytes and the magic first
		String msg = new String(decodeText, MAGIC.length,
			decodeText.length - MAGIC.length - EXTRABYTES);
		return msg;
	}
	return null;		// Could not decipher the message; no key available
}

static DateFormat dfmt;
static {
	dfmt = new SimpleDateFormat("yyyyMMdd-HHmm");
	dfmt.setTimeZone(TimeZone.getTimeZone("GMT"));
}

/** Assumes USB drive is plugged in */
public void createNewMasterKey()
throws GeneralSecurityException, IOException
{	
	// Generate public/private key pair
	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	keyGen.initialize(1024);
	KeyPair key = keyGen.generateKeyPair();
	
	// Name private key file according to timestamp
	java.util.Date dt = new java.util.Date();
	File keyFile = new File(privDir, "privkey-" + dfmt.format(dt) + ".txt");

	// Store the private key
	byte[] bkey = key.getPrivate().getEncoded();
	String skey = pubdomain.Base64.encodeBytes(bkey);
	PrintWriter pw = new PrintWriter(
		new FileWriter(keyFile));
	pw.println("-----BEGIN PRIVATE KEY-----"); //$NON-NLS-1$
	pw.println(skey);
	pw.println("-----END PRIVATE KEY-----"); //$NON-NLS-1$
	pw.close();
	
	// Store the public key
	File[] pubFiles = new File[] {
		new File(privDir, "pubkey-" + dfmt.format(dt) + ".txt"),
		new File(pubDir, "pubkey-" + dfmt.format(dt) + ".txt"),
	};
    bkey = key.getPublic().getEncoded();
	skey = pubdomain.Base64.encodeBytes(bkey);
	for (File f : pubFiles) {
		pw = new PrintWriter(
			new FileWriter(f));
		pw.println("-----BEGIN PUBLIC KEY-----"); //$NON-NLS-1$
		pw.println(skey);
		pw.println("-----END PUBLIC KEY-----"); //$NON-NLS-1$
		pw.close();
	}

}

/** Returns the contents of the file in a byte array.
From Java Developers Almanac */
public static byte[] getBytesFromFile(File file) throws IOException {
	InputStream is = new FileInputStream(file);

	// Get the size of the file
	long length = file.length();

	// You cannot create an array using a long type.
	// It needs to be an int type.
	// Before converting to an int type, check
	// to ensure that file is not larger than Integer.MAX_VALUE.
	if (length > Integer.MAX_VALUE) {
		// File is too large
	}

	// Create the byte array to hold the data
	byte[] bytes = new byte[(int)length];

	// Read in the bytes
	int offset = 0;
	int numRead = 0;
	while (offset < bytes.length
		   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		offset += numRead;
	}

	// Ensure all the bytes have been read in
	if (offset < bytes.length) {
		throw new IOException("Could not completely read file "+file.getName());
	}

	// Close the input stream and return bytes
	is.close();
	return bytes;
}

public static class PlainFile
{
	public String name;		// Leaf name
	public byte[] bytes;
}

public static List<PlainFile> readAllKeyFiles(File dir)
throws IOException
{
	String[] files = dir.list();
//	ArrayList<PlainFile> data = new ArrayList();
	ArrayList data = new ArrayList();
	for (int i=0; i<files.length; i++) {
		String fname = files[i];
		if ((fname.startsWith("privkey") || fname.startsWith("pubkey"))
			&& fname.endsWith(".txt")) {
			File f = new File(dir, fname);
			byte[] bytes = getBytesFromFile(f);
			PlainFile pfile = new PlainFile();
				pfile.name = fname;
				pfile.bytes = bytes;
			data.add(pfile);
		}
	}
	Collections.sort(data);
	return data;
}

public static void writeKeyFiles(File dir, List<PlainFile> data)
throws IOException
{
	for (PlainFile pfile : data) {
		File f = new File(dir, pfile.name);
		FileOutputStream out = new FileOutputStream(f);
		out.write(pfile.bytes);
		out.close();
	}
}


//
//make new master key
//copy master key


public static void main (String[] args) throws Exception
{
	File keyFile = new File("/export/home/citibob/tmp");
	KeyFactory keyFactory = KeyFactory.getInstance("RSA");

	// read public key DER file
	
	byte[] bytes = Base64.readBase64File(
		new File(keyFile, "public.txt"));

	// decode public key
	X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
	PublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

	// Use public key to encrypt a message
	Cipher ecipher = Cipher.getInstance("RSA");
	ecipher.init(Cipher.ENCRYPT_MODE, pubKey);
	
	String plainString = "Hello World!  How are you today?";
	byte[] plainText = plainString.getBytes("UTF8");
	byte[] cipherText = ecipher.doFinal(plainText);
	
	System.out.println("cipher text:");
	System.out.println(pubdomain.Base64.encodeBytes(cipherText));

	
	
	// decode private key
	bytes = Base64.readBase64File(
		new File(keyFile, "private.txt"));
	PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
	PrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

	Cipher decipher = Cipher.getInstance("RSA");
	decipher.init(Cipher.DECRYPT_MODE, privKey);
	byte[] decodeText = decipher.doFinal(cipherText);
	String cipherString = new String(decodeText, "UTF8");
	
	System.out.println("decoded: " + cipherString);
}




}
